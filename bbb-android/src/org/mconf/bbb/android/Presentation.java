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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Presentation extends Dialog implements IBigBlueButtonClientListener{
	
	
	private static final Logger log = LoggerFactory.getLogger(Presentation.class);
	private int currentSlideNum;
	private String presentationName;
	private Slide slide;
	private ImageView slideImage;
	private Context context;
	private PresentationAdapter presentationAdapter;

	
	//receivers of broadcast intents
	private BroadcastReceiver finishedReceiver = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			presentationAdapter.getPresentation().clear();
			Presentation.this.cancel(); // we finish PrivateChat here when receiving the broadcast 

		} 
	};

	
	public Presentation(Context context, String presentationName, int currentSlide) {
		super(context);
		setContentView(R.layout.presentation);
		slideImage = (ImageView) findViewById(R.id.slide);
		this.context=context;
		((BigBlueButton)context.getApplicationContext()).getHandler().addListener(this);


		presentationAdapter = new PresentationAdapter(((BigBlueButton)context.getApplicationContext()).getHandler().getPresentation());
		//\TODO class cast exception

		this.presentationName=presentationName;
		
		RelativeLayout relative = (RelativeLayout)findViewById(R.id.presentationView);
		ViewGroup.LayoutParams  params = relative.getLayoutParams();
		params.height=context.getResources().getDisplayMetrics().widthPixels; 
		//\TODO params.height = slideImage.getHeight(); when the image have the right size
		relative.setLayoutParams(params);
		relative.requestLayout(); 
		
		
		showSlide(currentSlide);
	}

	
	@Override
	public void onPresentationLoaded(String presentationName,ArrayList<ISlide> presentation, int currentSlide) {
		// TODO Auto-generated method stub
		

	}

	@Override
	public void onSlideChanged(int currentSlide) {

				log.debug("slide changed android presentation {}", currentSlide);
				showSlide(currentSlide);
			

	}

	

	@Override
	public void onPresentationRemoved() {

		Toast.makeText(context, R.string.presentation_removed, Toast.LENGTH_SHORT).show();
		presentationAdapter.getPresentation().clear();

		dismiss(); 
	}
	
	@Override
	public void onPresentationShared(String presentationName) {
		// TODO Auto-generated method stub
		
	}
	
	public void showSlide(final int currentSlide)
	{ slideImage.post(new Runnable() {
		public void run() {

			log.debug("show slide {}", currentSlide);
			currentSlideNum=currentSlide;
			slide =  (Slide) presentationAdapter.getPresentation().get(currentSlideNum); 
			byte[] bytes = ((BigBlueButton)context.getApplicationContext()).getHandler().getSlideData(slide);
			log.debug(slide.getThumbUri().toString());

			slideImage.setImageDrawable(getSlideDrawable(bytes));


		}
	}
	);


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
