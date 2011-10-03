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

package org.mconf.bbb.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.MainRtmpConnection;
import org.mconf.bbb.Module;
import org.mconf.bbb.api.JoinService0Dot7;
import org.mconf.bbb.api.JoinService0Dot8;
import org.mconf.bbb.api.JoinServiceBase;
import org.red5.server.api.IAttributeStore;
import org.red5.server.api.so.IClientSharedObject;
import org.red5.server.api.so.ISharedObjectBase;
import org.red5.server.api.so.ISharedObjectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.message.Command;
import com.flazr.rtmp.message.CommandAmf0;

public class UsersModule extends Module implements ISharedObjectListener {
	private static final Logger log = LoggerFactory.getLogger(UsersModule.class);

	private final IClientSharedObject participantsSO;

	private Map<Integer, Participant> participants = new ConcurrentHashMap<Integer, Participant>();
	private int moderatorCount = 0, participantCount = 0;
	private Class<? extends JoinServiceBase> joinServiceVersion;

	public UsersModule(MainRtmpConnection handler, Channel channel) {
		super(handler, channel);
		
		joinServiceVersion = handler.getContext().getJoinService().getClass();
		
		participantsSO = handler.getSharedObject("participantsSO", false);
		participantsSO.addSharedObjectListener(this);
		participantsSO.connect(channel);
	}

	@Override
	public void onSharedObjectClear(ISharedObjectBase so) {
		log.debug("onSharedObjectClear");
		doQueryParticipants();
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

	@SuppressWarnings("unchecked")
	@Override
	public void onSharedObjectSend(ISharedObjectBase so, 
			String method, List<?> params) {
		log.debug("onSharedObjectSend");

		if (so.equals(participantsSO)) {
			if (method.equals("kickUserCallback")) {
				int userId = ((Double) params.get(0)).intValue();
				if (userId == handler.getContext().getMyUserId()) {
					// \TODO the kickUserCallback should be handled with the userId as parameter
					for (IBigBlueButtonClientListener l : handler.getContext().getListeners())
						l.onKickUserCallback();
					channel.close();
				}
				return;
			}
			if (method.equals("participantLeft")) {
				IParticipant p = participants.get(((Double) params.get(0)).intValue());
				for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
					l.onParticipantLeft(p);
				}

				if(p.getRole().equals("MODERATOR"))
					moderatorCount--;
				else
					participantCount--;

				log.debug("participantLeft: {}", p);
				participants.remove(p.getUserId());

				return;
			}
			if (method.equals("participantJoined")) {
				Participant p = new Participant((Map<String, Object>) params.get(0), joinServiceVersion);
				onParticipantJoined(p);
				return;
			}
			if (method.equals("participantStatusChange")) {
				Participant p = participants.get(((Double) params.get(0)).intValue());
				onParticipantStatusChange(p, (String) params.get(1), params.get(2));
				return;
			}
		}
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so, String key,
			Object value) {
		log.debug("onSharedObjectUpdate 1");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so,
			IAttributeStore values) {
		log.debug("onSharedObjectUpdate 2");
	}

	@Override
	public void onSharedObjectUpdate(ISharedObjectBase so,
			Map<String, Object> values) {
		log.debug("onSharedObjectUpdate 3");
	}

	/**
	 * {@link} https://github.com/bigbluebutton/bigbluebutton/blob/master/bigbluebutton-client/src/org/bigbluebutton/modules/chat/services/PrivateChatSharedObjectService.as#L142
	 */
	public void doQueryParticipants() {
		Command cmd = new CommandAmf0("participants.getParticipants", null);
		handler.writeCommandExpectingResult(channel, cmd);
	}

	/**
	 * example:
	 * [MAP {count=2.0, participants={112={status={raiseHand=false, hasStream=false, presenter=false}, name=Eclipse, userid=112.0, role=VIEWER}, 97={status={raiseHand=false, hasStream=false, presenter=true}, name=Felipe, userid=97.0, role=MODERATOR}}}]
	 * [1 COMMAND_AMF0 c3 #0 t0 (0) s299] name: _result, transactionId: 4, object: null, args: [{count=2.0, participants={112={status={raiseHand=false, hasStream=false, presenter=false}, name=Eclipse, userid=112.0, role=VIEWER}, 97={status={raiseHand=false, hasStream=false, presenter=true}, name=Felipe, userid=97.0, role=MODERATOR}}}]
	 */
	@SuppressWarnings("unchecked")
	public boolean onQueryParticipants(String resultFor, Command command) {
		if (resultFor.equals("participants.getParticipants")) {
			Map<String, Object> args = (Map<String, Object>) command.getArg(0);

			participants.clear();

			@SuppressWarnings("unused")
			int count = ((Double) args.get("count")).intValue();

			Map<String, Object> participantsMap = (Map<String, Object>) args.get("participants");

			for (Map.Entry<String, Object> entry : participantsMap.entrySet()) {
				Participant p = new Participant((Map<String, Object>) entry.getValue(), joinServiceVersion);
				onParticipantJoined(p);
			}
			return true;
		}
		return false;
	}

