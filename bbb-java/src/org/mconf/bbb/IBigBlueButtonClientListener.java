package org.mconf.bbb;

import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.Participant;

public interface IBigBlueButtonClientListener {

	public void onPublicChatMessage(ChatMessage message, Participant source);
	public void onPrivateChatMessage(ChatMessage message, Participant source);
	public void onConnected();
	public void onDisconnected();
	public void onKickUserCallback();
	public void onParticipantLeft(Participant p);
	public void onParticipantJoined(Participant p);
	
}
