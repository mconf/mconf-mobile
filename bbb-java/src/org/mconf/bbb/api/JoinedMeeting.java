package org.mconf.bbb.api;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JoinedMeeting {
	
	private static final Logger log = LoggerFactory.getLogger(JoinedMeeting.class);
	
	private String returncode;
	private String fullname;
	private String confname;
	private String meetingID;
	private String externUserID;
	private String role;
	private String conference;
	private String room;
	private String voicebridge;
	private String webvoiceconf;
	private String mode;
	private String record;
	private String welcome;

	public JoinedMeeting() {
		
	}
	
	/*
	 *  	
	 * <response>
	 * 	<returncode>SUCCESS</returncode>
	 * 	<fullname>My name</fullname>
	 * 	<confname>Demo Meeting</confname>
	 * 	<meetingID>Demo Meeting</meetingID>
	 * 	<externUserID>cabfia5gbt9z</externUserID>
	 * 	<role>MODERATOR</role>
	 * 	<conference>19697a6c-e69c-484b-8bf0-cbf3f2542a4a</conference>
	 * 	<room>19697a6c-e69c-484b-8bf0-cbf3f2542a4a</room>
	 * 	<voicebridge>77293</voicebridge>
	 * 	<webvoiceconf>77293</webvoiceconf>
	 * 	<mode>LIVE</mode>
	 * 	<record>false</record>
	 * 	<welcome>&lt;br&gt;Welcome to this BigBlueButton Demo Server.&lt;br&gt;&lt;br&gt;For help using BigBlueButton &lt;a href="event:http://www.bigbluebutton.org/content/videos"&gt;&lt;u&gt;check out these videos&lt;/u&gt;&lt;/a&gt;.&lt;br&gt;&lt;br&gt;</welcome>
	 * </response>
	 */
	public boolean parse(String str) {	
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));
			doc.getDocumentElement().normalize();

			Element nodeResponse = (Element) doc.getElementsByTagName("response").item(0);
			returncode = nodeResponse.getElementsByTagName("returncode").item(0).getFirstChild().getNodeValue();
			
			if (returncode.equals("SUCCESS")) {		
				fullname = nodeResponse.getElementsByTagName("fullname").item(0).getFirstChild().getNodeValue();
				confname = nodeResponse.getElementsByTagName("confname").item(0).getFirstChild().getNodeValue();
				meetingID = nodeResponse.getElementsByTagName("meetingID").item(0).getFirstChild().getNodeValue();
				externUserID = nodeResponse.getElementsByTagName("externUserID").item(0).getFirstChild().getNodeValue();
				role = nodeResponse.getElementsByTagName("role").item(0).getFirstChild().getNodeValue();
				conference = nodeResponse.getElementsByTagName("conference").item(0).getFirstChild().getNodeValue();
				room = nodeResponse.getElementsByTagName("room").item(0).getFirstChild().getNodeValue();
				voicebridge = nodeResponse.getElementsByTagName("voicebridge").item(0).getFirstChild().getNodeValue();
				webvoiceconf = nodeResponse.getElementsByTagName("webvoiceconf").item(0).getFirstChild().getNodeValue();
				mode = nodeResponse.getElementsByTagName("mode").item(0).getFirstChild().getNodeValue();
				record = nodeResponse.getElementsByTagName("record").item(0).getFirstChild().getNodeValue();
				welcome = nodeResponse.getElementsByTagName("welcome").item(0).getFirstChild().getNodeValue();			
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Failed to parse: {}", str);
			return false;
		}
		
		log.debug(toString());
		return true;	
	}

	public String getReturncode() {
		return returncode;
	}

	public String getFullname() {
		return fullname;
	}

	public String getConfname() {
		return confname;
	}

	public String getMeetingID() {
		return meetingID;
	}

	public String getExternUserID() {
		return externUserID;
	}

	public String getRole() {
		return role;
	}

	public String getConference() {
		return conference;
	}

	public String getRoom() {
		return room;
	}

	public String getVoicebridge() {
		return voicebridge;
	}

	public String getWebvoiceconf() {
		return webvoiceconf;
	}

	public String getMode() {
		return mode;
	}

	public String getRecord() {
		return record;
	}

	public String getWelcome() {
		return welcome;
	}

	@Override
	public String toString() {
		return "JoinedMeeting [conference=" + conference + ", confname="
				+ confname + ", externUserID=" + externUserID + ", fullname="
				+ fullname + ", meetingID=" + meetingID + ", mode=" + mode
				+ ", record=" + record + ", returncode=" + returncode
				+ ", role=" + role + ", room=" + room + ", voicebridge="
				+ voicebridge + ", webvoiceconf=" + webvoiceconf + ", welcome="
				+ welcome + "]";
	}
}
