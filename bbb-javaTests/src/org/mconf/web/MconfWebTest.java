package org.mconf.web;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class MconfWebTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void testParseRooms() {
		List<Room> list = new ArrayList<Room>();
		MconfWebServiceItf ws = new MconfWebServiceStub();
		
		boolean exception = false;
		try {
			MconfWebImpl.parseRooms(list, ws.getRooms(null));
		} catch (Exception e) {
			exception = true;
		}
		assertFalse(exception);
		
		assertEquals(list.size(), 3);
		
		assertEquals(list.get(0).getName(), "Room 1");
		assertEquals(list.get(1).getName(), "Room 2");
		assertEquals(list.get(2).getName(), "Room 3");
		
		assertEquals(list.get(0).getPath(), "/bigbluebutton/servers/server-1/rooms/room-1/join?mobile=1");
		assertEquals(list.get(1).getPath(), "/bigbluebutton/servers/server-2/rooms/room-2/join?mobile=1");
		assertEquals(list.get(2).getPath(), "/bigbluebutton/servers/server-2/rooms/room-3/join?mobile=1");
		
		assertEquals(list.get(0).getOwner().getClass(), User.class);
		assertEquals(list.get(1).getOwner().getClass(), Space.class);
		assertEquals(list.get(2).getOwner().getClass(), Space.class);
		
		assertEquals(list.get(0).getOwner().getType(), Owner.TYPE_USER);
		assertEquals(list.get(1).getOwner().getType(), Owner.TYPE_SPACE);
		assertEquals(list.get(2).getOwner().getType(), Owner.TYPE_SPACE);

		assertEquals(list.get(0).getOwner().getId(), 1);
		assertEquals(list.get(1).getOwner().getId(), 1);
		assertEquals(list.get(2).getOwner().getId(), 2);
		
		assertEquals(((Space) list.get(1).getOwner()).getName(), "Space 1");
		assertEquals(((Space) list.get(2).getOwner()).getName(), "Space 2");
		
		assertFalse(((Space) list.get(1).getOwner()).isPublic());
		assertTrue(((Space) list.get(2).getOwner()).isPublic());
		
		assertTrue(((Space) list.get(1).getOwner()).isMember());
		assertFalse(((Space) list.get(2).getOwner()).isMember());
	}
}
