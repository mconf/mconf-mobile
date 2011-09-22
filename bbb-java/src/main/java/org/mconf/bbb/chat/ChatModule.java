/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.Module;
import org.mconf.bbb.MainRtmpConnection;
import org.mconf.bbb.users.IParticipant;
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

	public ChatModule(MainRtmpConnection handler, Channel channel) {
		super(handler, channel);
		
		publicChatSO = handler.getSharedObject("chatSO", false);
		publicChatSO.addSharedObjectListener(this);
		publicChatSO.connect(channel);
		
		privateChatSO = handler.getSharedObject(Integer.toString(handler.getContext().getMyUserId()), false);
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
				onPublicChatMessage(new ChatMessage(strParams));
				return;
			}
		}
		if (so.equals(privateChatSO)) {
			if (method.equals("messageReceived") && params != null) {
				// example: [97, oi|Felipe|0|14:35|en|97]
				int userid = Integer.parseInt((String) ((LinkedList<String>) params).get(0));
				String strParams = ((LinkedList<String>) params).get(1);
				onPrivateChatMessage(new ChatMessage(strParams), handler.getContext().getUsersModule().getParticipants().get(userid));
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
				onPublicChatMessage(new ChatMessage((String) message));
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
		chatMessage.setUsername(handler.getContext().getJoinService().getJoinedMeeting().getFullname());
		chatMessage.setUserId(handler.getContext().getMyUserId());

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
		chatMessage.setUsername(handler.getContext().getJoinService().getJoinedMeeting().getFullname());
		chatMessage.setUserId(handler.getContext().getMyUserId());
		
    	Command command = new CommandAmf0("chat.privateMessage", null, chatMessage.encode(), Double.valueOf(handler.getContext().getMyUserId()), Double.valueOf(userid));
    	handler.writeCommandExpectingResult(channel, command);

    	// the message sent should be received like on public chat
    	onPrivateChatMessage(chatMessage, handler.getContext().getUsersModule().getParticipants().get(userid));
	}
	
	public void onPublicChatMessage(ChatMessage chatMessage) {
		IParticipant source = handler.getContext().getUsersModule().getParticipants().get(chatMessage.getUserId());
		onPublicChatMessage(chatMessage, source);
	}
	
	public void onPublicChatMessage(ChatMessage chatMessage, IParticipant source) {
		for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
			l.onPublicChatMessage(chatMessage, source);
		}
		log.info("handling public chat message: {}", chatMessage);
		publicChatMessages.add(chatMessage);
	}

	public void onPrivateChatMessage(ChatMessage chatMessage, IParticipant source) {
		for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
			l.onPrivateChatMessage(chatMessage, source);
		}
		synchronized (privateChatMessages) {
			if (!privateChatMessages.containsKey(source.getUserId()))
				privateChatMessages.put(source.getUserId(), new ArrayList<ChatMessage>());
			privateChatMessages.get(source.getUserId()).add(chatMessage);
		}
		log.info("handling private chat message from {}: {}", source, chatMessage);
	}
	
	public List<ChatMessage> getPublicChatMessage() {
		return publicChatMessages;
	}
	
	public Map<Integer, List<ChatMessage>> getPrivateChatMessage() {
		return privateChatMessages;
	}

	@Override
	public boolean onCommand(String resultFor, Command command) {
		if (onGetChatMessages(resultFor, command))
			return true;
		else
			return false;
	}

}
