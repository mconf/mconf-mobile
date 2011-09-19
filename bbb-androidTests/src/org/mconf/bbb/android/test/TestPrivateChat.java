package org.mconf.bbb.android.test;

import org.mconf.android.bbbandroid.R;
import org.mconf.android.bbbandroid.LoginPage;
import org.mconf.android.core.Client;
import org.mconf.android.core.PrivateChat;
import org.mconf.bbb.users.IParticipant;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.SlidingDrawer;

import com.jayway.android.robotium.solo.Solo;

public class TestPrivateChat extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	
	public TestPrivateChat() {
		super("org.mconf.android.bbbandroid", LoginPage.class);
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
	
	public void testChangeChat() {
		IParticipant p1 = Common.getRandomUser(solo, false),
			p2 = Common.getRandomUser(solo, false);
		
		while (p2.getUserId() == p1.getUserId()) {
			p2 = Common.getRandomUser(solo, false);
		}
		
		solo.clickInList(Common.getParticipantsList(solo).getPositionById(p1.getUserId()) + 1);
		solo.assertCurrentActivity("wrong activity", PrivateChat.class);
		assertTrue(solo.getCurrentActivity().getTitle().toString().contains(p1.getName()));
		solo.goBackToActivity("Client");
		
		solo.clickInList(Common.getParticipantsList(solo).getPositionById(p2.getUserId()) + 1);
		solo.assertCurrentActivity("wrong activity", PrivateChat.class);
		assertTrue(solo.getCurrentActivity().getTitle().toString().contains(p2.getName()));
		
		solo.scrollToSide(Solo.RIGHT);
		assertTrue(solo.getCurrentActivity().getTitle().toString().contains(p1.getName()));
		solo.scrollToSide(Solo.RIGHT);
		assertTrue(solo.getCurrentActivity().getTitle().toString().contains(p2.getName()));
		
		solo.scrollToSide(Solo.LEFT);
		assertTrue(solo.getCurrentActivity().getTitle().toString().contains(p1.getName()));
		solo.scrollToSide(Solo.LEFT);
		assertTrue(solo.getCurrentActivity().getTitle().toString().contains(p2.getName()));
	}
	
	public void testOpenAndClose() {
		IParticipant p = Common.getRandomUser(solo, false);
		solo.clickInList(Common.getParticipantsList(solo).getPositionById(p.getUserId()) + 1);
		solo.assertCurrentActivity("wrong activity", PrivateChat.class);
		assertTrue(solo.getCurrentActivity().getTitle().toString().contains(p.getName()));
		
		/*
		 * \TODO close the chat is not working always, implementation (of the 
		 * feature, not the test) should be reviewed
		 */
		solo.clickOnMenuItem(solo.getString(R.string.close_chat));
//		solo.waitForText(solo.getString(R.string.list_participants));	
		solo.waitForView(SlidingDrawer.class);
		solo.assertCurrentActivity("wrong activity", Client.class);
	}
	
	public void testWriteMessage() {
		IParticipant p = Common.getRandomUser(solo, false);
		solo.clickInList(Common.getParticipantsList(solo).getPositionById(p.getUserId()) + 1);
		solo.assertCurrentActivity("wrong activity", PrivateChat.class);

		String test = "testing message";
		solo.clearEditText(0);
		solo.enterText(0, test);
		solo.clickOnButton(solo.getString(R.string.send_message));
		assertEquals(solo.getEditText(0).getText().toString().length(), 0);
		assertTrue(solo.searchText(test));
	}
	
}
