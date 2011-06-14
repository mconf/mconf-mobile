package org.mconf.bbb.android.test;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.CustomListview;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;

import com.jayway.android.robotium.solo.Solo;

public class Common {

	private static BigBlueButtonClient[] users = null;
	
	public final static String DEFAULT_NAME = "My name";
	public final static String DEFAULT_TEST_USERS_NAME = "User []";
	public final static String DEFAULT_TEST_ROOM = "Test meeting";
	public final static String DEFAULT_SERVER = "http://mconfdev.inf.ufrgs.br";

	public static String exactly(String s) {
		return "^" + s + "$";
	}
	
	public static String startsWith(String s) {
		return "^" + s;
	}
	
	public static String endsWith(String s) {
		return s + "$";
	}
	
	public static void addContactsToMeeting(Solo solo, int numberOfUsers) {
		users = new BigBlueButtonClient[numberOfUsers];
		for (int i = 0; i < numberOfUsers; ++i) {
			users[i] = new BigBlueButtonClient();
			users[i].getJoinService().load(Common.DEFAULT_SERVER);
			users[i].getJoinService().join(Common.DEFAULT_TEST_ROOM, Common.DEFAULT_TEST_USERS_NAME.replace("[]", Integer.toString(i + 1)), false);
			if (users[i].getJoinService().getJoinedMeeting() != null)
				users[i].connectBigBlueButton();
		}
	}

	public static void removeContactsFromMeeting() {
		if (users == null)
			return;
		for (BigBlueButtonClient user : users) {
			if (user.isConnected())
				user.disconnect();
		}
		users = null;
	}

	private static void login(Solo solo, int role) {
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
		solo.clickOnView(solo.getView(R.id.login_spinner));
		if (!solo.searchText(Common.exactly(Common.DEFAULT_TEST_ROOM))) {
			solo.clickOnText(solo.getString(R.string.new_meeting));
			solo.clearEditText(0);
			solo.enterText(0, Common.DEFAULT_TEST_ROOM);
			solo.clickOnButton(solo.getString(R.string.create));
		}
		solo.clickOnText(Common.exactly(Common.DEFAULT_TEST_ROOM));
		junit.framework.Assert.assertTrue(solo.searchText(Common.exactly(Common.DEFAULT_TEST_ROOM)));
		// test if the spinner listview disappears
		junit.framework.Assert.assertTrue(solo.getCurrentListViews().isEmpty());
		solo.clickOnText(solo.getString(role));
		solo.clickOnButton(solo.getString(R.string.login_button_join));
		solo.assertCurrentActivity("wrong activity", Client.class);
		junit.framework.Assert.assertTrue(solo.searchText(Common.DEFAULT_NAME));
	}
	
	public static void loginAsModerator(Solo solo) {
		login(solo, R.string.moderator);
		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		junit.framework.Assert.assertTrue(contacts.getChildAt(0).findViewById(R.id.moderator).isShown());
	}

	public static void loginAsViewer(Solo solo) {
		login(solo, R.string.viewer);
		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		junit.framework.Assert.assertFalse(contacts.getChildAt(0).findViewById(R.id.moderator).isShown());
	}

}
