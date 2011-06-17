package org.mconf.bbb.android;

import org.mconf.bbb.api.IMeeting;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ConferencesActivity extends BigBlueButtonActivity{
	
	protected ConferenceAdapter privateAdapter = new ConferenceAdapter(this);
	protected ConferenceAdapter publicAdapter = new ConferenceAdapter(this);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		setContentView(R.layout.conferences_list);
		
		final CustomListview privateConferences = (CustomListview) findViewById(R.id.private_rooms);
		privateConferences.setAdapter(privateAdapter);
		addPrivateMeetings();
		privateConferences.setHeight();
		registerForContextMenu(privateConferences);
		
		final CustomListview publicConferences = (CustomListview) findViewById(R.id.public_rooms);
		publicConferences.setAdapter(publicAdapter);
		addPublicMeetings();
		publicConferences.setHeight();
		registerForContextMenu(publicConferences);
		
		//addYourMeeting();
		
	}

	private void addYourMeeting(IMeeting meeting) {
		// TODO get the meetings from the portal and insert them
		Conference myConference = new Conference(meeting);
		View yourMeeting = findViewById(R.id.your_room);
		
		TextView meetingID = (TextView) yourMeeting.findViewById(R.id.conference_name);
		meetingID.setText(myConference.getMeetingID());
		
		TextView meetingParticipants = (TextView) yourMeeting.findViewById(R.id.users_online);
		int participants= myConference.getParticipantsInMeeting();
		String participantText;
		if(participants>0)
			participantText= "(" + getResources().getString(R.string.active)+ Integer.toString(participants) + getResources().getString(R.string.users)+")";
		else
			participantText= "(" + getResources().getString(R.string.inactive) + ")";
		meetingParticipants.setText(participantText);
		
		
	}

	private void addPublicMeetings() {
		// TODO get the meetings from the portal and insert them
		//publicAdapter.addSection(meeting);
		publicAdapter.notifyDataSetChanged();
		
	}

	private void addPrivateMeetings() {
		// TODO get the meetings from the portal and insert them
		//privateAdapter.addSection(meeting);
		privateAdapter.notifyDataSetChanged();
	}
	
	

}
