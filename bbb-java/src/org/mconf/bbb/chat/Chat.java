package org.mconf.bbb.chat;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpDecoder;
import com.flazr.rtmp.RtmpEncoder;
import com.flazr.rtmp.client.ClientHandshakeHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.util.Utils;

public class Chat {

	private final static Logger logger = LoggerFactory.getLogger(Chat.class);

	public final static String serverUrl = "143.54.12.199";
	public final static String so = "chatSO";

	public final static String conferenceId = "9814507a-5ce5-4162-975b-f77cc05d5a28";
	public final static String appName = "bigbluebutton/" + conferenceId;
	
	private static void connectToChatModule() {
		ClientOptions opt = new ClientOptions();
		opt.setClientVersionToUse(Utils.fromHex("00000000"));
		opt.setHost(serverUrl);
		//opt.setAppName("bigbluebutton/" + conferenceId);
		//opt.setAppName("bigbluebutton");
		opt.setAppName(appName);
		
		connect(opt);
	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//BasicConfigurator.configure();
		connectToChatModule();
		
	}

}
