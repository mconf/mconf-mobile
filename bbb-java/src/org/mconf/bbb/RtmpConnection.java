package org.mconf.bbb;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientOptions;

public abstract class RtmpConnection extends ClientHandler {

	private static final Logger log = LoggerFactory.getLogger(RtmpConnection.class);

	final protected BigBlueButtonClient context;
	
	public RtmpConnection(ClientOptions options, BigBlueButtonClient context) {
		super(options);
		this.context = context;
	}
	
	private ClientBootstrap bootstrap;
	private ChannelFuture future;
	
	public void connect() {  
        bootstrap = getBootstrap(Executors.newCachedThreadPool());
        future = bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));
        future.awaitUninterruptibly();
        if(!future.isSuccess()) {
            future.getCause().printStackTrace();
            log.error("error creating client connection: {}", future.getCause().getMessage());
        }
    }
	
	public void disconnect() {
		future.getChannel().close();
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
	}
	
	abstract protected ClientBootstrap getBootstrap(final Executor executor);
}
