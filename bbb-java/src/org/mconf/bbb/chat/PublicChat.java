package org.mconf.bbb.chat;

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

public class PublicChat extends Module implements ISharedObjectListener {
	private static final Logger log = LoggerFactory.getLogger(PublicChat.class);

	private final IClientSharedObject so;
	
	public PublicChat(RtmpConnectionHandler handler, Channel channel) {
		super(handler, channel);	
		
		so = handler.getSharedObject("chatSO", false);
		so.addSharedObjectListener(this);
		so.connect(channel);
	}

	@Override
	public void onSharedObjectClear(ISharedObjectBase so) {
		log.debug("onSharedObjectClear");
		doGetChatMessages();
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
		
		if (method.equals("newChatMessage") && params != null) {
			String strParams = ((LinkedList<String>) params).get(0);
			handlePublicChatMessage(new ChatMessage(strParams));
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
	
	public void doGetChatMessages() {
    	Command test = new CommandAmf0("chat.getChatMessages", null);
    	handler.writeCommandExpectingResult(channel, test);
	}
	
	public boolean onGetChatMessages(String resultFor, Command command) {
		if (resultFor.equals("chat.getChatMessages")) {
			return true;
		}
		return false;
	}
	
	public void sendChatMessage(String message) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setUsername(handler.getJoinedMeeting().getFullname());
		chatMessage.setUserid(handler.getMyUserId());

    	Command command = new CommandAmf0("chat.sendMessage", null, chatMessage.encode());
    	handler.writeCommandExpectingResult(channel, command);
	}
	
	public void handlePublicChatMessage(ChatMessage chatMessage) {
		
	}

}
