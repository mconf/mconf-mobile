package org.mconf.web;

public class Room {
	public static final String TYPE_NONE = "TYPE_NONE";
	public static final String TYPE_USER = "TYPE_USER";
	public static final String TYPE_USER_SPACE = "TYPE_USER_SPACE";
	public static final String TYPE_PUBLIC_SPACE = "TYPE_PUBLIC_SPACE";
	
	private String name, 
			path,
			type = TYPE_NONE;

	public Room(String name, String path, String type) {
		this.name = name;
		this.path = path;
		this.type = type; 
	}
	
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return name + " -> " + path;
	}
}
