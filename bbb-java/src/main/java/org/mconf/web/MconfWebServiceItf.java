package org.mconf.web;

import java.io.IOException;

import org.apache.http.HttpException;


public interface MconfWebServiceItf {
	
	public String getRooms(Authentication auth) throws HttpException, IOException;
	public String getJoinUrl(Authentication auth, String joinUrl) throws HttpException, IOException;
	
}
