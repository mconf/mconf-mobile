package org.mconf.bbb.listeners;

import java.util.List;
import java.util.Map;

public class Listener {
	private int userId;
	private String cidName;
	private String cidNum;
	private boolean muted;
	private boolean talking;
	private boolean locked;
	
	public Listener(List<?> params) {
		userId = ((Double) params.get(0)).intValue();
		cidName = (String) params.get(1);
		cidNum = (String) params.get(2);
		muted = (Boolean) params.get(3);
		talking = (Boolean) params.get(4);
		locked = (Boolean) params.get(5);
	}
	public Listener(Map<String, Object> attributes) {
		userId = ((Double) attributes.get("participant")).intValue();
		cidName = (String) attributes.get("name");
		talking = (Boolean) attributes.get("talking");					
		muted = (Boolean) attributes.get("muted");					
		locked = (Boolean) attributes.get("locked");
	}
	
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}
	public String getCidName() {
		return cidName;
	}
	public void setCidName(String cidName) {
		this.cidName = cidName;
	}
	public String getCidNum() {
		return cidNum;
	}
	public void setCidNum(String cidNum) {
		this.cidNum = cidNum;
	}
	public boolean isMuted() {
		return muted;
	}
	public void setMuted(boolean muted) {
		this.muted = muted;
	}
	public boolean isTalking() {
		return talking;
	}
	public void setTalking(boolean talking) {
		this.talking = talking;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	@Override
	public String toString() {
		return "Listener [cidName=" + cidName + ", cidNum=" + cidNum
				+ ", locked=" + locked + ", muted=" + muted + ", talking="
				+ talking + ", userId=" + userId + "]";
	}
}
