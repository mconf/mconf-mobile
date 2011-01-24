package org.mconf.bbb;

import java.util.Collection;
import java.util.List;

import org.mconf.bbb.api.JoinedMeeting;
import org.mconf.bbb.api.Meeting;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.Participant;

public interface IBigBlueButtonClient {

	public boolean load(String filepath);
	public List<Meeting> getMeetings();
	public JoinedMeeting join(Meeting meeting, String name, boolean moderator);
	
	public Collection<Participant> getParticipants();
	public List<ChatMessage> getPublicChatMessages();
	public void sendPublicChatMessage(String message);
	public void sendPrivateChatMessage(String message, int userId);
	
	public void addListener(IBigBlueButtonClientListener listener);
	public void removeListener(IBigBlueButtonClientListener listener);
	
}
