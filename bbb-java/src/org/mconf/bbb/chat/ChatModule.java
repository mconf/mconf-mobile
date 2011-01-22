package org.mconf.bbb.chat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.Module;
import org.mconf.bbb.RtmpConnectionHandler;
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

	private final IClientSharedObject publicChat, privateChat;

	private Map<Integer, Participant> participants = new HashMap<Integer, Participant>();

	public ChatModule(RtmpConnectionHandler handler, Channel channel) {
		super(handler, channel);
		
		publicChat = handler.getSharedObject("chatSO", false);
		publicChat.addSharedObjectListener(this);
		publicChat.connect(channel);
		
		privateChat = handler.getSharedObject(Integer.toString(handler.getMyUserId()), false);
		privateChat.addSharedObjectListener(this);
		privateChat.connect(channel);
	}

	@Override
	public void onSharedObjectClear(ISharedObjectBase so) {
		log.debug("onSharedObjectClear");
		if (so.equals(publicChat)) {
			doGetChatMessages();
			doQueryParticipants();
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
		
		if (so.equals(publicChat)) {
			if (method.equals("newChatMessage") && params != null) {
				// example: [oi|Felipe|0|14:35|en|97]
				String strParams = ((LinkedList<String>) params).get(0);
				handlePublicChatMessage(new ChatMessage(strParams));
				return;
			}
		}
		if (so.equals(privateChat)) {
			if (method.equals("messageReceived") && params != null) {
				// example: [97, oi|Felipe|0|14:35|en|97]
				@SuppressWarnings("unused")
				String userid = ((LinkedList<String>) params).get(0);
				String strParams = ((LinkedList<String>) params).get(1);
				handlePrivateChatMessage(new ChatMessage(strParams));
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
			List<Object> messages = (List<Object>) Arrays.asList((Object[]) command.getArg(0));
			for (Object message : messages) {
				handlePublicChatMessage(new ChatMessage((String) message));
			}
			return true;
		}
		return false;
	}

	/**
	 * {@link} https://github.com/bigbluebutton/bigbluebutton/blob/master/bigbluebutton-client/src/org/bigbluebutton/modules/chat/services/PrivateChatSharedObjectService.as#L142
	 */
	public void doQueryParticipants() {
    	Command cmd = new CommandAmf0("participants.getParticipants", null);
    	handler.writeCommandExpectingResult(channel, cmd);
	}
	
	/**
	 * example:
	 * [MAP {count=2.0, participants={112={status={raiseHand=false, hasStream=false, presenter=false}, name=Eclipse, userid=112.0, role=VIEWER}, 97={status={raiseHand=false, hasStream=false, presenter=true}, name=Felipe, userid=97.0, role=MODERATOR}}}]
	 * [1 COMMAND_AMF0 c3 #0 t0 (0) s299] name: _result, transactionId: 4, object: null, args: [{count=2.0, participants={112={status={raiseHand=false, hasStream=false, presenter=false}, name=Eclipse, userid=112.0, role=VIEWER}, 97={status={raiseHand=false, hasStream=false, presenter=true}, name=Felipe, userid=97.0, role=MODERATOR}}}]
	 */
	@SuppressWarnings("unchecked")
	public boolean onQueryParticipants(String resultFor, Command command) {
		if (resultFor.equals("participants.getParticipants")) {
			Map<String, Object> args = (Map<String, Object>) command.getArg(0);
			
			participants.clear();
			
			@SuppressWarnings("unused")
			int count = ((Double) args.get("count")).intValue();
			
			Map<String, Object> participantsMap = (Map<String, Object>) args.get("participants");
			
			for (Map.Entry<String, Object> entry : participantsMap.entrySet()) {
				Participant participant = new Participant((Map<String, Object>) entry.getValue());
				log.info("new participant: {}", participant.toString());
				participants.put(participant.getUserId(), participant);			
			}
			return true;
		}
		return false;
	}
	
	public Collection<Participant> getParticipants() {
		return participants.values();
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
		log.info("handling public chat message: {}", chatMessage);
	}

	public void handlePrivateChatMessage(ChatMessage chatMessage) {
		log.info("handling private chat message: {}", chatMessage);
	}

}
