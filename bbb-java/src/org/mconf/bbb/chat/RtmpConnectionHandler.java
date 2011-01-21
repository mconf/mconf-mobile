package org.mconf.bbb.chat;

import java.util.Map;

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
import com.flazr.rtmp.message.AbstractMessage;
import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.CommandAmf0;

/*
 * getMyUserId
 * participantsSO
 * participants.getParticipants
 * meetMeUsersSO
 * voice.getMeetMeUsers
 * voice.isRoomMuted
 * presentationSO
 * presentation.getPresentationInfo
 * presentation.assignPresenter
 * breakoutSO
 * drawSO
 * deskSO
 * deskshare.checkIfStreamIsPublishing
 * chat.getChatMessages
 */

/*
 * PRIVATE MESSAGE
 * 2011-01-20 16:52:53,741 [New I/O client worker #1-1] DEBUG com.flazr.amf.Amf0Value.decode(Amf0Value.java:173) - << [STRING messageReceived]
 * 2011-01-20 16:52:53,741 [New I/O client worker #1-1] DEBUG com.flazr.amf.Amf0Value.decode(Amf0Value.java:173) - << [STRING 69]
 * 2011-01-20 16:52:53,742 [New I/O client worker #1-1] DEBUG com.flazr.amf.Amf0Value.decode(Amf0Value.java:173) - << [STRING oi|Felipe|0|16:52|en|69]
 * 2011-01-20 16:52:53,742 [New I/O client worker #1-1] DEBUG com.flazr.rtmp.RtmpDecoder.decode(RtmpDecoder.java:82) - << [1 SHARED_OBJECT_AMF0 c3 #0 t0 (0) s71] SharedObject name: 101 version: 1 non persistent { SOEvent(SERVER_SEND_MESSAGE, messageReceived, [69, oi|Felipe|0|16:52|en|69]) }
 * 
 * PUBLIC MESSAGE
 * 2011-01-20 16:52:34,672 [New I/O client worker #1-1] DEBUG com.flazr.amf.Amf0Value.decode(Amf0Value.java:173) - << [STRING newChatMessage]
 * 2011-01-20 16:52:34,672 [New I/O client worker #1-1] DEBUG com.flazr.amf.Amf0Value.decode(Amf0Value.java:173) - << [STRING teste|Felipe|0|16:52|en|69]
 * 2011-01-20 16:52:34,673 [New I/O client worker #1-1] DEBUG com.flazr.rtmp.RtmpDecoder.decode(RtmpDecoder.java:82) - << [1 SHARED_OBJECT_AMF0 c3 #0 t0 (0) s71] SharedObject name: chatSO version: 1 non persistent { SOEvent(SERVER_SEND_MESSAGE, newChatMessage, [teste|Felipe|0|16:52|en|69]) }

 */
public class RtmpConnectionHandler extends ClientHandler {

    private static final Logger log = LoggerFactory.getLogger(RtmpConnectionHandler.class);
    private JoinedMeeting meeting;
    
	private String myUserId;

	public RtmpConnectionHandler(ClientOptions options, JoinedMeeting meeting) {
		super(options);		
		this.meeting = meeting;
	}
	
    public Command connect() {
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
        return connect;
    }
    
    @SuppressWarnings("unchecked")
	public String connectGetCode(Command command) {
    	return ((Map<String, Object>) command.getArg(0)).get("code").toString();
    }
	
	public Command getMyUserId() {
    	Command command = new CommandAmf0("getMyUserId", null);
    	return command;
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
	                	String code = connectGetCode(command);
	                	if (code.equals("NetConnection.Connect.Success"))
	                		writeCommandExpectingResult(channel, getMyUserId());
	                	else {
	                		log.error("method connect result in {}, quitting", code);
	                		channel.close();
	                	}
	                	getSharedObject("participantsSO", false).connect(channel);
	                	getSharedObject("chatSO", false).connect(channel);
	                	return;
	                } else if(resultFor.equals("getMyUserId")) {
	                	myUserId = (String) command.getArg(0);
	                	log.info("My userID is {}", myUserId);
	                	// the shared object responsible to handle private chat 
	                	getSharedObject(myUserId, false).connect(channel);
	                }
	            }
	            break;
        }
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		writeCommandExpectingResult(e.getChannel(), connect());
	}
	
}
