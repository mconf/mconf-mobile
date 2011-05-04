package org.mconf.bbb.android.test;

import org.mconf.bbb.android.LoginPage;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TestPrivateChat extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	
	public TestPrivateChat() {
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
	
	public void changeChat(){}
	
	public void closeCurrentChat(){}
	
	public void writeMessage(String message){}
	
}
