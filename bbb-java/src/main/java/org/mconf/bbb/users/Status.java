/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	
	public Status() {
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
	
	@Override
	public Status clone() {
		Status clone = new Status();
		clone.hasStream = this.hasStream;
		clone.presenter = this.presenter;
		clone.raiseHand = this.raiseHand;
		clone.streamName = this.streamName;
		return clone;
	}

}
