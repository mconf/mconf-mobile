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

package org.mconf.android.core;


import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.api.JoinedMeeting;
import org.mconf.bbb.api.Meeting;

import android.app.Dialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MeetingInfDialog extends Dialog {

	public static final int ROW_HEIGHT = 60;
	
	private MeetingInfAdapter meetingAdapter = new MeetingInfAdapter();
	
	public MeetingInfDialog(Context context) {
		super(context);
		
		setTitle(R.string.meeting_information);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		
		setCancelable(true);
		setContentView(R.layout.meeting_inf_dialog);
		Button buttonOk = (Button) findViewById(R.id.ok);
		buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
 
		BigBlueButtonClient bigbluebutton = ((BigBlueButton) getContext().getApplicationContext()).getHandler();
		
		RelativeLayout relative = (RelativeLayout)findViewById(R.id.meeting_information);
		CustomListview meetingInfList = (CustomListview)findViewById(R.id.meeting_infs_list);
		meetingInfList.setAdapter(meetingAdapter);
		
		JoinedMeeting joinedMeeting = bigbluebutton.getJoinService().getJoinedMeeting();
		if (joinedMeeting != null) {
			meetingAdapter.addSection(context.getResources().getString(R.string.server), bigbluebutton.getJoinService().getServerUrl());
			meetingAdapter.addSection(context.getResources().getString(R.string.meeting_id), joinedMeeting.getConfname());
			Meeting meeting = bigbluebutton.getJoinService().getMeetingByName(joinedMeeting.getConfname());
			if (meeting != null) {
				if (meeting.getMessage() != null
						&& meeting.getMessage().length() > 0)
					meetingAdapter.addSection(context.getResources().getString(R.string.meeting_message), meeting.getMessage());
				if (meeting.getStartTime() != null)
					meetingAdapter.addSection(context.getResources().getString(R.string.start_time), meeting.getStartTime().toLocaleString());
			}
			meetingAdapter.addSection(context.getResources().getString(R.string.moderator_count), Integer.toString(bigbluebutton.getUsersModule().getModeratorCount()));
			meetingAdapter.addSection(context.getResources().getString(R.string.participant_count), Integer.toString(bigbluebutton.getUsersModule().getParticipantCount()));
		}
		
		ViewGroup.LayoutParams  params = relative.getLayoutParams();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT, context.getResources().getDisplayMetrics());
		params.height= (meetingAdapter.getCount()+1)*px+(meetingInfList.getDividerHeight() * (meetingAdapter.getCount() - 1));
		relative.setLayoutParams(params);
		relative.requestLayout();  
		
	}
}