package org.mconf.bbb.api;

import java.util.Date;
import java.util.List;

public interface IMeeting {

	public abstract String getReturncode();

	public abstract void setReturncode(String returncode);

	public abstract String getMeetingID();

	public abstract void setMeetingID(String meetingID);

	public abstract String getAttendeePW();

	public abstract void setAttendeePW(String attendeePW);

	public abstract String getModeratorPW();

	public abstract void setModeratorPW(String moderatorPW);

	public abstract String getMessageKey();

	public abstract void setMessageKey(String messageKey);

	public abstract String getMessage();

	public abstract void setMessage(String message);

	public abstract boolean isRunning();

	public abstract void setRunning(boolean running);

	public abstract boolean isHasBeenForciblyEnded();

	public abstract void setHasBeenForciblyEnded(boolean hasBeenForciblyEnded);

	public abstract Date getStartTime();

	public abstract void setStartTime(Date startTime);

	public abstract Date getEndTime();

	public abstract void setEndTime(Date endTime);

	public abstract int getParticipantCount();

	public abstract void setParticipantCount(int participantCount);

	public abstract int getModeratorCount();

	public abstract void setModeratorCount(int moderatorCount);

	public abstract List<Attendee> getAttendees();

	public abstract void setAttendees(List<Attendee> attendees);

	public abstract String toString();

}