package org.mconf.bbb.android.test;

import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.Contact;
import org.mconf.bbb.android.ContactAdapter;
import org.mconf.bbb.android.CustomListview;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.PrivateChat;
import org.mconf.bbb.android.R;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TestClientContacts extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	public static int LINE_NUMBER = 2;
	
	public TestClientContacts() {
		super("org.mconf.bbb.android", LoginPage.class);
		}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.solo = new Solo(getInstrumentation(), getActivity());
		loginAsModerator();
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
			TestLogin.removeContactsFromMeeting();
			}
	
	
	public	void loginAsModerator()
	{
		TestLogin.connectOnMeeting(solo, 0);
	}
	
	void loginAsViewer()
	{
		TestLogin.connectOnMeeting(solo,  1);
	}
	
	
	public	void testCloseRoom()
	{

		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.logout));
		solo.assertCurrentActivity("didn't logout", LoginPage.class);
		
	}
	
	public void testQuit()
	{

		Activity client = solo.getCurrentActivity();
		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.quit));
		
		assertFalse(solo.getCurrentActivity()==client); //TODO?? how to know that it's over
		//assertation failed error
	}
	
	public void testPublicChat()
	{
		String test = "testing chat";

		solo.assertCurrentActivity("not on Client", Client.class);
		test="portrait testing";
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clickOnText(solo.getString(R.string.public_chat));
		solo.clearEditText(0);
		solo.enterText(0, test);
		solo.clickOnButton(solo.getString(R.string.send_message));
		solo.clearEditText(0);
		assertTrue(solo.searchText(test));
		solo.clickOnText(solo.getString(R.string.public_chat));
				
	}
	
	public void testAbout()
	{

		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.menu_about));
		solo.scrollDown();
		solo.clickOnButton(0);
		solo.assertCurrentActivity("didn't close the About", Client.class);
	}
	
	public void testRaiseHand()
	{

		solo.assertCurrentActivity("not on Client", Client.class);
		assertFalse(isMyHandRaised(solo));
		solo.clickOnMenuItem(solo.getString(R.string.raise_hand));
		assertTrue(isMyHandRaised(solo));
	}
	
	void Kick(int num)
	{

		solo.assertCurrentActivity("not on Client", Client.class);
		String name= getContactName(num, solo);
		assertFalse(name.equals(TestLogin.NAME));
		solo.clickLongInList(num);
		solo.clickOnMenuItem(solo.getString(R.string.kick));
		assertFalse(solo.searchText(name));
	}
	
	public void testKick()
	{
		Kick(LINE_NUMBER);
		
	}
	
	public void testOpenChatLongPress()
	{
		int num =0;

		String name= getContactName(num, solo);
		solo.clickLongInList(num);
		solo.clickOnText(solo.getString(R.string.open_private_chat));
		solo.assertCurrentActivity("didn't open private chat", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		assertTrue(title.contains(name));
		
	}
	
	public void assignPresenter(int num)
	{

		assertFalse(isPresenterAssigned(num, solo));
		solo.clickLongInList(num);
		solo.clickOnText(solo.getString(R.string.assign_presenter));
		assertTrue(isPresenterAssigned(num, solo));
	}
	
	public void testAssignPresenter()
	{
		assignPresenter(LINE_NUMBER);
	}
	
	public void testOpenChat()
	{

		int num = 0;
		
		String name = getContactName(num, solo);
		
		solo.clickInList(num);
		solo.assertCurrentActivity("didn't open private chat", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		assertTrue(title.contains(name));
	}
	
	public static void openPrivateChat(Solo solo, int num)
	{
		TestLogin.connectOnMeeting(solo, 0);
		
		String name = getContactName(num, solo);
		
		solo.clickInList(num);
		solo.assertCurrentActivity("didn't open private chat", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		assertTrue(title.contains(name));
	}
	
	private static String getContactName(int num, Solo solo)
	{
		solo.waitForText(solo.getString(R.string.list_participants));
		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		ContactAdapter contactAdapter = (ContactAdapter) contacts.getAdapter();
		Contact contact = contactAdapter.getUserById(contactAdapter.getUserId(num));
		return contact.getContactName();
	}
	
	private static boolean isMyHandRaised(Solo solo)
	{
		solo.waitForText(solo.getString(R.string.list_participants));
		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		ContactAdapter contactAdapter = (ContactAdapter) contacts.getAdapter();
		Contact contact = contactAdapter.getUserById(Client.myID);
		return contact.isRaiseHand();
	}
	
	private static boolean isPresenterAssigned(int num, Solo solo)
	{
		solo.waitForText(solo.getString(R.string.list_participants));
		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		ContactAdapter contactAdapter = (ContactAdapter) contacts.getAdapter();
		Contact contact = contactAdapter.getUserById(contactAdapter.getUserId(num));
		return contact.isPresenter();
	}
	
	
	
	
}
