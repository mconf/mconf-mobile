package org.mconf.bbb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.mconf.bbb.api.BigBlueButtonApi;
import org.mconf.bbb.api.JoinedMeeting;
import org.mconf.bbb.api.Meeting;
import org.mconf.bbb.api.Meetings;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpDecoder;
import com.flazr.rtmp.RtmpEncoder;
import com.flazr.rtmp.client.ClientHandshakeHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.util.Utils;

public class BigBlueButtonClient implements IBigBlueButtonClient {
	
	private static final Logger log = LoggerFactory.getLogger(BigBlueButtonClient.class);
	
	private String serverUrl, salt;
	private BigBlueButtonApi api;
	private Meetings meetings = new Meetings();
	private RtmpConnectionHandler handler;

	private ClientBootstrap bootstrap;

	private ChannelFuture future;
	
	
	public List<Meeting> getMeetings() {
		return meetings.getMeetings();
	}
	
	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public boolean load() {
		return load("bigbluebutton.properties");
	}
	
	@Override
	public boolean load(String filepath) {
		Properties p = new Properties();
		try {
			p.load(new BufferedReader(new FileReader(filepath)));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't find/load the properties file");
			return false;
		}
		
		return load(p.getProperty("bigbluebutton.web.serverURL"), p.getProperty("beans.dynamicConferenceService.securitySalt"));
	}

	public boolean load(String serverUrl, String salt) {
		this.serverUrl = serverUrl;
		this.salt = salt;
		this.api = new BigBlueButtonApi(serverUrl + "/bigbluebutton/", salt);
		
		String strMeetings = api.getMeetings();
		if (strMeetings == null) {
			log.info("Can't connect to {}", serverUrl);
			return false;
		}
		
		meetings.parse(strMeetings);
		log.debug(meetings.toString());
		
		return true;
	}

	public JoinedMeeting join(String meetingID, String name, boolean moderator) {
		for (Meeting m : meetings.getMeetings()) {
			log.info(m.getMeetingID());
			if (m.getMeetingID().equals(meetingID)) {
				return join(m, name, moderator);
			}
		}
		return null;
	}
	
	public boolean createMeeting(Meeting meeting) {
		String create = api.createMeeting(meeting.getMeetingID(), 
				"Welcome message", 
				meeting.getModeratorPW(), 
				meeting.getAttendeePW(), 
				0, 
				serverUrl);
		log.debug("createMeeting: {}", create);
		return create.equals(meeting.getMeetingID()); 
	}
	
	@Override
	public JoinedMeeting join(Meeting meeting, String name, boolean moderator) {
		if (api.isMeetingRunning(meeting.getMeetingID()).equals("false")) {
			if (!createMeeting(meeting)) {
				log.error("The meeting {} is not running", meeting.getMeetingID());
				return null;
			}
		}
		
		String joinUrl = api.getJoinMeetingURL(name, meeting.getMeetingID(), moderator? meeting.getModeratorPW() : meeting.getAttendeePW());
		log.debug(joinUrl);
		
		JoinedMeeting joined = new JoinedMeeting();
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(joinUrl);
			client.executeMethod(method);
			method.releaseConnection();
			
			method = new GetMethod(serverUrl + "/bigbluebutton/api/enter");
			client.executeMethod(method);
			joined.parse(method.getResponseBodyAsString());
			method.releaseConnection();

			connectToRtmp(joined);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't join the meeting {}", meeting.getMeetingID());
			
			return null;
		}

		return joined;
	}
	
	private void connectToRtmp(JoinedMeeting meeting) {
		ClientOptions opt = new ClientOptions();
		opt.setClientVersionToUse(Utils.fromHex("00000000"));
		opt.setHost(serverUrl.toLowerCase().replace("http://", ""));
		opt.setAppName("bigbluebutton/" + meeting.getConference());
		log.debug(opt.toString());
		connect(opt, meeting);
	}
	
	private void connect(final ClientOptions options, final JoinedMeeting meeting) {  
        bootstrap = getBootstrap(Executors.newCachedThreadPool(), options, meeting);
        future = bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));
        future.awaitUninterruptibly();
        if(!future.isSuccess()) {
            future.getCause().printStackTrace();
            log.error("error creating client connection: {}", future.getCause().getMessage());
        }
//        future.getChannel().getCloseFuture().awaitUninterruptibly();
//        bootstrap.getFactory().releaseExternalResources();
    }
	
	public void disconnect() {
		future.getChannel().close();
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
	}
	
	private ClientBootstrap getBootstrap(final Executor executor, final ClientOptions options, final JoinedMeeting meeting) {
        final ChannelFactory factory = new NioClientSocketChannelFactory(executor, executor);
        final ClientBootstrap bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
		        final ChannelPipeline pipeline = Channels.pipeline();
		        pipeline.addLast("handshaker", new ClientHandshakeHandler(options));
		        pipeline.addLast("decoder", new RtmpDecoder());
		        pipeline.addLast("encoder", new RtmpEncoder());
		        pipeline.addLast("handler", handler = new RtmpConnectionHandler(options, meeting));
		        return pipeline;
			}
		});
        bootstrap.setOption("tcpNoDelay" , true);
        bootstrap.setOption("keepAlive", true);
        return bootstrap;
    }
	
	public RtmpConnectionHandler getHandler() {
		return handler;
	}
	
	@Override
	public Collection<Participant> getParticipants() {
		return handler.getUsers().getParticipants().values();
	}

	@Override
	public List<ChatMessage> getPublicChatMessages() {
		return handler.getChat().getPublicChatMessage();
	}

	@Override
	public void sendPrivateChatMessage(String message, int userId) {
		handler.getChat().sendPrivateChatMessage(message, userId);
	}

	@Override
	public void sendPublicChatMessage(String message) {
		handler.getChat().sendPublicChatMessage(message);		
	}
	
	public static void main(String[] args) {
		BigBlueButtonClient client = new BigBlueButtonClient();
		client.load();
		
		if (!client.getMeetings().isEmpty()) {
			JoinedMeeting meeting = client.join(client.getMeetings().get(0), "Eclipse", false);
			if (meeting != null) {
				client.connectToRtmp(meeting);
			}
		}
	}

	@Override
	public void addListener(IBigBlueButtonClientListener listener) {
		handler.addListener(listener);
	}

	@Override
	public void removeListener(IBigBlueButtonClientListener listener) {
		handler.removeListener(listener);
	}

}
