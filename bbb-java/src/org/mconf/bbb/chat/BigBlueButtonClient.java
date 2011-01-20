package org.mconf.bbb.chat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.mconf.bbb.api.BigBlueButtonApi;
import org.mconf.bbb.api.Meeting;
import org.mconf.bbb.api.Meetings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigBlueButtonClient {
	
	private static final Logger log = LoggerFactory.getLogger(BigBlueButtonClient.class);
	
	private String serverUrl, salt;
	private BigBlueButtonApi api;
	private Meetings meetings = new Meetings();
	
	public boolean load() {
		
		Properties p = new Properties();
		try {
			p.load(new BufferedReader(new FileReader("bigbluebutton.properties")));
		} catch (Exception e) {
			e.printStackTrace();
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
		
		return true;
	}

	public List<Meeting> getMeetings() {
		return meetings.getMeetings();
	}
	
	public boolean join(String meetingID, String name, boolean moderator) {
		for (Meeting m : meetings.getMeetings()) {
			log.info(m.getMeetingID());
			if (m.getMeetingID().equals(meetingID))
				return join(m, name, moderator);
		}
		return false;
	}
	
	public boolean join(Meeting meeting, String name, boolean moderator) {
		String joinUrl = api.getJoinMeetingURL(name, meeting.getMeetingID(), moderator? meeting.getModeratorPW() : meeting.getAttendeePW());
		log.debug(joinUrl);
		
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(joinUrl);
			client.executeMethod(method);
			method.releaseConnection();
			
			method = new GetMethod(serverUrl + "/bigbluebutton/api/enter");
			client.executeMethod(method);
			log.debug(method.getResponseBodyAsString());
			method.releaseConnection();
		} catch (Exception e) {
			log.debug(e.getMessage());
			return false;
		}

		return true;
	}
}
