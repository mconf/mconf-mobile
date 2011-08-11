package org.mconf.bbb.android;

import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
//class where the user chooses the server where he wants to connect
public class ServerChoosing extends BigBlueButtonActivity  {

	private static final int DELETE_SERVER = 0;
	private ListView serversListView;
	private ArrayAdapter<String> listViewAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.server_choosing);
		listViewAdapter = new ArrayAdapter<String>(this, R.layout.server);
		serversListView = (ListView) findViewById(R.id.servers);
		serversListView.setTextFilterEnabled(true);
		serversListView.setAdapter(listViewAdapter);
		registerForContextMenu(serversListView);

		//select server on click
		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText editTextServer = (EditText)findViewById(R.id.serverURL);
				String serverUrl = getServerUrl(editTextServer.getText().toString());
				if (serverUrl.length() > 0) {
					
					if (getPreferences().contains(serverUrl)) {
						String serverPassword = getPreferences().getString(serverUrl, "");
						backToLogin(serverUrl, serverPassword);
					} else {
						createPasswordDialog().show();
					}
				}
			}		
		});

		serversListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EditText server = (EditText)findViewById(R.id.serverURL);
				String serverURL = serversListView.getItemAtPosition(position).toString();
				server.setText(serverURL);

			}
		});

		loadList();
	}
	
	private AlertDialog.Builder createPasswordDialog() {
		final EditText editTextPassword = new EditText(this);
		AlertDialog.Builder passwordDialog = new AlertDialog.Builder(this);
		passwordDialog.setCancelable(false);
		passwordDialog.setTitle(R.string.server_password);
		passwordDialog.setView(editTextPassword);
		passwordDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				EditText editTextServer = (EditText) findViewById(R.id.serverURL);
				String serverUrl = getServerUrl(editTextServer.getText().toString());
				String serverPassword = editTextPassword.getText().toString();
				
				addServer(serverUrl, serverPassword);
				backToLogin(serverUrl, serverPassword);
			}
		});
		return passwordDialog;
	}

	private void backToLogin(String serverUrl, String serverPassword) {
		final Intent callLogin = new Intent (LoginPage.SERVER_CHOSED);
		callLogin.putExtra("serverUrl", serverUrl);
		callLogin.putExtra("serverPassword", serverPassword);
		sendBroadcast(callLogin);
		finish(); 
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, DELETE_SERVER, 0, R.string.delete_server);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

		switch (item.getItemId()) {
			case DELETE_SERVER:
				deleteServer(serversListView.getItemAtPosition(info.position).toString());
				return true;
			}
		return super.onContextItemSelected(item);
	}

	private void loadList() {
		for (Entry<String, ?> entry : getPreferences().getAll().entrySet()) {
			addServer(entry.getKey(), (String) entry.getValue());
		}
		addServer("http://test.bigbluebutton.org", "03b07");
		addServer("http://mconfdev.inf.ufrgs.br", "03b07");
	}
	
	private SharedPreferences getPreferences() {
		return getSharedPreferences("storedServers", MODE_PRIVATE);
	}
	
	private String getServerUrl(String serverUrl) {
		while (serverUrl.endsWith("/")) {
			serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
		}
		return serverUrl;
	}
	
	private void addServer(final String serverUrl, String serverPassword)
	{
		Log.d("===============> ", "adding server " + serverUrl + " password " + serverPassword);
		
		SharedPreferences.Editor serverEditor = getPreferences().edit();
		serverEditor.putString(serverUrl, serverPassword);
		serverEditor.commit();

		if (listViewAdapter.getPosition(serverUrl) == -1) {
			runOnUiThread(new Runnable() {
	
				@Override  
				public void run() {
					listViewAdapter.add(serverUrl);
					listViewAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	public void deleteServer(final String server)
	{
		SharedPreferences.Editor serverEditor = getPreferences().edit();
		serverEditor.remove(server);
		serverEditor.commit();

		runOnUiThread(new Runnable() {

			@Override  
			public void run() {
				listViewAdapter.remove(server);
				listViewAdapter.notifyDataSetChanged();
			}
		});
	}

}
