package org.mconf.bbb.android.test;

import org.mconf.bbb.android.Client;
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
	}

	void kickListener(int num)
	{
		startVoice();
	}

	void muteListener(int num){}

	void tapToSpeak(){}

	void voiceButton(){}

	void menuSpeaker(){}

	void menuStopVoice(){}

	void menuMicGain(){}

	void menuEarphone(){}

	void menuSpeakerSound(){}
}
