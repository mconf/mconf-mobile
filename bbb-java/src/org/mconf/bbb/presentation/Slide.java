package org.mconf.bbb.presentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Slide {

	private int slideNum;
	private URL slideUri;
	private String thumbUri;
	private URLConnection connection;
	private boolean loaded=false;
	private byte[] slideData;

	
	Slide( int slideNum, String slideUri, String thumbUri)
	{
		this.setSlideNum(slideNum);
		try {
			this.slideUri= new URL (slideUri);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.thumbUri=thumbUri;
		
	}
	
	public boolean load()
	{
		if(loaded)
		{
			return true;
		}
		else
		{
			try {
				connection =slideUri.openConnection();
				InputStream iStrm = connection.getInputStream();

				ByteArrayOutputStream bStrm = null;    
				bStrm = new ByteArrayOutputStream();
				int ch;
				while ((ch = iStrm.read()) != -1)
					bStrm.write(ch);
				setSlideData(bStrm.toByteArray());
				bStrm.close();
				
				loaded=true;
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}                
		}
	}

	public void setSlideData(byte[] slideData) {
		this.slideData = slideData;
	}

	public byte[] getSlideData() {
		return slideData;
	}

	public void setSlideNum(int slideNum) {
		this.slideNum = slideNum;
	}

	public int getSlideNum() {
		return slideNum;
	}
}
