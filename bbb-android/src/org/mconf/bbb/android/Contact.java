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

package org.mconf.bbb.android;

import org.mconf.bbb.users.IParticipant;
import org.mconf.bbb.users.Participant;
//class to put contacts on the contacts list
public class Contact extends Participant {
	
	public static final int CONTACT_NORMAL = 0;
	public static final int CONTACT_ON_PUBLIC_MESSAGE = 1;
	public static final int CONTACT_ON_PRIVATE_MESSAGE = 2;
	
	private int chatStatus = CONTACT_NORMAL;

	public Contact(IParticipant partic) {
		this.setName(partic.getName());
		this.setStatus(partic.getStatus().clone());
		this.setRole(partic.getRole());
		this.setUserId(partic.getUserId());
	}

	public void setChatStatus(int chatStatus) {
		this.chatStatus = chatStatus;
	}

	public int getChatStatus() {
		return chatStatus;
	}

}
