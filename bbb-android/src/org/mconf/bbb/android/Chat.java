package org.mconf.bbb.android;

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
import org.mconf.bbb.chat.ChatModule;


public class Chat extends Activity{
	final static int PUBLIC_CHAT = 1;
	final static int PRIVATE_CHAT= 0;
	int chatMode;
	private ArrayAdapter<String> listViewAdapter;
	
	private void setChatMode(int mode)
	{
		this.chatMode=mode;
	}
	
	private int getChatMode()
	{
		return this.chatMode;
	}
	
	public void addChatMessage(String message)
	{
		
		listViewAdapter.notifyDataSetChanged();
		listViewAdapter.add(message);
		
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
        
        Bundle extras = getIntent().getExtras();
        int mode = extras.getInt("chatMode");
        TextView chatMode;
        if(mode==PUBLIC_CHAT)
			{
        	chatMode=(TextView) findViewById(R.id.chatMode);
        	chatMode.setText("Public chat");
        	this.setChatMode(mode);
			}
		if(mode==PRIVATE_CHAT)
		{
			 String contactName = extras.getString("contactName");
			 chatMode=(TextView) findViewById(R.id.chatMode);
			 chatMode.setText("Private chat with " + contactName);
			 this.setChatMode(mode);
		}
		
		Button send = (Button)findViewById(R.id.sendMessage);
		send.setOnClickListener( new OnClickListener()
        {
               @Override
                public void onClick(View viewParam)
                {
					EditText chatMessageEdit = (EditText) findViewById(R.id.chatMessage);
			    	String chatMessage = chatMessageEdit.getText().toString();
			    	System.out.println(chatMessage);
			    	addChatMessage(chatMessage);
			    	chatMessageEdit.setText("");
					//mandar a mensagem
			    	//if(this.getChatMode()==PUBLIC_CHAT)
			    		//mandar para todos
			    	//else
			    		
                }
        }
		);
	}
}
