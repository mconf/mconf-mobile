package org.mconf.bbb.android.test;

import java.util.Random;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.BigBlueButton;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.ContactAdapter;
import org.mconf.bbb.android.CustomListview;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.ServerChoosing;
import org.mconf.bbb.users.IParticipant;

import com.jayway.android.robotium.solo.Solo;

public class Common {

	private static BigBlueButtonClient[] users = null;
	
	public final static String DEFAULT_NAME = "My name";
	public final static String DEFAULT_TEST_USERS_NAME = "User []";
	public final static String DEFAULT_TEST_ROOM = "Test meeting";
	public final static String DEFAULT_SERVER = "http://mconfweb.inf.ufrgs.br";
	public final static int TIMEOUT_SMALL = 5000;
	public final static int SLEEP_SMALL = 1000;
	
	public static BigBlueButtonClient getUser(int userId)
	{
		for(BigBlueButtonClient user : users)
		{
			if(user.getMyUserId()==userId)
				return user;
		}
		return null;
	}
	// http://download.oracle.com/javase/tutorial/essential/regex/bounds.html
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
		prepareToLogin(solo);
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
	
	public static void prepareToLogin(Solo solo) {
		// select the server
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
		if (!solo.searchText(Common.DEFAULT_SERVER)) {
			solo.clickOnView(solo.getView(R.id.server));
			solo.assertCurrentActivity("wrong activity", ServerChoosing.class);
			solo.enterText(0, Common.DEFAULT_SERVER);
			junit.framework.Assert.assertTrue(solo.searchText(Common.DEFAULT_SERVER));
			solo.clickOnButton(solo.getString(R.string.connect));
			solo.assertCurrentActivity("wrong activity", LoginPage.class);
		}
		// enter the name
		if (!solo.getEditText(0).getText().toString().equals(Common.DEFAULT_NAME)) {
			solo.clearEditText(0);
			solo.enterText(0, Common.DEFAULT_NAME);
		}
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
	
	public static CustomListview getParticipantsListView(Solo solo) {
		solo.assertCurrentActivity("wrong activity", Client.class);
		CustomListview participantsListView = (CustomListview) solo.getView(R.id.contacts_list);
		junit.framework.Assert.assertNotNull(participantsListView);
		junit.framework.Assert.assertTrue(solo.getCurrentViews().contains(participantsListView));
		return participantsListView;
	}
	
	public static ContactAdapter getParticipantsList(Solo solo) {
		solo.assertCurrentActivity("wrong activity", Client.class);
		ContactAdapter participantsList = (ContactAdapter) getParticipantsListView(solo).getAdapter();
		junit.framework.Assert.assertFalse(participantsList.isEmpty());
		return participantsList;
	}
	
	public static int getMyUserId(Solo solo) {
		return ((BigBlueButton) solo.getCurrentActivity().getApplication()).getHandler().getMyUserId();		
	}
	
	public static IParticipant getRandomUser(Solo solo, boolean includeMe) {
		ContactAdapter participantsList = getParticipantsList(solo);
		while (true) {
			IParticipant p = (IParticipant) participantsList.getItem(new Random().nextInt(participantsList.getCount()));
			if (includeMe || !p.getName().startsWith(Common.DEFAULT_NAME))
				return p;
		}
	}
}
