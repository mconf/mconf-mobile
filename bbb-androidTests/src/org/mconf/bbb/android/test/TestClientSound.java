package org.mconf.bbb.android.test;

import org.mconf.bbb.android.LoginPage;

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
	
public	void loginAsModerator(){}
	
	void loginAsViewer(){}
	
	void loginAtRoom(int num){}
	
	void loginAtServer(String server){}
	
	void startVoice(){}
	
	void kickListener(int num){}
	
	void muteListener(int num){}
	
	void tapToSpeak(){}
	
	void voiceButton(){}
	
	void menuSpeaker(){}
	
	void menuStopVoice(){}
	
	void menuMicGain(){}
	
	void menuEarphone(){}
	
	void menuSpeakerSound(){}
}
