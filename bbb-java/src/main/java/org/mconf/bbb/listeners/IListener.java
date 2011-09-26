package org.mconf.bbb.listeners;

public interface IListener {

	public abstract void setUserId(int userId);

	public abstract int getUserId();

	public abstract String getCidName();

	public abstract void setCidName(String cidName);

	public abstract String getCidNum();

	public abstract void setCidNum(String cidNum);

	public abstract boolean isMuted();

	public abstract void setMuted(boolean muted);

	public abstract boolean isTalking();

	public abstract void setTalking(boolean talking);

	public abstract boolean isLocked();

	public abstract void setLocked(boolean locked);

}