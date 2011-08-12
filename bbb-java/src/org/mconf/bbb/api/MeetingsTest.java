package org.mconf.bbb.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class MeetingsTest {

//	<meetings>
//		<meeting>
//			<returncode>SUCCESS</returncode>
//			<meetingName>English 101</meetingName>
//			<meetingID>English 101</meetingID>
//			<createTime>1312994955454</createTime>
//			<attendeePW>ap</attendeePW>
//			<moderatorPW>mp</moderatorPW>
//			<running>true</running>
//			<hasBeenForciblyEnded>false</hasBeenForciblyEnded>
//			<startTime>1312994958384</startTime>
//			<endTime>0</endTime>
//			<participantCount>1</participantCount>
//			<maxUsers>20</maxUsers>
//			<moderatorCount>1</moderatorCount>
//			<attendees>
//				<attendee>
//					<userID>236</userID>
//					<fullName>fcecagno@gmail.com</fullName>
//					<role>MODERATOR</role>
//				</attendee>
//					<attendee>
//					<userID>237</userID>
//					<fullName>test@gmail.com</fullName>
//					<role>VIEWER</role>
//				</attendee>
//			</attendees>
//			<metadata>
//				<email>fcecagno@gmail.com</email>
//				<description>Test</description>
//				<meetingId>English 101</meetingId>
//			</metadata>
//			<messageKey></messageKey>
//			<message></message>
//		</meeting>
//	</meetings>	
	@Test
	public void testParseCorrect() throws UnsupportedEncodingException, DOMException, ParserConfigurationException, SAXException, IOException, ParseException {
		Meetings meetings = new Meetings();
		meetings.parse("<meetings><meeting><returncode>SUCCESS</returncode><meetingName>English 101</meetingName><meetingID>English 101</meetingID><createTime>1312994955454</createTime><attendeePW>ap</attendeePW><moderatorPW>mp</moderatorPW><running>true</running><hasBeenForciblyEnded>false</hasBeenForciblyEnded><startTime>1312994958384</startTime><endTime>0</endTime><participantCount>1</participantCount><maxUsers>20</maxUsers><moderatorCount>1</moderatorCount><attendees><attendee><userID>236</userID><fullName>fcecagno@gmail.com</fullName><role>MODERATOR</role></attendee><attendee><userID>237</userID><fullName>test@gmail.com</fullName><role>VIEWER</role></attendee></attendees><metadata><email>fcecagno@gmail.com</email><description>Test</description><meetingId>English 101</meetingId></metadata><messageKey></messageKey><message></message></meeting></meetings>");
		assertTrue(meetings.getMeetings().size() == 1);
		
		Meeting meeting = meetings.getMeetings().get(0);
			
		assertTrue(meeting.getReturncode().equals("SUCCESS"));
		assertTrue(meeting.getMeetingName().equals("English 101"));
		assertTrue(meeting.getMeetingID().equals("English 101"));
		assertTrue(meeting.getCreateTime().equals(new Date(new Long("1312994955454"))));
		assertTrue(meeting.getAttendeePW().equals("ap"));
		assertTrue(meeting.getModeratorPW().equals("mp"));
		assertFalse(meeting.isHasBeenForciblyEnded());
		assertTrue(meeting.getStartTime().equals(new Date(new Long("1312994958384"))));
		assertTrue(meeting.getEndTime().equals(new Date(new Long("0"))));
		assertTrue(meeting.getParticipantCount() == 1);
		assertTrue(meeting.getMaxUsers() == 20);
		assertTrue(meeting.getModeratorCount() == 1);
		
		assertTrue(meeting.getAttendees().size() == 2);
		assertTrue(meeting.getAttendees().get(0).getUserID().equals("236"));
		assertTrue(meeting.getAttendees().get(0).getFullName().equals("fcecagno@gmail.com"));
		assertTrue(meeting.getAttendees().get(0).getRole().equals("MODERATOR"));
		assertTrue(meeting.getAttendees().get(1).getUserID().equals("237"));
		assertTrue(meeting.getAttendees().get(1).getFullName().equals("test@gmail.com"));
		assertTrue(meeting.getAttendees().get(1).getRole().equals("VIEWER"));
		
		assertTrue(meeting.getMetadata().getEmail().equals("fcecagno@gmail.com"));
		assertTrue(meeting.getMetadata().getDescription().equals("Test"));
		assertTrue(meeting.getMetadata().getMeetingId().equals("English 101"));
		
		assertTrue(meeting.getMessageKey().isEmpty());
		assertTrue(meeting.getMessage().isEmpty());
	}
	
	@Test
	public void testParseMissingTags() throws UnsupportedEncodingException, DOMException, ParserConfigurationException, SAXException, IOException, ParseException {
		Meetings meetings = new Meetings();
		meetings.parse("<meetings><meeting><returncode>SUCCESS</returncode><meetingID>English 101</meetingID><createTime>1312994955454</createTime><attendeePW>ap</attendeePW><moderatorPW>mp</moderatorPW><running>true</running><hasBeenForciblyEnded>false</hasBeenForciblyEnded><startTime>1312994958384</startTime><endTime>0</endTime><participantCount>1</participantCount><maxUsers>20</maxUsers><moderatorCount>1</moderatorCount><attendees><attendee><userID>236</userID><fullName>fcecagno@gmail.com</fullName><role>MODERATOR</role></attendee><attendee><userID>237</userID><fullName>test@gmail.com</fullName><role>VIEWER</role></attendee></attendees><metadata><email>fcecagno@gmail.com</email><description>Test</description><meetingId>English 101</meetingId></metadata></meeting></meetings>");
		assertTrue(meetings.getMeetings().size() == 1);
		
		Meeting meeting = meetings.getMeetings().get(0);
			
		assertTrue(meeting.getReturncode().equals("SUCCESS"));
//		assertTrue(meeting.getMeetingName().equals("English 101"));
		assertTrue(meeting.getMeetingID().equals("English 101"));
		assertTrue(meeting.getCreateTime().equals(new Date(new Long("1312994955454"))));
		assertTrue(meeting.getAttendeePW().equals("ap"));
		assertTrue(meeting.getModeratorPW().equals("mp"));
		assertFalse(meeting.isHasBeenForciblyEnded());
		assertTrue(meeting.getStartTime().equals(new Date(new Long("1312994958384"))));
		assertTrue(meeting.getEndTime().equals(new Date(new Long("0"))));
		assertTrue(meeting.getParticipantCount() == 1);
		assertTrue(meeting.getMaxUsers() == 20);
		assertTrue(meeting.getModeratorCount() == 1);
		
		assertTrue(meeting.getAttendees().size() == 2);
		assertTrue(meeting.getAttendees().get(0).getUserID().equals("236"));
		assertTrue(meeting.getAttendees().get(0).getFullName().equals("fcecagno@gmail.com"));
		assertTrue(meeting.getAttendees().get(0).getRole().equals("MODERATOR"));
		assertTrue(meeting.getAttendees().get(1).getUserID().equals("237"));
		assertTrue(meeting.getAttendees().get(1).getFullName().equals("test@gmail.com"));
		assertTrue(meeting.getAttendees().get(1).getRole().equals("VIEWER"));
		
		assertTrue(meeting.getMetadata().getEmail().equals("fcecagno@gmail.com"));
		assertTrue(meeting.getMetadata().getDescription().equals("Test"));
		assertTrue(meeting.getMetadata().getMeetingId().equals("English 101"));
		
		assertTrue(meeting.getMessageKey().isEmpty());
		assertTrue(meeting.getMessage().isEmpty());
	}
	
	@Test
	public void testParseNoAttendees() throws UnsupportedEncodingException, DOMException, ParserConfigurationException, SAXException, IOException, ParseException {
		Meetings meetings = new Meetings();
		meetings.parse("<meetings><meeting><returncode>SUCCESS</returncode><meetingName>English 101</meetingName><meetingID>English 101</meetingID><createTime>1313009478450</createTime><attendeePW>ap</attendeePW><moderatorPW>mp</moderatorPW><running>false</running><hasBeenForciblyEnded>true</hasBeenForciblyEnded><startTime>1313009481417</startTime><endTime>1313010108752</endTime><participantCount>0</participantCount><maxUsers>20</maxUsers><moderatorCount>0</moderatorCount><attendees></attendees><metadata><email>ck@test</email><description>ck</description><meetingId>English 101</meetingId></metadata><messageKey></messageKey><message></message></meeting></meetings>");
	}
}
