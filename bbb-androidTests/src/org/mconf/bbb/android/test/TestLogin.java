package org.mconf.bbb.android.test;

import org.mconf.bbb.android.AboutDialog;
import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.ServerChoosing;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;

public class TestLogin extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	
	public TestLogin() {
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
	
	public void createRoom(String room)
	{
		solo.clickOnView(solo.getView(R.id.login_spinner));
		solo.clickOnText(solo.getString(R.string.create));
		solo.enterText(0, room);
		solo.clickOnButton(0);
	}
	
	public void  about()
	{
		solo.clickOnMenuItem(solo.getString(R.string.about));
		solo.assertCurrentActivity("didn't open the dialog", AboutDialog.class);
		solo.scrollDown();
		solo.clickOnButton(0);
		solo.assertCurrentActivity("didn't close the about dialog", LoginPage.class);
	}
	
	public void choseServer()
	{
		TestServers testServers = new TestServers();
		testServers.goToServers();
	}
	
	public void roleModerator()
	{
		solo.clickOnRadioButton(0);
	}
	
	public void roleViewer()
	{
		solo.clickOnRadioButton(1);
	}
	
	public void changeName(String name)
	{
		solo.clearEditText(0);
		solo.enterText(0, name);
		
	}
	
	public void type() //conseguir usar as funções das outras classes
	{
		solo.assertCurrentActivity("not on login", LoginPage.class);
		solo.clickOnView(solo.getView(R.id.server));
		solo.assertCurrentActivity("didn't go to Serveres", ServerChoosing.class);
		solo.enterText(0, "http://mconfdev.inf.ufrgs.br/");
		solo.clickOnButton(solo.getString(R.string.connect));
	}
	
	public void changeRoom(int num)
	{
		type();
		solo.waitForText(solo.getString(R.string.login_name));
		Spinner spinner = (Spinner) solo.getView(R.id.login_spinner);
		solo.clickOnView(solo.getView(R.id.login_spinner));
		solo.waitForText("Demo Meeting");
		String room = spinner.getItemAtPosition(num).toString();
		solo.clickOnText(room);
		assertTrue(solo.searchText(room));
	}
	
	public void testLogin(){
		solo.assertCurrentActivity("hhhh", LoginPage.class);
	}
	
	
}
