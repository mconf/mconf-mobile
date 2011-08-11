package org.mconf.bbb.presentation;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Slide implements ISlide {

	private int slideNum;


	private URL slideUri;
	private URL thumbUri;
	private URLConnection connection;
	private boolean loaded=false;
	private byte[] slideData;


	public Slide(){
	}

	public Slide( int slideNum, String slideUri, String thumbUri)
	{
		this.setSlideNum(slideNum);
		try {
			this.slideUri= new URL (slideUri);
			this.thumbUri= new URL(thumbUri);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}




	}
	public URL getThumbUri() {
		return thumbUri;
	}

	public void setThumbUri(URL thumbUri) {
		this.thumbUri = thumbUri;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.presentation.ISlide#load()
	 */
	@Override
	public byte[] load()
	{
		if(loaded)
		{
			return slideData;
		}
		else
		{
			try {
				//\TODO getting the slide thumb
				connection =thumbUri.openConnection();


				InputStream iStrm = connection.getInputStream(); 
				ByteArrayOutputStream bStrm = new ByteArrayOutputStream();

				int ch;

				while ((ch = iStrm.read()) != -1)
					bStrm.write(ch);


				setSlideData(bStrm.toByteArray());


				loaded=true;
				return slideData;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}                
		}
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.presentation.ISlide#setSlideData(byte[])
	 */
	@Override
	public void setSlideData(byte[] slideData) {
		this.slideData = slideData;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.presentation.ISlide#getSlideData()
	 */
	@Override
	public byte[] getSlideData() {
		return slideData;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.presentation.ISlide#setSlideNum(int)
	 */
	@Override
	public void setSlideNum(int slideNum) {
		this.slideNum = slideNum;
	}

	/* (non-Javadoc)
	 * @see org.mconf.bbb.presentation.ISlide#getSlideNum()
	 */
	@Override
	public int getSlideNum() {
		return slideNum;
	}

	public URL getSlideUri() {
		return slideUri;
	}

	public void setSlideUri(URL slideUri) {
		this.slideUri = slideUri;
	}


}
