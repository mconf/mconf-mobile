package org.mconf.bbb.android;


import java.util.ArrayList;
import java.util.List;

import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.Participant;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.contacts_list);   
        
        
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        Toast.makeText(getApplicationContext(),"Be welcome, " + username, Toast.LENGTH_SHORT).show(); 
        
        ListView list = (ListView)findViewById(R.id.list);
        
        List<Contact> listOfContacts = new ArrayList<Contact>();
        adapter = new ContactAdapter(this, listOfContacts);
        
        
//       listViewAdapter = new ArrayAdapter<String>(this, R.layout.contact);
   /*     listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView view = getListView();
        view.setTextFilterEnabled(true);
        view.setAdapter(listViewAdapter);
        */
        bbbClient.addListener(this);
        
        list.setAdapter(adapter);
        
        
        //ListView lv = getListView();
        //lv.setTextFilterEnabled(true);
        
        final Context context = this;
        list.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {


        		// When clicked, show a dialog to confirm the private chat
        		// set the message to display
        		AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
        		alertbox.setMessage("Start private chat with " + ((TextView) view).getText() +"?");
        		contactName = (String) ((TextView) view).getText();
        		// add a neutral button to the alert box and assign a click listener
        		alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				//start private chat
        				Intent chatIntent = new Intent(getApplicationContext(), Chat.class);
		                chatIntent.putExtra("contactName", contactName);
		                chatIntent.putExtra("chatMode", PRIVATE_CHAT);
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
            	Intent chatIntent = new Intent(getApplicationContext(), Chat.class);
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
	public void onParticipantJoined(final Participant p) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				
				adapter.notifyDataSetChanged();
				System.out.println("novo");
				 adapter.addSection(p);
			}
		});		
	}
	@Override
	public void onParticipantLeft(final Participant p) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				// modificar adapter.remove(p.getName());
			}
		});
	}
	@Override
	public void onPrivateChatMessage(ChatMessage message, Participant source) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPublicChatMessage(ChatMessage message, Participant source) {
		// TODO Auto-generated method stub
		
	}
}
