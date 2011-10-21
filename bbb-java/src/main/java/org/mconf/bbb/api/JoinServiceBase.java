package org.mconf.bbb.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JoinServiceBase {
	private static final Logger log = LoggerFactory.getLogger(JoinServiceBase.class);

	protected JoinedMeeting joinedMeeting = null;
	protected String serverUrl = "";
	protected int serverPort = 0;
	protected Meetings meetings = new Meetings();
	protected boolean loaded = false;
	
	public abstract String getVersion();
	protected abstract String getCreateMeetingUrl(String meetingID);
	protected abstract String getLoadUrl();
	protected abstract String getJoinUrl(Meeting meeting, String name, boolean moderator);
	protected abstract String getDemoPath();
	
	private String getFullDemoPath() {
		return getFullServerUrl() + getDemoPath();
	}
	
	private String getFullServerUrl() {
		return serverUrl + ":" + serverPort;
	}
	
	public boolean createMeeting(String meetingID) {
		String createUrl = getFullDemoPath() + getCreateMeetingUrl(meetingID);
		String response = "Unknown error";
		try {
			response = getUrl(createUrl);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't get the url {}", createUrl);
		}
		
		if (meetingID.equals(response))
			return true;
		else
			return false;
	}

	public boolean load() {
		String loadUrl = getFullDemoPath() + getLoadUrl();
		try {
			meetings.parse(getUrl(loadUrl));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Can't connect to {}", loadUrl);
			return false;
		}

		log.debug(meetings.toString());
		loaded = true;
		return true;
	}
	
	public boolean join(String meetingID, String name, boolean moderator) {
		if (!loaded)
			return false;
		
		for (Meeting meeting : meetings.getMeetings()) {
			if (meeting.getMeetingID().equals(meetingID))
				return join(meeting, name, moderator);
		}
		return false;
	}

	public boolean join(Meeting meeting, String name, boolean moderator) {
		return join(getFullDemoPath() + getJoinUrl(meeting, name, moderator));
	}
	
	private boolean join(String joinUrl) {
		joinedMeeting = new JoinedMeeting();
		try {
			joinedMeeting.parse(getUrl(joinUrl));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't join the url {}", joinUrl);
			return false;
		}
		
		if (joinedMeeting.getReturncode().equals("SUCCESS")) {
			return true;
		} else {
			if (joinedMeeting.getMessage() != null)
				log.error(joinedMeeting.getMessage());
			return false;
		}
	}

	public boolean standardJoin(String joinUrl) {
		String enterUrl = getFullServerUrl() + "/bigbluebutton/api/enter";
			
		joinedMeeting = new JoinedMeeting();
		try {
			HttpClient client = new DefaultHttpClient();
			getUrl(client, joinUrl);
			joinedMeeting.parse(getUrl(client, enterUrl));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't join the url {}", joinUrl);
			return false;
		}

		if (joinedMeeting.getReturncode().equals("SUCCESS")) {
			return true;
		} else {
			if (joinedMeeting.getMessage() != null)
				log.error(joinedMeeting.getMessage());
			return false;
		}
	}
	
	public JoinedMeeting getJoinedMeeting() {
		return joinedMeeting;
	}

	public void resetJoinedMeeting() {
		joinedMeeting=null;
	}

	public List<Meeting> getMeetings() {
		return meetings.getMeetings();
	}

	public int getPort() {
		return serverPort;
	}

	public String getServerUrl() {
		return serverUrl;
	}
	
	public void setServer(String serverUrl) {
		if (serverUrl.matches(".*:\\d*")) {
			this.serverPort = Integer.parseInt(serverUrl.substring(serverUrl.lastIndexOf(":") + 1));
			this.serverUrl = serverUrl.substring(0, serverUrl.lastIndexOf(":"));
		} else {
			this.serverUrl = serverUrl;
			this.serverPort = 80;
		}
	}	

	public Meeting getMeetingByName(String meetingName) {
		for(Meeting meeting : meetings.getMeetings()) {
			if(meeting.getMeetingName().equals(meetingName))
				return meeting;
		}
		return null;
	}
	
	public Meeting getMeetingByID(String meetingID) {
		for(Meeting meeting : meetings.getMeetings()) {
			if(meeting.getMeetingID().equals(meetingID))
				return meeting;
		}
		return null;
	}
	
	protected static String getUrl(String url) throws IOException, ClientProtocolException {
    	HttpClient client = new DefaultHttpClient();
		return getUrl(client, url);
	}
	
	protected static String getUrl(HttpClient client, String url) throws IOException, ClientProtocolException {
		HttpGet method = new HttpGet(url);
		HttpResponse httpResponse = client.execute(method);
		
		if (httpResponse.getStatusLine().getStatusCode() != 200)
			log.debug("HTTP GET {} return {}", url, httpResponse.getStatusLine().getStatusCode());
		
		return EntityUtils.toString(httpResponse.getEntity());
	}

	protected static String checksum(String s) {
		String checksum = "";
		try {
			checksum = DigestUtils.shaHex(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checksum;
	}
	
	protected static String urlEncode(String s) {	
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
