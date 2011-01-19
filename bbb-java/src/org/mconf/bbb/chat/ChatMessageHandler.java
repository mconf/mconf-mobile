package org.mconf.bbb.chat;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientOptions;

public class ChatMessageHandler extends ClientHandler {

	public ChatMessageHandler(ClientOptions options) {
		super(options);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) {
		super.messageReceived(ctx, me);
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) {
		super.channelConnected(ctx, e);
	}
	
}
