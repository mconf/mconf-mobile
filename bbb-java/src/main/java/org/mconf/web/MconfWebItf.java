package org.mconf.web;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpException;
import org.json.JSONException;

public interface MconfWebItf {
	
	public List<Room> getRooms(Authentication auth) throws HttpException, IOException, JSONException;
	public String getJoinUrl(Authentication auth, String joinUrl) throws HttpException, IOException;
}
