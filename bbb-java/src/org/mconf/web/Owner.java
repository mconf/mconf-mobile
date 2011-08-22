package org.mconf.web;

public abstract class Owner {
	public static final String TYPE_USER = "User";
	public static final String TYPE_SPACE = "Space";
	
	protected int id;
	
	public abstract String getType();
	
	public String toString() {
		return getType() + " room, id: " + id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
