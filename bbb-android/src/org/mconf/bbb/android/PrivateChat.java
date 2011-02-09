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
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
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


public class PrivateChat extends Activity {

	
	private class RemoteParticipant implements IBigBlueButtonClientListener {
		private int userId;
		private int viewId;
		private String username;
		private ChatAdapter chatAdapter;
		
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
		@SuppressWarnings("unused")
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
	
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeft;
	private Animation slideRight;
	private ViewFlipper flipper;

	private int addView() {
		int index = flipper.getChildCount();
		flipper.addView(getView(), index);
		return index;
	}
	
	private View getView() {
		return getLayoutInflater().inflate(R.layout.chat, null);        
	}
	
	private RemoteParticipant getParticipantByViewId(int viewId) {
		Iterator<Integer> it = participants.keySet().iterator(); 
		while(it.hasNext()) { 
			Object key = it.next();  
			RemoteParticipant part = participants.get(key);
			log.debug("view id: " + viewId + "partId: " + part.getViewId());
			if(viewId==part.getViewId())
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
		
		slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_right);

        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
		
		final ListView chatListView = (ListView) flipper.getChildAt(p.getViewId()).findViewById(R.id.messages);
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
		
		List<ChatMessage> messages = Client.bbb.getHandler().getChat().getPrivateChatMessage().get(userId);
		if (messages != null)
			for (ChatMessage message : messages) {
				p.onPrivateChatMessage(message);
			}

		flipper.setDisplayedChild(p.getViewId());
		cancelNotification(userId);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_chat);

		
		log.debug("creating first PrivateChat activity");

		flipper = (ViewFlipper) findViewById(R.id.manyPages); 
		
        
		displayView(getIntent().getExtras());
	}
	
	private void cancelNotification(int userId) {
		log.debug("cancelling notification from " + userId);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(Client.CHAT_NOTIFICATION_ID + userId);
	}

	@Override
	public void onNewIntent(Intent intent) {
		log.debug("onNewIntent");
		
		super.onNewIntent(intent);
		
		displayView(intent.getExtras());
	}
	 
	
	class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                int viewID;
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	flipper.setInAnimation(slideLeft);
                	flipper.setOutAnimation(slideLeft);
                	viewID =flipper.getChildAt(flipper.getDisplayedChild() + 1).getId();
                    
                    setTitle(getParticipantByViewId(viewID).getUsername());
            
                    flipper.showNext();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	flipper.setInAnimation(slideRight);
                	flipper.setOutAnimation(slideRight);
                	viewID =flipper.getChildAt(flipper.getDisplayedChild() -1).getId();
                   System.out.println("viewId: " + viewID);
                    	setTitle(getParticipantByViewId(viewID).getUsername());
                    
                	flipper.showPrevious();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
	        return true;
	    else
	    	return false;
    }
}



