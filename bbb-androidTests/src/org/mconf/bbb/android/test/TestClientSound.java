package org.mconf.bbb.android.test;

import org.mconf.android.bbbandroid.R;
import org.mconf.android.bbbandroid.LoginPage;
import org.mconf.android.core.Client;
import org.mconf.android.core.CustomListview;
import org.mconf.android.core.ListenerAdapter;
import org.mconf.android.core.ListenerContact;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TestClientSound extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	private static int LINE_NUMBER=1;
	
	public TestClientSound() {
		super("org.mconf.android.bbbandroid", LoginPage.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.solo = new Solo(getInstrumentation(), getActivity());
		Common.addContactsToMeeting(solo, 10);
		Common.loginAsModerator(solo);

		// start voice conference
		solo.assertCurrentActivity("didn't go to client", Client.class);
		solo.clickOnMenuItem(solo.getString(R.string.start_voice));
		assertTrue(solo.searchButton(solo.getString(R.string.taptospeak)));
		assertTrue(solo.searchText(Common.DEFAULT_NAME, 2));		
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

	public void KickListener()
	{
		//TODO make user i put on the conference have audio
		String name = getListenerName(solo, LINE_NUMBER);
		solo.clickLongOnText(name);
		solo.clickOnText(solo.getString(R.string.kick));
		assertFalse(solo.searchText(name));
	}

	public void muteListener()
	{
		assertFalse(isListenerMuted(LINE_NUMBER, solo));
		String name = getListenerName(solo, LINE_NUMBER);
		solo.clickLongOnText(name);
		solo.clickOnText(solo.getString(R.string.mute));
		assertTrue(isListenerMuted(LINE_NUMBER, solo));
	}

	public void tapToSpeak(){
		//TODO como testar?
	}

	public void voiceButton()
	{
		solo.clickOnButton(solo.getString(R.string.lockspeak));	
		//TODO como testar?
	}

	public void menuSpeaker()
	{
		solo.clickOnMenuItem(solo.getString(R.string.speaker));
		//TODO como testar?
	}

	public void testMenuStopVoice()
	{
		solo.clickOnMenuItem(solo.getString(R.string.stop_voice));
		solo.waitForText(solo.getString(R.string.connection_closed));
		assertFalse(solo.searchText(Common.exactly(Common.DEFAULT_NAME), 2));
		
	}

	public void menuMicGain()
	{
		solo.clickOnMenuItem(solo.getString(R.string.audio_config));
		solo.waitForText(solo.getString(R.string.audio_config));
		//como mudar os par�metros?
		//TODO como testar?
	}

	public void menuEarphone()
	{
		solo.clickOnMenuItem(solo.getString(R.string.audio_config));
		solo.waitForText(solo.getString(R.string.audio_config));
		//como mudar os par�metros?
		//TODO como testar?
	}

	public void menuSpeakerSound()
	{
		solo.clickOnMenuItem(solo.getString(R.string.audio_config));
		solo.waitForText(solo.getString(R.string.audio_config));
		//como mudar os par�metros?
		//TODO como testar?
	}
	
	public static String getListenerName(Solo solo, int num)
	{
		solo.waitForText(solo.getString(R.string.list_listeners));
		CustomListview listeners = (CustomListview) solo.getView(R.id.listeners_list);
		ListenerAdapter listAdapter = (ListenerAdapter) listeners.getAdapter();
		ListenerContact listener = listAdapter.getListener(num-1);
		return listener.getListenerName();
	}
	
	public  static boolean isListenerMuted(int num, Solo solo)
	{
		solo.waitForText(solo.getString(R.string.list_listeners));
		CustomListview listeners = (CustomListview) solo.getView(R.id.listeners_list);
		ListenerAdapter listenerAdapter = (ListenerAdapter) listeners.getAdapter();
		ListenerContact listener = listenerAdapter.getListener(num-1);
		return listener.isMuted();
	}
}
