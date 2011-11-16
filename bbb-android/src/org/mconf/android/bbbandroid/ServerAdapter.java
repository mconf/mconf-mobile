package org.mconf.android.bbbandroid;

import java.util.ArrayList;
import java.util.List;

import org.mconf.android.core.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ServerAdapter extends BaseAdapter{

	private static int selectedPosition =-1;
	
	private List<Server> listServer = new ArrayList<Server>();


	public void addSection(Server server) {

		if(server!=null)
			listServer.add(server);
	}

	public void removeSection(Server server) {
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
		TextView idText =(TextView) convertView.findViewById(R.id.serverID); 
		TextView urlText = (TextView) convertView.findViewById(R.id.serverUrl);
		TextView passwordText = (TextView) convertView.findViewById(R.id.serverSalt);
		TextView versionText = (TextView) convertView.findViewById(R.id.serverVersion);
		ImageView serverStatus = (ImageView) convertView.findViewById(R.id.serverStatusIcon);

		
		idText.setText(server.getId());
		urlText.setText(server.getUrl());
		passwordText.setText(server.getPassword());
		versionText.setText(server.getVersion());
		
		switch(server.getStatus())
		{
			case Server.SERVER_UP:
				serverStatus.setImageDrawable(parent.getContext().getResources().getDrawable(R.drawable.serverup));
				break;
			case Server.SERVER_DOWN:
				serverStatus.setImageDrawable(parent.getContext().getResources().getDrawable(R.drawable.serverdown));
				break;
			case Server.SERVER_TESTING:
				serverStatus.setImageDrawable(parent.getContext().getResources().getDrawable(R.drawable.testing));
				break;
			case Server.SERVER_UNKNOWN:
				serverStatus.setImageDrawable(parent.getContext().getResources().getDrawable(R.drawable.unknown));
				break;
		}
		
		// change the row color based on selected state
        if(selectedPosition == position){
        	convertView.setBackgroundColor(Color.argb(128,21, 193, 253));
        }else{
        	convertView.setBackgroundResource(R.color.background);
        }
		return convertView;
	}

	public void clear() {
		listServer.clear();
	}

	public void setSelectedPosition(int selectedPosition) {
		ServerAdapter.selectedPosition = selectedPosition;
		notifyDataSetChanged();
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}
	
	

}
