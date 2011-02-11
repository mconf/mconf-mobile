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

package org.mconf.bbb.android;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.IParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ViewFlipper;


public class PrivateChat extends Activity{

	

	private class RemoteParticipant implements IBigBlueButtonClientListener {
		private int userId;
		private int viewId;
		private String username;
		private ChatAdapter chatAdapter;
//		private boolean chatClosed=false;
//		private boolean notify =false;
//		
//		public void setChatClosed(boolean chatClosed) {
//			this.chatClosed = chatClosed;
//		}
//
//		public boolean isChatClosed() {
//			return chatClosed;
//		}
//		public void setNotify(boolean notify) {
//			this.notify = notify;
//		}
//
//		public boolean isNotify() {
//			return notify;
//		}
		public int getUserId() {
			return userId;
		}
		public void setUserId(int userId) {
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
		public void onConnected() {}
		@Override
		public void onDisconnected() {}
		@Override
		public void onKickUserCallback() {}
		@Override
		public void onParticipantJoined(IParticipant p) {}
		@Override
		public void onParticipantLeft(IParticipant p) {}
		@Override
		public void onParticipantStatusChangeHasStream(IParticipant p) {}
		@Override
		public void onParticipantStatusChangePresenter(IParticipant p) {}
		@Override
		public void onParticipantStatusChangeRaiseHand(IParticipant p) {}

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
			if (source.getUserId() == userId) {
				onPrivateChatMessage(message);
				if (flipper.isShown() && flipper.getDisplayedChild() == viewId)
					cancelNotification(userId);
			}
		}
		
		
		
		@Override
		public void onPublicChatMessage(ChatMessage message, IParticipant source) {}

		
	}

	private static final Logger log = LoggerFactory.getLogger(PrivateChat.class);

