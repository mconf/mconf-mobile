package org.mconf.bbb.android;

import org.mconf.bbb.api.IMeeting;
import org.mconf.bbb.api.Meeting;
import org.mconf.bbb.listeners.IListener;

public class Conference extends Meeting{
	
	public Conference (IMeeting meeting)
	{
		this.setMeetingID(meeting.getMeetingID());
		this.setMessage(meeting.getMessage());
		this.setModeratorCount(meeting.getModeratorCount());
		this.setParticipantCount(meeting.getParticipantCount());
		this.setStartTime(meeting.getStartTime());
	}
	
	public int getParticipantsInMeeting()
	{
		return this.getModeratorCount()+this.getParticipantCount();
	}
	
	


}
