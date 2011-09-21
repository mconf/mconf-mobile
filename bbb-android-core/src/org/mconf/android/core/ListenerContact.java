package org.mconf.android.core;

import org.mconf.bbb.listeners.IListener;
import org.mconf.bbb.listeners.Listener;

public class ListenerContact extends Listener{

//class to put listeners on the listeners list	
	public ListenerContact (IListener listener)
	{
		this.setCidName(listener.getCidName());
		this.setCidNum(listener.getCidNum());
		this.setLocked(listener.isLocked());
		this.setMuted(listener.isMuted());
		this.setTalking(listener.isTalking());
		this.setUserId(listener.getUserId());
	}

	public String getListenerName()
	{
		return this.getCidName();
	}
}
