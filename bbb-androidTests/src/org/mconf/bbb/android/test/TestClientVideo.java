package org.mconf.bbb.android.test;

import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.video.VideoFullScreen;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class TestClientVideo extends ActivityInstrumentationTestCase2<LoginPage> {
	
	private Solo solo;

	public TestClientVideo() {
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

	public	void loginAsModerator(int meetingNum)
	{
		TestLogin.connectOnMeeting(solo, meetingNum, 0);
	}

	public void loginAsViewer(int meetingNum)
	{
		TestLogin.connectOnMeeting(solo, meetingNum, 1);
	}
	
	public void showVideo(int contactNum)
	{
		solo.clickLongInList(contactNum);
		solo.clickOnText(solo.getString(R.string.show_video));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.assertCurrentActivity("where's the video?", VideoFullScreen.class);
	}
	
	public void testShowVideo()
	{
		int contactNum=0;
		showVideo(contactNum);
	}
	
	
}
