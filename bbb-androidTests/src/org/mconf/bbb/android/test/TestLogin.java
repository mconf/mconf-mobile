package org.mconf.bbb.android.test;



import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.android.AboutDialog;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class TestLogin extends ActivityInstrumentationTestCase2<LoginPage>  {

	public static final String NAME = "MyName";
	public final static String TEST_ROOM = "TestAndroid";

	private static BigBlueButtonClient user1;
	private static BigBlueButtonClient user2;
	private static BigBlueButtonClient user3;

	private Solo solo;

	public TestLogin() {
		super("org.mconf.bbb.android", LoginPage.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception{
		try {
			this.solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testCreateRoom()
	{
		TestServers.typeServerConnect(solo);
		solo.assertCurrentActivity("didn't close the about dialog", LoginPage.class);
		solo.clickOnView(solo.getView(R.id.login_spinner));
		solo.clickOnText(solo.getString(R.string.create));
		solo.enterText(0, TEST_ROOM);
		solo.clickOnButton(0);
		solo.clickOnText(TEST_ROOM);
		solo.clickOnView(solo.getView(R.id.login_button_join));
		solo.assertCurrentActivity("didn't go to client", Client.class);
	}

	public void  testAbout()
	{
		solo.clickOnMenuItem(solo.getString(R.string.menu_about));
		solo.scrollDown();
		solo.clickOnButton(0);
		solo.assertCurrentActivity("didn't close the about dialog", LoginPage.class);
	}



	public void roleModerator()
	{
		solo.clickOnRadioButton(0);
	}

	public void roleViewer()
	{
		solo.clickOnRadioButton(1);
	}

	public void changeName(String name)
	{
		solo.clearEditText(0);
		solo.enterText(0, name);

	}


	public void testLogin(){
		solo.assertCurrentActivity("hhhh", LoginPage.class);
		connectOnMeeting(solo, 0);//moderator
		solo.assertCurrentActivity("didn't go to Client", Client.class);
	}

	public static void connectOnMeeting(Solo solo, int role)
	{
		TestServers.typeServerConnect(solo);
		
		solo.assertCurrentActivity("not on Login", LoginPage.class);
		
		solo.clickOnView(solo.getView(R.id.login_spinner));
		solo.waitForText(solo.getString(R.string.create));
		solo.clickOnText(solo.getString(R.string.create));
		solo.enterText(0, TEST_ROOM);
		solo.clickOnButton(0);
		solo.waitForText(TEST_ROOM);
		solo.clickOnText(TEST_ROOM);
		assertTrue(solo.searchText(TEST_ROOM));
		solo.clickOnRadioButton(role);
		solo.clearEditText(0); 
		solo.enterText(0, NAME);
		solo.clickOnButton(solo.getString(R.string.login_button_join));
		solo.assertCurrentActivity("didn't go to client", Client.class);
		//ok so far 
		
		addContactsToMeeting(solo);

	}
//TODO java.lang.IllegalAccessError: Class ref in pre-verified class resolved to unexpected implementation

	public static void addContactsToMeeting(Solo solo)
	{
		user1 = new BigBlueButtonClient();
		user1.getJoinService().load(TestServers.server);

		user1.getJoinService().join(TEST_ROOM, "TestFirst", false);
		if (user1.getJoinService().getJoinedMeeting() != null) {
			user1.connectBigBlueButton();
			
		}

		user2= new BigBlueButtonClient();
		user2.getJoinService().load(TestServers.server);

		user2.getJoinService().join(TEST_ROOM, "TestSecond", false);
		if (user2.getJoinService().getJoinedMeeting() != null) {
			user2.connectBigBlueButton();


		}
		user3 = new BigBlueButtonClient();
		user3.getJoinService().load(TestServers.server);

		user3.getJoinService().join(TEST_ROOM, "TestThird", false);
		if (user3.getJoinService().getJoinedMeeting() != null) {
			user3.connectBigBlueButton();
		}
		assertTrue(solo.searchText("TestThird"));
	}

	public static void removeContactsFromMeeting()
	{
		user1.disconnect();
		user2.disconnect();
		user3.disconnect();
	}

}
