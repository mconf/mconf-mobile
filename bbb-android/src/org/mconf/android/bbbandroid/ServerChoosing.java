package org.mconf.android.bbbandroid;

import java.util.Map.Entry;

import org.mconf.android.bbbandroid.ServerPasswordDialog.OnPasswordEntered;
import org.mconf.android.core.BigBlueButtonActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
//class where the user chooses the server where he wants to connect
public class ServerChoosing extends BigBlueButtonActivity  {
	private static final int DELETE_SERVER = Menu.FIRST;
	private static final int CHANGE_PASSWORD = Menu.FIRST + 1;
	private ListView serverListView;  
	private ServerAdapter serverAdapter = new ServerAdapter();
	private String serverURL;
	private String serverPassword;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.server_choosing);
		
		serverListView = (ListView) findViewById(R.id.servers);
		serverListView.setTextFilterEnabled(true);
		serverListView.setAdapter(serverAdapter);
		loadServers();
		registerForContextMenu(serverListView);
		
		//select server on click
		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText editTextServer = (EditText)findViewById(R.id.serverURL);
				if (editTextServer.getText().toString().length() > 0) {
					
					serverURL = editTextServer.getText().toString();
					
					if (!getPreferences().contains(serverURL)) {
						ServerPasswordDialog passwordDialog = new ServerPasswordDialog(ServerChoosing.this, "");
						passwordDialog.setOnPasswordEntered(new OnPasswordEntered() {
							
							@Override
							public void onPassword(String password) {
								serverPassword = password;
								backToLogin();
							}
						});
						passwordDialog.show();
					} else {
						serverPassword = getPreferences().getString(serverURL, "");
						backToLogin();
					}
						
				}
			}
		});

		serverListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EditText server = (EditText)findViewById(R.id.serverURL);
				String serverURL = ((Server) serverAdapter.getItem(position)).getUrl();
				server.setText(serverURL);
			}
		});
	}

	private void backToLogin() {
		while (serverURL.endsWith("/")) {
			serverURL = serverURL.substring(0, serverURL.length() - 1);
		}	

		final Intent callLogin = new Intent (LoginPage.SERVER_CHOSED);
		callLogin.putExtra("serverUrl", serverURL);
		callLogin.putExtra("serverPassword", serverPassword);
		sendBroadcast(callLogin);
		addServer(serverURL, serverPassword);
		finish(); 
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, DELETE_SERVER, 0, R.string.delete_server);
		menu.add(0, CHANGE_PASSWORD, 0, R.string.change_password);
	}
	
	//delete server on long press
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

		switch (item.getItemId()) {
		case DELETE_SERVER:
			deleteServer(((Server) serverAdapter.getItem(info.position)).getUrl());
			return true;
		case CHANGE_PASSWORD:	
			ServerPasswordDialog passwordDialog = new ServerPasswordDialog(this, ((Server) serverAdapter.getItem(info.position)).getPassword());
			passwordDialog.setOnPasswordEntered(new OnPasswordEntered() {
				
				@Override
				public void onPassword(String password) {
					serverPassword = password;
					addServer(((Server) serverAdapter.getItem(info.position)).getUrl(), serverPassword);
				}
			});
			passwordDialog.show();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public SharedPreferences getPreferences() {
		return getSharedPreferences("storedServers", MODE_PRIVATE);
	}
	
	public void loadServers() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				serverAdapter.clear();
				for (Entry<String, ?> entry : getPreferences().getAll().entrySet()) {
					serverAdapter.addSection(entry.getKey(), (String) entry.getValue());
				}
				serverAdapter.notifyDataSetChanged();
			}
		});
		addServer("http://demo.bigbluebutton.org", "03b07");
		addServer("http://143.54.85.35:8888", "");
	}

	private void addServer(final String newServer, final String serverPassword) {	
		SharedPreferences.Editor serverEditor = getPreferences().edit();
		serverEditor.putString(newServer, serverPassword);
		serverEditor.commit();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				serverAdapter.addSection(newServer, serverPassword);
				serverAdapter.notifyDataSetChanged();
			}
		});
	}


	private void deleteServer(final String server) {
		SharedPreferences.Editor serverEditor = getPreferences().edit();
		serverEditor.remove(server);
		serverEditor.commit();
		
		runOnUiThread(new Runnable() {

			@Override  
			public void run() {
				serverAdapter.removeSection(server);
				serverAdapter.notifyDataSetChanged();
			}
		});
	}

}
