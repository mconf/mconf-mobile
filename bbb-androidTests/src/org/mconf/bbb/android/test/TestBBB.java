package org.mconf.bbb.android.test;

import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.PrivateChat;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.ServerChoosing;

import junit.framework.TestCase;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.jayway.android.robotium.solo.Solo;


public class TestBBB extends ActivityInstrumentationTestCase2<ServerChoosing> {

	
	private Solo solo;
	
	public TestBBB() throws ClassNotFoundException {
			super("org.mconf.bbb.android", ServerChoosing.class);
			}
		

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
	
	public void TypeServer() {
		solo.enterText(0, "http://devbbb-mconf.no-ip.org/");
		solo.clickOnButton("Connect");
		
		solo.getCurrentActivity();
		solo.assertCurrentActivity("wrong", LoginPage.class); 
	}
	
	public void ClickOnServer(){
		solo.clickInList(0);
		solo.clickOnButton("Connect");
		solo.getCurrentActivity();
		solo.assertCurrentActivity("wrong", LoginPage.class); 
	}
	
	public void LoginInDemo(){
		TypeServer();
		Spinner spinner = (Spinner) solo.getView(R.id.login_spinner);
		solo.clickOnView(spinner);
		solo.clickOnText("Demo Meeting");
		solo.clearEditText(0);
		solo.enterText(0, "Tester");
		Moderator();
		solo.clickOnButton("Join");
		solo.assertCurrentActivity("wrong", Client.class); 
	}
	
	public void Moderator(){
		solo.clickOnView(solo.getView(R.id.login_role_moderator));
	}
	
	public void BackToServers(){
		TypeServer();
		solo.clickOnButton("Back to servers");
		solo.assertCurrentActivity("wrong", ServerChoosing.class);
		ClickOnServer();
		solo.assertCurrentActivity("wrong",LoginPage.class);
		solo.clickOnButton("Join");
		solo.assertCurrentActivity("wrong",LoginPage.class);
	}
	
	public void PublicChat(){
		LoginInDemo();
		solo.clickOnText("Public chat");
		solo.enterText(0, "public chat testing");
		solo.clickOnButton("Send");
		assertTrue(solo.searchText("public chat testing"));
	}
	
	public void Kick(){
		
		solo.clickLongOnText("ale");
		solo.clickOnText("Kick");
		assertFalse(solo.searchText("ale"));
	}
	
	public void testPrivateChat()
	{
		LoginInDemo();
		solo.clickOnText("ale");
		solo.assertCurrentActivity("wrong",PrivateChat.class);
		solo.enterText(0, "testing privateChat");
		solo.clickOnButton(0);
		solo.goBack();
		solo.clickOnText("Tester");
		solo.assertCurrentActivity("wrong",Client.class);
		solo.clickOnText("ale");
		assertTrue(solo.searchText("testing privateChat"));
		solo.goBack();
		Kick();
		solo.clickInList(0);
		solo.assertCurrentActivity("wrong",PrivateChat.class);
		
	}
	

}

