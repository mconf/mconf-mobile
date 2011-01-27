package org.mconf.bbb;

import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.IParticipant;

public interface IBigBlueButtonClientListener {

	public void onPublicChatMessage(ChatMessage message, IParticipant source);
	public void onPrivateChatMessage(ChatMessage message, IParticipant source);
	public void onConnected();
	public void onDisconnected();
	public void onKickUserCallback();
	public void onParticipantLeft(IParticipant p);
	public void onParticipantJoined(IParticipant p);
	
}
