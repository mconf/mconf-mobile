package org.mconf.bbb.android;

import org.mconf.bbb.users.IParticipant;
import org.mconf.bbb.users.Participant;

import android.content.Intent;

public class Contact extends Participant {

	private Intent intent = null;
	
	public Contact(IParticipant partic) {
		this.setName(partic.getName());
		this.setStatus(partic.getStatus());
		this.setRole(partic.getRole());
		this.setUserId(partic.getUserId());
	}
	
	public Intent getIntent() {
		return intent;
	}
	
	public void setIntent(Intent intent) {
		this.intent = intent;
	}

}
