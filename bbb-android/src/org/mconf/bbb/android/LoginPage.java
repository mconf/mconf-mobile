package org.mconf.bbb.android;

import java.util.List;
import java.util.Properties;

import org.mconf.bbb.api.Meeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginPage extends Activity {
	
	private static final Logger log = LoggerFactory.getLogger(LoginPage.class);
	private ArrayAdapter<String> spinnerAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        
        final Spinner spinner = (Spinner) findViewById(R.id.login_spinner);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					updateMeetingsList();
				}
				return false;
			}
		});
        
        final Button join = (Button)findViewById(R.id.login_button_join);       
        join.setOnClickListener( new OnClickListener()
        {
               @Override
                public void onClick(View viewParam)
                {
                	EditText usernameEditText = (EditText) findViewById(R.id.login_edittext_name);
                	String username = usernameEditText.getText().toString();
                	                	 
                	if (username.length() < 1) {                		
                		Toast.makeText(getApplicationContext(),"Empty username", Toast.LENGTH_SHORT).show();  
                        return;
                	}
                	
                	if (spinner.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
                		Toast.makeText(getApplicationContext(),"Please select a meeting", Toast.LENGTH_SHORT).show();
                		return;
                	}

                	if (Client.bbbClient.join(Client.bbbClient.getMeetings().get(spinner.getSelectedItemPosition()), username, false) == null) {
	                	Toast.makeText(getApplicationContext(), "Can't join the meeting", Toast.LENGTH_SHORT);
	                	return;
	                }
	                
	                //chamar a outra view
	                Intent myIntent = new Intent(getApplicationContext(), Client.class);
	                myIntent.putExtra("username", username);
	                startActivity(myIntent);
         
	                finish();
                }
        	}
        );
    }
    
    private void updateMeetingsList() {
        Resources resources = getApplicationContext().getResources();
        AssetManager assetManager = resources.getAssets();

		Properties p = new Properties();
		try {
			p.load(assetManager.open("bigbluebutton.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Can't find the properties file", Toast.LENGTH_LONG);
			log.error("Can't find/load the properties file");
		}
			        
        if (!Client.bbbClient.load(p.getProperty("bigbluebutton.web.serverURL"), p.getProperty("beans.dynamicConferenceService.securitySalt"))) {
        	Toast.makeText(getApplicationContext(), "Can't load the properties file", Toast.LENGTH_LONG);
        	return;
        }
        
        final List<Meeting> meetings = Client.bbbClient.getMeetings();
        
        runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
		        spinnerAdapter.clear();
		        for (Meeting m : meetings) {
		        	spinnerAdapter.add(m.getMeetingID());
		        }
		        spinnerAdapter.notifyDataSetChanged();
			}
		});
    }
}
