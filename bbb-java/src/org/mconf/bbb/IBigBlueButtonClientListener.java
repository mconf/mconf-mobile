package org.mconf.bbb;

import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.IParticipant;
import org.mconf.bbb.users.Participant;

public interface IBigBlueButtonClientListener {

	public void onPublicChatMessage(ChatMessage message, IParticipant source);
	public void onPrivateChatMessage(ChatMessage message, IParticipant source);
	public void onConnected();
	public void onDisconnected();
	public void onKickUserCallback();
	public void onParticipantLeft(IParticipant p);
	public void onParticipantJoined(IParticipant p);
	
	public void onParticipantStatusChangePresenter(IParticipant p);
	public void onParticipantStatusChangeHasStream(IParticipant p);
	public void onParticipantStatusChangeRaiseHand(IParticipant p);
	
}
