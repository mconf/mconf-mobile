package org.mconf.bbb.android;

import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.api.IMeeting;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

public class ConferenceAdapter   extends BaseAdapter {
	//list of the listeners on the meeting
	private List<IMeeting> listConferences = new ArrayList<IMeeting>();
	private Context context;

	public ConferenceAdapter(Context context) {
		this.context=context;
	}

	public void addSection(IMeeting meeting) {
		Conference conference = new Conference(meeting);

		listConferences.add(conference);
	}

	public void removeSection(IMeeting meeting){
		Conference conference = new Conference(meeting);
		if (conference != null)
			listConferences.remove(conference);
	}



	public int getCount() { 
		return listConferences.size();
	} 

	public Object getItem(int position) {
		return listConferences.get(position);
	}

	public long getItemId(int position) {
		return 0; //no id to return
	}

	public Conference getConference ( int position)
	{
		return new Conference (listConferences.get(position));
	}



	public View getView(int position, View convertView, ViewGroup viewGroup) {
		Conference entry = (Conference) listConferences.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.conference, null);
		}

		String meetingName = entry.getMeetingID();
		final TextView meetingID = (TextView) convertView.findViewById(R.id.conference_name);
		meetingID.setTextAppearance(viewGroup.getContext(), R.style.ParticipantNameStyle);
		meetingID.setText(meetingName);
		meetingID.setTag(meetingName);


		int participants = entry.getParticipantsInMeeting();
		String participantText;
		if(participants>0)
			participantText= "(" + context.getResources().getString(R.string.active)+ Integer.toString(participants) + context.getResources().getString(R.string.users)+")";
		else
			participantText= "(" + context.getResources().getString(R.string.inactive) + ")";
		final TextView meetingParticipants = (TextView) convertView.findViewById(R.id.participants);

		meetingParticipants.setText(participantText);
		meetingParticipants.setTag(meetingName);
		meetingParticipants.setTextAppearance(viewGroup.getContext(), R.style.UsersOnline);


		return convertView;
	}

	public void clearList() {
		listConferences.clear();
	}

}


