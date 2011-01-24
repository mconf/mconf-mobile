package org.mconf.bbb.users;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.Channel;
import org.mconf.bbb.IBigBlueButtonClientListener;
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

public class UsersModule extends Module implements ISharedObjectListener {
	private static final Logger log = LoggerFactory.getLogger(UsersModule.class);
	
	private final IClientSharedObject participantsSO;
	
	private Map<Integer, Participant> participants = new ConcurrentHashMap<Integer, Participant>();

	public UsersModule(RtmpConnectionHandler handler, Channel channel) {
		super(handler, channel);
		
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
				for (IBigBlueButtonClientListener l : handler.getListeners()) {
					l.onKickUserCallback();
				}
				channel.close();
				return;
			}
			if (method.equals("participantLeft")) {
				Participant p = participants.get(((Double) params.get(0)).intValue());
				for (IBigBlueButtonClientListener l : handler.getListeners()) {
					l.onParticipantLeft(p);
				}
				log.debug("participantLeft: {}", p);
				participants.remove(p.getUserId());
				return;
			}
			if (method.equals("participantJoined")) {
				Participant p = new Participant((Map<String, Object>) params.get(0));
				for (IBigBlueButtonClientListener l : handler.getListeners()) {
					l.onParticipantJoined(p);
				}
				log.debug("participant joined: {}", p);
				participants.put(p.getUserId(), p);
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
				Participant participant = new Participant((Map<String, Object>) entry.getValue());
				log.info("new participant: {}", participant.toString());
				participants.put(participant.getUserId(), participant);			
			}
			return true;
		}
		return false;
	}
	
	public Map<Integer, Participant> getParticipants() {
		return participants;
	}
	
}
