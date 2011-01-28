package org.mconf.bbb.android;


import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.IParticipant;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;

public class Client extends Activity implements IBigBlueButtonClientListener {
	public static final int MENU_PUBLIC_CHAT = Menu.FIRST;
	public static final int MENU_PRIVATE_CHAT = Menu.FIRST + 1;
	public static final int MENU_QUIT = Menu.FIRST + 2;
	
	public static final int PUBLIC_CHAT = 1;
	public static final int PRIVATE_CHAT= 0;

	public static BigBlueButtonClient bbbClient = new BigBlueButtonClient();
	private ContactAdapter adapter;
	public String contactName= new String();
	List<IParticipant> listOfContacts;
	int userID;
	String username = new String();
	final static int NOTIFICATION_ID = 1;
	
	public String getContactName()
	{
		return this.contactName;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contacts_list);   
        
        
        Bundle extras = getIntent().getExtras();
       username = extras.getString("username");
        Toast.makeText(getApplicationContext(),"Be welcome, " + username, Toast.LENGTH_SHORT).show(); 
        
        ListView list = (ListView)findViewById(R.id.list);
        
         listOfContacts = new ArrayList<IParticipant>();
        adapter = new ContactAdapter(this, listOfContacts);
        
        

        bbbClient.addListener(this);
        
        list.setAdapter(adapter);
        
                       
        final Context context = this;
        list.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {

        		
        		// When clicked, show a dialog to confirm the private chat
        		// set the message to display
        		AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
        		
        		alertbox.setMessage("Start private chat with " + listOfContacts.get(position).getName() +"?");
        		contactName = listOfContacts.get(position).getName();
        		userID = listOfContacts.get(position).getUserId();
        		// add a neutral button to the alert box and assign a click listener
        		alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				//start private chat
        				Intent chatIntent = new Intent(getApplicationContext(), PrivateChat.class);
		                
        				chatIntent.putExtra("contactName", contactName);
		                chatIntent.putExtra("myName", username);
		                chatIntent.putExtra("userID", userID );
		                chatIntent.putExtra("chatMessage", "");
		                
		                startActivityForResult(chatIntent, 0);
        				
        			}
        		});
        		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				dialog.cancel();
        			}
        		});

        		// show it

        		alertbox.show();

        	}

        });
      
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PUBLIC_CHAT, 0, "Public chat");
        menu.add(0, MENU_QUIT, 0, "Quit");
        return result;
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case MENU_PUBLIC_CHAT:
	            //abrir o chat
            	Intent chatIntent = new Intent(getApplicationContext(), PrivateChat.class);
                chatIntent.putExtra("chatMode", PUBLIC_CHAT);
                startActivityForResult(chatIntent, 0);
                
                return true;
	        case MENU_QUIT:
	        	bbbClient.removeListener(this);
	        	bbbClient.disconnect();
	        	finish();
                return true;
        }
       
        return super.onOptionsItemSelected(item);
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
				adapter.addSection(p);
				adapter.notifyDataSetChanged();
			}
		});		
	}
	@Override
	public void onParticipantLeft(final IParticipant p) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				adapter.removeSection(p);
				adapter.notifyDataSetChanged();		
			}
		});
	}
	
	
	
	@Override
	public void onPrivateChatMessage(ChatMessage message, IParticipant source) {
		 CharSequence title = "Hello";
	        CharSequence messagewhat = "Hello, Android!";
	 
	        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	        Notification notification = new Notification(R.drawable.icon_bbb, "New Private Message!", System.currentTimeMillis());
	 
	        Intent notificationIntent = new Intent(this, PrivateChat.class);
	        
	        notificationIntent.putExtra("chatMessage", message.getMessage());
	        notificationIntent.putExtra("contactName", source.getName());
	        notificationIntent.putExtra("myName", username);
	        notificationIntent.putExtra("userID", source.getUserId() );
	        
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

	        
            
	        notification.setLatestEventInfo(getApplicationContext(), title, messagewhat, contentIntent);
	        notificationManager.notify(NOTIFICATION_ID, notification);
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPublicChatMessage(ChatMessage message, IParticipant source) {
		// TODO Auto-generated method stub

	}

	
	
	
	public BigBlueButtonClient getBbbClient()
	{
		return this.bbbClient;
	}

	@Override
	public void onParticipantStatusChangePresenter(final IParticipant p) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				
				adapter.setPresenterStatus((Contact) p);
				//cast exception
			}
		});
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
