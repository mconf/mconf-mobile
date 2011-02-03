/*
 * This file is part of MConf-Mobile.
 *
 * MConf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MConf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MConf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.mconf.bbb.api.JoinedMeeting;
import org.mconf.bbb.chat.ChatModule;
import org.mconf.bbb.users.UsersModule;
import org.red5.server.so.SharedObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.amf.Amf0Object;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.client.ClientHandler;
import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.message.AbstractMessage;
import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.CommandAmf0;
import com.flazr.rtmp.message.Control;

/*
 * - what happens when a client join a session
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
 * 
 * - web client module division
 * breakout  
 * chat  
 * deskshare  
 * example  
 * listeners  
 * phone  
 * present  
 * videoconf  
 * viewers  
 * whiteboard
 */

public class RtmpConnectionHandler extends ClientHandler {

    private static final Logger log = LoggerFactory.getLogger(RtmpConnectionHandler.class);
    
    private JoinedMeeting meeting;
    
	private int myUserId;
	
	private ChatModule chat;
	private UsersModule users;

	Set<IBigBlueButtonClientListener> listeners = new LinkedHashSet<IBigBlueButtonClientListener>();
	
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
	
    public void doGetMyUserId(Channel channel) {
    	Command command = new CommandAmf0("getMyUserId", null);
    	writeCommandExpectingResult(channel, command);
    }
    
    public boolean onGetMyUserId(String resultFor, Command command) {
    	if (resultFor.equals("getMyUserId")) {
	    	myUserId = Integer.parseInt((String) command.getArg(0));
	    	log.info("My userID is {}", myUserId);
	    	return true;
    	} else
    		return false;
    }
    
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent me) {
        final Channel channel = me.getChannel();
        final RtmpMessage message = (RtmpMessage) me.getMessage();
        switch(message.getHeader().getMessageType()) {
        	case CONTROL:
                Control control = (Control) message;
                switch(control.getType()) {
                    case PING_REQUEST:
                        final int time = control.getTime();
                        Control pong = Control.pingResponse(time);
                        channel.write(pong);
                        break;
                }
        		break;
        	
	        case COMMAND_AMF0:
	        case COMMAND_AMF3:
	            Command command = (Command) message;                
	            String name = command.getName();
	            log.debug("server command: {}", name);
	            if(name.equals("_result")) {
	                String resultFor = transactionToCommandMap.get(command.getTransactionId());
	                if (resultFor == null) {
	                	log.warn("result for method without tracked transaction");
	                	break;
	                }
	                log.info("result for method call: {}", resultFor);
	                if(resultFor.equals("connect")) {
	                	String code = connectGetCode(command);
	                	if (code.equals("NetConnection.Connect.Success"))
	                		doGetMyUserId(channel);
	                	else {
	                		log.error("method connect result in {}, quitting", code);
	                		channel.close();
	                	}
	                	return;
	                } else if(onGetMyUserId(resultFor, command)) {
	                	users = new UsersModule(this, channel);
	                	break;
	                } else if (users.onQueryParticipants(resultFor, command)) {
                		chat = new ChatModule(this, channel);
	                	break;
	                } else if (chat.onGetChatMessages(resultFor, command)) {
	                	break;
	                }
	            }
	            break;
	            
	        case SHARED_OBJECT_AMF0:
	        case SHARED_OBJECT_AMF3:
	        	onSharedObject(channel, (SharedObjectMessage) message);
	        	break;
    		default:
    			log.info("ignoring rtmp message: {}", message);
	        	break;
        }
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		writeCommandExpectingResult(e.getChannel(), connect());
	}
	
	public int getMyUserId() {
		return myUserId;
	}

	public JoinedMeeting getJoinedMeeting() {
		return meeting;
	}

	public ChatModule getChat() {
		return chat;
	}
	
	public UsersModule getUsers() {
		return users;
	}
	
	public void addListener(IBigBlueButtonClientListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IBigBlueButtonClientListener listener) {
		listeners.remove(listener);
	}

	public Set<IBigBlueButtonClientListener> getListeners() {
		return listeners;
	}
}
