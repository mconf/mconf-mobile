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
import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.MainRtmpConnection;
import org.mconf.bbb.Module;
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
	private boolean roomMuted;

	public ListenersModule(MainRtmpConnection handler, Channel channel) {
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
	@SuppressWarnings("unchecked")
	public boolean onGetCurrentUsers(String resultFor, Command command) {
		if (resultFor.equals("voice.getMeetMeUsers")) {
			
			
			listeners.clear();
			// the userId is different from the UsersModule
			Map<String, Object> currentUsers = (Map<String, Object>) command.getArg(0);
			int count = ((Double) currentUsers.get("count")).intValue();
			if (count > 0) {
				Map<String, Object> participants = (Map<String, Object>) currentUsers.get("participants");
				
				for (Map.Entry<String, Object> entry : participants.entrySet()) {
					int userId = Integer.parseInt(entry.getKey());
					log.debug("userId=" + userId);
					
					Listener listener = new Listener((Map<String, Object>) entry.getValue());
					log.debug("new listener: " + listener.toString());
					listeners.put(userId, listener);
					onListenerJoined(listener);
				}
			}
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
			setRoomMuted((Boolean) command.getArg(0));
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
    	doGetRoomMuteState();
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


	@Override
	public void onSharedObjectSend(ISharedObjectBase so, String method,
			List<?> params) {
		if (method.equals("userJoin")) {
			//	meetMeUsersSO { SOEvent(SERVER_SEND_MESSAGE, userJoin, [5.0, Felipe, Felipe, false, false, false]) }
			Listener listener = new Listener(params);
			if (!listeners.containsKey(listener.getUserId())) {
				listeners.put(listener.getUserId(), listener);
				onListenerJoined(listener);
			} else {
				log.error("This listener is already in the list");
			}
		} else if (method.equals("userTalk")) {
			//	meetMeUsersSO { SOEvent(SERVER_SEND_MESSAGE, userTalk, [5.0, true]) }
			int userId = ((Double) params.get(0)).intValue();
			IListener listener = listeners.get(userId);
			
			if (listener != null) {
				listener.setTalking((Boolean) params.get(1));
				for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
					l.onListenerStatusChangeIsTalking(listener);
				}
			} else
				log.error("Can't find the listener (userTalk)");
		} else if (method.equals("userLockedMute")) {
			// meetMeUsersSO { SOEvent(SERVER_SEND_MESSAGE, userLockedMute, [4.0, true]) }
			int userId = ((Double) params.get(0)).intValue();
			IListener listener = listeners.get(userId);
			if (listener != null)
				listener.setLocked((Boolean) params.get(1));
			else
				log.error("Can't find the listener (userLockedMute)");
		} else if (method.equals("userMute")) {
			// meetMeUsersSO { SOEvent(SERVER_SEND_MESSAGE, userMute, [4.0, true]) }
			int userId = ((Double) params.get(0)).intValue();
			IListener listener = listeners.get(userId);
			if (listener != null) {
				listener.setMuted((Boolean) params.get(1));
				for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
					l.onListenerStatusChangeIsMuted(listener);
				}
			} else
				log.error("Can't find the listener (userMute)");			
		} else if (method.equals("userLeft")) {
			// meetMeUsersSO { SOEvent(SERVER_SEND_MESSAGE, userLeft, [2.0]) }
			int userId = ((Double) params.get(0)).intValue();
			IListener listener = listeners.get(userId);
			if (listener != null) {
				for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
					l.onListenerLeft(listener);
				}
				listeners.remove(userId);
			} else
				log.error("Can't find the listener (userLeft)");			
		} else if (method.equals("muteStateCallback")) {
			// meetMeUsersSO { SOEvent(SERVER_SEND_MESSAGE, muteStateCallback, [false]) }
			setRoomMuted((Boolean) params.get(0));
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

	@Override
	public boolean onCommand(String resultFor, Command command) {
		if (onGetCurrentUsers(resultFor, command)
				|| onGetRoomMuteState(resultFor, command)) {
			return true;
		} else
			return false;
	}

	public void setRoomMuted(boolean roomMuted) {
		this.roomMuted = roomMuted;
	}

	public boolean isRoomMuted() {
		return roomMuted;
	}

	public Map<Integer, Listener> getListeners() {
		return listeners;
	}
	
	public void onListenerJoined(Listener p) {
		for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
			l.onListenerJoined(p);
		}				
		log.info("new participant: {}", p.toString());
		listeners.put(p.getUserId(), p);			
	}
	
	public void onListenerStatusChanged (Listener p, String key, Object value)
	{
		
	}

	

}
