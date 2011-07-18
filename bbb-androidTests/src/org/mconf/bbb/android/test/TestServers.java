package org.mconf.bbb.android.test;




import org.mconf.bbb.android.LoginPage;
import org.mconf.bbb.android.R;
import org.mconf.bbb.android.Server;
import org.mconf.bbb.android.ServerAdapter;
import org.mconf.bbb.android.ServerChoosing;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.EditText;


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
	public void testAddChangePassword()
	{
		Common.addServers(solo, this);

		ServerAdapter servers = Common.getServersList(solo);
		for (int i=0; i< servers.getCount(); i++) {
			String server = ((Server)servers.getItem(i)).getUrl();
			if (!server.startsWith(Common.DEFAULT_SERVER + "/test"))
				continue;
			solo.clickLongOnText(server);
			solo.clickOnText(solo.getString(R.string.change_password));
			assertTrue(solo.searchText(Common.exactly(solo.getString(R.string.server_password))));
			solo.enterText(0, Common.DEFAULT_PASSWORD+server);
			solo.clickOnButton(solo.getString(R.string.ok));
			assertTrue(solo.searchText(Common.DEFAULT_PASSWORD+server));
		}
		Common.removeServers(solo);
	}

	public void testConnect()
	{
		if (!solo.searchText(Common.exactly(Common.DEFAULT_SERVER)))
			solo.enterText(0, Common.DEFAULT_SERVER);
		else
			solo.clickOnText(Common.exactly(Common.DEFAULT_SERVER));
		solo.clickOnButton(solo.getString(R.string.connect));
		if(solo.searchText(Common.exactly(solo.getString(R.string.server_password))))
		{
			solo.enterText(0, Common.DEFAULT_PASSWORD);
			TouchUtils.clickView(this, solo.getButton("Ok"));
		}
		solo.assertCurrentActivity("wrong activity", LoginPage.class);
	}

	public void testSelect() {
		ServerAdapter servers = Common.getServersList(solo);
		EditText editText = (EditText) solo.getView(R.id.serverURL);
		for (int i=0; i< servers.getCount(); i++) {
			solo.clickOnText(Common.exactly(((Server)servers.getItem(i)).getUrl()));
			// wait for 2 matches of the server URL
			solo.waitForText(((Server)servers.getItem(i)).getUrl(), 2, 0);
			assertEquals(((Server)servers.getItem(i)).getUrl(), editText.getText().toString());
		}
	}

	public void testAddRemove() {

		Common.addServers(solo, this);

		Common.removeServers(solo);
	}
}


