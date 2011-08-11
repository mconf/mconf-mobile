package org.mconf.bbb.api;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JoinService {
	private static final Logger log = LoggerFactory.getLogger(JoinService.class);

	private String serverUrl;
	private Meetings meetings = new Meetings();
	private JoinedMeeting joinedMeeting = null;
	private String salt;
	private long timestamp = 0,
			lastRequest = 0;
	
	private static String DEMO_PATH = "/demo/mobile.jsp";
//	private static String DEMO_PATH = "/bigbluebutton/demo/mobile.jsp";

	public JoinedMeeting getJoinedMeeting() {
		return joinedMeeting;
	}

	public void resetJoinedMeeting() {
		joinedMeeting=null;
	}

	public List<Meeting> getMeetings() {
		return meetings.getMeetings();
	}

	public String getServerUrl() {
		return serverUrl;
	}
	
	public void setServer(String serverUrl, String salt) {
		this.serverUrl = serverUrl;
		this.salt = salt;
		lastRequest = 0;
	}

	public Meeting getMeetingById(String meetingID) {
		for(Meeting meeting:getMeetings()) {
			if(meeting.getMeetingID().equals(meetingID))
				return meeting;
		}
		return null;
	}

	public static final int E_OK = 0;
	public static final int E_LOAD_HTTP_REQUEST = 1;
	public static final int E_LOAD_PARSE_MEETINGS = 2;
	public static final int E_UNKNOWN = 3;

	// /demo/mobile.jsp?action=getTimestamp&checksum=???????????????
	// action=getTimestamp<senha> = checksum
	// action=getTimestamp&checksum=<checksum>
	public int load() {
		if (!updateTimestamp())
			return E_UNKNOWN;
		
		while (serverUrl.endsWith("/")) {
			serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
		}

		String parameters = "action=getMeetings"+"&timestamp=" +timestamp;
		String getMeetingsUrl = serverUrl + DEMO_PATH + "?" + parameters + "&checksum=" + checksum(parameters + salt);

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
		if (!updateTimestamp())
			return false;
		
		for (Meeting m : meetings.getMeetings()) {
			if (m.getMeetingID().equals(meetingID)) {
				return join(m, name, moderator);
			}
		}
		return false;
	}

	private static String checksum(String s) {
		String checksum = "";
		try {
			checksum = org.apache.commons.codec.digest.DigestUtils.shaHex(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checksum;
	}

	private boolean parseTimestamp(String str) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));
			doc.getDocumentElement().normalize();
			Element nodeResponse = (Element) doc.getElementsByTagName("response").item(0);
			String returncode = nodeResponse.getElementsByTagName("returncode").item(0).getFirstChild().getNodeValue();

			if (returncode.equals("SUCCESS")) {	
				timestamp = Long.parseLong(nodeResponse.getElementsByTagName("timestamp").item(0).getFirstChild().getNodeValue());
				return true;
			}
			else
			{
				log.debug("Failed getting the timestamp");
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.warn("Failed to parse: {}", str);
			return false;
		}
	}

	private boolean getTimestamp() {	
		String parameters = "action=getTimestamp";
		String timestampUrl = serverUrl + DEMO_PATH + "?" + parameters + "&checksum=" + checksum(parameters + salt);

		log.debug("timestampUrl=" + timestampUrl);
		String response = "Unknown error";
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(timestampUrl);
			client.executeMethod(method);
			response = method.getResponseBodyAsString().trim();
			method.releaseConnection();
			parseTimestamp(response);
			log.debug("timestamp:{}",timestamp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't get the Timestamp from {}", serverUrl);
			timestamp = 0;
			return false;
		}
	}
	
	private boolean updateTimestamp() {
		if (System.currentTimeMillis() < lastRequest + 55000)
			return true;
		else {
			if (getTimestamp()) {
				lastRequest = System.currentTimeMillis();
				return true;
			}
		}
		return false;
	}

	public boolean createMeeting(String meetingID) {
		if (!updateTimestamp())
			return false;
		
		String parameters= "action=create"+"&meetingID=" + urlEncode(meetingID) +"&timestamp=" + timestamp;
		String createUrl = serverUrl + DEMO_PATH + "?" + parameters + "&checksum=" + checksum(parameters + salt);
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
		if (!updateTimestamp())
			return false;

		String parameters = "action=join"
			+ "&meetingID=" + urlEncode(meeting.getMeetingID())
			+ "&fullName=" + urlEncode(name)
			+ "&password=" + urlEncode(moderator? meeting.getModeratorPW(): meeting.getAttendeePW())
			+ "&timestamp=" + timestamp;
		String joinUrl = serverUrl + DEMO_PATH + "?" + parameters + "&checksum=" + checksum(parameters + salt);

		log.debug(joinUrl);

		joinedMeeting = new JoinedMeeting();
		boolean parsedCorrectly = false;
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(joinUrl);
			client.executeMethod(method);
			parsedCorrectly = joinedMeeting.parse(method.getResponseBodyAsString().trim());
			method.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't join the meeting {}", meeting.getMeetingID());
			
			return false;
		}
		
		if (parsedCorrectly && joinedMeeting.getReturncode().equals("SUCCESS")) {
			return true;
		} else {
			if (joinedMeeting.getMessage() != null)
				log.error(joinedMeeting.getMessage());
			return false;
		}
	}

	public boolean join(String joinUrl) {
		serverUrl = joinUrl.substring(0, joinUrl.indexOf("/bigbluebutton/api/"));
		String enterUrl = serverUrl + "/bigbluebutton/api/enter";
			
		joinedMeeting = new JoinedMeeting();
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(joinUrl);
			client.executeMethod(method);
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

	private static String urlEncode(String s) {	
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
