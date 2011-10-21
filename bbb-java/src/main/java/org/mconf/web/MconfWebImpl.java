package org.mconf.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MconfWebImpl implements MconfWebItf {
	private static final Logger log = LoggerFactory.getLogger(MconfWebImpl.class);
	private MconfWebServiceItf ws = new MconfWebServiceImpl();
	
	@Override
	public List<Room> getRooms(Authentication auth) throws HttpException, IOException, JSONException {
		List<Room> list = new ArrayList<Room>();
		parseRooms(list, ws.getRooms(auth));
		for (Room room : list)
			log.debug(room.toString());
		return list;
	}
	
	public static void parseRooms(List<Room> list, String rooms) throws JSONException {
		JSONArray arrayRooms = new JSONArray(rooms);
		for (int i = 0; i < arrayRooms.length(); ++i) {
			JSONObject objectRoom = arrayRooms.getJSONObject(i).getJSONObject("bigbluebutton_room");
			
			Room room = new Room();
			room.setName(objectRoom.getString("name"));
			room.setPath(objectRoom.getString("join_path"));
			
			JSONObject objectOwner = objectRoom.getJSONObject("owner");
			String type = objectOwner.getString("type");
			
			Owner owner = null;
			if (type.equals(Owner.TYPE_USER))
				owner = new User();
			else if (type.equals(Owner.TYPE_SPACE)) {
				owner = new Space();
				((Space) owner).setName(objectOwner.getString("name"));
				((Space) owner).setPublic(objectOwner.getBoolean("public"));
				((Space) owner).setMember(objectOwner.getBoolean("member"));
			}
			owner.setId(objectOwner.getInt("id"));
			room.setOwner(owner);
			
			list.add(room);
		}
	}

	@Override
	public String getJoinUrl(Authentication auth, String joinUrl) throws HttpException, IOException {
		return ws.getJoinUrl(auth, joinUrl);
	}

}
