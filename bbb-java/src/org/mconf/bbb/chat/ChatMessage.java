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

package org.mconf.bbb.chat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class ChatMessage {
	
	/*
	 * newMessage = event.message + "|" + attributes.username + "|" + event.color + "|" + event.time + "|" + event.language + "|" + attributes.userid;
	 */
	private String message;
	private String username;
	private String color;
	private String time;
	private String language;
	private int userid;
	
	public ChatMessage() {
		color = "0";
		time = new SimpleDateFormat("hh:mm").format(System.currentTimeMillis());
		language = "en";
	}
	
	public ChatMessage(String s) {
		decode(s);
	}
	
	public void decode(String s) {
		List<String> param = Arrays.asList(s.split("\\|"));
		message = param.get(0);
		username = param.get(1);
		color = param.get(2);
		time = param.get(3);
		language = param.get(4);
		userid = Double.valueOf(Double.parseDouble(param.get(5))).intValue();
	}
	
	public String encode() {
		StringBuilder sb = new StringBuilder();
		sb.append(message)
			.append("|")
			.append(username)
			.append("|")
			.append(color)
			.append("|")
			.append(time)
			.append("|")
			.append(language)
			.append("|")
			.append(userid);
		return sb.toString();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getUserId() {
		return userid;
	}

	public void setUserId(int userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "ChatMessage [color=" + color + ", language=" + language
				+ ", message=" + message + ", time=" + time + ", userid="
				+ userid + ", username=" + username + "]";
	}
	
}
