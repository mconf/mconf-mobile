package org.mconf.bbb.users;

import java.util.Map;

public class Participant implements IParticipant {

	private Status status;
	private String name;
	private int userid;
	private String role;

	public Participant() {
		
	}
	
	public Participant(Map<String, Object> param) {
		decode(param);
	}
	
	/*
	 * example:
	 * {status={raiseHand=false, hasStream=false, presenter=false}, name=Eclipse, userid=112.0, role=VIEWER}
	 */
	@SuppressWarnings("unchecked")
	public void decode(Map<String, Object> param) {
		status = new Status((Map<String, Boolean>) param.get("status"));
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
		return "Participant [name=" + name + ", role=" + role + ", status="
				+ status + ", userid=" + userid + "]";
	}

}
