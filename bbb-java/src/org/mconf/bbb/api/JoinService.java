package org.mconf.bbb.api;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinService {
	private static final Logger log = LoggerFactory.getLogger(JoinService.class);

	private String serverUrl;
	private Meetings meetings = new Meetings();
	private JoinedMeeting joinedMeeting = null;
	
	public JoinedMeeting getJoinedMeeting() {
		return joinedMeeting;
	}
	
	public void resetJoinedMeeting()
	{
		joinedMeeting=null;
	}

	public List<Meeting> getMeetings() {
		return meetings.getMeetings();
	}
	
	public String getServerUrl() {
		return serverUrl;
	}
	
	public Meeting getMeetingById(String meetingID)
	{
		for(Meeting meeting:getMeetings())
		{
			if(meeting.getMeetingID().equals(meetingID))
				return meeting;
		}
		return null;
	}

	public static final int E_OK = 0;
	public static final int E_LOAD_HTTP_REQUEST = 1;
	public static final int E_LOAD_PARSE_MEETINGS = 2;
	
	public int load(String serverUrl) {
		while (serverUrl.endsWith("/")) {
			serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
		}
		
		this.serverUrl = serverUrl;
		
		String getMeetingsUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?action=getMeetings";
		String strMeetings = null;
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(getMeetingsUrl);
			client.executeMethod(method);
			strMeetings = method.getResponseBodyAsString().trim();
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Can't connect to {}", serverUrl);
			return E_LOAD_HTTP_REQUEST;
		}
		
		try {
			meetings.parse(strMeetings);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("This server doesn't support mobile access");
			return E_LOAD_PARSE_MEETINGS;
		}
		log.debug(meetings.toString());
		
		return E_OK;
	}

	public boolean join(String meetingID, String name, boolean moderator) {
		for (Meeting m : meetings.getMeetings()) {
			log.info(m.getMeetingID());
			if (m.getMeetingID().equals(meetingID)) {
				return join(m, name, moderator);
			}
		}
		return false;
	}
	
	public boolean createMeeting(String meetingID) {
		String createUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?action=create"
			+ "&meetingID=" + urlEncode(meetingID);
		log.debug(createUrl);
		
		String response = "Unknown error";
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(createUrl);
			client.executeMethod(method);
			response = method.getResponseBodyAsString().trim();
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't create the meeting {}", meetingID);
		}
		
		if (meetingID.equals(response))
			return true;
		else {
			log.error(response);
			return false;
		}
	}
	
	public boolean join(Meeting meeting, String name, boolean moderator) {
		String joinUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?action=join"
			+ "&meetingID=" + urlEncode(meeting.getMeetingID())
			+ "&fullName=" + urlEncode(name)
			+ "&password=" + urlEncode(moderator? meeting.getModeratorPW(): meeting.getAttendeePW());
		log.debug(joinUrl);
		
		joinedMeeting = new JoinedMeeting();
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(joinUrl);
			client.executeMethod(method);
			joinedMeeting.parse(method.getResponseBodyAsString().trim());
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't join the meeting {}", meeting.getMeetingID());
			
			return false;
		}
		
		if (joinedMeeting.getReturncode().equals("SUCCESS")) {
			return true;
		} else {
			log.error(joinedMeeting.getMessage());
			return false;
		}
	}
	
	public boolean join(String serverUrl, String joinUrl) {
		this.serverUrl = serverUrl;
		String enterUrl = serverUrl + "/bigbluebutton/api/enter";
			
		joinedMeeting = new JoinedMeeting();
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(joinUrl);
			client.executeMethod(method);
//			log.info(method.getResponseBodyAsString().trim());
			method.releaseConnection();

			method = new GetMethod(enterUrl);
			client.executeMethod(method);
			joinedMeeting.parse(method.getResponseBodyAsString().trim());
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't join the url {}", joinUrl);
			
			return false;
		}
		
		if (joinedMeeting.getReturncode().equals("SUCCESS")) {
			return true;
		} else {
			log.error(joinedMeeting.getMessage());
			return false;
		}
	}

	public static String urlEncode(String s) {	
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
