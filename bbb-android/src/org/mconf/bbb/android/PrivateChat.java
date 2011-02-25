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
		private boolean notified =false;
		

		public void setNotified(boolean notified) {
			this.notified = notified;
		}

		public boolean isNotified() {
			return notified;
		}
		
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

	public static final int MENU_CLOSE = Menu.FIRST;

	private static final String FINISH = "bbb.android.action.FINISH";

	private static final String SEND_TO_BACK = "bbb.android.action.SEND_TO_BACK";

	public static final String CHAT_CLOSED = "bbb.android.action.CHAT_CLOSED";

	public static final String KICKED_USER = "bbb.android.action.KICKED_USER";


	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation LeftIn;
	private Animation LeftOut;
	private Animation RightIn;
	private Animation RightOut;
	private ViewFlipper flipper;
	
	public static boolean hasUserOnPrivateChat(int userId)
	{
		for(RemoteParticipant part:participants.values())
		{
			if(part.getUserId()==userId)
				return true;
		}
		return false;
	}
	
	
	BroadcastReceiver finishedReceiver = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			removeAllParticipants();
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
		
	BroadcastReceiver kickedUser = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			log.debug("closing a chat");
			Bundle extras = intent.getExtras();
			int userId = extras.getInt("userId");
			if(hasUserOnPrivateChat(userId))
				removeParticipant(userId);
			
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
	
	private void removeAllParticipants()
	{
		for(Iterator<Integer> iter=participants.keySet().iterator(); iter.hasNext();)
		{
			Integer key = iter.next();
			if(participants.containsKey(key))
				iter.remove();
		}
			
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

	private RemoteParticipant createParticipant(int userId, String username, boolean notified) {
		log.debug("creating a new remote participant");
		
		final RemoteParticipant p = new RemoteParticipant();
		p.setUserId(userId);
		p.setUsername(username);
		p.setViewId(addView());
		p.setChatAdapter(new ChatAdapter(this));
		participants.put(userId, p);
		if(notified)
			p.setNotified(true);
		else
			p.setNotified(false);
		
		if(p.isNotified()) //se há uma notificação
		{
			List<ChatMessage> messages = Client.bbb.getChatModule().getPrivateChatMessage().get(userId);
			if (messages != null)
			{
				
				for (ChatMessage message : messages) {
					if(message.getUserId()!=Client.bbb.getMyUserId())
						p.onPrivateChatMessage(message);
				}
				
						
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
		boolean notified = extras.getBoolean("notified");
		
		setTitle(getResources().getString(R.string.private_chat_title) + username);		

		RemoteParticipant p = participants.get(userId);
		
		
		if (p == null)
			p = createParticipant(userId, username, notified);
	
		log.debug("displaying view of userId=" + userId + " and username=" + username);

		flipper.setDisplayedChild(p.getViewId());
		
		cancelNotification(userId);
		p.setNotified(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_chat);


		log.debug("ON CREATE");

		flipper = (ViewFlipper) findViewById(R.id.manyPages); 

		
		displayView(getIntent().getExtras());
		LeftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		LeftOut=AnimationUtils.loadAnimation(this, R.anim.push_left_out);
		RightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		RightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out);
		
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
		IntentFilter filter = new IntentFilter(FINISH); 
		registerReceiver(finishedReceiver, filter); 
	}
	
	private void registerMoveToBackReceiver(){ 
		IntentFilter filter = new IntentFilter(SEND_TO_BACK); 
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
					if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						//flipper.setInAnimation(RightIn);
						//flipper.setOutAnimation(RightOut);
						flipper.showNext();
					}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
						//flipper.setInAnimation(LeftIn);
						//flipper.setOutAnimation(LeftOut);
						flipper.showPrevious();
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
	
	   
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		
    		Intent bringBackClient = new Intent(getApplicationContext(), Client.class);

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case MENU_CLOSE:
				int viewID =flipper.getDisplayedChild();
				Intent chatClosed = new Intent(CHAT_CLOSED);
				chatClosed.putExtra("userId", getParticipantByViewId(viewID).getUserId());
				sendBroadcast(chatClosed);
				if(participants.size()>1)
				{
					flipper.showPrevious();
					removeParticipant(getParticipantKeyByViewId(viewID));
					viewID=flipper.getDisplayedChild();
					setTitle(getResources().getString(R.string.private_chat_title) + getParticipantByViewId(viewID).getUsername());
				}
				else
				{

		        	removeParticipant(getParticipantKeyByViewId(viewID));
		        	finish();

				}
				
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	
}



