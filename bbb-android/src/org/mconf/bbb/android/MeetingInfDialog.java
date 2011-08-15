/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb.android;


import java.util.Date;
import java.util.zip.Inflater;

import org.mconf.bbb.api.JoinService;
import org.mconf.bbb.api.JoinedMeeting;
import org.mconf.bbb.api.Meeting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

public class MeetingInfDialog extends Dialog implements OnClickListener{

	public static final int ROW_HEIGHT = 60;
	
	private MeetingInfAdapter meetingAdapter = new MeetingInfAdapter();
	private String meetingID;
	private String message;
	private Date startTime;
	private int participantCount;
	private int moderatorCount;
	
	private Button ok;
	private Context context;
	
	public MeetingInfDialog(Context context) {
		
		super(context);
		this.context=context;
		setMeetingID();
		setMessage();
		setStartTime();
		setParticipantCount();
		setModeratorCount();  
		
		setTitle(R.string.meeting_information);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		

		setCancelable(true);
		setContentView(R.layout.meeting_inf_dialog);
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
		
		
 
		RelativeLayout relative = (RelativeLayout)findViewById(R.id.meeting_information);
		CustomListview meetingInfList =(CustomListview)findViewById(R.id.meeting_infs_list);
		meetingInfList.setAdapter(meetingAdapter);
		
		meetingAdapter.addSection(context.getResources().getString(R.string.meeting_id), meetingID);
		meetingAdapter.addSection(context.getResources().getString(R.string.meeting_message), message);
		meetingAdapter.addSection(context.getResources().getString(R.string.start_time), startTime.toLocaleString());
		meetingAdapter.addSection(context.getResources().getString(R.string.moderator_count), Integer.toString(moderatorCount));
		meetingAdapter.addSection(context.getResources().getString(R.string.participant_count), Integer.toString(participantCount));
		
		
		ViewGroup.LayoutParams  params = relative.getLayoutParams();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT, context.getResources().getDisplayMetrics());
		params.height= (meetingAdapter.getCount()+1)*px+(meetingInfList.getDividerHeight() * (meetingAdapter.getCount() - 1));
		relative.setLayoutParams(params);
		relative.requestLayout();  
		
	}
	
	@Override
	public void onClick(View v) {
		if(v==ok)
			dismiss();
		
	}


	public String getMeetingID() {
		return meetingID;
	}

	public Meeting getJoined()
	{
		JoinService joined = ((BigBlueButton) context.getApplicationContext()).getHandler().getJoinService();
		String meetingID= joined.getJoinedMeeting().getMeetingID();
		return joined.getMeetingById(meetingID);
		
		
	}
	public void setMeetingID() {
		
		this.meetingID=getJoined().getMeetingID();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage() {
		this.message = getJoined().getMessage();
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime() {
		this.startTime = getJoined().getStartTime();
		if(startTime==null)
			System.out.println("damn");
	}

	public int getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount() {
		this.participantCount = ((BigBlueButton) context.getApplicationContext()).getHandler().getUsersModule().getParticipantCount();
	}

	public int getModeratorCount() {
		return moderatorCount;
	}

	public void setModeratorCount() {
		this.moderatorCount = ((BigBlueButton) context.getApplicationContext()).getHandler().getUsersModule().getModeratorCount();
	}

	

	
}