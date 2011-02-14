/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb.listeners;

import java.util.HashMap;
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

public class ListenersModule extends Module implements ISharedObjectListener {
	private static final Logger log = LoggerFactory.getLogger(ListenersModule.class);
	private final IClientSharedObject voiceSO;
	
	private Map<Integer, Listener> listeners = new HashMap<Integer, Listener>();

	public ListenersModule(RtmpConnectionHandler handler, Channel channel) {
		super(handler, channel);
		
		voiceSO = handler.getSharedObject("meetMeUsersSO", false);
		voiceSO.addSharedObjectListener(this);
		voiceSO.connect(channel);
	}

	@Override
	public void onSharedObjectClear(ISharedObjectBase so) {
		log.debug("onSharedObjectClear");
		doGetCurrentUsers();
		doGetRoomMuteState();
	}

	public void doGetCurrentUsers() {
    	Command cmd = new CommandAmf0("voice.getMeetMeUsers", null);
    	handler.writeCommandExpectingResult(channel, cmd);
	}

	/*
		<< [STRING _result]
		<< [NUMBER 5.0]
		<< [NULL null]
		<< [NUMBER 1.0]
		<< [BOOLEAN false]
		<< [BOOLEAN false]
		<< [STRING Felipe]
		<< [NUMBER 5.0]
		<< [BOOLEAN false]
		<< [MAP {talking=false, muted=false, name=Felipe, participant=5.0, locked=false}]	
		<< [MAP {5={talking=false, muted=false, name=Felipe, participant=5.0, locked=false}}]
		<< [MAP {count=1.0, participants={5={talking=false, muted=false, name=Felipe, participant=5.0, locked=false}}}]
		<< [1 COMMAND_AMF0 c3 #0 t0 (0) s144] name: _result, transactionId: 5, object: null, args: [{count=1.0, participants={5={talking=false, muted=false, name=Felipe, participant=5.0, locked=false}}}]
		server command: _result
		result for method call: voice.getMeetMeUsers
		
		<< [STRING _result]
		<< [NUMBER 5.0]
		<< [NULL null]
		<< [NUMBER 0.0]
		<< [MAP {count=0.0}]
		<< [1 COMMAND_AMF0 c3 #0 t0 (0) s44] name: _result, transactionId: 5, object: null, args: [{count=0.0}]
		server command: _result
		result for method call: voice.getMeetMeUsers
	 */
	public boolean onGetCurrentUsers(String resultFor, Command command) {
		if (resultFor.equals("voice.getMeetMeUsers")) {
//			\TODO the userId is different from the UsersModule
//			Map<String, Object> currentUsers = (Map<String, Object>) command.getArg(0);
//			int count = ((Double) currentUsers.get("count")).intValue();
//			if (count > 0) {
//				Map<String, Object> participants = (Map<String, Object>) currentUsers.get("participants");
//				
//				for (Map.Entry<String, Object> entry : participants.entrySet()) {
//					int userId = Integer.parseInt(entry.getKey());
//					log.debug("userId=" + userId);
//					Participant p = handler.getUsers().getParticipants().get(userId);
//					
//					Map<String, Object> attributes = (Map<String, Object>) entry.getValue();
//					p.setListener(true);
//					p.setTalking((Boolean) attributes.get("talking"));					
//					p.setMuted((Boolean) attributes.get("muted"));					
//					p.setLocked((Boolean) attributes.get("locked"));
//					log.debug("OK");
//				}
//			}
			return true;
		}
		return false;
	}

	public void doGetRoomMuteState() {
    	Command cmd = new CommandAmf0("voice.isRoomMuted", null);
    	handler.writeCommandExpectingResult(channel, cmd);
	}

	public boolean onGetRoomMuteState(String resultFor, Command command) {
		if (resultFor.equals("voice.isRoomMuted")) {
			return true;
		}
		return false;
	}

	public void doLockMuteUser(int userId, boolean lock) {
    	Command cmd = new CommandAmf0("voice.lockMuteUser", null, Double.valueOf(userId), Boolean.valueOf(lock));
    	handler.writeCommandExpectingResult(channel, cmd);
	}
	
	public void doMuteUnmuteUser(int userId, boolean mute) {
    	Command cmd = new CommandAmf0("voice.muteUnmuteUser", null, Double.valueOf(userId), Boolean.valueOf(mute));
    	handler.writeCommandExpectingResult(channel, cmd);
	}
	
	public void doMuteAllUsers(boolean mute) {
    	Command cmd = new CommandAmf0("voice.muteAllUsers", null, Boolean.valueOf(mute));
    	handler.writeCommandExpectingResult(channel, cmd);
	}
	
	public void doEjectUser(int userId) {
    	Command cmd = new CommandAmf0("voice.kickUSer", null, Double.valueOf(userId));
    	handler.writeCommandExpectingResult(channel, cmd);
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

	public class Listener {
		private int userId;
		private String cidName;
		private String cidNum;
		private boolean muted;
		private boolean talking;
		private boolean locked;
		
		public Listener(List<?> params) {
			cidName = (String) params.get(0);
			cidNum = (String) params.get(1);
			muted = (Boolean) params.get(2);
			talking = (Boolean) params.get(3);
			locked = (Boolean) params.get(4);
		}
		public void setUserId(int userId) {
			this.userId = userId;
		}
		public int getUserId() {
			return userId;
		}
		public String getCidName() {
			return cidName;
		}
		public void setCidName(String cidName) {
			this.cidName = cidName;
		}
		public String getCidNum() {
			return cidNum;
		}
		public void setCidNum(String cidNum) {
			this.cidNum = cidNum;
		}
		public boolean isMuted() {
			return muted;
		}
		public void setMuted(boolean muted) {
			this.muted = muted;
		}
		public boolean isTalking() {
			return talking;
		}
		public void setTalking(boolean talking) {
			this.talking = talking;
		}
		public boolean isLocked() {
			return locked;
		}
		public void setLocked(boolean locked) {
			this.locked = locked;
		}
	}
	
	@Override
	public void onSharedObjectSend(ISharedObjectBase so, String method,
			List<?> params) {
		if (method.equals("userJoin")) {
//			meetMeUsersSO { SOEvent(SERVER_SEND_MESSAGE, userJoin, [5.0, Felipe, Felipe, false, false, false]) }
			Listener user = new Listener(params);
		} else if (method.equals("userTalk")) {
//			meetMeUsersSO { SOEvent(SERVER_SEND_MESSAGE, userTalk, [5.0, true]) }
			Listener user = listeners.get(params.get(0));
			user.setTalking((Boolean) params.get(1));
		} else if (method.equals("userLockedMute")) {
			
		} else if (method.equals("userMute")) {
			
		} else if (method.equals("userLeft")) {
			
		} else if (method.equals("muteStateCallback")) {
			
		}
		log.debug("onSharedObjectSend");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so, String key,
			Object value) {
		log.debug("onSharedObjectUpdate1");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so,
			IAttributeStore values) {
		log.debug("onSharedObjectUpdate2");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so,
			Map<String, Object> values) {
		log.debug("onSharedObjectUpdate3");
	}

}
