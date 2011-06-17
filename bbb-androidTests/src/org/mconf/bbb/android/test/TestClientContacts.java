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
import android.util.Log;
import android.widget.ImageView;

import com.jayway.android.robotium.solo.Solo;

/*
 * \TODO implement some test cases as a viewer
 * \TODO better implement the test case for the longpress menu items
 * \TODO implement video test
 */
public class TestClientContacts extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	private CustomListview participantsListView;
	private ContactAdapter participantsList;
	private int myUserId;
	
	public TestClientContacts() {
		super("org.mconf.bbb.android", LoginPage.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.solo = new Solo(getInstrumentation(), getActivity());
		Common.addContactsToMeeting(solo, 5);
		Common.loginAsModerator(solo);

		participantsListView = (CustomListview) solo.getView(R.id.contacts_list);
		assertNotNull(participantsListView);
		assertTrue(solo.getCurrentViews().contains(participantsListView));
		
		participantsList = (ContactAdapter) participantsListView.getAdapter();
		assertFalse(participantsList.isEmpty());
		
		myUserId = ((BigBlueButton) solo.getCurrentActivity().getApplication()).getHandler().getMyUserId();		
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
	
	public void testCloseRoom() {
		solo.clickOnMenuItem(solo.getString(R.string.logout));
		solo.assertCurrentActivity("didn't logout", LoginPage.class);
	}

	public void testQuit() {
		assertFalse(solo.getCurrentViews().isEmpty());
		solo.clickOnMenuItem(solo.getString(R.string.quit));
		// current activity is not visible anymore
		assertTrue(solo.getCurrentViews().isEmpty());
	}
	
	public void testPublicChat() {
		String test = "portrait testing";
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clickOnText(solo.getString(R.string.public_chat));
		solo.clearEditText(0);
		solo.enterText(0, test);
		solo.clickOnButton(solo.getString(R.string.send_message));
		solo.clearEditText(0);
		assertTrue(solo.searchText(test));
		solo.clickOnText(solo.getString(R.string.public_chat));
	}

	public void testAbout() {
		solo.clickOnMenuItem(solo.getString(R.string.menu_about));
		solo.scrollDown();
		solo.clickOnButton(solo.getString(R.string.close));
		solo.assertCurrentActivity("didn't close the About", Client.class);
	}

	public void testRaiseHand() {
		Contact contact = (Contact) participantsList.getUserById(myUserId);
		assertFalse(contact.isRaiseHand());

		solo.clickOnMenuItem(solo.getString(R.string.raise_hand));
		int position = participantsList.getPositionById(myUserId);
		solo.waitForView(participantsListView.getChildAt(position).findViewById(R.id.raise_hand).getClass());	
		assertTrue(contact.isRaiseHand());
	}
	
	public void testKick() {
		IParticipant p = getRandomUser(false);
		String name = p.getName();
		solo.clickLongOnText(name);
		solo.clickOnText(solo.getString(R.string.kick));
		assertFalse(solo.searchText(name));
	}
	
	public void testOpenChatLongPress() {
		IParticipant p = getRandomUser(false);
		solo.clickLongInList(participantsList.getPositionById(p.getUserId()) + 1);
		solo.clickOnText(solo.getString(R.string.open_private_chat));
		solo.assertCurrentActivity("wrong activity", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		assertTrue(title.contains(p.getName()));
	}
	
	public void testAssignPresenter() {
		while (true) {
			IParticipant p = getRandomUser(true);
			if (!p.getStatus().isPresenter()) {
				int position = participantsList.getPositionById(p.getUserId());
				
				solo.clickLongInList(position + 1);
				solo.clickOnText(solo.getString(R.string.assign_presenter));			
				solo.waitForView(ImageView.class);
				
				assertTrue(p.getStatus().isPresenter());
				assertTrue(participantsListView.getChildAt(position).findViewById(R.id.presenter).isShown());
				break;
			}
		}
	}
	
	public void testPresenter() {
		int numberOfPresenters = 0;
		for (int i = 0; i < participantsList.getCount(); ++i) {
			IParticipant p = (IParticipant) participantsList.getItem(i);
			
			if (p.getStatus().isPresenter()) {
				solo.clickLongInList(i);
				assertFalse(solo.searchText(solo.getString(R.string.assign_presenter)));
				solo.goBack();
				numberOfPresenters++;
			}
		}
		assertTrue(numberOfPresenters <= 1);
	}

	public IParticipant getRandomUser(boolean includeMe) {
		while (true) {
			IParticipant p = (IParticipant) participantsList.getItem(new Random().nextInt(participantsList.getCount()));
			if (includeMe || !p.getName().startsWith(Common.DEFAULT_NAME))
				return p;
		}
	}

//	public void testShowVideo()
//	{
//		//TODO make user i put on the conference have video
//		solo.clickLongInList(2);
//		solo.clickOnText(solo.getString(R.string.show_video));
//		solo.setActivityOrientation(Solo.LANDSCAPE);
//		solo.assertCurrentActivity("where's the video?", VideoFullScreen.class);
//	}
		
}
