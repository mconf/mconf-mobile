package org.mconf.web;

import java.io.IOException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authentication {
	private static final Logger log = LoggerFactory.getLogger(Authentication.class);
	private String server, cookie;
	private boolean authenticated = false;
	
	public Authentication(String server, String username, String password) throws HttpException, IOException {
		this.server = server;
		authenticated = authenticate(username, password);
	}
	
	public boolean isAuthenticated() {
		return authenticated;
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
		method.addParameter("login", username);
		method.addParameter("password", password);
		
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
			throw new AuthenticationException();
		
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
			throw new AuthenticationException();
		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(server + path);
		method.setFollowRedirects(false);
		method.setRequestHeader("Cookie", cookie);
		int response = client.executeMethod(method);
		
		// the response code must be a redirect
		if (response != 302) {
			method.releaseConnection();
			throw new HttpException("Invalid response code: " + response);
		}
		
		String location = null;
		for (HeaderElement element : method.getResponseHeader("Location").getElements()) {
			location = element.getName() + "="+ element.getValue(); 
		}
		if (location == null) {
			method.releaseConnection();
			throw new HttpException("Invalid redirect URL");
		}
		
		method.releaseConnection();	
		return location;
	}
}
