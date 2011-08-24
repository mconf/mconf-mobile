package org.mconf.bbb.android;

import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.users.IParticipant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MeetingInfAdapter extends BaseAdapter{

	
	private List<MeetingInf> listInformation = new ArrayList<MeetingInf>();
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listInformation.size();
	}
	
	  public boolean areAllItemsEnabled() 
      { 
              return false; 
      } 
	
	public void addSection(String title, String data) {
		MeetingInf meeting = new MeetingInf();
		meeting.setTitle(title);
		meeting.setData(data);
		meeting.setId(MeetingInf.ID+getCount());
		listInformation.add(meeting);
	}

	public void removeSection(int position) {
		MeetingInf meeting = (MeetingInf) getItem(position);
		if (meeting != null)
			listInformation.remove(meeting);
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listInformation.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return listInformation.get(position).getId();
	}
	
	public void setItemId(int position){
		listInformation.get(position).setId(MeetingInf.ID + position);
	}
	
	public String getItemTitle(int position)
	{
		return listInformation.get(position).getTitle();
	}
	
	public String getItemData(int position)
	{
		return listInformation.get(position).getData();
	}
	
	public String setItemTitle(int position, String title)
	{
		return listInformation.get(position).getTitle();
	}
	
	public String setItemData(int position, String data)
	{
		return listInformation.get(position).getData();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MeetingInf meeting = (MeetingInf) getItem(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.meeting_data, null);
		}
		TextView titleText = (TextView) convertView.findViewById(R.id.meeting_inf_title);
		TextView dataText = (TextView) convertView.findViewById(R.id.meeting_inf_data);
		
		
		titleText.setText(meeting.getTitle());
		dataText.setText(meeting.getData());
		
		return convertView;
	}

	

}
