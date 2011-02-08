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
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.IParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;


public class Client extends Activity implements IBigBlueButtonClientListener {
	private static final Logger log = LoggerFactory.getLogger(Client.class);

	public static final int MENU_QUIT = Menu.FIRST;
	public static final int MENU_LOGOUT = Menu.FIRST + 1;

	public static final int CHAT_NOTIFICATION_ID = 77000;
	
	public static final String ACTION_OPEN_SLIDER = "org.mconf.bbb.android.Client.OPEN_SLIDER";

	public static BigBlueButtonClient bbb = new BigBlueButtonClient();
	protected ContactAdapter contactAdapter;
	protected ChatAdapter chatAdapter;

	protected String myusername;
	protected SlidingDrawer slidingDrawer;
	protected Button slideHandleButton;
	
//	protected ClientBroadcastReceiver receiver = new ClientBroadcastReceiver();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				if (contact.getUserId() != bbb.getHandler().getMyUserId())
					startPrivateChat(contact);
			}
		});
		
		bbb.addListener(this);

//		IntentFilter i = new IntentFilter(Client.ACTION_OPEN_SLIDER);
//		registerReceiver(receiver, i);
//		log.debug("registering broadcast receiver");
	}
	
	@Override
	protected void onDestroy() {
		bbb.removeListener(this);
		bbb.disconnect();

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
	
	private void startPrivateChat(Contact contact) {

		Intent intent = new Intent(getApplicationContext(), PrivateChat.class);
		intent.putExtra("username", contact.getName());
		intent.putExtra("userId", contact.getUserId());
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_LOGOUT, 0, "Logout").setIcon(android.R.drawable.ic_menu_revert);
		menu.add(0, MENU_QUIT, 0, "Quit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return result;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_LOGOUT:
				Intent login = new Intent(this, LoginPage.class);
				login.putExtra("username", myusername);
				startActivity(login);
				finish();
				return true;
			case MENU_QUIT:
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
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
		if (message.getUserId() == bbb.getHandler().getMyUserId())
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
		
		if (intent.getAction().equals(ACTION_OPEN_SLIDER)) {
			if (!slidingDrawer.isOpened())
				slidingDrawer.open();			
		}
	}
	
	public void showNotification(ChatMessage message, IParticipant source, boolean privateChat) {
		// remember that source could be null! that happens when a user send a message and log out - the list of participants don't have the entry anymore
		
		// change the background color of the message source
		contactAdapter.setChatStatus(message.getUserId(), privateChat? Contact.CONTACT_ON_PRIVATE_MESSAGE: Contact.CONTACT_ON_PUBLIC_MESSAGE);
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				contactAdapter.sort();
				contactAdapter.notifyDataSetInvalidated();
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
			/**
			 *  http://groups.google.com/group/android-developers/browse_thread/thread/e61ec1e8d88ea94d
			 */
			notificationIntent.setData(Uri.parse("custom://"+SystemClock.elapsedRealtime())); 

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
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
				contactAdapter.setPresenterStatus(new Contact(p));
				contactAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onParticipantStatusChangeHasStream(final IParticipant p) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				contactAdapter.setStreamStatus(new Contact(p));
				contactAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onParticipantStatusChangeRaiseHand(final IParticipant p) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				contactAdapter.setRaiseHandStatus(new Contact(p));			
				contactAdapter.notifyDataSetChanged();
			}
		});
	}

}
