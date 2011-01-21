package org.mconf.bbb.android;


import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BBBandroid extends ListActivity {
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

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
          }
        });
      
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, CHAT_ID, 0, "chat");
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
		String[] contacts;
		contacts = getContacts(); //função que busca os cantatos logados na sala do BBB [a ser definida]
		// Now create an array adapter and set it to display using our row
		this.setListAdapter(new ArrayAdapter<String>(this,
				R.layout.contact, contacts)); 

	}
}
