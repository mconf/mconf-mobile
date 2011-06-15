package org.mconf.bbb.android.test;

import java.util.Random;

import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.ServerChoosing;
import org.mconf.bbb.android.test.Common;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TestLogin extends ActivityInstrumentationTestCase2<LoginPage> {
	private Solo solo;

	public TestLogin() {
		super("org.mconf.bbb.android", LoginPage.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.solo = new Solo(getInstrumentation(), getActivity());

		// select the server
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
		if (!solo.searchText(Common.DEFAULT_SERVER)) {
			solo.clickOnView(solo.getView(R.id.server));
			solo.assertCurrentActivity("wrong activity", ServerChoosing.class);
			solo.enterText(0, Common.DEFAULT_SERVER);
			assertTrue(solo.searchText(Common.DEFAULT_SERVER));
			solo.clickOnButton(solo.getString(R.string.connect));
			solo.assertCurrentActivity("wrong activity", LoginPage.class);
		}
		// enter the name
		if (!solo.getEditText(0).getText().toString().equals(Common.DEFAULT_NAME)) {
			solo.clearEditText(0);
			solo.enterText(0, Common.DEFAULT_NAME);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testCreateRoom() throws Exception {
		// generate a random meeting name
		String meeting = Common.DEFAULT_TEST_ROOM + " " + new Random().nextInt(4096);	
		solo.clickOnView(solo.getView(R.id.login_spinner));
		solo.clickOnText(solo.getString(R.string.new_meeting));
		solo.clearEditText(0);
		solo.enterText(0, meeting);
		solo.clickOnButton(solo.getString(R.string.create));
		assertTrue(solo.searchText(Common.exactly(meeting)));
		solo.clickOnText(Common.exactly(meeting));
		solo.clickOnView(solo.getView(R.id.login_button_join));
		solo.assertCurrentActivity("wrong activity", Client.class);
		assertTrue(solo.searchText(Common.DEFAULT_NAME));
	}

	public void testAbout() throws Exception {
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
		solo.clickOnMenuItem(solo.getString(R.string.menu_about));
		solo.scrollDown();
		solo.clickOnButton(solo.getString(R.string.close));
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
	}

	public void testLoginModerator() throws Exception {
		Common.loginAsModerator(solo);
	}
	
	public void testLoginViewer() throws Exception {
		Common.loginAsViewer(solo);
	}

}
