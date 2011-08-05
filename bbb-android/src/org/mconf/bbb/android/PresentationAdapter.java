package org.mconf.bbb.android;

import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.presentation.ISlide;
import org.mconf.bbb.presentation.Slide;


public class PresentationAdapter {
	private ArrayList<ISlide> presentation = new ArrayList<ISlide>();

	
	PresentationAdapter(ArrayList<ISlide> presentation)
	{
		this.presentation = presentation;
	}
	
	public void setPresentation(ArrayList<ISlide> presentation) {
		this.presentation = presentation;
	}

	public ArrayList<ISlide> getPresentation() {
		return presentation;
	}
}
