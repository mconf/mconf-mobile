package org.mconf.bbb.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.Module;
import org.mconf.bbb.RtmpConnectionHandler;
import org.mconf.bbb.users.Participant;
import org.red5.server.api.IAttributeStore;
import org.red5.server.api.so.IClientSharedObject;
import org.red5.server.api.so.ISharedObjectBase;
import org.red5.server.api.so.ISharedObjectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.CommandAmf0;

public class ChatModule extends Module implements ISharedObjectListener {
	private static final Logger log = LoggerFactory.getLogger(ChatModule.class);

	private final IClientSharedObject publicChatSO, privateChatSO;

	private List<ChatMessage> publicChatMessages = Collections.synchronizedList(new ArrayList<ChatMessage>());
	private Map<Integer, List<ChatMessage>> privateChatMessages = new ConcurrentHashMap<Integer, List<ChatMessage>>();

	public ChatModule(RtmpConnectionHandler handler, Channel channel) {
		super(handler, channel);
		
		publicChatSO = handler.getSharedObject("chatSO", false);
		publicChatSO.addSharedObjectListener(this);
		publicChatSO.connect(channel);
		
		privateChatSO = handler.getSharedObject(Integer.toString(handler.getMyUserId()), false);
		privateChatSO.addSharedObjectListener(this);
		privateChatSO.connect(channel);
	}

	@Override
	public void onSharedObjectClear(ISharedObjectBase so) {
		log.debug("onSharedObjectClear");
		if (so.equals(publicChatSO)) {
			publicChatMessages.clear();
			doGetChatMessages();
			return;
		}
		if (so.equals(privateChatSO)) {
			privateChatMessages.clear();
		}
	}

	@Override
	public void onSharedObjectConnect(ISharedObjectBase so) {
		log.debug("onSharedObjectConnect");
	}

	@Override
	public void onSharedObjectDelete(ISharedObjectBase so, String key) {
		log.debug("onSharedObjectDelete");
	}

	@Override
	public void onSharedObjectDisconnect(ISharedObjectBase so) {
		log.debug("onSharedObjectDisconnect");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSharedObjectSend(ISharedObjectBase so, 
			String method, List<?> params) {
		log.debug("onSharedObjectSend");
		
		if (so.equals(publicChatSO)) {
			if (method.equals("newChatMessage") && params != null) {
				// example: [oi|Felipe|0|14:35|en|97]
				String strParams = ((LinkedList<String>) params).get(0);
				handlePublicChatMessage(new ChatMessage(strParams));
				return;
			}
		}
		if (so.equals(privateChatSO)) {
			if (method.equals("messageReceived") && params != null) {
				// example: [97, oi|Felipe|0|14:35|en|97]
				int userid = Integer.parseInt((String) ((LinkedList<String>) params).get(0));
				String strParams = ((LinkedList<String>) params).get(1);
				handlePrivateChatMessage(new ChatMessage(strParams), handler.getUsers().getParticipants().get(userid));
				return;
			}
		}
		
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so, String key,
			Object value) {
		log.debug("onSharedObjectUpdate 1");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so,
			IAttributeStore values) {
		log.debug("onSharedObjectUpdate 2");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so,
			Map<String, Object> values) {
		log.debug("onSharedObjectUpdate 3");
	}
	
	/**
	 * {@link} https://github.com/bigbluebutton/bigbluebutton/blob/master/bigbluebutton-client/src/org/bigbluebutton/modules/chat/services/PublicChatSharedObjectService.as#L128
	 */
	public void doGetChatMessages() {
    	Command cmd = new CommandAmf0("chat.getChatMessages", null);
    	handler.writeCommandExpectingResult(channel, cmd);
	}
	
	/**
	 * example:
	 * [ARRAY [oi|Felipe|0|21:37|en|61, alo|Felipe|0|21:48|en|61, testando|Felipe|0|21:48|en|61]]
	 * @param resultFor
	 * @param command
	 * @return
	 */
	/*
	 */
	public boolean onGetChatMessages(String resultFor, Command command) {
		if (resultFor.equals("chat.getChatMessages")) {
			publicChatMessages.clear();
			
			List<Object> messages = (List<Object>) Arrays.asList((Object[]) command.getArg(0));
			for (Object message : messages) {
				handlePublicChatMessage(new ChatMessage((String) message));
			}
			return true;
		}
		return false;
	}

	/**
	 * {@link} https://github.com/bigbluebutton/bigbluebutton/blob/master/bigbluebutton-client/src/org/bigbluebutton/modules/chat/services/PublicChatSharedObjectService.as#L89
	 * @param message
	 */
	public void sendPublicChatMessage(String message) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setMessage(message);
		chatMessage.setUsername(handler.getJoinedMeeting().getFullname());
		chatMessage.setUserid(handler.getMyUserId());

    	Command command = new CommandAmf0("chat.sendMessage", null, chatMessage.encode());
    	handler.writeCommandExpectingResult(channel, command);
	}
	
	/**
	 * {@link} https://github.com/bigbluebutton/bigbluebutton/blob/master/bigbluebutton-client/src/org/bigbluebutton/modules/chat/services/PrivateChatSharedObjectService.as#L103
	 * @param message
	 * @param userid
	 */
	public void sendPrivateChatMessage(String message, int userid) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setMessage(message);
		chatMessage.setUsername(handler.getJoinedMeeting().getFullname());
		chatMessage.setUserid(handler.getMyUserId());
		
    	Command command = new CommandAmf0("chat.privateMessage", null, chatMessage.encode(), Double.valueOf(handler.getMyUserId()), Double.valueOf(userid));
    	handler.writeCommandExpectingResult(channel, command);
	}
	
	public void handlePublicChatMessage(ChatMessage chatMessage) {
		publicChatMessages.add(chatMessage);
		log.info("handling public chat message: {}", chatMessage);
	}

	public void handlePrivateChatMessage(ChatMessage chatMessage, Participant source) {
		synchronized (privateChatMessages) {
			if (!privateChatMessages.containsKey(source.getUserId()))
				privateChatMessages.put(source.getUserId(), new ArrayList<ChatMessage>());
			privateChatMessages.get(source.getUserId()).add(chatMessage);
		}
		log.info("handling private chat message from {}: {}", source, chatMessage);
	}

}
