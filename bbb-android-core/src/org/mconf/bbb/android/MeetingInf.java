package org.mconf.bbb.android;

public class MeetingInf {

	public final static int ID= 500;
	
	private String title;
	private String data;
	private int id;
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getData() {
		return data;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
}
