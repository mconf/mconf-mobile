package org.mconf.web;

import static org.junit.Assert.*;

import org.junit.Test;

public class AuthenticationTest {
	@Test
	public void constructor() {
		Authentication auth = new Authentication("http://mconfmoodle.inf.ufrgs.br", "", "");
		assertTrue(auth.isAuthenticated());
		MconfWebAPI.getRooms(auth);
	}
}
