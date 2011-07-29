package org.mconf.bbb.presentation;

import java.net.URLConnection;

public class Slide {

	private int slideNum;
	private String slideUri;
	private String thumbUri;
	private URLConnection connection;
	
	Slide( int slideNum, String slideUri, String thumbUri)
	{
		this.slideNum=slideNum;
		this.slideUri=slideUri;
		this.thumbUri=thumbUri;
		//\TODO implement the URL donwload of the slide, such as in the Slide class and in the
		//loadPresentationListener of the PresentationService
	}
}
