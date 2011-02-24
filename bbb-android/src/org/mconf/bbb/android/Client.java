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

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.android.voip.VoiceModule;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.IParticipant;
import org.sipdroid.sipua.ui.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;


public class Client extends Activity implements IBigBlueButtonClientListener {
	private static final Logger log = LoggerFactory.getLogger(Client.class);

	public static final int MENU_QUIT = Menu.FIRST;
	public static final int MENU_LOGOUT = Menu.FIRST + 1;
	public static final int MENU_RAISE_HAND = Menu.FIRST + 2;
	public static final int MENU_START_VOICE = Menu.FIRST + 3;
	public static final int MENU_STOP_VOICE = Menu.FIRST + 4;
	public static final int MENU_MUTE = Menu.FIRST + 5;
	public static final int MENU_SPEAKER = Menu.FIRST + 6;
	
	public static final int CHAT_NOTIFICATION_ID = 77000;
	
	public static final String ACTION_OPEN_SLIDER = "org.mconf.bbb.android.Client.OPEN_SLIDER";
	private static final String FINISH = "bbb.android.action.FINISH";

	private static final String SEND_TO_BACK = "bbb.android.action.SEND_TO_BACK";

	public static final int KICK_USER = Menu.FIRST;
	public static final int MUTE_USER = Menu.FIRST+1;
	public static final int SET_PRESENTER = Menu.FIRST+2;

	

	

	

	
	
	public static BigBlueButtonClient bbb = new BigBlueButtonClient();
	protected ContactAdapter contactAdapter;
	protected ChatAdapter chatAdapter;

	protected String myusername;
	protected SlidingDrawer slidingDrawer;
	protected Button slideHandleButton;
	
	protected boolean loggedIn=true;

