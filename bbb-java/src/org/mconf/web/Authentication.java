package org.mconf.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authentication {
	private static final Logger log = LoggerFactory.getLogger(Authentication.class);
	private String server, token, cookie;
	private boolean authenticated = false;
	
	public Authentication(String server, String username, String password) {
		this.server = server;
		try {
//			if (retrieveInfo())
				authenticated = authenticate(username, password);
		} catch (Exception e) {
			authenticated = false;
			e.printStackTrace();
		}
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	/**
	 * Used to retrieve the authentication token and the session cookie, but it's unused now
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws BadLocationException
	 */
	@SuppressWarnings("unused")
	private boolean retrieveInfo() throws HttpException, IOException, BadLocationException {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(server);
		client.executeMethod(method);
		
		for (HeaderElement element : method.getResponseHeader("Set-Cookie").getElements()) {
			cookie = element.getName() + "=" + element.getValue(); 
		}

		HTMLEditorKit kit = new HTMLEditorKit(); 
	    HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument(); 
	    doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
	    Reader HTMLReader = new InputStreamReader(method.getResponseBodyAsStream()); 
	    kit.read(HTMLReader, doc, 0); 

	    //  Get an iterator for all HTML tags.
	    ElementIterator it = new ElementIterator(doc); 
	    Element elem;
	    
	    while ((elem = it.next()) != null) { 
    		if (elem.getName().equals("meta")) {
    			if (elem.getAttributes().containsAttribute(HTML.Attribute.NAME, "csrf-token")) {
    				token = elem.getAttributes().getAttribute(HTML.Attribute.CONTENT).toString();
    				log.debug("token = " + token);
    				log.debug("cookie = " + cookie);
    			}
    		} 
	    }	
	    
		method.releaseConnection();
		
		if (token.isEmpty() || cookie.isEmpty()) {
			log.error("Invalid Mconf-Web site");
			return false;
		}
		return true;
	}
	
	/**
	 * Example of the use of cookies:
	 * http://www.java-tips.org/other-api-tips/httpclient/how-to-use-http-cookies.html
	 * @param username
	 * @param password
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	private boolean authenticate(String username, String password) throws HttpException, IOException {
		HttpClient client = new HttpClient();
        client.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

        PostMethod method = new PostMethod(server + "/session");
		method.setFollowRedirects(false);
//		method.addParameter("authenticity_token", token);
		method.addParameter("login", username);
		method.addParameter("password", password);
//		method.setRequestHeader("Cookie", cookie);
		
		int response = client.executeMethod(method);
		
		// the response code must be a redirect
		if (response != 302) {
			log.error("Invalid response code");
			return false;
		}
		
		String location = null;
		for (HeaderElement element : method.getResponseHeader("Location").getElements()) {
			location = element.getName(); 
		}
		if (location == null ||
				!location.equals(server + "/home")) {
			log.error("Invalid password");
			return false;
		}
		
	    for (Cookie tmp : client.getState().getCookies()) {
	    	if (tmp.getName().equals("_vcc_session"))
	    		cookie = tmp.toString();
	    }
		method.releaseConnection();
		
		log.info("Authenticated on " + server);
		return true;
	}
	
	public String getUrl(String path) throws HttpException, IOException {
		if (!authenticated)
			return "";
		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(server + path);
		method.setRequestHeader("Cookie", cookie);
		client.executeMethod(method);
		String result = method.getResponseBodyAsString().trim();
		method.releaseConnection();
		
		return result;
	}
	
	public String getRedirectUrl(String path) throws HttpException, IOException {
		if (!authenticated)
			return "";
		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(server + path);
		method.setFollowRedirects(false);
		method.setRequestHeader("Cookie", cookie);
		int response = client.executeMethod(method);
		
		// the response code must be a redirect
		if (response != 302) {
			log.error("Invalid response code");
			return "";
		}
		
		String location = null;
		for (HeaderElement element : method.getResponseHeader("Location").getElements()) {
			location = element.getName() + "="+ element.getValue(); 
		}
		if (location == null) {
			log.error("Invalid redirect URL");
			return "";
		}
		
		method.releaseConnection();
		
		return location;
	}
}
