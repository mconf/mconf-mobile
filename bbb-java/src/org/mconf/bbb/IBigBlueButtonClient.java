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
