package org.mconf.bbb.android.test;

import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.PrivateChat;
import org.mconf.bbb.android.R;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TestClientContacts extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	
	public TestClientContacts() {
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
	
	public	void loginAsModerator(int num)
	{
		TestLogin.connectOnMeeting(solo, num, 0);
	}
	
	void loginAsViewer(int num)
	{
		TestLogin.connectOnMeeting(solo, num, 1);
	}
	
	
	public	void closeRoom()
	{
		loginAsModerator(0);
		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.logout));
		solo.assertCurrentActivity("didn't logout", LoginPage.class);
		
	}
	
	public void Quit()
	{
		loginAsModerator(0);
		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.quit));

		solo.assertCurrentActivity("quitted", Client.class); //?? how to know that it's over
	}
	
	public void PublicChat()
	{
		String test = "testing chat";
		loginAsModerator(0);
		solo.assertCurrentActivity("not on Client", Client.class);
//		solo.setActivityOrientation(Solo.LANDSCAPE);
//		solo.clearEditText(0);
//		solo.enterText(0, test);
//		solo.clickOnButton(solo.getString(R.string.send_message));
//		solo.clearEditText(0);
//		assertTrue(solo.searchText(test));
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
	
	public void About()
	{
		loginAsModerator(0);
		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.menu_about));
		solo.scrollDown();
		solo.clickOnButton(0);
		solo.assertCurrentActivity("didn't close the About", Client.class);
	}
	
	void raiseHand()
	{
		loginAsModerator(0);
		solo.assertCurrentActivity("not on Client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.raise_hand));
		//como testar?
	}
	
	void kick(int num)
	{
		loginAsModerator(0);
		solo.assertCurrentActivity("not on Client", Client.class);
		String name = solo.getCurrentTextViews(solo.getView(R.id.contacts_list)).get(num).getText().toString();
		solo.clickLongInList(num);
		solo.clickOnMenuItem(solo.getString(R.string.kick));
		assertFalse(solo.searchText(name));
	}
	
	public void OpenChatLongPress()
	{
		loginAsModerator(0);
		String name = solo.getCurrentTextViews(solo.getView(R.id.contacts_list)).get(1).getText().toString();
		solo.clickLongInList(1);
		solo.clickOnText(solo.getString(R.string.open_private_chat));
		solo.assertCurrentActivity("didn't open private chat", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		assertTrue(title.contains(name));
		//servidor não está ajudando
	}
	
	void assignPresenter(int num)
	{
		loginAsModerator(0);
		String name = solo.getCurrentTextViews(solo.getView(R.id.contacts_list)).get(num).getText().toString();
		solo.clickLongInList(num);
		solo.clickOnText(solo.getString(R.string.assign_presenter));
		//como testar?
	}
	
	public void testOpenChat()
	{
		loginAsModerator(1);
		
		//String name = solo.getView(R.id.contacts_list).getC; //how to get the textView inside an item on an custom listview??????
		solo.clickInList(0);
		solo.assertCurrentActivity("didn't open private chat", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		//assertEquals(title, name);
	}
	
	public static void openPrivateChat(Solo solo, int num)
	{
		TestLogin.connectOnMeeting(solo, num, 0);
		String name = solo.getCurrentTextViews(solo.getView(R.id.contacts_list)).get(num).getText().toString();
		solo.clickInList(num);
		solo.assertCurrentActivity("didn't open private chat", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		//assertTrue(title.contains(name));
	}
	
}
