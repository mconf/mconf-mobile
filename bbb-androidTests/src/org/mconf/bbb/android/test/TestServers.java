package org.mconf.bbb.android.test;


import java.util.ArrayList;

import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.ServerChoosing;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class TestServers extends ActivityInstrumentationTestCase2<LoginPage>  {

	private Solo solo;
	
	public TestServers() {
		super("org.mconf.bbb.android", LoginPage.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
		solo.clickOnView(solo.getView(R.id.server));
		solo.assertCurrentActivity("wrong activity", ServerChoosing.class);
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
	
	public void testConnect()
	{
		if (!solo.searchText(Common.exactly(Common.DEFAULT_SERVER)))
			solo.enterText(0, Common.DEFAULT_SERVER);
		else
			solo.clickOnText(Common.exactly(Common.DEFAULT_SERVER));
		solo.clickOnButton(solo.getString(R.string.connect));
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
	}
	
	public void testSelect() {
		ArrayList<TextView> servers = solo.getCurrentTextViews(solo.getView(R.id.servers));
		EditText editText = (EditText) solo.getView(R.id.serverURL);
		for (TextView view : servers) {
			solo.clickOnView(view);
			// wait for 2 matches of the server URL
			solo.waitForText(view.getText().toString(), 2, 0);
			assertEquals(view.getText().toString(), editText.getText().toString());
		}
	}
	
	public void testAddRemove() {
		for (int i = 0; i < 2; ++i) {
			String server = Common.DEFAULT_SERVER + "/test" + i;
			solo.enterText(0, server);
			solo.clickOnButton(solo.getString(R.string.connect));
			solo.assertCurrentActivity("wrong activity", LoginPage.class);
			assertTrue(solo.searchText(Common.exactly(server)));
			solo.clickOnView(solo.getView(R.id.server));
			solo.assertCurrentActivity("wrong activity", ServerChoosing.class);
			solo.waitForText(Common.exactly(Common.DEFAULT_SERVER + "/test" + i));
		}
		
		ArrayList<TextView> servers = solo.getCurrentTextViews(solo.getView(R.id.servers));
		for (TextView view : servers) {
			String server = view.getText().toString();
			if (!server.startsWith(Common.DEFAULT_SERVER + "/test"))
				continue;
			solo.clickLongOnView(view);
			solo.clickOnText(solo.getString(R.string.delete_server));
			assertFalse(solo.searchText(Common.exactly(server)));
		}
	}
	
}
