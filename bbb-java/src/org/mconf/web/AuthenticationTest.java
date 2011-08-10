package org.mconf.web;

import org.junit.Test;

public class AuthenticationTest {
	@Test
	public void constructor() {
		Authentication auth = new Authentication("http://mconf.inf.ufrgs.br");
		try {
			auth.authenticate("", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
