package org.mconf.android.bbbandroid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServerAdapter extends BaseAdapter{


	private List<Server> listServer = new ArrayList<Server>();


	public void addSection(String url, String password) {
		Server server = getItemByUrl(url);
		if (server == null) {
			server = new Server();
			server.setUrl(url);
			server.setPassword(password);
			listServer.add(server);
		} else {
			server.setPassword(password);
		}
	}

	public void removeSection(String url) {
		Server server = (Server) getItemByUrl(url);
		if (server != null)
			listServer.remove(server);
	}

	public Server getItemByUrl(String url) {

		for(Server server : listServer)
		{
			if(server!=null)
				if(server.getUrl().equals(url))
					return server;
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listServer.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listServer.get(position);
	}


	public String getUrl(int position) {
		// TODO Auto-generated method stub
		return listServer.get(position).getUrl();
	}

	public String getPassword(int position)
	{
		return listServer.get(position).getPassword();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Server server = (Server) getItem(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.server, null);
		}
		TextView urlText = (TextView) convertView.findViewById(R.id.serverUrl);
		TextView passwordText = (TextView) convertView.findViewById(R.id.password);


		urlText.setText(server.getUrl());
		passwordText.setText(server.getPassword());

		return convertView;
	}

	public void clear() {
		listServer.clear();
	}

}
