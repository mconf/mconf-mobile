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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Meeting {
	protected String returncode, 
		meetingName,
		meetingID,
		attendeePW, 
		moderatorPW,
		messageKey,
		message;
	protected boolean running,
		hasBeenForciblyEnded;
	protected Date startTime,
		endTime,
		createTime;
	protected int participantCount,
		moderatorCount,
		maxUsers;
	protected List<Attendee> attendees = new ArrayList<Attendee>();
	protected Metadata metadata = new Metadata();

	public Meeting() {
	}

//	<meetings>
//	<meeting>
//		<returncode>SUCCESS</returncode>
//		<meetingName>English 101</meetingName>
//		<meetingID>English 101</meetingID>
//		<createTime>1312994955454</createTime>
//		<attendeePW>ap</attendeePW>
//		<moderatorPW>mp</moderatorPW>
//		<running>true</running>
//		<hasBeenForciblyEnded>false</hasBeenForciblyEnded>
//		<startTime>1312994958384</startTime>
//		<endTime>0</endTime>
//		<participantCount>1</participantCount>
//		<maxUsers>20</maxUsers>
//		<moderatorCount>1</moderatorCount>
//		<attendees>
//			<attendee>
//				<userID>236</userID>
//				<fullName>fcecagno@gmail.com</fullName>
//				<role>MODERATOR</role>
//			</attendee>
//		</attendees>
//		<metadata>
//			<email>fcecagno@gmail.com</email>
//			<description>Test</description>
//			<meetingId>English 101</meetingId>
//		</metadata>
//		<messageKey></messageKey>
//		<message></message>
//	</meeting>
//</meetings>	
	public boolean parse(Element elementMeeting) {
		returncode = ParserUtils.getNodeValue(elementMeeting, "returncode");
		messageKey = ParserUtils.getNodeValue(elementMeeting, "messageKey");
		message = ParserUtils.getNodeValue(elementMeeting, "message");

		if (!returncode.equals("SUCCESS"))
			return false;
		
		meetingName = ParserUtils.getNodeValue(elementMeeting, "meetingName");
		meetingID = ParserUtils.getNodeValue(elementMeeting, "meetingID");
		createTime = new Date(Long.parseLong(ParserUtils.getNodeValue(elementMeeting, "createTime", true)));
		attendeePW = ParserUtils.getNodeValue(elementMeeting, "attendeePW");
		moderatorPW = ParserUtils.getNodeValue(elementMeeting, "moderatorPW");
		running = Boolean.parseBoolean(ParserUtils.getNodeValue(elementMeeting, "running", true));
		hasBeenForciblyEnded = Boolean.parseBoolean(ParserUtils.getNodeValue(elementMeeting, "hasBeenForciblyEnded", true));
		try {
			startTime = new Date(Long.parseLong(ParserUtils.getNodeValue(elementMeeting, "startTime", true)));
			endTime = new Date(Long.parseLong(ParserUtils.getNodeValue(elementMeeting, "endTime", true)));
		} catch (Exception e) {

		}
		try {
			startTime = parseDate(ParserUtils.getNodeValue(elementMeeting, "startTime"));
			endTime = parseDate(ParserUtils.getNodeValue(elementMeeting, "endTime"));
		} catch (Exception e) {
			
		}
		participantCount = Integer.parseInt(ParserUtils.getNodeValue(elementMeeting, "participantCount", true));
		maxUsers = Integer.parseInt(ParserUtils.getNodeValue(elementMeeting, "maxUsers", true));
		moderatorCount = Integer.parseInt(ParserUtils.getNodeValue(elementMeeting, "moderatorCount", true));

		NodeList nodeAttendees = elementMeeting.getElementsByTagName("attendee");
		for (int i = 0; i < nodeAttendees.getLength(); ++i) {
			Attendee attendee = new Attendee();
			if (attendee.parse((Element) nodeAttendees.item(i))) {
				attendees.add(attendee);
			}
		}

		NodeList nodeMetadata = elementMeeting.getElementsByTagName("metadata");
		if (nodeMetadata.getLength() > 0)
			metadata.parse((Element) nodeMetadata.item(0));

		return true;
	}
	
	private Date parseDate(String date) {
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.CANADA);
		try {
			date = date.replace(date.substring(20, 24), "");
			return dateFormat.parse(date);
		} catch (Exception e) {
			return new Date();
		}
	}
	
	public String getReturncode() {
		return returncode;
	}

	public String getMeetingID() {
		return meetingID;
	}

	public String getAttendeePW() {
		return attendeePW;
	}

	public String getModeratorPW() {
		return moderatorPW;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public String getMessage() {
		return message;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isHasBeenForciblyEnded() {
		return hasBeenForciblyEnded;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public int getParticipantCount() {
		return participantCount;
	}

	public int getModeratorCount() {
		return moderatorCount;
	}

	public List<Attendee> getAttendees() {
		return attendees;
	}

	public Metadata getMetadata() {
		return metadata;
	}
	
	public String getMeetingName() {
		return meetingName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public int getMaxUsers() {
		return maxUsers;
	}

	public void setParticipantCount(int participantCount) {
		this.participantCount = participantCount;
	}

	public void setModeratorCount(int moderatorCount) {
		this.moderatorCount = moderatorCount;
	}

	@Override
	public String toString() {
		return "Meeting \n     attendeePW=" + attendeePW + "\n     attendees="
				+ attendees + "\n     createTime=" + createTime
				+ "\n     endTime=" + endTime + "\n     hasBeenForciblyEnded="
				+ hasBeenForciblyEnded + "\n     maxUsers=" + maxUsers
				+ "\n     meetingID=" + meetingID + "\n     meetingName="
				+ meetingName + "\n     message=" + message
				+ "\n     messageKey=" + messageKey + "\n     metadata="
				+ metadata + "\n     moderatorCount=" + moderatorCount
				+ "\n     moderatorPW=" + moderatorPW
				+ "\n     participantCount=" + participantCount
				+ "\n     returncode=" + returncode + "\n     running="
				+ running + "\n     startTime=" + startTime;
	}

}
