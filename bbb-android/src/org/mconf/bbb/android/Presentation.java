package org.mconf.bbb.android;

import java.util.ArrayList;

import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.listeners.IListener;
import org.mconf.bbb.presentation.ISlide;
import org.mconf.bbb.presentation.Slide;
import org.mconf.bbb.users.IParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

public class Presentation extends BigBlueButtonActivity implements IBigBlueButtonClientListener{
	
	private static final Logger log = LoggerFactory.getLogger(Presentation.class);
	private int currentSlideNum;
	private String presentationName;
	private Slide slide;
	private ImageView slideImage;

	private PresentationAdapter presentationAdapter;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//created when the presentation is loaded
		log.debug("onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		setContentView(R.layout.presentation);
		 slideImage = (ImageView) findViewById(R.id.slide);
		
		 presentationAdapter = new PresentationAdapter(getBigBlueButton().getPresentation());
		 Bundle extras = getIntent().getExtras();
		 
		this.presentationName=extras.getString("presentationName");
		 showSlide(extras.getInt("currentSlide"));
		 	
			
	}
	
	@Override
	public void onPresentationLoaded(String presentationName,ArrayList<ISlide> presentation, int currentSlide) {
		// TODO Auto-generated method stub
		log.debug("on presentation loaded");

	}

	@Override
	public void onSlideChanged(int currentSlide) {
		// TODO Auto-generated method stub
		log.debug("slide changed {}", currentSlide);
		showSlide(currentSlide);
		
	}

	

	@Override
	public void onPresentationRemoved() {

		Toast.makeText(this, R.string.presentation_removed, Toast.LENGTH_SHORT).show();
		presentationAdapter.getPresentation().clear();
		
		Intent bringBackClient = new Intent(getApplicationContext(), Client.class);
		bringBackClient.setAction(Client.BACK_TO_CLIENT);
		startActivity(bringBackClient);
		finish(); 
	}
	
	@Override
	public void onPresentationShared(String presentationName) {
		// TODO Auto-generated method stub
		
	}
	
	public void showSlide(int currentSlide)
	{
		this.currentSlideNum=currentSlide;
		slide =  (Slide) presentationAdapter.getPresentation().get(currentSlide); 
		byte[] bytes = getBigBlueButton().getSlideData(slide);
		log.debug(slide.getThumbUri().toString());
		
		slideImage.setImageDrawable(getSlideDrawable(bytes));

		
	}
	
	public Drawable getSlideDrawable(byte[] slideData)
	{
		//\TODO the slideData is a flash object NOOOOOOOOOO!!! bitmapfactory doesnt work
		//using the thumbnails instead
		Bitmap bitmap = BitmapFactory.decodeByteArray(slideData, 0, slideData.length);
		
		return (new BitmapDrawable(bitmap));
		
	}
	
	/*
	 * unused methods from the IBigBlueButtonClientListener
	 * 
	 * 
	 */
	
	@Override
	public void onPublicChatMessage(ChatMessage message, IParticipant source) {
	}

	@Override
	public void onPrivateChatMessage(ChatMessage message, IParticipant source) {
	}

	@Override
	public void onConnected() {
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onKickUserCallback() {
	}

	@Override
	public void onParticipantLeft(IParticipant p) {
	}

	@Override
	public void onParticipantJoined(IParticipant p) {
	}

	@Override
	public void onParticipantStatusChangePresenter(IParticipant p) {
	}

	@Override
	public void onParticipantStatusChangeHasStream(IParticipant p) {
	}

	@Override
	public void onParticipantStatusChangeRaiseHand(IParticipant p) {
	}

	@Override
	public void onListenerJoined(IListener p) {
	}

	@Override
	public void onListenerLeft(IListener p) {
	}

	@Override
	public void onListenerStatusChangeIsMuted(IListener p) {
	}

	@Override
	public void onListenerStatusChangeIsTalking(IListener p) {
	}

	

	

}
