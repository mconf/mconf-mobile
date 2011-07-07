package org.mconf.bbb.android;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.Toast;
//class where the user chooses the server where he wants to connect
public class ServerChoosing extends BigBlueButtonActivity  {
	private static final int DELETE_SERVER = 0;
	private static final int CHANGE_PASSWORD = 1;
	public static final String PASSWORD_INPUTED ="org.mconf.bbb.android.Client.PASSWORD_INPUTED";
	ListView servers;
	private ArrayAdapter<String> listViewAdapter;
	SharedPreferences serverFile;
	Map<String,String> storedServers;
	Context context = this;
	String serverURL;
	String serverPassword;
	
	private BroadcastReceiver passwordInputed = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			Bundle extras = intent.getExtras();
			serverPassword= extras.getString("serverPassword");
			callLogin();
			
		} 
	};
	
	
	public String getServerPassword() {
		return serverPassword;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.server_choosing);
		listViewAdapter = new ArrayAdapter<String>(this, R.layout.server);
		servers = (ListView) findViewById(R.id.servers);
		servers.setTextFilterEnabled(true);
		servers.setAdapter(listViewAdapter);
		setServerFile();
		getServers();
		registerForContextMenu(servers);
		IntentFilter filter = new IntentFilter(ServerChoosing.PASSWORD_INPUTED);
		registerReceiver(passwordInputed, filter);
		
		
		
		//select server on click
		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText server = (EditText)findViewById(R.id.serverURL);
				if (server.getText().toString().length() > 0) {
					
					serverURL=server.getText().toString();

					

					if(storedServers.get(serverURL)==null || storedServers.get(serverURL)=="")
					{
						new ServerPasswordDialog(context).show();

					}

					else
						callLogin();
				}
			}
		});

		servers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EditText server = (EditText)findViewById(R.id.serverURL);
				String serverURL = servers.getItemAtPosition(position).toString();
				server.setText(serverURL);

			}
		});


	}
	
	

	private void callLogin()
	{
		getBigBlueButton().getJoinService().setSalt(serverPassword);
		
		while (serverURL.endsWith("/")) {
			serverURL = serverURL.substring(0, serverURL.length() - 1);
		}	

		final Intent callLogin = new Intent (LoginPage.SERVER_CHOSED);
		callLogin.putExtra("serverURL", serverURL);
		sendBroadcast(callLogin);
		finish(); 
		addServer(serverURL, serverPassword);
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
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

		switch (item.getItemId()) {
		case DELETE_SERVER:
			System.out.println(servers.getItemAtPosition(info.position).toString());
			deleteServer(servers.getItemAtPosition(info.position).toString());
			return true;
		case CHANGE_PASSWORD:	
			new ServerPasswordDialog(context).show();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public SharedPreferences getServerFile() {
		return serverFile;
	}
	//the servers are stored on a SharedPreferences file, private to the program
	@SuppressWarnings("unchecked")
	public void setServerFile() {
		if(this.getSharedPreferences("storedServers", MODE_PRIVATE)!=null)
			this.serverFile = this.getSharedPreferences("storedServers", MODE_PRIVATE);
		else {
			SharedPreferences.Editor serverEditor = serverFile.edit();
			serverEditor.commit(); 
			this.serverFile = this.getSharedPreferences("storedServers", MODE_PRIVATE);
		}
		// always insert the prav servers to the list
		addServer("http://mconf.inf.ufrgs.br", "helloPassword");//\TODO add the right password
		//		addServer("http://mconfdev.inf.ufrgs.br");
		this.storedServers = (Map<String, String>) serverFile.getAll();
	}

	public void getServers()
	{

		for(String server:storedServers.keySet())
			listViewAdapter.add(server);

	}

	public void addServer(String newServer, String serverPassword)
	{
		SharedPreferences.Editor serverEditor = serverFile.edit();
		if(!serverFile.contains(newServer))
		{
			serverEditor.putString(newServer, serverPassword);
			serverEditor.commit();
		}
	}


	public void deleteServer(final String server)
	{
		SharedPreferences.Editor serverEditor = serverFile.edit();
		if(serverFile.contains(server))
		{
			serverEditor.remove(server);
			serverEditor.commit();
			System.out.println("deleted");
			runOnUiThread(new Runnable() {

				@Override  
				public void run() {
					listViewAdapter.remove(server);
					listViewAdapter.notifyDataSetChanged();
					setServerFile();

				}
			}
			);
		}

	}

}
