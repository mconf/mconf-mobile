package org.mconf.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.json.JSONException;

public class MconfWebStub implements MconfWebItf {
	private MconfWebServiceItf ws = new MconfWebServiceStub();
	
	@Override
	public List<Room> getRooms(Authentication auth) throws HttpException, JSONException, IOException {
		List<Room> list = new ArrayList<Room>();
		MconfWebImpl.parseRooms(list , ws.getRooms(null));
		return list;
	}

	@Override
	public String getJoinUrl(Authentication auth, String joinUrl) {
		return null;
	}

}
