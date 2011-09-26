package org.mconf.web;

import org.junit.Test;

import junit.framework.TestCase;

public class RoomTest extends TestCase {
	private static final String name = "NAME";
	private static final String path = "PATH";
	
	private Room room = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		room = new Room();
		room.setName(name);
		room.setPath(path);
	}
	
	@Test
	public void testGetters() {
		assertEquals(name, room.getName());
		assertEquals(path, room.getPath());
	}
}
