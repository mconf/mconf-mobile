package org.mconf.web;

public class Room {
	private String name, 
			path;
	private Owner owner;

	public Room() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Name: " + name + ", " + owner.toString() + ", path: " + path;
	}
}