	public Map<Integer, Participant> getParticipants() {
		return participants;
	}

	public void onParticipantJoined(Participant p) {
		for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
			l.onParticipantJoined(p);
		}				
		log.info("new participant: {}", p.toString());
		participants.put(p.getUserId(), p);	
		if(p.isModerator())
			moderatorCount++;
		else
			participantCount++;
	}

	private void onParticipantStatusChange(Participant p, String key,
			Object value) {
		log.debug("participantStatusChange: " + p.getName() + " status: " + key + " value: " + value.toString());
		if (key.equals("presenter")) {
			p.getStatus().setPresenter((Boolean) value);
			for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
				l.onParticipantStatusChangePresenter(p);
			}
		} else if (key.equals("hasStream")) {
			p.getStatus().setHasStream(value);
			for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
				l.onParticipantStatusChangeHasStream(p);
			}
		} else if (key.equals("streamName")) {
			p.getStatus().setStreamName((String) value);
		} else if (key.equals("raiseHand")) {
			p.getStatus().setRaiseHand((Boolean) value);
			for (IBigBlueButtonClientListener l : handler.getContext().getListeners()) {
				l.onParticipantStatusChangeRaiseHand(p);
			}
		}
	}

	public void raiseHand(int userId, boolean value) {
		Command cmd = new CommandAmf0("participants.setParticipantStatus", null, userId, "raiseHand", value);
		handler.writeCommandExpectingResult(channel, cmd);
	}

	/*
	 * \TODO should be moved to the presentation module in the near future
	 */
	public void assignPresenter(int userId) {
		// as it's implemented on bigbluebutton-client/src/org/bigbluebutton/modules/present/business/PresentSOService.as:353
		Participant p = participants.get(userId);
		if (p == null) {
			log.debug("Inconsistent state here");
			return;
		}
		Command cmd = new CommandAmf0("presentation.assignPresenter", null, userId, p.getName(), 1);
		handler.writeCommandExpectingResult(channel, cmd);
	}

	public void addStream(String streamName) {
		if (joinServiceVersion == JoinService0Dot7.class) {
	    	Command cmd = new CommandAmf0("participants.setParticipantStatus", null, handler.getContext().getMyUserId(), "streamName", streamName);
	    	handler.writeCommandExpectingResult(channel, cmd);
	    	
	    	cmd = new CommandAmf0("participants.setParticipantStatus", null, handler.getContext().getMyUserId(), "hasStream", true);
	    	handler.writeCommandExpectingResult(channel, cmd);
		} else if (joinServiceVersion == JoinService0Dot8.class) {
	    	Command cmd = new CommandAmf0("participants.setParticipantStatus", null, handler.getContext().getMyUserId(), "hasStream", "true,stream=" + streamName);
	    	handler.writeCommandExpectingResult(channel, cmd);
		}
	}

	public void removeStream(String streamName) {
		if (joinServiceVersion == JoinService0Dot7.class) {
			Command cmd = new CommandAmf0("participants.setParticipantStatus", null, handler.getContext().getMyUserId(), "");
			handler.writeCommandExpectingResult(channel, cmd);
	
			cmd = new CommandAmf0("participants.setParticipantStatus", null, handler.getContext().getMyUserId(), "hasStream", false);
			handler.writeCommandExpectingResult(channel, cmd);
		} else if (joinServiceVersion == JoinService0Dot8.class) {
	    	Command cmd = new CommandAmf0("participants.setParticipantStatus", null, handler.getContext().getMyUserId(), "hasStream", "false,stream=" + streamName);
	    	handler.writeCommandExpectingResult(channel, cmd);
		}
	}

	public void kickUser(int userId) {
		if (handler.getContext().getUsersModule().getParticipants().get(handler.getContext().getMyUserId()).isModerator()) {
			List<Object> list = new ArrayList<Object>();
			list.add(userId);
			participantsSO.sendMessage("kickUserCallback", list);
		}
	}

	@Override
	public boolean onCommand(String resultFor, Command command) {
		if (onQueryParticipants(resultFor, command)) {
			handler.getContext().createChatModule(handler, channel);
			handler.getContext().createListenersModule(handler, channel);
			return true;
		} else
			return false;
	}

	public int getModeratorCount() {
		return moderatorCount;
	}

	public int getParticipantCount() {
		return participantCount;
	}

}
