package org.mconf.bbb.api;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JoinServiceProxy {
	private static final Logger log = LoggerFactory.getLogger(JoinServiceProxy.class);

	private String serverVersion = null, serverUrl = null;
	private JoinServiceBase joinService = null;
	
	public void setServer(String serverUrl) {
		if (this.serverUrl != null
				&& this.serverUrl.equals(serverUrl))
			return;
		
		joinService = null;
		if (isBigBlueButtonServer(serverUrl)) {
			if (serverVersion.equals(new JoinService0Dot7().getVersion()))
				joinService = new JoinService0Dot7();
			else if (serverVersion.equals(new JoinService0Dot8().getVersion()))
				joinService = new JoinService0Dot8();
			else
				log.debug("Unknown server version {}", serverVersion);
			
			if (joinService != null)
				joinService.setServer(serverUrl);
		}
	}
	
	public void setServer(String serverUrl, String salt) {
		setServer(serverUrl);
		
		if (joinService != null
				&& joinService.getClass() == JoinService0Dot8.class)
			((JoinService0Dot8) joinService).setSalt(salt);
	}
	
	public JoinServiceBase getJoinService() {
		return joinService;
	}
	
	public String getServerVersion() {
		return serverVersion;
	}
	
	public boolean isBigBlueButtonServer(String serverUrl) {
		if (this.serverUrl != null && this.serverUrl.equals(serverUrl))
			return serverVersion == null;
		
		serverVersion = getBigBlueButtonVersion(serverUrl);
		if (serverVersion != null) {
			this.serverUrl = serverUrl;
			return true;
		} else {
			return false;
		}
	}
	
	public static String getBigBlueButtonVersion(String serverUrl) {
		try {
	    	HttpClient client = new DefaultHttpClient();
			HttpGet method = new HttpGet(serverUrl + "/bigbluebutton/api");
			HttpResponse httpResponse = client.execute(method);
			
			if (httpResponse.getStatusLine().getStatusCode() != 200)
				return null;
			
			String response = EntityUtils.toString(httpResponse.getEntity());
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(response.getBytes("UTF-8")));
			doc.getDocumentElement().normalize();
			Element nodeResponse = (Element) doc.getElementsByTagName("response").item(0);
			String returncode = ParserUtils.getNodeValue(nodeResponse, "returncode");
			if (!returncode.equals("SUCCESS"))
				return null;
			
			return ParserUtils.getNodeValue(nodeResponse, "version");
		} catch (Exception e) {
			return null;
		}
	}

	public void resetJoinedMeeting() {
		if (joinService != null)
			joinService.resetJoinedMeeting();
	}
}
