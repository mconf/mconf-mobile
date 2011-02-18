package org.mconf.bbb.android;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ServerChoosing extends Activity  {
	ListView servers;
	private ArrayAdapter<String> listViewAdapter;
	SharedPreferences serverFile;
	Map<String,String> storedServers;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.server_choosing);
		listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        servers = (ListView) findViewById(R.id.servers);
        servers.setTextFilterEnabled(true);
        servers.setAdapter(listViewAdapter);
		setServerFile();
		getServers();
		
		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText text = (EditText)findViewById(R.id.serverURL);
				if (text.getText().toString().length() > 0) {
					addServer(text.getText().toString());
					Intent callLogin = new Intent (getApplicationContext(), LoginPage.class);
					callLogin.putExtra("serverURL", text.getText().toString());
					startActivity(callLogin);
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

}
