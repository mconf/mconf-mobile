package org.mconf.web;

import java.io.IOException;

import org.apache.http.HttpException;


public class MconfWebServiceImpl implements MconfWebServiceItf {
	
	@Override
	public String getRooms(Authentication auth) throws HttpException, IOException {
		return auth.getUrl("/home/user_rooms.json");
	}

	@Override
	public String getJoinUrl(Authentication auth, String joinUrl) throws HttpException, IOException {
		String url = auth.getRedirectUrl(joinUrl);
		if (url.contains("/bigbluebutton/api"))
			return url;
		else
			throw new HttpException("Invalid BigBlueButton API call");
	}

}
