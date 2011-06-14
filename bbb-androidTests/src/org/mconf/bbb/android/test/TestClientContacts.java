package org.mconf.bbb.android.test;

import java.util.Random;

import org.mconf.bbb.android.BigBlueButton;
import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.Contact;
import org.mconf.bbb.android.ContactAdapter;
import org.mconf.bbb.android.CustomListview;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.PrivateChat;
import org.mconf.bbb.android.R;
import org.mconf.bbb.users.IParticipant;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

/*
 * \TODO implement some test cases as a viewer
 * \TODO better implement the test case for the longpress menu items
 * \TODO remove the static stuff, or move it to the Common class
 * \TODO don't use a fixed listview id on tests - try to randomize between the existent users
 * \TODO implement video test
 */
public class TestClientContacts extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	
	public TestClientContacts() {
		super("org.mconf.bbb.android", LoginPage.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.solo = new Solo(getInstrumentation(), getActivity());
		Common.addContactsToMeeting(solo, 5);
		Common.loginAsModerator(solo);
	}
	
	@Override
	protected void tearDown() throws Exception{
		try {
			this.solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		Common.removeContactsFromMeeting();
		super.tearDown();
	}
	
	public void testCloseRoom()
	{

		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.logout));
		solo.assertCurrentActivity("didn't logout", LoginPage.class);
		
	}
	
	public void testQuit()
	{
		assertFalse(solo.getCurrentViews().isEmpty());
		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.quit));
		// current activity is not visible anymore
		assertTrue(solo.getCurrentViews().isEmpty());
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
		solo.clickOnButton(solo.getString(R.string.close));
		solo.assertCurrentActivity("didn't close the About", Client.class);
	}
	
	public void testRaiseHand()
	{
		solo.assertCurrentActivity("not on Client", Client.class);

		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		ContactAdapter contactAdapter = (ContactAdapter) contacts.getAdapter();
		int myUserId = ((BigBlueButton) solo.getCurrentActivity().getApplication()).getHandler().getMyUserId();
		Contact contact = (Contact) contactAdapter.getUserById(myUserId);
		assertFalse(contact.isRaiseHand());

		solo.clickOnMenuItem(solo.getString(R.string.raise_hand));
		int myPosition = contactAdapter.getPositionById(myUserId);
		solo.waitForView(contacts.getChildAt(myPosition).findViewById(R.id.raise_hand).getClass());	
		assertTrue(contact.isRaiseHand());
	}
	
	public void testKick()
	{
		solo.assertCurrentActivity("not on Client", Client.class);
		
		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		assertFalse(contacts == null);
		assertTrue(solo.getCurrentViews().contains(contacts));
		
		ContactAdapter contactAdapter = (ContactAdapter) contacts.getAdapter();
		
		for (int i = 0; i < contactAdapter.getCount(); ++i) {
			IParticipant contact = (IParticipant) contactAdapter.getItem(i);
			String name = contact.getName();
			// look for a user different than me
			if (!name.startsWith(Common.DEFAULT_NAME)) {
				solo.clickLongOnText(name);
				solo.clickOnText(solo.getString(R.string.kick));
				assertFalse(solo.searchText(name));
				break;
			}
		}
	}
	
	public void testOpenChatLongPress()
	{
		int num = 2;

		String name= getContactName(num, solo);
		solo.clickLongInList(num);
		solo.clickOnText(solo.getString(R.string.open_private_chat));
		solo.assertCurrentActivity("didn't open private chat", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		assertTrue(title.contains(name));
		
	}
	
	public void testAssignPresenter()
	{
		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		assertNotNull(contacts);
		assertTrue(solo.getCurrentViews().contains(contacts));
		
		ContactAdapter contactAdapter = (ContactAdapter) contacts.getAdapter();
		
		for (int i = 0; i < contactAdapter.getCount(); ++i) {
			IParticipant contact = (IParticipant) contactAdapter.getItem(i);
			
			if (!contact.getStatus().isPresenter()) {
				solo.clickLongInList(i);
				solo.clickOnText(solo.getString(R.string.assign_presenter));
				solo.waitForView(contacts.getChildAt(i).findViewById(R.id.presenter).getClass());
				assertTrue(contact.getStatus().isPresenter());
				assertTrue(contacts.getChildAt(i).findViewById(R.id.presenter).isShown());
				break;
			}
		}
	}

	// different than me
	public IParticipant getRandomUser() {
		CustomListview contacts = (CustomListview) solo.getView(R.id.contacts_list);
		assertFalse(contacts == null);
		assertTrue(solo.getCurrentViews().contains(contacts));
		
		ContactAdapter contactAdapter = (ContactAdapter) contacts.getAdapter();
		
		while (true) {
			IParticipant contact = (IParticipant) contactAdapter.getItem(new Random().nextInt(contactAdapter.getCount()));
			if (!contact.getName().startsWith(Common.DEFAULT_NAME))
				return contact;
		}
	}
	
	public void testOpenChat()
	{

		int num = 2;
		
		solo.waitForText(Common.startsWith(Common.DEFAULT_NAME));
		String name = getContactName(num, solo);
		solo.clickInList(num);
		solo.assertCurrentActivity("didn't open private chat", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		assertTrue(title.contains(name));
	}
	
//	public void testShowVideo()
//	{
//		//TODO make user i put on the conference have video
//		solo.clickLongInList(2);
//		solo.clickOnText(solo.getString(R.string.show_video));
//		solo.setActivityOrientation(Solo.LANDSCAPE);
//		solo.assertCurrentActivity("where's the video?", VideoFullScreen.class);
//	}
		
	public static void openPrivateChat(Solo solo, int num)
	{
		Common.loginAsModerator(solo);
		
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
		// solo list starts by 1, while Java lists start by 0
		assertTrue(num <= contactAdapter.getCount());
		Contact contact = (Contact) contactAdapter.getItem(num - 1);
		return contact.getContactName();
	}
	
}
