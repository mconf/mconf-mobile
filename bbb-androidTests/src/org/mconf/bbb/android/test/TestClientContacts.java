package org.mconf.bbb.android.test;

import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.LoginPage;
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
	
	void publicChat(){}
	
	public void testAbout()
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
	}
	
	void kick(int num)
	{
		loginAsModerator(0);
		solo.assertCurrentActivity("not on Client", Client.class);
		
		solo.clickLongInList(num);
		solo.clickOnMenuItem(solo.getString(R.string.kick));
	}
	
	void openChatLongPress(int num){}
	
	void assignPresenter(int num){}
	
	void openChat(int num){}
	
}
