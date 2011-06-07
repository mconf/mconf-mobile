package org.mconf.bbb.android.test;


import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.ServerChoosing;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TestServers extends ActivityInstrumentationTestCase2<LoginPage>  {

	public static String server = "http://mconfdev.inf.ufrgs.br/";
	private Solo solo;
	
	public TestServers() {
		super("org.mconf.bbb.android", LoginPage.class);
		}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
  }
  
	@Override
	   public void tearDown() throws Exception {

	        try {
	                solo.finalize();
	        } catch (Throwable e) {

	                e.printStackTrace();
	        }
	        getActivity().finish();
	        super.tearDown();

	  }
	
	public static void typeServerConnect(Solo solo)
	{
		solo.clickOnView(solo.getView(R.id.server));
		solo.enterText(0, server);
		solo.clickOnButton(solo.getString(R.string.connect));
		
	}

	
	public void deleteServer(int num)
	{
		String name=solo.getCurrentListViews().get(num).toString();
		solo.clickLongInList(num);
		solo.clickOnText(solo.getString(R.string.delete_server));
		assertFalse(solo.searchText(name));
	}
	
	public void chooseServer(int num)
	{
		String name= solo.getCurrentTextViews(solo.getView(R.id.servers)).get(num).getText().toString();
		assertEquals(name, server);
		solo.clickInList(num);
		assertEquals(name, solo.getCurrentEditTexts().get(0).getText().toString());
	}
	
	public void typeServer()
	{
		solo.enterText(0, server);
		assertTrue(solo.searchEditText(server));
	}
	
	public void connect()
	{
		solo.clickOnButton(solo.getString(R.string.connect));
		solo.assertCurrentActivity("didn't go to login", LoginPage.class);
	}
	
	public void goToServers()
	{
		solo.assertCurrentActivity("not on login", LoginPage.class);
		solo.clickOnView(solo.getView(R.id.server));
		solo.assertCurrentActivity("didn't go to Serveres", ServerChoosing.class);
	}
	
	public void  testType()
	{
		goToServers();
		
		typeServer();
		connect();
		
	}
	
	public void Delete()
	{
		goToServers();
		solo.assertCurrentActivity("didn't go to Serveres", ServerChoosing.class);
		for(int i=1; i<-1; i--)
		{
			deleteServer(i);
		}
		
	}
	
	
	
	public void testChoose()
	{
		goToServers();
		solo.assertCurrentActivity("didn't go to Serveres", ServerChoosing.class);
		for(int i=0; i<2; i++)
		{
			chooseServer(i);
		}
	}
}
