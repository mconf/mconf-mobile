package org.mconf.bbb.api;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JoinService0Dot8 extends JoinServiceBase {
	private static final Logger log = LoggerFactory.getLogger(JoinService0Dot8.class);

	private String salt;
	private long timestamp = 0,
			lastRequest = 0;

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	@Override
	protected String getCreateMeetingUrl(String meetingID) {
		String parameters = "action=create" + "&meetingID=" + urlEncode(meetingID) + "&timestamp=" + timestamp;
		return "?" + parameters + "&checksum=" + checksum(parameters + salt);
	}
	
	@Override
	public boolean createMeeting(String meetingID) {
		if (!updateTimestamp())
			return false;

		return super.createMeeting(meetingID);
	}

	@Override
	protected String getDemoPath() {
		return "/demo/mobile.jsp";
	}

	@Override
	protected String getJoinUrl(Meeting meeting, String name, boolean moderator) {
		String parameters = "action=join"
			+ "&meetingID=" + urlEncode(meeting.getMeetingID())
			+ "&fullName=" + urlEncode(name)
			+ "&password=" + urlEncode(moderator? meeting.getModeratorPW(): meeting.getAttendeePW())
			+ "&timestamp=" + timestamp;
		return "?" + parameters + "&checksum=" + checksum(parameters + salt);
	}
	
	@Override
	public boolean join(String meetingID, String name, boolean moderator) {
		if (!updateTimestamp())
			return false;
		
		return super.join(meetingID, name, moderator);
	}

	@Override
	protected String getLoadUrl() {
		String parameters = "action=getMeetings" + "&timestamp=" + timestamp;
		return "?" + parameters + "&checksum=" + checksum(parameters + salt);
	}
	
	@Override
	public boolean load() {
		if (!updateTimestamp())
			return false;
		
		return super.load();
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
		String timestampUrl = serverUrl + getDemoPath() + "?" + parameters + "&checksum=" + checksum(parameters + salt);

		log.debug("timestampUrl=" + timestampUrl);
		String response = "Unknown error";
		try {
			response = getUrl(timestampUrl);
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

	@Override
	public String getVersion() {
		return "0.8";
	}

}
