package org.mconf.bbb.android;

import org.mconf.bbb.users.IParticipant;
import org.mconf.bbb.users.Participant;

public class Contact extends Participant {

	public 
	Contact(IParticipant partic)
	{
		this.setName(partic.getName());
		this.setStatus(partic.getStatus());
		this.setRole(partic.getRole());
		this.setUserId(partic.getUserId());

	}
	String getContactName()
	{
		
		return this.getName();
	}
	
	void setContactName(String _name)
	{
		this.setName(_name);
	}
	
	boolean isModerator()
	{
		
		return this.getRole().equals("MODERATOR");
	}
	
	void setModerator(boolean _mod)
	{
		this.setRole(_mod? "MODERATOR" : "VIEWER");
	}
	
	boolean isPresenter()
	{
		return this.getStatus().isPresenter();
	}
	
	void setIsPresenter(boolean pre)
	{
		this.getStatus().setPresenter(pre);
	}
	
	
	boolean hasStream()
	{
		return this.getStatus().isHasStream();
	}
	
	void setHasStream(boolean str)
	{
		this.getStatus().setHasStream(str);
	}
	
	boolean isRaiseHand()
	{
		return this.getStatus().isRaiseHand();
	}
	
	void setRaiseHand( boolean raise)
	{
		this.getStatus().setRaiseHand(raise);
	}
}
