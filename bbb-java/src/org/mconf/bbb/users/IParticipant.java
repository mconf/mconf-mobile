package org.mconf.bbb.users;

public interface IParticipant {

	public abstract Status getStatus();

	public abstract void setStatus(Status status);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract int getUserId();

	public abstract void setUserId(int userid);

	public abstract String getRole();

	public abstract void setRole(String role);

}