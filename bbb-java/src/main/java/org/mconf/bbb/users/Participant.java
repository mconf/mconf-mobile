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

import java.util.Map;

import org.mconf.bbb.api.JoinServiceBase;

public class Participant implements IParticipant {

	private Status status;
	private String name;
	private int userid;
	private String role;
	
	// listener stuff
	private boolean listener;
	private boolean muted;
	private boolean locked;
	private boolean talking;
	
	public Participant() {
		
	}
	
	public Participant(Map<String, Object> param, Class<? extends JoinServiceBase> joinServiceClass) {
		decode(param, joinServiceClass);
	}
	
	/*
	 * example:
	 * {status={raiseHand=false, hasStream=false, presenter=false}, name=Eclipse, userid=112.0, role=VIEWER}
	 */
	@SuppressWarnings("unchecked")
	public void decode(Map<String, Object> param, Class<? extends JoinServiceBase> joinServiceClass) {
		status = new Status((Map<String, Object>) param.get("status"), joinServiceClass);
		name = (String) param.get("name");
		userid = ((Double) param.get("userid")).intValue();
		role = (String) param.get("role");
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.users.IParticipant#getStatus()
	 */
	@Override
	public Status getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.users.IParticipant#setStatus(org.mconf.bbb.users.Status)
	 */
	@Override
	public void setStatus(Status status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.users.IParticipant#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.users.IParticipant#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.users.IParticipant#getUserId()
	 */
	@Override
	public int getUserId() {
		return userid;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.users.IParticipant#setUserId(int)
	 */
	@Override
	public void setUserId(int userid) {
		this.userid = userid;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.users.IParticipant#getRole()
	 */
	@Override
	public String getRole() {
		return role;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.users.IParticipant#setRole(java.lang.String)
	 */
	@Override
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Participant [userid=" + userid 
				+ ", name=" + name 
				+ ", role=" + role 
				+ ", status=" + status + "]";
	}

	public boolean isModerator() {
		return role.equals("MODERATOR");
	}
	
	public void setModerator(boolean moderator) {
		role = moderator? "MODERATOR" : "VIEWER";
	}
	
	public boolean isPresenter() {
		return status.isPresenter();
	}
	
	public void setPresenter(boolean presenter) {
		status.setPresenter(presenter);
	}
	
	
	public boolean hasStream() {
		return status.isHasStream();
	}
	
	public void setHasStream(boolean hasStream) {
		status.setHasStream(hasStream);
	}
	
	public boolean isRaiseHand() {
		return status.isRaiseHand();
	}
	
	public void setRaiseHand(boolean raiseHand) {
		status.setRaiseHand(raiseHand);
	}

	public boolean isListener() {
		return listener;
	}

	public void setListener(boolean listener) {
		this.listener = listener;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isTalking() {
		return talking;
	}

	public void setTalking(boolean talking) {
		this.talking = talking;
	}	
}
