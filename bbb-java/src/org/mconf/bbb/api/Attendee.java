package org.mconf.bbb.api;

public class Attendee {
	private String userID,
		fullName,
		role;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "Attendee [fullName=" + fullName + ", role=" + role
				+ ", userID=" + userID + "]";
	}
}
