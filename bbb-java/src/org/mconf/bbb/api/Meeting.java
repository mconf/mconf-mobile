package org.mconf.bbb.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Meeting {
	protected String returncode, 
		meetingID,
		attendeePW, 
		moderatorPW,
		messageKey,
		message;
	protected boolean running,
		hasBeenForciblyEnded;
	protected Date startTime,
		endTime;
	protected int participantCount,
		moderatorCount;
	protected List<Attendee> attendees;

	public Meeting() {
		this.attendees = new ArrayList<Attendee>();
	}

	public String getReturncode() {
		return returncode;
	}

	public void setReturncode(String returncode) {
		this.returncode = returncode;
	}

	public String getMeetingID() {
		return meetingID;
	}

	public void setMeetingID(String meetingID) {
		this.meetingID = meetingID;
	}

	public String getAttendeePW() {
		return attendeePW;
	}

	public void setAttendeePW(String attendeePW) {
		this.attendeePW = attendeePW;
	}

	public String getModeratorPW() {
		return moderatorPW;
	}

	public void setModeratorPW(String moderatorPW) {
		this.moderatorPW = moderatorPW;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isHasBeenForciblyEnded() {
		return hasBeenForciblyEnded;
	}

	public void setHasBeenForciblyEnded(boolean hasBeenForciblyEnded) {
		this.hasBeenForciblyEnded = hasBeenForciblyEnded;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(int participantCount) {
		this.participantCount = participantCount;
	}

	public int getModeratorCount() {
		return moderatorCount;
	}

	public void setModeratorCount(int moderatorCount) {
		this.moderatorCount = moderatorCount;
	}

	public List<Attendee> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<Attendee> attendees) {
		this.attendees = attendees;
	}

	@Override
	public String toString() {
		return "Meeting [attendeePW=" + attendeePW + ", attendees="
				+ attendees.toString() + ", endTime=" + endTime
				+ ", hasBeenForciblyEnded=" + hasBeenForciblyEnded
				+ ", meetingID=" + meetingID + ", message=" + message
				+ ", messageKey=" + messageKey + ", moderatorCount="
				+ moderatorCount + ", moderatorPW=" + moderatorPW
				+ ", participantCount=" + participantCount
				+ ", returncode=" + returncode + ", running=" + running
				+ ", startTime=" + startTime + "]";
	}
}
