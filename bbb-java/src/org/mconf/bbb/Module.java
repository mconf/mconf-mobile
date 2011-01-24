package org.mconf.bbb;

import org.jboss.netty.channel.Channel;

public abstract class Module {
	protected final RtmpConnectionHandler handler;
	protected final Channel channel;
	
	public Module(final RtmpConnectionHandler handler, final Channel channel) {
		this.handler = handler;
		this.channel = channel;
	}
}
