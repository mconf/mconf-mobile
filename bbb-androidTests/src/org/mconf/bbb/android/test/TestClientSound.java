package org.mconf.bbb.android.test;

import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.Contact;
import org.mconf.bbb.android.ContactAdapter;
import org.mconf.bbb.android.CustomListview;
import org.mconf.bbb.android.ListenerAdapter;
import org.mconf.bbb.android.ListenerContact;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TestClientSound extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;

	public TestClientSound() {
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

	

	void startVoice()
	{
		loginAsModerator(0);
		solo.assertCurrentActivity("didn't go to client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.start_voice));
		assertTrue(solo.searchButton(solo.getString(R.string.taptospeak)));
		assertTrue(solo.searchText(TestLogin.NAME, 2));
	}

	void KickListener(int num)
	{
		startVoice();

		String name = getListenerName(solo, num);
		solo.clickLongOnText(name);
		solo.clickOnText(solo.getString(R.string.kick));
		assertFalse(solo.searchText(name));
	}

	void muteListener(int num)
	{
		startVoice();
		String name = getListenerName(solo, num);
		solo.clickLongOnText(name);
		solo.clickOnText(solo.getString(R.string.mute));
		//como testar?
	}

	void tapToSpeak(){
		//TODO como testar?
	}

	void voiceButton()
	{
		startVoice();
		solo.clickOnButton(solo.getString(R.string.lockspeak));	
		//TODO como testar?
	}

	void menuSpeaker()
	{
		startVoice();
		solo.clickOnMenuItem(solo.getString(R.string.speaker));
		//TODO como testar?
	}

	void testMenuStopVoice()
	{
		startVoice();
		solo.clickOnMenuItem(solo.getString(R.string.stop_voice));
		assertFalse(solo.searchButton(solo.getString(R.string.taptospeak)));
		assertFalse(solo.searchText(TestLogin.NAME, 2));
		
	}

	void menuMicGain()
	{
		startVoice();
		solo.clickOnMenuItem(solo.getString(R.string.audio_config));
		solo.waitForText(solo.getString(R.string.audio_config));
		//como mudar os parâmetros?
		//TODO como testar?
	}

	void menuEarphone()
	{
		startVoice();
		solo.clickOnMenuItem(solo.getString(R.string.audio_config));
		solo.waitForText(solo.getString(R.string.audio_config));
		//como mudar os parâmetros?
		//TODO como testar?
	}

	void menuSpeakerSound()
	{
		startVoice();
		solo.clickOnMenuItem(solo.getString(R.string.audio_config));
		solo.waitForText(solo.getString(R.string.audio_config));
		//como mudar os parâmetros?
		//TODO como testar?
	}
	
	private String getListenerName(Solo solo, int num)
	{
		solo.waitForText(solo.getString(R.string.list_listeners));
		CustomListview listeners = (CustomListview) solo.getView(R.id.listeners_list);
		ListenerAdapter listAdapter = (ListenerAdapter) listeners.getAdapter();
		ListenerContact listener = listAdapter.getListener(num);
		return listener.getListenerName();
	}
}
