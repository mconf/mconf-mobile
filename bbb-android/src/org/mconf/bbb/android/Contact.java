package org.mconf.bbb.android;

import org.mconf.bbb.users.Participant;

public class Contact extends Participant {

	public 
	String getContactName()
	{
		return this.getName();
	}
	
	boolean isModerator()
	{
		return this.getRole().equals("MODERATOR");
	}
	
	boolean isPresenter()
	{
		return this.getStatus().isPresenter();
	}
	
	boolean hasStream()
	{
		return this.getStatus().isHasStream();
	}
	
	boolean isRaiseHand()
	{
		return this.getStatus().isRaiseHand();
	}
}
