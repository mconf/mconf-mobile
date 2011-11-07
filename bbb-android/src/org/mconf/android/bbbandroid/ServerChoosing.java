package org.mconf.android.bbbandroid;

import java.util.Map.Entry;

import org.mconf.android.bbbandroid.NewServerDialog.OnInformationEntered;
import org.mconf.android.core.BigBlueButtonActivity;
import org.mconf.android.core.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

//class where the user chooses the server where he wants to connect
public class ServerChoosing extends BigBlueButtonActivity  {
	private static final int DELETE_SERVER = Menu.FIRST;
	private static final int EDIT_SERVER = Menu.FIRST + 1;
	private static final int TEST_SERVER = Menu.FIRST + 2;
	private ListView serverListView;  
	private ServerAdapter serverAdapter = new ServerAdapter();

	private Server focusedServer;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.server_choosing);
		
		serverListView = (ListView) findViewById(R.id.servers);
		serverListView.setTextFilterEnabled(true);
		serverListView.setAdapter(serverAdapter);
		serverListView.setFocusableInTouchMode(true);
		loadServers();
		registerForContextMenu(serverListView);
		
		Button newServer = (Button) findViewById(R.id.new_server);
		newServer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				NewServerDialog newServerDialog = new NewServerDialog(ServerChoosing.this, "", "", "");
				
				newServerDialog.setOnInformationEntered(new OnInformationEntered() {
					
					@Override
					public void onInformation(String ID, String Url, String password) {
						Server server = new Server(ID, Url, password);
						
						server.setStatus(Server.SERVER_UNKNOWN);
						addServer(server);
					}
				});
				newServerDialog.show();
			}
		});
		
		
		
		Button editServer = (Button) findViewById(R.id.edit);
		editServer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				focusedServer = serverAdapter.getServer(serverListView.getFocusedChild().getId());
				serverEdition(focusedServer);
			}
		});
		
		//select server on click
		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				focusedServer = serverAdapter.getServer(serverListView.getFocusedChild().getId());
				
				backToLogin();
		
			}
		});	
	}

	private void serverEdition(final Server server)
	{
		
		NewServerDialog editServerDialog = new NewServerDialog(ServerChoosing.this, server.getId(), server.getUrl(), server.getPassword());
		
		editServerDialog.setOnInformationEntered(new OnInformationEntered() {
			
			@Override
			public void onInformation(String ID, String url, String password) {
				deleteServer(server);
				if(password.length()>1)
					server.setPassword(password);
				if(url.length()>1)
					server.setUrl(url);
				if(ID.length()>1)
					server.setId(ID);
				server.setStatus(Server.SERVER_UNKNOWN);
				
				addServer(server);
			}
		});
		editServerDialog.show();
	}
	
	private void backToLogin() {
		while (focusedServer.getUrl().endsWith("/")) {
			focusedServer.setUrl(focusedServer.getUrl().substring(0, focusedServer.getUrl().length() - 1));
		}	

		final Intent callLogin = new Intent (LoginPage.SERVER_CHOSED);
		callLogin.putExtra("serverUrl", focusedServer.getUrl());
		callLogin.putExtra("serverPassword", focusedServer.getPassword());
		sendBroadcast(callLogin);
		addServer(focusedServer);
		finish(); 
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, DELETE_SERVER, 0, R.string.delete_server);
		menu.add(0, EDIT_SERVER, 0, R.string.edit_server);
		menu.add(0, TEST_SERVER, 0, R.string.test_server);
	}
	
	//delete server on long press
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

		switch (item.getItemId()) {
		case DELETE_SERVER:
			deleteServer(((Server) serverAdapter.getItem(info.position)));
			return true;
		case EDIT_SERVER:
			serverEdition(((Server) serverAdapter.getItem(info.position)));
			return true;
		
		case TEST_SERVER:
			final Server server = ((Server) serverAdapter.getItem(info.position));
			server.setStatus(Server.SERVER_TESTING);
			serverAdapter.notifyDataSetChanged();
			new AsyncTask<Integer, Integer, Boolean>() {
				
				@Override
				protected Boolean doInBackground(Integer... params) {
					return getBigBlueButton().isBigBlueButtonServer(server.getUrl());
				}
				
				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					if (result == true)
						server.setStatus(Server.SERVER_UP);
					else
						server.setStatus(Server.SERVER_DOWN);
					serverAdapter.notifyDataSetChanged();
				}
			}.execute();
			
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
					String ID= entry.getKey();
					String[] temp = ((String) entry.getValue()).split("@");
					String Url = temp[0];
					String password = temp[1];
					Server loadedServer = new Server(ID, Url, password);
					loadedServer.setStatus(Server.SERVER_UNKNOWN);
					
					serverAdapter.addSection(loadedServer);
				}
				serverAdapter.notifyDataSetChanged();
			}
		});
		Server defaultServer1=new Server("Demo BBB", "http://demo.bigbluebutton.org", "03b07");
		addServer(defaultServer1);
		Server defaultServer2=new Server("Mconf", "http://mconf.org:8888", "");
		addServer(defaultServer2);
	}

	
	
	private void addServer(final Server newServer) {	
		SharedPreferences.Editor serverEditor = getPreferences().edit();
		serverEditor.putString(newServer.getId(), newServer.getUrl()+"@"+newServer.getPassword());
		serverEditor.commit();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				serverAdapter.addSection(newServer);
				serverAdapter.notifyDataSetChanged();
			}
		});
	}


	private void deleteServer(final Server server) {
		SharedPreferences.Editor serverEditor = getPreferences().edit();
		serverEditor.remove(server.getId());
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
