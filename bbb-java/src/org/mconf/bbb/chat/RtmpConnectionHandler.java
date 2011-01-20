package org.mconf.bbb.chat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.mconf.bbb.api.JoinedMeeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.amf.Amf0Object;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.client.ClientSharedObject;
import com.flazr.rtmp.message.AbstractMessage;
import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.CommandAmf0;
import com.flazr.rtmp.message.SharedObject;
import com.flazr.rtmp.message.SharedObjectAmf0;

public class RtmpConnectionHandler extends ClientHandler {

    private static final Logger log = LoggerFactory.getLogger(RtmpConnectionHandler.class);
    private JoinedMeeting meeting;
    
    private SharedObject so = new SharedObjectAmf0("participantsSO", false);

	/**
	 * Shared objects map
	 */
	private volatile ConcurrentMap<String, ClientSharedObject> sharedObjects = new ConcurrentHashMap<String, ClientSharedObject>();

	public RtmpConnectionHandler(ClientOptions options, JoinedMeeting meeting) {
		super(options);		
		this.meeting = meeting;
	}
	
	/**
	 * Connect to client shared object.
	 * 
	 * @param name Client shared object name
	 * @param persistent SO persistence flag
	 * @return Client shared object instance
	 */
	public synchronized ClientSharedObject getSharedObject(String name, boolean persistent) {
		log.debug("getSharedObject name: {} persistent {}", new Object[] { name, persistent });
		ClientSharedObject result = sharedObjects.get(name);
		if (result != null) {
			if (result.isPersistentObject() != persistent) {
				throw new RuntimeException("Already connected to a shared object with this name, but with different persistence.");
			}
			return result;
		}

		result = new ClientSharedObject(name, persistent);
		sharedObjects.put(name, result);
		return result;
	}	

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) {
        final Channel channel = me.getChannel();
        final RtmpMessage message = (RtmpMessage) me.getMessage();
        switch(message.getHeader().getMessageType()) {
	        case COMMAND_AMF0:
	        case COMMAND_AMF3:
	            Command command = (Command) message;                
	            String name = command.getName();
	            log.debug("server command: {}", name);
	            if(name.equals("_result")) {
	                String resultFor = transactionToCommandMap.get(command.getTransactionId());
	                log.info("result for method call: {}", resultFor);
	                if(resultFor.equals("connect")) {
	                	so.connect(channel);
	                	return;
	                }
	            }
	            break;
        }
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		
        Amf0Object object = AbstractMessage.object(
                AbstractMessage.pair("app", options.getAppName()),
                AbstractMessage.pair("flashVer", "WIN 9,0,124,2"),
                AbstractMessage.pair("tcUrl", options.getTcUrl()),
                AbstractMessage.pair("fpad", false),
                AbstractMessage.pair("audioCodecs", 1639.0),
                AbstractMessage.pair("videoCodecs", 252.0),
                AbstractMessage.pair("objectEncoding", 0.0),
                AbstractMessage.pair("capabilities", 15.0),
                AbstractMessage.pair("videoFunction", 1.0));

        Command connect = new CommandAmf0("connect", object, meeting.getFullname(), meeting.getRole(), meeting.getConference(), meeting.getMode(), meeting.getRoom(), meeting.getVoicebridge(), meeting.getRecord().equals("true"), meeting.getExternUserID());
		writeCommandExpectingResult(e.getChannel(), connect);
	}
	
}