	private VoiceModule voice;
	
//	protected ClientBroadcastReceiver receiver = new ClientBroadcastReceiver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.debug("onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts_list);   
		slidingDrawer = (SlidingDrawer) findViewById(R.id.slide);
		slideHandleButton = (Button) findViewById(R.id.handle);

		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.cancel(CHAT_NOTIFICATION_ID);
			}
		});
		
		Bundle extras = getIntent().getExtras();
		myusername = extras.getString("username");
		Toast.makeText(getApplicationContext(),"Be welcome, " + myusername, Toast.LENGTH_SHORT).show(); 

		chatAdapter = new ChatAdapter(this);
		final ListView chatListView = (ListView)findViewById(R.id.messages);
		chatListView.setAdapter(chatAdapter);

		final ListView contactListView = (ListView)findViewById(R.id.list);
		contactAdapter = new ContactAdapter(this);
		contactListView.setAdapter(contactAdapter);
		registerForContextMenu(contactListView);

		Button send = (Button)findViewById(R.id.sendMessage);
		send.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View viewParam) {
				EditText chatMessageEdit = (EditText) findViewById(R.id.chatMessage);
				String chatMessage = chatMessageEdit.getText().toString();
				bbb.sendPublicChatMessage(chatMessage);
				chatMessageEdit.setText("");
				// it's not working correctly
				chatListView.setSelection(chatListView.getCount());
			}
		});

		contactListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				final Contact contact = (Contact) contactAdapter.getItem(position); 

				//se o ID da pessoa clicada for diferente do meu ID
				if (contact.getUserId() != bbb.getMyUserId())
					startPrivateChat(contact);
			}
		});
		
		Receiver.mContext = this;
		voice = new VoiceModule(this,
				bbb.getJoinService().getJoinedMeeting().getFullname(), 
				bbb.getJoinService().getServerUrl());
		
		bbb.addListener(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		final Contact contact = (Contact) contactAdapter.getItem(info.position);
		if (bbb.getUsersModule().getParticipants().get(bbb.getMyUserId()).isModerator()) {
			if (contact.getUserId() != bbb.getMyUserId()) {
				menu.add(0, KICK_USER, 0, "Kick");
				menu.add(0, MUTE_USER, 0, "Mute");
			}
			
			if (!contact.isPresenter())
				menu.add(0, SET_PRESENTER, 0, "Assign presenter");
		}
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		final Contact contact = (Contact) contactAdapter.getItem(info.position);
		log.debug("clicked on participant " + contact.toString());
		switch (item.getItemId()) {
			case KICK_USER:
				bbb.kickUser(contact.getUserId());
				return true;
			case MUTE_USER:
				Toast.makeText(this, "Not implemented feature", Toast.LENGTH_SHORT).show();
				return true;
			case SET_PRESENTER:
				bbb.assignPresenter(contact.getUserId());
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		log.debug("onDestroy");
		
		quit();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancelAll();	

//		unregisterReceiver(receiver);

		super.onDestroy();
	}
	
//	private class ClientBroadcastReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(ACTION_OPEN_SLIDER)) {
//				if (!slidingDrawer.isOpened())
//					slidingDrawer.open();
//			}
//		}
//		
//	}
	
	private void quit() {
		if (voice.isOnCall())
			voice.hang();
		bbb.removeListener(this);
		bbb.disconnect();
	}
	
	private void startPrivateChat(Contact contact) {

		Intent intent = new Intent(getApplicationContext(), PrivateChat.class);
		intent.putExtra("username", contact.getName());
		intent.putExtra("userId", contact.getUserId());
		intent.putExtra("notified", false);
		startActivity(intent);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (voice.isOnCall()) {
			if (voice.isMuted())
				menu.add(Menu.NONE, MENU_MUTE, Menu.NONE, "Unmute").setIcon(android.R.drawable.ic_lock_silent_mode_off);
			else
				menu.add(Menu.NONE, MENU_MUTE, Menu.NONE, "Mute").setIcon(android.R.drawable.ic_lock_silent_mode);
			if (voice.getSpeaker() == AudioManager.MODE_NORMAL)
				menu.add(Menu.NONE, MENU_SPEAKER, Menu.NONE, "Speaker").setIcon(android.R.drawable.button_onoff_indicator_on);
			else
				menu.add(Menu.NONE, MENU_SPEAKER, Menu.NONE, "Speaker").setIcon(android.R.drawable.button_onoff_indicator_off);
			menu.add(Menu.NONE, MENU_STOP_VOICE, Menu.NONE, "Stop voice").setIcon(android.R.drawable.ic_btn_speak_now);
		} else {
			menu.add(Menu.NONE, MENU_START_VOICE, Menu.NONE, "Start voice").setIcon(android.R.drawable.ic_btn_speak_now);
		}
		menu.add(Menu.NONE, MENU_RAISE_HAND, Menu.NONE, "Raise hand").setIcon(android.R.drawable.ic_menu_myplaces);
		menu.add(Menu.NONE, MENU_LOGOUT, Menu.NONE, "Logout").setIcon(android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, MENU_QUIT, Menu.NONE, "Quit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent= new Intent(FINISH);
		switch (item.getItemId()) {
			case MENU_START_VOICE:
				voice.call(bbb.getJoinService().getJoinedMeeting().getVoicebridge());
				break;
			case MENU_STOP_VOICE:
				voice.hang();
				break;
			case MENU_MUTE:
				voice.muteCall(!voice.isMuted());
				break;
			case MENU_SPEAKER:
				voice.setSpeaker(voice.getSpeaker() != AudioManager.MODE_NORMAL? AudioManager.MODE_NORMAL: AudioManager.MODE_IN_CALL);
				break;
			case MENU_LOGOUT:
				quit();
				Intent login = new Intent(this, LoginPage.class);
				login.putExtra("username", myusername);
				startActivity(login);
				sendBroadcast(intent);
				finish();
				return true;
			case MENU_QUIT:
				quit();
				sendBroadcast(intent);
				finish();
				return true;
				
			case MENU_RAISE_HAND:
				if (bbb.getUsersModule().getParticipants().get(bbb.getMyUserId()).isRaiseHand())
					bbb.raiseHand(false);
				else
					bbb.raiseHand(true);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		Intent intent = new Intent(SEND_TO_BACK);
    		sendBroadcast(intent);
    		log.debug("KEYCODE_BACK");
    		moveTaskToBack(true);
    		return true;
    	}    		
    	return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub

	}
	@Override
	public void onDisconnected() {

	}
	@Override
	public void onKickUserCallback() {
		// TODO Auto-generated method stub

	}
	@Override
	public void onParticipantJoined(final IParticipant p) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				contactAdapter.addSection(p);
				contactAdapter.sort();
				contactAdapter.notifyDataSetChanged();
			}
		});		
	}
	@Override
	public void onParticipantLeft(final IParticipant p) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				contactAdapter.removeSection(p);
				contactAdapter.sort();
				contactAdapter.notifyDataSetChanged();		
			}
		});
	}

	@Override
	public void onPrivateChatMessage(ChatMessage message, IParticipant source) {

		// if the message received was sent from me, don't show any notification
		if (message.getUserId() == bbb.getMyUserId())
			return;

		showNotification(message, source, true);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		log.debug("onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode);
		super.onActivityResult(requestCode, resultCode, data);		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		log.debug("onNewIntent");
		super.onNewIntent(intent);
		
		if (intent.getAction() == null)
			return;
		else if (intent.getAction().equals(ACTION_OPEN_SLIDER)) {
			if (!slidingDrawer.isOpened())
				slidingDrawer.open();			
		}
	}
	
	public void showNotification(final ChatMessage message, IParticipant source, final boolean privateChat) {
		// remember that source could be null! that happens when a user send a message and log out - the list of participants don't have the entry anymore
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// change the background color of the message source
				contactAdapter.setChatStatus(message.getUserId(), privateChat? Contact.CONTACT_ON_PRIVATE_MESSAGE: Contact.CONTACT_ON_PUBLIC_MESSAGE);
				contactAdapter.sort();
				contactAdapter.notifyDataSetChanged();
			}
		});		
		
		String contentTitle = "New " + (privateChat? "private": "public") + " message from " + message.getUsername();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon_bbb, contentTitle, System.currentTimeMillis());
		
		Intent notificationIntent = null;
		if (privateChat) {
			Contact contact = contactAdapter.getUserById(source.getUserId());			

			notificationIntent = new Intent(getApplicationContext(), PrivateChat.class);
			notificationIntent.putExtra("username", contact.getName());
			notificationIntent.putExtra("userId", contact.getUserId());
			notificationIntent.putExtra("notified", true);
			/**
			 *  http://groups.google.com/group/android-developers/browse_thread/thread/e61ec1e8d88ea94d
			 */
			notificationIntent.setData(Uri.parse("custom://"+SystemClock.elapsedRealtime())); 

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0, notificationIntent,Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			notification.setLatestEventInfo(getApplicationContext(), contentTitle, message.getMessage(), contentIntent);
			notificationManager.notify(CHAT_NOTIFICATION_ID + message.getUserId(), notification);
		} else {
			notificationIntent = new Intent(getApplicationContext(), Client.class);
			notificationIntent.setAction(ACTION_OPEN_SLIDER);
			
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
			notification.setLatestEventInfo(getApplicationContext(), contentTitle, message.getMessage(), contentIntent);
			notificationManager.notify(CHAT_NOTIFICATION_ID, notification);	
		}
	}
	
	public void dismissNotification(int userId) {
		
	}
	
	@Override
	public void onPublicChatMessage(final ChatMessage message, final IParticipant source) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chatAdapter.add(message);
				chatAdapter.notifyDataSetChanged();
			}
		});
		
		if (!slidingDrawer.isShown() || !slidingDrawer.isOpened())
			showNotification(message, source, false);
	}

	@Override
	public void onParticipantStatusChangePresenter(final IParticipant p) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				contactAdapter.getUserById(p.getUserId()).getStatus().setPresenter(p.getStatus().isPresenter());
				contactAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onParticipantStatusChangeHasStream(final IParticipant p) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				contactAdapter.getUserById(p.getUserId()).getStatus().setHasStream(p.getStatus().isHasStream());
				contactAdapter.getUserById(p.getUserId()).getStatus().setStreamName(p.getStatus().getStreamName());
				contactAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onParticipantStatusChangeRaiseHand(final IParticipant p) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				contactAdapter.getUserById(p.getUserId()).getStatus().setRaiseHand(p.getStatus().isRaiseHand());
				contactAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public boolean isLoggedIn()
	{
		return loggedIn;
	}
	
	public void setLoggedIn(boolean state)
	{
		loggedIn=state;
	}

}
