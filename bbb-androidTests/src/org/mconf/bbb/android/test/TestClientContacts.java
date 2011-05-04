package org.mconf.bbb.android.test;

import org.mconf.bbb.android.LoginPage;

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
	
public	void loginAsModerator(){}
	
	void loginAsViewer(){}
	
	void loginAtRoom(int num){}
	
	void loginAtServer(String server){}
	
	public	void closeRoom(){}
	
	void quit(){}
	
	void publicChat(){}
	
	void about(){}
	
	void raiseHand(){}
	
	void kick(int num){}
	
	void openChatLongPress(int num){}
	
	void assignPresenter(int num){}
	
	void openChat(int num){}
	
}
