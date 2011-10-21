package org.mconf.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.transdroid.util.FakeSocketFactory;

public class Authentication {
	private static final Logger log = LoggerFactory.getLogger(Authentication.class);
	private String server, cookie;
	private boolean authenticated = false;
	private DefaultHttpClient client;
	
	@SuppressWarnings("deprecation")
	/**
	 * The depreciated stuff is supported on Android, so we have to use it
	 */
	public Authentication(String server, String username, String password) throws HttpException, IOException {
		this.server = server;
		
		SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("http", new PlainSocketFactory(), 80));
	    /**
	     * http://stackoverflow.com/questions/2899079/custom-ssl-handling-stopped-working-on-android-2-2-froyo
	     * Credits of FakeSocketFactory and FakeTrustManager for http://code.google.com/p/transdroid/
	     */
	    registry.register(new Scheme("https", new FakeSocketFactory(), 443));
	    HttpParams params = new BasicHttpParams();
	    params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, registry), params);

	    
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
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("login", username));
		formparams.add(new BasicNameValuePair("password", password));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		HttpPost method = new HttpPost(server + "/session");
		method.setEntity(entity);
		HttpContext localContext = new BasicHttpContext();
		
		HttpResponse response = client.execute(method, localContext);
		
		// the response code must be a redirect
		if (response.getStatusLine().getStatusCode() != 302) {
			log.error("Invalid response code " + response.getStatusLine().getStatusCode());
			return false;
		}
		
		if (!response.containsHeader("Location") ||
				!response.getFirstHeader("Location").getValue().equals(server + "/home")) {
			log.error("Invalid password");
			return false;
		}
		
		if (!response.containsHeader("Set-Cookie")) {
			log.error("The response doesn't have an authenticated cookie");
			return false;
		}
		cookie = response.getFirstHeader("Set-Cookie").getValue();
		
		log.info("Authenticated on " + server);
		return true;
	}
	
	public String getUrl(String path) throws HttpException, IOException {
		if (!authenticated)
			throw new AuthenticationException();
		
		HttpGet method = new HttpGet(server + path);
		method.setHeader("Cookie", cookie);
		HttpResponse response = client.execute(method);
		
		if (response.getStatusLine().getStatusCode() != 200)
			throw new HttpException("Invalid response code: " + response.getStatusLine().getStatusCode());
		
		return EntityUtils.toString(response.getEntity());
	}
	
	public String getRedirectUrl(String path) throws HttpException, IOException {
		if (!authenticated)
			throw new AuthenticationException();
		
		HttpGet method = new HttpGet(server + path);
		method.setHeader("Cookie", cookie);
		HttpResponse response = client.execute(method);
		
		if (response.getStatusLine().getStatusCode() != 302)
			throw new HttpException("Invalid response code: " + response.getStatusLine().getStatusCode());
		
		if (!response.containsHeader("Location"))
			throw new HttpException("Invalid redirect URL");
		
		return response.getFirstHeader("Location").getValue();
	}
}
