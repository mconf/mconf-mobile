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
	private String userid;
	
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
		userid = param.get(5);
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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
}
