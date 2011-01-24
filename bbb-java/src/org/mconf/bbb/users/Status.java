package org.mconf.bbb.users;

import java.util.Map;

public class Status {

	private boolean raiseHand;
	private boolean hasStream;
	private boolean presenter;

	public Status(Map<String, Boolean> param) {
		decode(param);
	}
	
	/*
	 * example:
	 * {raiseHand=false, hasStream=false, presenter=true}
	 */
	public void decode(Map<String, Boolean> param) {
		raiseHand = param.get("raiseHand");
		hasStream = param.get("hasStream");
		presenter = param.get("presenter");
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

	public boolean isPresenter() {
		return presenter;
	}

	public void setPresenter(boolean presenter) {
		this.presenter = presenter;
	}

	@Override
	public String toString() {
		return "Status [hasStream=" + hasStream + ", presenter=" + presenter
				+ ", raiseHand=" + raiseHand + "]";
	}

}
