package org.mconf.web;

import static org.junit.Assert.*;

import org.junit.Test;

public class AuthenticationTest {
	private final String DEFAULT_USERNAME = "";
	private final String DEFAULT_PASSWORD = "";
	@Test
	public void constructor() {
		Authentication auth = new Authentication("http://mconfmoodle.inf.ufrgs.br", DEFAULT_USERNAME, DEFAULT_PASSWORD);
		assertTrue(auth.isAuthenticated());
		MconfWebAPI.getRooms(auth);
		MconfWebAPI.getJoinUrl(auth, "/bigbluebutton/servers/default-server/rooms/reuniao-gt-mconf/join?mobile=1");
	}
}
