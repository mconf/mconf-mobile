package org.mconf.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MconfWebAPI {
	private static final Logger log = LoggerFactory.getLogger(MconfWebAPI.class);
	
	
	public static List<Room> getRooms(Authentication auth) {
		List<Room> list = new ArrayList<Room>();
		try {
			String str = auth.getUrl("/home/user_rooms.json");
			JSONArray arrayRooms = new JSONArray(str);
			for (int i = 0; i < arrayRooms.length(); ++i) {
				JSONObject objectRoom = arrayRooms.getJSONObject(i).getJSONObject("bigbluebutton_room");
				list.add(new Room(objectRoom.getString("name"), objectRoom.getString("join_path")));
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Room room : list)
			log.debug(room.toString());
		return list;
	}


	public static String getJoinUrl(Authentication auth, String path) {
		String result = "";
		try {
			result = auth.getRedirectUrl(path);
			log.debug("getJoinUrl = {}", result);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
