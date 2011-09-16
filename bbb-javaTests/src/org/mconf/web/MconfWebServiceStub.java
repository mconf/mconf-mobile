package org.mconf.web;


public class MconfWebServiceStub implements MconfWebServiceItf {

	@Override
	public String getRooms(Authentication auth) {
		return 
			"[" +
			  "{\"bigbluebutton_room\":" +
			    "{\"name\":\"Room 1\",\"join_path\":\"/bigbluebutton/servers/server-1/rooms/room-1/join?mobile=1\",\"owner\":{\"type\":\"User\",\"id\":1}}" +
			  "}," +
			  "{\"bigbluebutton_room\":" +
			    "{\"name\":\"Room 2\",\"join_path\":\"/bigbluebutton/servers/server-2/rooms/room-2/join?mobile=1\",\"owner\":{\"type\":\"Space\",\"id\":1,\"name\":\"Space 1\",\"public\":false,\"member\":true}}" +
			  "}," +
			  "{\"bigbluebutton_room\":" +
			    "{\"name\":\"Room 3\",\"join_path\":\"/bigbluebutton/servers/server-2/rooms/room-3/join?mobile=1\",\"owner\":{\"type\":\"Space\",\"id\":2,\"name\":\"Space 2\",\"public\":true,\"member\":false}}" +
			  "}" +
			"]";
	}

	@Override
	public String getJoinUrl(Authentication auth, String joinUrl) {
		return joinUrl;
	}

}
