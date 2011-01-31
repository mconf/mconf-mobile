package org.mconf.bbb.android;

import java.util.StringTokenizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.chat.ChatModule;
import org.mconf.bbb.users.IParticipant;


public class PrivateChat extends Activity implements IBigBlueButtonClientListener {
	final static int PUBLIC_CHAT = 1;
	final static int PRIVATE_CHAT= 0;
	String myName;
	private ArrayAdapter<String> listViewAdapter;
	int userID;
	String contactName;
	String chatMessage;
	
	
	public String getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	private void setMyName(String _myName)
	{
		this.myName=_myName;
	}
	
	private String getMyName()
	{
		return this.myName;
	}
	
	
	public void sendPrivateChatMessage(String message, int userID)
	{
		
		listViewAdapter.notifyDataSetChanged();
		listViewAdapter.add(getMyName()+": " + message);
		Client.bbbClient.sendPrivateChatMessage(message, userID);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat);
        
        listViewAdapter = new ArrayAdapter<String>(this, R.layout.chat_message);
        ListView view = (ListView)findViewById(R.id.messages);
        view.setTextFilterEnabled(true);
        view.setAdapter(listViewAdapter);
        
        Client.bbbClient.addListener(this);
        
        Bundle extras = getIntent().getExtras();
        setMyName( extras.getString("myName"));
        
        
        TextView chatMode;
        setContactName( extras.getString("contactName"));
        chatMode=(TextView) findViewById(R.id.chatMode);
        chatMode.setText("Private chat with " + contactName);
                
        setChatMessage(extras.getString("chatMessage"));
        if(getChatMessage().length()> 1)
        {
        	listViewAdapter.notifyDataSetChanged();
			listViewAdapter.add(getContactName()+": " + getChatMessage());
        }
        
        userID = extras.getInt("userID");
        Button send = (Button)findViewById(R.id.sendMessage);
        send.setOnClickListener( new OnClickListener()
        {
        	@Override
        	public void onClick(View viewParam)
        	{
        		EditText chatMessageEdit = (EditText) findViewById(R.id.chatMessage);
        		String chatMessage = chatMessageEdit.getText().toString();
        		sendPrivateChatMessage(chatMessage, userID);
        		chatMessageEdit.setText("");

        	}
        }
        );
        
          
	}
	
	@Override
	public void onPrivateChatMessage(final ChatMessage message, final IParticipant source) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(source.getUserId()== userID)
				{
					
				String realMessage = new String(); 
				realMessage = message.getMessage();
				listViewAdapter.notifyDataSetChanged();
				listViewAdapter.add(message.getUsername()+": " + realMessage);
				}
				else
				{
					//cria outra tab, com outro chat privado
					//se já existe a tab, só abre ela
				}
			}
		});
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
		
		

