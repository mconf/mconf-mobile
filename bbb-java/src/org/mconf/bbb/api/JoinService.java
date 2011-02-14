package org.mconf.bbb.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinService {
	private static final Logger log = LoggerFactory.getLogger(JoinService.class);

	private String serverUrl, salt;
	private BigBlueButtonApi api;
	private Meetings meetings = new Meetings();

	public List<Meeting> getMeetings() {
		return meetings.getMeetings();
	}
	
	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public boolean load() {
		return load("bigbluebutton.properties");
	}
	
	public boolean load(String filepath) {
		Properties p = new Properties();
		try {
			p.load(new BufferedReader(new FileReader(filepath)));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't find/load the properties file");
			return false;
		}
		
		return load(p.getProperty("bigbluebutton.web.serverURL"), p.getProperty("beans.dynamicConferenceService.securitySalt"));
	}

	public boolean load(String serverUrl, String salt) {
		this.serverUrl = serverUrl;
		this.salt = salt;
		this.api = new BigBlueButtonApi(serverUrl + "/bigbluebutton/", salt);
		
		String strMeetings = api.getMeetings();
		if (strMeetings == null) {
			log.info("Can't connect to {}", serverUrl);
			return false;
		}
		
		meetings.parse(strMeetings);
		log.debug(meetings.toString());
		
		return true;
	}

	public JoinedMeeting join(String meetingID, String name, boolean moderator) {
		for (Meeting m : meetings.getMeetings()) {
			log.info(m.getMeetingID());
			if (m.getMeetingID().equals(meetingID)) {
				return join(m, name, moderator);
			}
		}
		return null;
	}
	
	public boolean createMeeting(Meeting meeting) {
		String create = api.createMeeting(meeting.getMeetingID(), 
				"Welcome message", 
				meeting.getModeratorPW(), 
				meeting.getAttendeePW(), 
				0, 
				serverUrl);
		log.debug("createMeeting: {}", create);
		return create.equals(meeting.getMeetingID()); 
	}
	
	public JoinedMeeting join(Meeting meeting, String name, boolean moderator) {
		if (api.isMeetingRunning(meeting.getMeetingID()).equals("false")) {
			if (!createMeeting(meeting)) {
				log.error("The meeting {} is not running", meeting.getMeetingID());
				return null;
			}
		}
		
		String joinUrl = api.getJoinMeetingURL(name, meeting.getMeetingID(), moderator? meeting.getModeratorPW() : meeting.getAttendeePW());
		log.debug(joinUrl);
		
		JoinedMeeting joined = new JoinedMeeting();
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(joinUrl);
			client.executeMethod(method);
			method.releaseConnection();
			
			method = new GetMethod(serverUrl + "/bigbluebutton/api/enter");
			client.executeMethod(method);
			joined.parse(method.getResponseBodyAsString());
			method.releaseConnection();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't join the meeting {}", meeting.getMeetingID());
			
			return null;
		}
		
		return joined;
	}
		
}
