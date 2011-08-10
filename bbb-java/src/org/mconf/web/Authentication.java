package org.mconf.web;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authentication {
	private static final Logger log = LoggerFactory.getLogger(Authentication.class);
	private String server, token, cookie;
	
	public Authentication(String server) {
		this.server = server;
		try {
			retrieveInfo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void retrieveInfo() throws Exception {
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(server);
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
		
		if (token.isEmpty() || cookie.isEmpty())
			throw new Exception("Invalid Mconf-Web site");
	}
	
	public void authenticate(String username, String password) throws Exception {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(server + "/session");
//		method.addParameter("utf8", "✓");
		method.addParameter("authenticity_token", token);
		method.addParameter("login", username);
		method.addParameter("password", password);
		method.setRequestHeader("Cookie", cookie);
		method.setDoAuthentication(true);
		
		client.executeMethod(method);
		String result = method.getResponseBodyAsString().trim();
		method.releaseConnection();
		
		if (result.equals("<html><body>You are being <a href=\"http://mconf.inf.ufrgs.br/home\">redirected</a>.</body></html>"))
			log.info("Authenticated!");
		else
			throw new Exception("Invalid password");
	}
}