	// userId x remote participant
	protected static Map<Integer, RemoteParticipant> participants = new HashMap<Integer, RemoteParticipant>();


	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 400;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
//	public static final String ACTION_BRING_TO_FRONT = "org.mconf.bbb.android.Client.BRING_TO_FRONT";
	public static final int MENU_CLOSE = Menu.FIRST;


	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation LeftIn;
	private Animation LeftOut;
	private Animation RightIn;
	private Animation RightOut;
	private ViewFlipper flipper;
	
    
	
	
	BroadcastReceiver finishedReceiver = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			PrivateChat.this.finish(); // we finish PrivateChat here when receiving the broadcast 
		} 
	};

	BroadcastReceiver moveToBack = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			log.debug("sent to back");
			PrivateChat.this.moveTaskToBack(true); 
		} 
	};
		

	
	private int addView() {
		int index = flipper.getChildCount();
		flipper.addView(getView(), index);
		return index;
	}

	private View getView() {
		return getLayoutInflater().inflate(R.layout.chat, null);        
	}
	
	private void removeParticipant(Integer key)
	{
		participants.remove(key);
	}

	private Integer getParticipantKeyByViewId(int viewID)
	{
		Iterator<Integer> it = participants.keySet().iterator();

		while (it.hasNext()) {

			Integer key = it.next();
			RemoteParticipant part = participants.get(key);

			if (part.getViewId() == viewID)
				return key;
		}

		return null;
	}
	
	private RemoteParticipant getParticipantByViewId(int viewId) {

		Iterator<RemoteParticipant> it = participants.values().iterator();

		while (it.hasNext()) {

			RemoteParticipant part = it.next();


			if (part.getViewId() == viewId)
				return part;
		}

		return null;

	}

	private RemoteParticipant createParticipant(int userId, String username) {
		log.debug("creating a new remote participant");
		
		final RemoteParticipant p = new RemoteParticipant();
		p.setUserId(userId);
		p.setUsername(username);
		p.setViewId(addView());
		p.setChatAdapter(new ChatAdapter(this));
		participants.put(userId, p);
//		if(!p.isChatClosed()) //se o chat não foi fechado pelo usuário e tem notificações
		{
			List<ChatMessage> messages = Client.bbb.getHandler().getChat().getPrivateChatMessage().get(userId);
			if (messages != null)
				for (ChatMessage message : messages) {
					p.onPrivateChatMessage(message);
				}
		}
		
		
//		p.setChatClosed(false);
		final ListView chatListView = (ListView) flipper.getChildAt(p.getViewId()).findViewById(R.id.messages);
		chatListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		chatListView.setAdapter(p.getChatAdapter());
		Client.bbb.addListener(p);



		Button send = (Button) flipper.getChildAt(p.getViewId()).findViewById(R.id.sendMessage);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText text = (EditText) flipper.getChildAt(p.getViewId()).findViewById(R.id.chatMessage);
				if (text.getText().toString().length() > 0) {
					Client.bbb.sendPrivateChatMessage(text.getText().toString(), p.getUserId());
					text.setText("");
					chatListView.setSelection(chatListView.getCount());
				}
			}
		});
		return p;
	}

	private void displayView(Bundle extras) {
		int userId = extras.getInt("userId");
		String username = extras.getString("username");

		
		setTitle("Private chat with " + username);		

		RemoteParticipant p = participants.get(userId);
		if (p == null)
			p = createParticipant(userId, username);

		log.debug("displaying view of userId=" + userId + " and username=" + username);

		

		flipper.setDisplayedChild(p.getViewId());
		cancelNotification(userId);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_chat);


		log.debug("ON CREATE");

		flipper = (ViewFlipper) findViewById(R.id.manyPages); 

		
		displayView(getIntent().getExtras());
		LeftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		LeftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		RightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right);
		RightOut=AnimationUtils.loadAnimation(this, R.anim.fade);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		registerFinishedReceiver();
		registerMoveToBackReceiver();
	}

	private void registerFinishedReceiver(){ 
		IntentFilter filter = new IntentFilter("bbb.android.action.FINISH"); 
		registerReceiver(finishedReceiver, filter); 
	}
	
	private void registerMoveToBackReceiver(){ 
		IntentFilter filter = new IntentFilter("bbb.android.action.SEND_TO_BACK"); 
		registerReceiver(moveToBack, filter); 
	}
	
	@Override
	public void onDestroy() { 
		super.onDestroy(); 
		unregisterReceiver(finishedReceiver);
		unregisterReceiver(moveToBack);
	}
	
	
	
	private void cancelNotification(int userId) {
		log.debug("cancelling notification from " + userId);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(Client.CHAT_NOTIFICATION_ID + userId);
	}

	@Override
	public void onNewIntent(Intent intent) {
		

		super.onNewIntent(intent);

		log.debug("ON NEW INTENT");
		displayView(intent.getExtras());
		
	}


	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(participants.size()>1)
			{
				try {
					if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
						return false;
					// right to left swipe
					int viewID;
					if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						flipper.setInAnimation(LeftIn);
						flipper.setOutAnimation(LeftOut);
						flipper.showNext();
						viewID =flipper.getDisplayedChild();

						setTitle("Private chat with "+getParticipantByViewId(viewID).getUsername());


					}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						flipper.setInAnimation(RightIn);
						flipper.setOutAnimation(RightOut);
						flipper.showPrevious();
						viewID =flipper.getDisplayedChild();
						setTitle("Private chat with " +getParticipantByViewId(viewID).getUsername());


					}
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
	
	   
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		
    		Intent bringBackClient = new Intent(getApplicationContext(), Client.class);
//        	bringBackClient.setAction(ACTION_BRING_TO_FRONT);
        	startActivity(bringBackClient);
        	return true;
    	}
    	log.debug("killing");
    	return super.onKeyDown(keyCode, event);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_CLOSE, 0, "Close Chat").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case MENU_CLOSE:
				int viewID =flipper.getDisplayedChild();
				if(participants.size()>1)
				{
					flipper.showPrevious();
					removeParticipant(getParticipantKeyByViewId(viewID));
					viewID=flipper.getDisplayedChild();
					setTitle("Private chat with "+getParticipantByViewId(viewID).getUsername());
				}
				else
				{
//					Intent bringBackClient = new Intent(getApplicationContext(), Client.class);
//		        	bringBackClient.setAction(ACTION_BRING_TO_FRONT);
		        	removeParticipant(getParticipantKeyByViewId(viewID));
		        	finish();
//		        	startActivity(bringBackClient);
				}
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	
}



