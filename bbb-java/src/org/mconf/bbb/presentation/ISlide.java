package org.mconf.bbb.presentation;

import java.net.URL;

public interface ISlide {

	public abstract byte[] load();

	public abstract void setSlideData(byte[] slideData);

	public abstract byte[] getSlideData();

	public abstract void setSlideNum(int slideNum);

	public abstract int getSlideNum();
	
	public abstract URL getSlideUri();
	
	public abstract void setSlideUri(URL slideUri);

}