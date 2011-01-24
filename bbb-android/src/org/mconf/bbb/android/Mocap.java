
package org.mconf.bbb.android;

import android.util.Log;
public class Mocap {
	
	
	String[] contacts;
	int numberOfContacts;//pegar o número certo de contatos na sala
	
	Mocap()
	{
		setNumberContacts();
		setContacts();
		
	}

	private void setNumberContacts()
	{
		this.numberOfContacts=3;//pegar o número certo de contatos na sala
	}
	
	public String[] getContacts()
	
	{
		
		return this.contacts;
		
	}
	
	private void setContacts()
	{
		 
		
		contacts=new String[this.numberOfContacts];
		this.contacts[0]="Alessandra";
		this.contacts[1]="Felipe";
		this.contacts[2]="Daronco";
		
	}
}
