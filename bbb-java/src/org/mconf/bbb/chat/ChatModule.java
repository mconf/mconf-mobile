package org.mconf.bbb.chat;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.Module;
import org.mconf.bbb.RtmpConnectionHandler;

import com.flazr.rtmp.message.Command;

public class ChatModule extends Module {
	private PublicChat publicChat;
	
	public ChatModule(RtmpConnectionHandler handler, Channel conn) {
		super(handler, conn);
		
		publicChat = new PublicChat(handler, conn);
	}

	public boolean onGetChatMessages(String resultFor, Command command) {
		return publicChat.onGetChatMessages(resultFor, command);
	}

}
