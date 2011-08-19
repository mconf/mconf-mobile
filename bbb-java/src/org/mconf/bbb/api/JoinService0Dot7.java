package org.mconf.bbb.api;

public class JoinService0Dot7 extends JoinServiceBase {

	@Override
	protected String getCreateMeetingUrl(String meetingID) {
		return "?action=create" + "&meetingID=" + urlEncode(meetingID);	
	}

	@Override
	protected String getDemoPath() {
		return "/bigbluebutton/demo/mobile.jsp";
	}

	@Override
	protected String getJoinUrl(Meeting meeting, String name, boolean moderator) {
		return "?action=join"
				+ "&meetingID=" + urlEncode(meeting.getMeetingID())
				+ "&fullName=" + urlEncode(name)
				+ "&password=" + urlEncode(moderator? meeting.getModeratorPW(): meeting.getAttendeePW());
	}

	@Override
	protected String getLoadUrl() {
		return "?action=getMeetings";
	}

	@Override
	public String getVersion() {
		return "0.7";
	}

}
