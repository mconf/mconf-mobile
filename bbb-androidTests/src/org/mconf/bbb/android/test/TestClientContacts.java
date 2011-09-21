package org.mconf.bbb.android.test;

import org.mconf.android.bbbandroid.R;
import org.mconf.android.bbbandroid.LoginPage;
import org.mconf.android.core.BigBlueButton;
import org.mconf.android.core.Client;
import org.mconf.android.core.Contact;
import org.mconf.android.core.ContactAdapter;
import org.mconf.android.core.CustomListview;
import org.mconf.android.core.PrivateChat;
import org.mconf.bbb.users.IParticipant;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
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
		super("org.mconf.android.bbbandroid", LoginPage.class);
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
	
	public void testCloseDialogPutInBackground() {
		solo.sendKey(KeyEvent.KEYCODE_BACK);
		solo.waitForText(Common.exactly(solo.getString(R.string.back_pressed)));
		assertTrue(solo.searchText(solo.getString(R.string.back_pressed)));
		solo.clickOnButton(solo.getString(R.string.minimize));
		//assertTrue(solo.getCurrentViews().isEmpty());
		assertTrue(solo.searchText(solo.getString(R.string.application_on_background_text)));
	}
	
	public void testCloseDialogQuit() {
		solo.sendKey(KeyEvent.KEYCODE_BACK);
		solo.waitForText(Common.exactly(solo.getString(R.string.back_pressed)));
		assertTrue(solo.searchText(solo.getString(R.string.back_pressed)));
		solo.clickOnButton(solo.getString(R.string.quit));
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
	}
	
	public void testQuit() {
		solo.clickOnMenuItem(solo.getString(R.string.quit));
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
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
	
	public void testLowerHand() throws InterruptedException{
		IParticipant p = null;
		while (true) {
			p = Common.getRandomUser(solo, false);
			if (!p.getStatus().isRaiseHand()) {
				Common.getUser(p.getUserId()).raiseHand(true);
				break;
			}			
		}
		
		String name = p.getName();
		solo.clickLongOnText(name);
		solo.clickOnText(solo.getString(R.string.lower_hand));
		solo.clickLongOnText(name);
		assertFalse(solo.searchText(solo.getString(R.string.lower_hand)));
	}
	
	public void testKick() {
		IParticipant p = Common.getRandomUser(solo, false);
		String name = p.getName();
		solo.clickLongOnText(name);
		solo.clickOnText(solo.getString(R.string.kick));
		assertFalse(solo.searchText(name));
	}
	
	public void testOpenChatLongPress() {
		IParticipant p = Common.getRandomUser(solo, false);
		solo.clickLongInList(participantsList.getPositionById(p.getUserId()) + 1);
		solo.clickOnText(solo.getString(R.string.open_private_chat));
		solo.assertCurrentActivity("wrong activity", PrivateChat.class);
		String title = solo.getCurrentActivity().getTitle().toString();
		assertTrue(title.contains(p.getName()));
	}
	
	public void testAssignPresenter() {
		while (true) {
			IParticipant p = Common.getRandomUser(solo, true);
			if (!p.getStatus().isPresenter()) {
				int position = participantsList.getPositionById(p.getUserId());		
				View icon = participantsListView.getChildAt(position).findViewById(R.id.presenter);
				
				assertFalse(icon.isShown());
				
				solo.clickLongInList(position + 1);
				solo.clickOnText(solo.getString(R.string.assign_presenter));

				// wait for the update of the presenter icon
				// \TODO extract this function to Common in order to implement this wait in other tests
//				long endTime = System.currentTimeMillis() + Common.TIMEOUT_SMALL;
//				while (System.currentTimeMillis() < endTime) {
//					position = participantsList.getPositionById(p.getUserId());
//					icon = participantsListView.getChildAt(position).findViewById(R.id.presenter);
//					if (icon.isShown())
//						break;
//					SystemClock.sleep(Common.SLEEP_SMALL);
//				}
				solo.waitForView(ImageView.class);
				icon = participantsListView.getChildAt(position).findViewById(R.id.presenter);
				assertTrue(icon.isShown());
				
				assertTrue(p.getStatus().isPresenter());
				assertTrue(icon.isShown());
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

//	public void testShowVideo()
//	{
//		//TODO make user i put on the conference have video
//		solo.clickLongInList(2);
//		solo.clickOnText(solo.getString(R.string.show_video));
//		solo.setActivityOrientation(Solo.LANDSCAPE);
//		solo.assertCurrentActivity("where's the video?", VideoFullScreen.class);
//	}
		
}
