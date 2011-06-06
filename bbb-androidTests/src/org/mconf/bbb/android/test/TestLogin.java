package org.mconf.bbb.android.test;

import org.mconf.bbb.android.AboutDialog;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.ServerChoosing;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;

public class TestLogin extends ActivityInstrumentationTestCase2<LoginPage>  {

	public static String NAME = "MyName";
	public static String TEST_ROOM = "TestAndroid";
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
	
	public void createRoom(String room)
	{
		solo.clickOnView(solo.getView(R.id.login_spinner));
		solo.clickOnText(solo.getString(R.string.create));
		solo.enterText(0, room);
		solo.clickOnButton(0);
		solo.clickOnText(room);
		solo.clickOnView(solo.getView(R.id.login_button_join));
		solo.assertCurrentActivity("didn't go to client", Client.class);
	}
	
	public void  testAbout()
	{
		solo.clickOnMenuItem(solo.getString(R.string.menu_about));
		solo.assertCurrentActivity("didn't open the dialog", AboutDialog.class);
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
		solo.waitForText(solo.getString(R.string.login_name));
		solo.clickOnView(solo.getView(R.id.login_spinner));
		solo.clickOnText(solo.getString(R.string.create));
		solo.enterText(0, TEST_ROOM);
		solo.clickOnButton(0);
		solo.clickOnText(TEST_ROOM);
		assertTrue(solo.searchText(TEST_ROOM));
		solo.clickOnRadioButton(role);
		solo.clearEditText(0);
		solo.enterText(0, NAME);
		solo.clickOnButton(solo.getString(R.string.login_button_join));
		solo.assertCurrentActivity("didn't go to client", Client.class);
		addContactsToMeeting();
		
	}
	
	public static void addContactsToMeeting()
	{
		
	}
	
	
}
