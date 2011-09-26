package org.mconf.web;

public class User extends Owner {

	@Override
	public String getType() {
		return Owner.TYPE_USER;
	}

}
