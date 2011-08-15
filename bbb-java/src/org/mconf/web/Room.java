package org.mconf.web;

public class Room {
	private String name, path;

	public Room(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		return name + " -> " + path;
	}
}
