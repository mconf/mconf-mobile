package org.mconf.web;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.HttpException;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore
public class AuthenticationTest {
	private final String DEFAULT_USERNAME = "";
	private final String DEFAULT_PASSWORD = "";
	@Test
	public void constructor() {
		Authentication auth = null;
		try {
			auth = new Authentication("https://mconf.org", DEFAULT_USERNAME, DEFAULT_PASSWORD);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(auth);
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
