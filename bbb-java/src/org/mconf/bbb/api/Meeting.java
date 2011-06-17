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

package org.mconf.bbb.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Meeting implements IMeeting {
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

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getReturncode()
	 */
	@Override
	public String getReturncode() {
		return returncode;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setReturncode(java.lang.String)
	 */
	@Override
	public void setReturncode(String returncode) {
		this.returncode = returncode;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getMeetingID()
	 */
	@Override
	public String getMeetingID() {
		return meetingID;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setMeetingID(java.lang.String)
	 */
	@Override
	public void setMeetingID(String meetingID) {
		this.meetingID = meetingID;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getAttendeePW()
	 */
	@Override
	public String getAttendeePW() {
		return attendeePW;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setAttendeePW(java.lang.String)
	 */
	@Override
	public void setAttendeePW(String attendeePW) {
		this.attendeePW = attendeePW;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getModeratorPW()
	 */
	@Override
	public String getModeratorPW() {
		return moderatorPW;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setModeratorPW(java.lang.String)
	 */
	@Override
	public void setModeratorPW(String moderatorPW) {
		this.moderatorPW = moderatorPW;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getMessageKey()
	 */
	@Override
	public String getMessageKey() {
		return messageKey;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setMessageKey(java.lang.String)
	 */
	@Override
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setMessage(java.lang.String)
	 */
	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setRunning(boolean)
	 */
	@Override
	public void setRunning(boolean running) {
		this.running = running;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#isHasBeenForciblyEnded()
	 */
	@Override
	public boolean isHasBeenForciblyEnded() {
		return hasBeenForciblyEnded;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setHasBeenForciblyEnded(boolean)
	 */
	@Override
	public void setHasBeenForciblyEnded(boolean hasBeenForciblyEnded) {
		this.hasBeenForciblyEnded = hasBeenForciblyEnded;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startTime;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setStartTime(java.util.Date)
	 */
	@Override
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getEndTime()
	 */
	@Override
	public Date getEndTime() {
		return endTime;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setEndTime(java.util.Date)
	 */
	@Override
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getParticipantCount()
	 */
	@Override
	public int getParticipantCount() {
		return participantCount;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setParticipantCount(int)
	 */
	@Override
	public void setParticipantCount(int participantCount) {
		this.participantCount = participantCount;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getModeratorCount()
	 */
	@Override
	public int getModeratorCount() {
		return moderatorCount;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setModeratorCount(int)
	 */
	@Override
	public void setModeratorCount(int moderatorCount) {
		this.moderatorCount = moderatorCount;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#getAttendees()
	 */
	@Override
	public List<Attendee> getAttendees() {
		return attendees;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#setAttendees(java.util.List)
	 */
	@Override
	public void setAttendees(List<Attendee> attendees) {
		this.attendees = attendees;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.api.IMeeting#toString()
	 */

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
