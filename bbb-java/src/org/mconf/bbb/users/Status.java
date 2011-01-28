package org.mconf.bbb.users;

import java.util.Map;

public class Status {

	private boolean raiseHand;
	private boolean hasStream;
	private boolean presenter;
	private String streamName;

	public Status(Map<String, Object> param) {
		decode(param);
	}
	
	/*
	 * example:
	 * {raiseHand=false, hasStream=false, presenter=true}
	 */
	public void decode(Map<String, Object> param) {
		raiseHand = (Boolean) param.get("raiseHand");
		hasStream = (Boolean) param.get("hasStream");
		streamName = hasStream? (String) param.get("streamName") : "";
		presenter = (Boolean) param.get("presenter");
	}

	public boolean isRaiseHand() {
		return raiseHand;
	}

	public void setRaiseHand(boolean raiseHand) {
		this.raiseHand = raiseHand;
	}

	public boolean isHasStream() {
		return hasStream;
	}

	public void setHasStream(boolean hasStream) {
		this.hasStream = hasStream;
	}

	public String getStreamName() {
		return streamName;
	}

	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	public boolean isPresenter() {
		return presenter;
	}

	public void setPresenter(boolean presenter) {
		this.presenter = presenter;
	}

	@Override
	public String toString() {
		return "Status [hasStream=" + hasStream + ", presenter=" + presenter
				+ ", raiseHand=" + raiseHand + ", streamName=" + streamName
				+ "]";
	}

}
