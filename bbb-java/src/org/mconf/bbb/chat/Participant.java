package org.mconf.bbb.chat;

import java.util.Map;

public class Participant {

	private Status status;
	private String name;
	private int userid;
	private String role;

	
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUserId() {
		return userid;
	}

	public void setUserId(int userid) {
		this.userid = userid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Participant [name=" + name + ", role=" + role + ", status="
				+ status + ", userid=" + userid + "]";
	}

}
