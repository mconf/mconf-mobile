package org.mconf.bbb.android;


import org.mconf.bbb.BigBlueButtonClient;
import org.mconf.bbb.IBigBlueButtonClientListener;
import org.mconf.bbb.chat.ChatMessage;
import org.mconf.bbb.users.Participant;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Client extends ListActivity implements IBigBlueButtonClientListener {
	public static final int MENU_PUBLIC_CHAT = Menu.FIRST;
	public static final int MENU_QUIT = MENU_PUBLIC_CHAT + 1;
	
	public static BigBlueButtonClient bbbClient = new BigBlueButtonClient();
	private ArrayAdapter<String> listViewAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.contacts_list);   
        
        
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        Toast.makeText(getApplicationContext(),"Be welcome, " + username, Toast.LENGTH_SHORT).show(); 
        
//        listViewAdapter = new ArrayAdapter<String>(this, R.layout.contact);
        listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView view = getListView();
        view.setTextFilterEnabled(true);
        view.setAdapter(listViewAdapter);
        
        bbbClient.addListener(this);
        
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        final Context context = this;
        lv.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {


        		// When clicked, show a dialog to confirm the private chat
        		// set the message to display
        		AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
        		alertbox.setMessage("Start private chat with " + ((TextView) view).getText() +"?");

        		// add a neutral button to the alert box and assign a click listener
        		alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				//start private chat
        				System.out.println("private chat");
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
	            break;
	        case MENU_QUIT:
	        	bbbClient.removeListener(this);
	        	bbbClient.disconnect();
	        	finish();
	        	break;
        }
       
        return super.onOptionsItemSelected(item);
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
	public void onParticipantJoined(final Participant p) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				listViewAdapter.notifyDataSetChanged();
				listViewAdapter.add(p.getName());
			}
		});		
	}
	@Override
	public void onParticipantLeft(final Participant p) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				listViewAdapter.notifyDataSetChanged();
				listViewAdapter.remove(p.getName());
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
