package org.mconf.bbb.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class JoinService {
	private static final Logger log = LoggerFactory.getLogger(JoinService.class);

	private String serverUrl;
	private Meetings meetings = new Meetings();
	private JoinedMeeting joinedMeeting = null;
	private static String salt;
	private static String timestamp;
	protected boolean validTimestamp=false;
	Timer timer1 = new Timer();
	
	public boolean isValidTimestamp() {
		return validTimestamp;
	}



	public void setValidTimestamp(boolean validTimestamp) {
		this.validTimestamp = validTimestamp;
	}



	public String getSalt() {
		return salt;
	}


	
	public void setSalt(String salt) {
		this.salt = salt;
	}

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
	public static final int E_GET_TIMESTAMP = 3;

	// /bigbluebutton/demo/mobile.jsp?action=getTimestamp&checksum=???????????????
	// action=getTimestamp<senha> = checksum
	// action=getTimestamp&checksum=<checksum>
	public int load(String serverUrl) {

		this.serverUrl = serverUrl;
		if(!getTimestamp()){
			return E_GET_TIMESTAMP;
		}


		while (serverUrl.endsWith("/")) {
			serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
		}



		String parameters = "action=getMeetings"+"&timestamp=" +timestamp;
		String getMeetingsUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?" + parameters + "&checksum=" + checksum(parameters + salt);

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
			log.debug(strMeetings);
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

	public static String checksum(String s) {
		String checksum = "";
		try {
			checksum = org.apache.commons.codec.digest.DigestUtils.shaHex(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checksum;
	}

	public boolean parse (String str)
	{
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));
			doc.getDocumentElement().normalize();
			Element nodeResponse = (Element) doc.getElementsByTagName("response").item(0);
			String returncode = nodeResponse.getElementsByTagName("returncode").item(0).getFirstChild().getNodeValue();
			

			if (returncode.equals("SUCCESS")) {	
				timestamp = nodeResponse.getElementsByTagName("timestamp").item(0).getFirstChild().getNodeValue();
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
	
	public void setTimestampTimer()
	{
		long delay = 60*1000;                   // 60 seconds delay

		// Schedule the two timers to run with different delays.
		timer1.schedule(new InvalidateTimestamp(), delay);
	}
	
	class  InvalidateTimestamp extends TimerTask 
	{
		
		@Override
		public void run() {
			log.debug("timestamp expired");
			setValidTimestamp(false);

		}
	}
	

	public boolean getTimestamp()
	{
		if(validTimestamp)
			return true;

		String parameters = "action=getTimestamp";
		String timestampUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?" + parameters + "&checksum=" + checksum(parameters + salt);
		String response = "Unknown error";
		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(timestampUrl);
			client.executeMethod(method);
			response = method.getResponseBodyAsString().trim();
			method.releaseConnection();
			log.debug(response);
			if(!parse(response))
				return false;
			log.debug("timestamp obtained");
			setTimestampTimer();
			validTimestamp=true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Can't get the Timestamp from {}", serverUrl);
			timestamp = null;
			return false;
		}

	}

	public boolean createMeeting(String meetingID) {
		String parameters= "action=create"+"&meetingID=" + urlEncode(meetingID) +"&timestamp=" +timestamp;
		String createUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?"+parameters
		+ checksum(parameters+salt);


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
		if(!getTimestamp())
		{
			return false;
		}
		String parameters = "action=join"
			+ "&meetingID=" + urlEncode(meeting.getMeetingID())
			+ "&fullName=" + urlEncode(name)
			+ "&password=" + urlEncode(moderator? meeting.getModeratorPW(): meeting.getAttendeePW())
			+ "&timestamp=" + timestamp;
		String joinUrl = serverUrl + "/bigbluebutton/demo/mobile.jsp?" + parameters + "&checksum=" + checksum(parameters + salt);

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
		if (!getTimestamp()) {
			return false;
		}

		this.serverUrl = serverUrl;
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
