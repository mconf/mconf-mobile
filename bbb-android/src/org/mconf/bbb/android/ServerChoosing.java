package org.mconf.bbb.android;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ServerChoosing extends Activity  {
	private static final int DELETE_SERVER = 0;
	ListView servers;
	private ArrayAdapter<String> listViewAdapter;
	SharedPreferences serverFile;
	Map<String,String> storedServers;
	
	
	
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
		
		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText text = (EditText)findViewById(R.id.serverURL);
				if (text.getText().toString().length() > 0) {
					
					Intent callLogin = new Intent (getApplicationContext(), LoginPage.class);
					String serverURL=text.getText().toString();
					if(serverURL.charAt(serverURL.length()-1)=='/')
			             serverURL=serverURL.substring(0, serverURL.length() - 1);
					
					addServer(serverURL);
					callLogin.putExtra("serverURL", serverURL);
					startActivity(callLogin);
					finish(); 
				}
			}
		});
		
		servers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EditText text = (EditText)findViewById(R.id.serverURL);
				text.setText(servers.getItemAtPosition(position).toString());
			}
		});
		
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, DELETE_SERVER, 0, "Delete server");

	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		
		switch (item.getItemId()) {
			case DELETE_SERVER:
				System.out.println(servers.getItemAtPosition(info.position).toString());
				deleteServer(servers.getItemAtPosition(info.position).toString());
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	public SharedPreferences getServerFile() {
		return serverFile;
	}

	public void setServerFile() {
		if(this.getSharedPreferences("storedServers", MODE_PRIVATE)!=null)
			this.serverFile = this.getSharedPreferences("storedServers", MODE_PRIVATE);
		else
		{
			SharedPreferences.Editor serverEditor = serverFile.edit();
			serverEditor.commit(); 
			this.serverFile = this.getSharedPreferences("storedServers", MODE_PRIVATE);
		}
		this.storedServers = (Map<String, String>) serverFile.getAll();
	}
	
	public void getServers()
	{
		
		for(String server:storedServers.values())
			listViewAdapter.add(server);
		
	}
	
	public void addServer(String newServer)
	{
		SharedPreferences.Editor serverEditor = serverFile.edit();
		if(!serverFile.contains(newServer))
		{
			serverEditor.putString(newServer, newServer);
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
