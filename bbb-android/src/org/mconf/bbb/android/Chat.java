package org.mconf.bbb.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Chat extends Activity{
	final static int PUBLIC_CHAT = 1;
	final static int PRIVATE_CHAT= 0;
	int chatMode;
	
	private void setChatMode(int mode)
	{
		this.chatMode=mode;
	}
	
	private int getChatMode()
	{
		return this.chatMode;
	}
	
	public void addChatMessage()
	{
		
//		String[] contacts;
//		contacts = handler.getContacts(); //função que busca os cantatos logados na sala do BBB [a ser definida]
		// Now create an array adapter and set it to display using our row
//		this.setListAdapter(new ArrayAdapter<String>(this,R.layout.contact, contacts)); 

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        
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
