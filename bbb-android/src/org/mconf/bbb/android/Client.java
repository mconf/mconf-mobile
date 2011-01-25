package org.mconf.bbb.android;


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

public class Client extends ListActivity {
	public static final int CHAT_ID = Menu.FIRST;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.contacts_list);   
        
        
        Bundle extras = getIntent().getExtras();
        String Username = extras.getString("Username");
        
        Toast welcome = Toast.makeText(getApplicationContext(),"hello " + Username, Toast.LENGTH_LONG);  
        welcome.show(); 
        
        addContacts();


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
        menu.add(0, CHAT_ID, 0, "Chat Público");
        return result;
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case CHAT_ID:
            //abrir o chat
            return true;
        }
       
        return super.onOptionsItemSelected(item);
    }
	
	public void addContacts()
	{
		Mocap handler = new Mocap();
		String[] contacts;
		contacts = handler.getContacts(); //função que busca os cantatos logados na sala do BBB [a ser definida]
		// Now create an array adapter and set it to display using our row
		this.setListAdapter(new ArrayAdapter<String>(this,R.layout.contact, contacts)); 

	}
}
