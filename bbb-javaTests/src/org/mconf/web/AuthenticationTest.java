package org.mconf.web;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.json.JSONException;
import org.junit.Test;

public class AuthenticationTest {
	private final String DEFAULT_USERNAME = "fcecagno@gmail.com";
	private final String DEFAULT_PASSWORD = "debora";
	@Test
	public void constructor() {
		Authentication auth = new Authentication("http://mconfmoodle.inf.ufrgs.br", DEFAULT_USERNAME, DEFAULT_PASSWORD);
		assertTrue(auth.isAuthenticated());
		MconfWebItf mconf = new MconfWebImpl();
		try {
			mconf.getRooms(auth);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		MconfWebAPI.getJoinUrl(auth, "/bigbluebutton/servers/default-server/rooms/reuniao-gt-mconf/join?mobile=1");
	}
}
