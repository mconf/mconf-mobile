package org.mconf.bbb.android;

import java.util.List;

import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.IParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class PrivateChat extends Activity implements IBigBlueButtonClientListener {

	private static final Logger log = LoggerFactory.getLogger(PrivateChat.class);
	protected ChatAdapter chatAdapter;
	
	protected int userId;
	protected String username;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        Bundle extras = getIntent().getExtras();
        userId = extras.getInt("userId");
        username = extras.getString("username");
        
        setTitle("Private chat with " + username);

        log.debug("creating PrivateChat activity");
        
		chatAdapter = new ChatAdapter(this);
		final ListView chatListView = (ListView)findViewById(R.id.messages);
		chatListView.setAdapter(chatAdapter);

		Button send = (Button) findViewById(R.id.sendMessage);
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText text = (EditText) findViewById(R.id.chatMessage);
				if (text.getText().toString().length() > 0) {
					Client.bbb.sendPrivateChatMessage(text.getText().toString(), userId);
					text.setText("");
				}
			}
		});
		
		List<ChatMessage> messages = Client.bbb.getHandler().getChat().getPrivateChatMessage().get(userId);
		if (messages != null)
			for (ChatMessage message : messages) {
				onPrivateChatMessage(message);
			}
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(Client.PRIVATE_CHAT_NOTIFICATION_ID + userId);
   	}
	
	@Override
	protected void onStart() {
		super.onStart();
		log.debug("adding this as bbb listener");
        Client.bbb.addListener(this);		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		log.debug("removing this as bbb listener");
		Client.bbb.removeListener(this);
	}
	
	void onPrivateChatMessage(final ChatMessage message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chatAdapter.add(message);
				chatAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public void onPrivateChatMessage(final ChatMessage message, final IParticipant source) {
		if (source.getUserId()== userId) {
			onPrivateChatMessage(message);
		}
	}

	@Override
	public void onPublicChatMessage(ChatMessage message, IParticipant source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKickUserCallback() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onParticipantLeft(IParticipant p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onParticipantJoined(IParticipant p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onParticipantStatusChangePresenter(IParticipant p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onParticipantStatusChangeHasStream(IParticipant p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onParticipantStatusChangeRaiseHand(IParticipant p) {
		// TODO Auto-generated method stub
		
	}

}
		
		

