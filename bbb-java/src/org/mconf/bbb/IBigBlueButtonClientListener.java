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
	
	public void onParticipantStatusChangePresenter(IParticipant p);
	public void onParticipantStatusChangeHasStream(IParticipant p);
	public void onParticipantStatusChangeRaiseHand(IParticipant p);
	
}
