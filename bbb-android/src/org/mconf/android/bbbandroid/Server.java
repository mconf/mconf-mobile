package org.mconf.android.bbbandroid;

public class Server {

	public final static int SERVER_UNKNOWN= 0000;
	public final static int SERVER_UP= 1111;
	public final static int SERVER_DOWN= 2222;
	public final static int SERVER_TESTING= 3333;
	
	private String url;
	private String password;
	private String Id;
	private int viewID;
	private int status;
	
	Server(String ID, String Url, String password)
	{
		this.setId(ID);
		this.setUrl(Url);
		this.setPassword(password);
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setViewID(int viewID) {
		this.viewID = viewID;
	}
	public int getViewID() {
		return viewID;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getId() {
		return Id;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getStatus() {
		return status;
	}
	
	
}
