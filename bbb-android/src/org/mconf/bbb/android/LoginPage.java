/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb.android;

import java.util.Comparator;
import java.util.List;

import org.mconf.bbb.api.Meeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class LoginPage extends Activity {
	
	private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

	public static final String SERVER_CHOSED ="org.mconf.bbb.android.Client.SERVER_CHOSED";
	
	private ArrayAdapter<String> spinnerAdapter;
	private boolean moderator;
//	private static final String labelCreateMeeting = "== Create a new meeting ==";
	private String username;
	private String serverURL="";
	BroadcastReceiver serverChosed = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			Bundle extras = intent.getExtras();
		    serverURL=extras.getString("serverURL");
		    Button serverView = (Button) findViewById(R.id.server);
		    serverView.setText(serverURL);
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
      
        Bundle extras = getIntent().getExtras();
        
        if (extras == null || extras.getString("username") == null)
        	username = "Android";
        else
        	username = extras.getString("username");
        
        final EditText editName = (EditText) findViewById(R.id.login_edittext_name);
        editName.setText(username);
        
        final Spinner spinner = (Spinner) findViewById(R.id.login_spinner);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					updateMeetingsList();
					return true;
				} 
				return false;
			}
		});
        
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				
//				// the create new meeting label
//				if (spinnerAdapter.getItem(position).equals(labelCreateMeeting)) {
//					final AlertDialog.Builder alert = new AlertDialog.Builder(LoginPage.this);
//					final EditText input = new EditText(LoginPage.this);
//					alert.setTitle("New meeting");
//					alert.setMessage("Enter the meeting name:");
//					alert.setView(input);
//					alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							runOnUiThread(new Runnable() {
//								
//								@Override
//								public void run() {
//									spinnerAdapter.add(input.getText().toString());
//									spinnerAdapter.notifyDataSetChanged();
//									spinner.setSelection(spinnerAdapter.getCount()-1);
//								}
//							});
//						}
//					});
//					alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							
//						}
//					});
//					alert.show();
//				}
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//			}
//		});
        
        final Button join = (Button) findViewById(R.id.login_button_join);       
        join.setOnClickListener( new OnClickListener()
        {
               @Override
                public void onClick(View viewParam)
                {
                	EditText usernameEditText = (EditText) findViewById(R.id.login_edittext_name);
                	String username = usernameEditText.getText().toString();
                	                	 
                	if (username.length() < 1) {
                		Toast.makeText(getApplicationContext(), R.string.login_empty_name, Toast.LENGTH_SHORT).show();  
                        return;
                	}
                	
                	if (spinner.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
                		Toast.makeText(getApplicationContext(), R.string.login_select_meeting, Toast.LENGTH_SHORT).show();
                		return;
                	}
                	
               		Client.bbb.getJoinService().join((String) spinner.getSelectedItem(), username, moderator);
               		if (Client.bbb.getJoinService().getJoinedMeeting() == null) {
	                	Toast.makeText(getApplicationContext(), R.string.login_cant_join, Toast.LENGTH_SHORT).show();
	                	return;
	                }
	                
	                Intent myIntent = new Intent(getApplicationContext(), Client.class);
	                myIntent.putExtra("username", username);
	                startActivity(myIntent);
         
	                finish();
                }
        	}
        );

        final Button server = (Button) findViewById(R.id.server);       
        server.setOnClickListener( new OnClickListener()
        {
        	@Override
            public void onClick(View viewParam)
            {
        		Intent intent = new Intent(getApplicationContext(), ServerChoosing.class);
        		log.debug("BACK_TO_SERVERS");
        		startActivity(intent);
        	}
        	
        }
        );
        
        
    	updateRoleOption();
    	RadioGroup role = (RadioGroup) findViewById(R.id.login_role);
    	role.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				updateRoleOption();
			}
		});
    	
		IntentFilter filter = new IntentFilter(SERVER_CHOSED); 
		registerReceiver(serverChosed, filter); 
    }
    
    @Override
    protected void onDestroy() {
    	unregisterReceiver(serverChosed);
    	super.onDestroy();
    }
    
    private void updateRoleOption() {
		RadioButton moderator = (RadioButton) findViewById(R.id.login_role_moderator);
		if (moderator.isChecked())
			this.moderator = true;
		else
			this.moderator = false;
    }
    
    private void updateMeetingsList() {
    	final ProgressDialog progressDialog = new ProgressDialog(this);
    	progressDialog.setTitle(R.string.wait);
    	progressDialog.setMessage(getResources().getString(R.string.login_updating));

    	final Thread updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
//		        Resources resources = getApplicationContext().getResources();
//		        AssetManager assetManager = resources.getAssets();
//
//				Properties p = new Properties();
//				try {
//					p.load(assetManager.open("bigbluebutton.properties"));
//				} catch (Exception e) {
//		        	progressDialog.dismiss();
//		        	runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							Toast.makeText(getApplicationContext(), "Can't find the properties file", Toast.LENGTH_SHORT).show();							
//						}
//					});
//					log.error("Can't find the properties file");
//					return;
//				}
//					        
		        if (!Client.bbb.getJoinService().load(serverURL)) {
		        	progressDialog.dismiss();
		        	runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), R.string.login_cant_contact_server, Toast.LENGTH_SHORT).show();
						}
					});
					log.error("Can't contact the server. Try it later");
		        	return;
		        }
		        
		        if (Thread.interrupted())
		        	return;

				final List<Meeting> meetings = Client.bbb.getJoinService().getMeetings();

				progressDialog.dismiss();
				
		        runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
				        spinnerAdapter.clear();
				        for (Meeting m : meetings) {
				        	spinnerAdapter.add(m.getMeetingID());
				        }
				        spinnerAdapter.sort(new Comparator<String>() {
							
							@Override
							public int compare(String s1, String s2) {
								return s1.compareTo(s2);
							}
						});
//				        spinnerAdapter.add(labelCreateMeeting);
				        spinnerAdapter.notifyDataSetChanged();
				        Spinner spinner = (Spinner) findViewById(R.id.login_spinner);
				        spinner.performClick();
					}
				});
			}
		});
		
    	progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateThread.interrupt();
				progressDialog.dismiss();
			}
		});
    	
    	progressDialog.show();
    	updateThread.start();		
    }
    
    
    
    
}
