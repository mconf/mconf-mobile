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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Meetings {

	private static final Logger log = LoggerFactory.getLogger(Meetings.class);

	private List<Meeting> meetings = new ArrayList<Meeting>();

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

	public List<Meeting> getMeetings() {
		return meetings;
	}

	public Meetings() {

	}

	/*
	 * bbb.getMeetings()
	 * 
	 * <meetings>
	 * 	<meeting>
	 * 		<returncode>SUCCESS</returncode>
	 * 		<meetingID>Minha sala's meeting</meetingID>
	 * 		<attendeePW>ap</attendeePW>
	 * 		<moderatorPW>mp</moderatorPW>
	 * 		<running>true</running>
	 * 		<hasBeenForciblyEnded>false</hasBeenForciblyEnded>
	 * 		<startTime>Wed Jan 26 14:57:24 UTC 2011</startTime>
	 * 		<endTime>null</endTime>
	 * 		<participantCount>1</participantCount>
	 * 		<moderatorCount>1</moderatorCount>
	 * 		<attendees>
	 * 			<attendee>
	 * 				<userID>ilf7tbt6b6lm</userID>
	 * 				<fullName>Minha sala</fullName>
	 * 				<role>MODERATOR</role>
	 * 			</attendee>
	 * 		</attendees>
	 * 		<messageKey></messageKey>
	 * 		<message></message>
	 * 	</meeting>
	 * 	<meeting>
	 * 		<returncode>SUCCESS</returncode>
	 * 		<meetingID>Demo Meeting</meetingID>
	 * 		<attendeePW>ap</attendeePW>
	 * 		<moderatorPW>mp</moderatorPW>
	 * 		<running>true</running>
	 * 		<hasBeenForciblyEnded>false</hasBeenForciblyEnded>
	 * 		<startTime>Wed Jan 26 14:54:01 UTC 2011</startTime>
	 * 		<endTime>null</endTime>
	 * 		<participantCount>1</participantCount>
	 * 		<moderatorCount>1</moderatorCount>
	 * 		<attendees>
	 * 			<attendee>
	 * 				<userID>jgrnbt7di7aj</userID>
	 * 				<fullName>Felipe</fullName>
	 * 				<role>MODERATOR</role>
	 * 			</attendee>
	 * 		</attendees>
	 * 		<messageKey></messageKey>
	 * 		<message></message>
	 * 	</meeting>
	 * </meetings>
	 */
	public boolean parse(String str) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException, DOMException, ParseException {
		meetings.clear();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));
		doc.getDocumentElement().normalize();

		NodeList nodeMeetings = doc.getElementsByTagName("meeting");
		// if nodeMeetings.getLength() == 0 and the conference is on, probably the "salt" is wrong
		log.debug("parsing: {}", str);
		log.debug("nodeMeetings.getLength() = {}", nodeMeetings.getLength());
		for (int i = 0; i < nodeMeetings.getLength(); ++i) {
			Element elementMeeting = (Element) nodeMeetings.item(i);

			DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.UK);
			//used Locale to avoid unparseable date exception. apparently the locale doesn't affect the time
			Meeting meeting = new Meeting(); 
 
			meeting.setReturncode(elementMeeting.getElementsByTagName("returncode").item(0).getFirstChild().getNodeValue());

			if (!meeting.getReturncode().equals("SUCCESS"))
				continue;

			meeting.setMeetingID(elementMeeting.getElementsByTagName("meetingID").item(0).getFirstChild().getNodeValue());
			meeting.setAttendeePW(elementMeeting.getElementsByTagName("attendeePW").item(0).getFirstChild().getNodeValue());
			meeting.setModeratorPW(elementMeeting.getElementsByTagName("moderatorPW").item(0).getFirstChild().getNodeValue());
			meeting.setRunning(Boolean.parseBoolean(elementMeeting.getElementsByTagName("running").item(0).getFirstChild().getNodeValue()));
			meeting.setHasBeenForciblyEnded(Boolean.parseBoolean(elementMeeting.getElementsByTagName("hasBeenForciblyEnded").item(0).getFirstChild().getNodeValue()));
			// TODO dates parsing

			//removed "UTC" due to android bug http://code.google.com/p/android/issues/detail?id=14963
			String startTime = elementMeeting.getElementsByTagName("startTime").item(0).getFirstChild().getNodeValue();
			if (!startTime.equals("null"))
			{
				String timezone = startTime.substring(20, 24);
				log.debug("timezone{}", timezone);
				//startTime = startTime.replace(timezone, TimeZone.getTimeZone(timezone).getDisplayName());
				startTime=startTime.replace(timezone, "");
				meeting.setStartTime(dateFormat.parse(startTime));
				log.debug("StartTimeOK");
			}

			String endTime = elementMeeting.getElementsByTagName("endTime").item(0).getFirstChild().getNodeValue();
			if (!endTime.equals("null"))
			{	
				String timezone = endTime.substring(20, 24);
				//endTime = endTime.replace(timezone, TimeZone.getTimeZone(timezone).getDisplayName(Locale.getDefault()));
				endTime=endTime.replace(timezone, "");
				meeting.setEndTime(dateFormat.parse(endTime));
				log.debug("EndTimeOK");
			}

			meeting.setParticipantCount(Integer.parseInt(elementMeeting.getElementsByTagName("participantCount").item(0).getFirstChild().getNodeValue()));
			meeting.setModeratorCount(Integer.parseInt(elementMeeting.getElementsByTagName("moderatorCount").item(0).getFirstChild().getNodeValue()));

			NodeList nodeAttendees = elementMeeting.getElementsByTagName("attendees");

			if (meeting.getParticipantCount() + meeting.getModeratorCount() > 0)
				for (int j = 0; j < nodeAttendees.getLength(); ++j) {
					Element elementAttendee = (Element) nodeAttendees.item(j);

					Attendee attendee = new Attendee();

					attendee.setUserID(elementAttendee.getElementsByTagName("userID").item(0).getFirstChild().getNodeValue());
					attendee.setFullName(elementAttendee.getElementsByTagName("fullName").item(0).getFirstChild().getNodeValue());
					attendee.setRole(elementAttendee.getElementsByTagName("role").item(0).getFirstChild().getNodeValue());

					meeting.getAttendees().add(attendee);
				}

			meetings.add(meeting);				
		}

		return true;
	}

	@Override
	public String toString() {
		return "meetings=" + meetings;
	}

}
