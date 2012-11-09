/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.android.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.BigBlueButtonClient.OnParticipantLeftListener;
import org.mconf.bbb.BigBlueButtonClient.OnPrivateChatMessageListener;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.IParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class PrivateChat extends BigBlueButtonActivity {



	private class RemoteParticipant implements
			OnParticipantLeftListener,
			OnPrivateChatMessageListener {
		private String userId;
		private int viewId;
		private String username;
		private ChatAdapter chatAdapter;

		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public int getViewId() {
			return viewId;
		}
		public void setViewId(int viewId) {
			this.viewId = viewId;
		}

		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public ChatAdapter getChatAdapter() {
			return chatAdapter;
		}
		public void setChatAdapter(ChatAdapter chatAdapter) {
			this.chatAdapter = chatAdapter;
		}
		@Override
		public void onParticipantLeft(final IParticipant p) {
			//se o participante que saiu é o que está sendo mostrado o chat
			if(p.getUserId().equals(userId) && getParticipantByViewId(flipper.getDisplayedChild()).getUserId().equals(userId) && !movedToBack) //null pointer exeption
			{
				
				showPartcicipantLeftDialog();//works fine
			} 
			
			//se o participante saiu, mas está por trás nas abas de chat
			else if(p.getUserId().equals(userId) && !getParticipantByViewId(flipper.getDisplayedChild()).getUserId().equals(userId))
			{
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						removeParticipant(p.getUserId());
					}
				});
			}

		}
		public void onPrivateChatMessage(final ChatMessage message) {
			log.debug("private message handled");
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
					chatAdapter.add(message);
					chatAdapter.notifyDataSetChanged();
				}
			});
		}
		@Override
		public void onPrivateChatMessage(final ChatMessage message,
				IParticipant source) {

			if (source.getUserId().equals(userId)) {
				onPrivateChatMessage(message);
				if (flipper.isShown() && flipper.getDisplayedChild() == viewId)
					cancelNotification(userId);
			}
		}
		public void registerListeners(BigBlueButtonClient bigBlueButton) {
			bigBlueButton.addParticipantLeftListener(this);
			bigBlueButton.addPrivateChatMessageListener(this);
		}
		public void unregisterListeners(BigBlueButtonClient bigBlueButton) {
			bigBlueButton.removeParticipantLeftListener(this);
			bigBlueButton.removePrivateChatMessageListener(this);
		}
	}

	private static final Logger log = LoggerFactory.getLogger(PrivateChat.class);

	// userId x remote participant
	protected static Map<String, RemoteParticipant> participants = new HashMap<String, RemoteParticipant>();

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 400;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	public static final int MENU_CLOSE = Menu.FIRST;

	public static final String CHAT_CLOSED = "bbb.android.action.CHAT_CLOSED";
	public static final String KICKED_USER = "bbb.android.action.KICKED_USER";

	private static final int INVALIDATED = 6678;


	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	// \TODO animations. still to resolve
	//	private Animation LeftIn;
	//	private Animation LeftOut;
	//	private Animation RightIn;
	//	private Animation RightOut;
	private ViewFlipper flipper; 
	private Context context= this;
	
	// \TODO review the needed of these kind of flags
	private boolean movedToBack=false;
	
	public static boolean hasUserOnPrivateChat(String userId)
	{
		for(RemoteParticipant part:participants.values())
		{
			if(part.getUserId().equals(userId))
				return true;
		}
		return false;
	}

	public void showPartcicipantLeftDialog() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				AlertDialog alert = new AlertDialog.Builder(context).create();
				alert.setTitle(R.string.participant_left);
				alert.setCancelable(false);
				alert.setIcon(android.R.drawable.ic_dialog_alert);
				alert.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						closeChat();
					}
				});

				alert.show();
			}
		});
	}

	//receivers of broadcast intents
	private BroadcastReceiver finishedReceiver = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			removeAllParticipants();
			finish(); // we finish PrivateChat here when receiving the broadcast 

		} 
	};

	

	private BroadcastReceiver moveToBack = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			log.debug("sent to back");
			moveTaskToBack(true); 
			movedToBack=true;
		} 
	};

	private BroadcastReceiver kickedUser = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			log.debug("closing a chat");
			Bundle extras = intent.getExtras();
			String userId = extras.getString("userId");
			if(hasUserOnPrivateChat(userId))
				removeParticipant(userId);
			
		} 
	};
	
	private int addView() {
		int index = flipper.getChildCount();
		flipper.addView(getView(), index);
		return index;
	}

	private void changeView(int index){
		flipper.addView(getView(), index);
	}


	private View getView() {
		return getLayoutInflater().inflate(R.layout.chat, null);   
	}

	//remove all the participants on the chat, when the aplication is closed
	private void removeAllParticipants() {
		participants.clear();
	}

	//remove one participant
	private void removeParticipant(String key) {
		if (!removeParticipant(participants.get(key))) {
			log.warn("Tryed to remove the participant {} from private chat, but couldn't find him", key);
		}
	}
	
	private boolean removeParticipant(RemoteParticipant p) {
		log.debug("Invalidating a remote participant");
		if (p == null) {
			log.warn("Removing participant that is null");
			return false;
		} else {
			p.unregisterListeners(getBigBlueButton());
			flipper.getChildAt(p.getViewId()).setId(INVALIDATED);
			participants.remove(p.getUserId());
			return true;
		}
	}

	//get the participant key associated with a viewFlipper view
	private String getParticipantKeyByViewId(int viewId) {
		for (RemoteParticipant p : participants.values()) {
			
			if (p.getViewId() == viewId)
				return p.getUserId();
		}
		return null;
	}

	private RemoteParticipant getParticipantByViewId(int viewId) {
		String key = getParticipantKeyByViewId(viewId);
		if (key != null)
			return participants.get(key);
		else
			return null;
	}

	//create a new participant when a new chat is opened
	private RemoteParticipant createParticipant(String userId, String username, boolean notified) {
		log.debug("creating a new remote participant");

		final RemoteParticipant p = new RemoteParticipant();
		p.setUserId(userId);
		p.setUsername(username);
		p.setViewId(addView());
		p.setChatAdapter(new ChatAdapter());
		participants.put(userId, p);

		//\TODO problem occurring here http://code.google.com/p/mconf/issues/detail?id=256
		List<ChatMessage> messages = getBigBlueButton().getChatModule().getPrivateChatMessage().get(userId);
		if (messages != null) {
			for (ChatMessage message : messages) {
				p.onPrivateChatMessage(message);
			}
		}
		


		final ListView chatListView = (ListView) flipper.getChildAt(p.getViewId()).findViewById(R.id.messages);
		chatListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		chatListView.setAdapter(p.getChatAdapter()); 

		p.registerListeners(getBigBlueButton());
		Button send = (Button) flipper.getChildAt(p.getViewId()).findViewById(R.id.sendMessage);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText text = (EditText) flipper.getChildAt(p.getViewId()).findViewById(R.id.chatMessage);
				if (text.getText().toString().length() > 0) {

					if(hasUserOnPrivateChat(p.getUserId()))
						getBigBlueButton().sendPrivateChatMessage(text.getText().toString(), p.getUserId());
					else
						Toast.makeText(getApplicationContext(), R.string.user_disconnected, Toast.LENGTH_SHORT).show();
					text.setText("");
					chatListView.setSelection(chatListView.getCount());
				}
			}
		});
		return p;
	}

	//show a specific participant, or creates him if he doesn't already exists
	private void displayView(Bundle extras) {
		String userId = extras.getString("userId");
		String username = extras.getString("username");
		boolean notified = extras.getBoolean("notified");

		setTitle(getResources().getString(R.string.private_chat_title) + username);		

		RemoteParticipant p = participants.get(userId);

		if (p == null)
		{
			if(getBigBlueButton().getChatModule()!=null)
				p = createParticipant(userId, username, notified);
			else
				chatModuleUnconnected();//will finish the activity
			
		}

		log.debug("displaying view of userId=" + userId + " and username=" + username);

		flipper.setDisplayedChild(p.getViewId()); //null pointer exception
		cancelNotification(userId);
	}
	
	private void chatModuleUnconnected()
	{
		//\TODO improve this implementation and track the real problem of http://code.google.com/p/mconf/issues/detail?id=256
		Toast.makeText(getApplicationContext(), R.string.chat_module_problem, Toast.LENGTH_SHORT).show();
		Intent bringBackClient = new Intent(this, Client.class);
		bringBackClient.setAction(Client.BACK_TO_CLIENT);
		startActivity(bringBackClient);
		finish(); 
	}

	private void orientationChanged() {
		flipper.removeAllViews();
		for(String userId:participants.keySet())
		{
			final RemoteParticipant p= participants.get(userId);
			p.setChatAdapter(new ChatAdapter());
			p.setViewId(addView());

			List<ChatMessage> messages = getBigBlueButton().getChatModule().getPrivateChatMessage().get(userId);
			if (messages != null)
			{
				for (ChatMessage message : messages) {
					p.onPrivateChatMessage(message);
				} 
			}

			final ListView chatListView = (ListView) flipper.getChildAt(p.getViewId()).findViewById(R.id.messages);
			chatListView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return gestureDetector.onTouchEvent(event);
				}
			});
			chatListView.setAdapter(p.getChatAdapter());
			p.registerListeners(getBigBlueButton());
			Button send = (Button) flipper.getChildAt(p.getViewId()).findViewById(R.id.sendMessage);
			send.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText text = (EditText) flipper.getChildAt(p.getViewId()).findViewById(R.id.chatMessage);
					if (text.getText().toString().length() > 0) {
						getBigBlueButton().sendPrivateChatMessage(text.getText().toString(), p.getUserId());
						text.setText("");
						chatListView.setSelection(chatListView.getCount());
					}
				}
			});

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_chat);


		//		Configuration config = getResources().getConfiguration();
		//		orientation = config.orientation;
		log.debug("ON CREATE");

		flipper = (ViewFlipper) findViewById(R.id.manyPages); 


		displayView(getIntent().getExtras());
		//animation on swippe, still need to resolve
		//		LeftIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
		//		LeftOut=AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
		//		RightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		//		RightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out);

		//gesture detector, to change the participant shown
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		registerFinishedReceiver();
		registerMoveToBackReceiver();
		registerKickedUser();
	}

	private void registerFinishedReceiver(){ 
		IntentFilter filter = new IntentFilter(Client.FINISH); 
		registerReceiver(finishedReceiver, filter); 
	}

	private void registerMoveToBackReceiver(){ 
		IntentFilter filter = new IntentFilter(Client.SEND_TO_BACK); 
		registerReceiver(moveToBack, filter); 
	}

	private void registerKickedUser(){
		IntentFilter filter = new IntentFilter(KICKED_USER);
		registerReceiver(kickedUser, filter);
	}
	
	@Override
	public void onDestroy() { 
		super.onDestroy(); 
		unregisterReceiver(finishedReceiver);
		unregisterReceiver(moveToBack);
		unregisterReceiver(kickedUser);
	}



	private void cancelNotification(String userId) {
		log.debug("cancelling notification from " + userId);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(Client.CHAT_NOTIFICATION_ID + userId.hashCode());
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		log.debug("ON NEW INTENT");
		System.out.println(flipper.getChildCount());
		displayView(intent.getExtras());
		movedToBack=false;

	}


	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(participants.size()>1)
			{
				System.out.println("SWIPE");
				try {
					if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
						return false;
					// right to left swipe
					if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						//flipper.setAnimation(LeftIn);
						//flipper.setOutAnimation(RightOut);

						do{
							flipper.showNext();
						}while(flipper.getChildAt(flipper.getDisplayedChild()).getId()==INVALIDATED);

						//left to right swipe
					}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

						//flipper.setAnimation(LeftOut);
						do{
							flipper.showPrevious();
						}while(flipper.getChildAt(flipper.getDisplayedChild()).getId()==INVALIDATED);
					} else
						return false;

					int viewId = flipper.getDisplayedChild();
					setTitle(getResources().getString(R.string.private_chat_title) + getParticipantByViewId(viewId).getUsername());
					ListView chatListView = (ListView) flipper.getChildAt(viewId).findViewById(R.id.messages);
					chatListView.setSelection(chatListView.getCount());
					return true;
				} catch (Exception e) {
					// nothing
				}
			}
			return false;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}


	//when a user hits BACK, need to go back to client activity
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent bringBackClient = new Intent(getApplicationContext(), Client.class);
			bringBackClient.setAction(Client.BACK_TO_CLIENT);
			movedToBack=true;
			startActivity(bringBackClient);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_CLOSE, 0, R.string.close_chat).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return result;
	}

	//to close a private chat
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_CLOSE:
			closeChat();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//		orientation = newConfig.orientation;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				setContentView(R.layout.private_chat);



				flipper = (ViewFlipper) findViewById(R.id.manyPages);


				//gesture detector, to change the participant shown
				gestureDetector = new GestureDetector(new MyGestureDetector());
				gestureListener = new View.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						return gestureDetector.onTouchEvent(event);
					}
				};
				orientationChanged();
				registerFinishedReceiver();
				registerMoveToBackReceiver();
				registerKickedUser();
			}


		});

	}

	private void closeChat() {
		log.debug("Closing a chat view");
		int viewID =flipper.getDisplayedChild();
		Intent chatClosed = new Intent(CHAT_CLOSED);
		RemoteParticipant participant = getParticipantByViewId(viewID);
		
		if(participant != null)
			chatClosed.putExtra("userId", participant.getUserId());
		else
			chatClosed.putExtra("userId", "");
		sendBroadcast(chatClosed);
		
		if(participants.size()>1)
		{
			
			do{
				flipper.showPrevious();		
			}while(flipper.getChildAt(flipper.getDisplayedChild()).getId()==INVALIDATED);
						
			removeParticipant(participant);
			viewID=flipper.getDisplayedChild();
			RemoteParticipant new_participant = getParticipantByViewId(viewID);
					
			setTitle(getResources().getString(R.string.private_chat_title) + new_participant.getUsername());
		}
		else 
		{
			Intent bringBackClient = new Intent(this, Client.class);
			bringBackClient.setAction(Client.BACK_TO_CLIENT);
			startActivity(bringBackClient);
			removeParticipant(participant);
			finish(); 
		}
	}

	
}



