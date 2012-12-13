/*
mobil * GT-Mconf: Multiconference system for interoperable web and mobile
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

package org.mconf.android.bbbandroid;

import java.util.Comparator;
import java.util.List;

import org.mconf.android.core.AboutDialog;
import org.mconf.android.core.BarcodeHandler;
import org.mconf.android.core.BigBlueButtonActivity;
import org.mconf.android.core.Client;
import org.mconf.android.core.NetworkPropertiesDialog;
import org.mconf.android.core.ReportCrashDialog;
import org.mconf.bbb.api.Meeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
//page where the user chooses the room, the name, and connects to a conference
public class LoginPage extends BigBlueButtonActivity {
	
	private final int E_OK = 0;
	//private final int E_CHECKSUM_NOT_INFORMED = 1;
	private final int E_INVALID_CHECKSUM = 2;
	//private final int E_INVALID_TIMESTAMP = 3;
	private final int E_EMPTY_SECURITY_KEY = 4;
	//private final int E_MISSING_PARAM_MEETINGID = 5;
	//private final int E_MISSING_PARAM_FULLNAME = 6;
	//private final int E_MISSING_PARAM_PASSWORD = 7;
	//private final int E_MISSING_PARAM_TIMESTAMP = 8;
	//private final int E_INVALID_URL = 9;
	//private final int E_SERVER_UNREACHABLE = 10;
	private final int E_MOBILE_NOT_SUPPORTED = 11;
	//public static final int E_UNKNOWN_ERROR = 12;

	private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

	public static final String SERVER_CHOSED ="org.mconf.bbb.android.Client.SERVER_CHOSED";
	public static final int MENU_QR_CODE = Menu.FIRST;
	public static final int MENU_ABOUT = Menu.FIRST + 1;

	private ArrayAdapter<String> spinnerAdapter;
	private boolean moderator;
	private String username = "Android";
	private String serverUrl = "";
	private String serverPassword = "";
	private String createdMeeting = "";	
	
	BroadcastReceiver serverChosed = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			Bundle extras = intent.getExtras();
			serverUrl = extras.getString("serverUrl");
			serverPassword = extras.getString("serverPassword");
			Button serverView = (Button) findViewById(R.id.server);
			serverView.setText(serverUrl);
			storePreferences(username, serverUrl, serverPassword);
		}
	};

	private BarcodeHandler barcodeHandler;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		// get username and server if already saved on the preferences file
		loadUserPreferences();

		new ReportCrashDialog(this).show();

		final EditText editName = (EditText) findViewById(R.id.login_edittext_name);
		editName.setText(username);
		Button serverView = (Button) findViewById(R.id.server);
		if(serverUrl.length() > 3)
			serverView.setText(serverUrl);
		else
			serverView.setText(R.string.choose_a_server);

		final Spinner spinner = (Spinner) findViewById(R.id.login_spinner);
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		spinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					spinnerAdapter.clear();
					updateMeetingsList();
					return true;
				} 
				return false;
			}
		});

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				// create a new meeting is the last option on the list
				if (position == spinnerAdapter.getCount() - 1) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(LoginPage.this);
					LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					final EditText input = new EditText(LoginPage.this);
					input.setLayoutParams(params);
					
					// need to use a linear layout to set padding
					final LinearLayout layout = new LinearLayout(LoginPage.this);
					layout.setPadding(40, 0, 40, 0);
					layout.setLayoutParams(params);
					layout.addView(input);
					
					alert.setMessage(R.string.new_meeting);
					alert.setView(layout);
					alert.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							createdMeeting = input.getText().toString().trim();
							
							if (getBigBlueButton().getJoinService().createMeeting(createdMeeting) != E_OK) { //.
								AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
								builder.setCancelable(false)
								       .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
								           public void onClick(DialogInterface dialog, int id) {
								                dialog.cancel();
								           }
								       });
								builder.setMessage(R.string.error);
								builder.show();
								
								return;
							}
							
							updateMeetingsList();
						}
					});
					alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							spinnerAdapter.clear();
						}
					});
					alert.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        
      

        
		final Button join = (Button) findViewById(R.id.login_button_join);       
		join.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View viewParam)
			{
				EditText usernameEditText = (EditText) findViewById(R.id.login_edittext_name);
				username = usernameEditText.getText().toString();

				if (username.length() < 1) {
					Toast.makeText(getApplicationContext(), R.string.login_empty_name, Toast.LENGTH_SHORT).show();  
					return;
				}

				if (spinner.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
					Toast.makeText(getApplicationContext(), R.string.login_select_meeting, Toast.LENGTH_SHORT).show();
					return;
				}

				String meetingId = (String) spinner.getSelectedItem();

				storePreferences(username, serverUrl, serverPassword);

				Intent myIntent = new Intent(getApplicationContext(), Client.class);
				myIntent.putExtra("username", username);
				myIntent.putExtra("moderator", moderator);
				myIntent.putExtra("meetingId", meetingId);
				log.debug(meetingId);
				startActivity(myIntent);
			}
		});
		//button to change the server
		final Button server = (Button) findViewById(R.id.server);       
		server.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View viewParam) {
				spinnerAdapter.clear();
				Intent intent = new Intent(getApplicationContext(), ServerChoosing.class);
				log.debug("BACK_TO_SERVERS");
				startActivity(intent);
			}

		});


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
		
		barcodeHandler = new BarcodeHandler(getApplicationContext());
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

	private void showToast(final int resource) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), resource, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void updateMeetingsList() {
		if (serverUrl.length() < 1) {
			showToast(R.string.choose_a_server_to_login);
			return;
		}
		
		if (isNetworkDown()) {
			NetworkPropertiesDialog networkProperties = new NetworkPropertiesDialog(LoginPage.this);
			networkProperties.show();
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.wait);
		progressDialog.setMessage(getResources().getString(R.string.login_updating));
		
		
		final Handler h = new Handler() {

			@Override
			public void handleMessage(Message msg) {
	            	showTimestampFailedDialog(setCorrectMsg(msg.what));	                				
	        }
			
			private String setCorrectMsg(int errorCode)
			{
				String message= getString(R.string.server_unreachable);
				switch(errorCode) 
				{
	            	case E_INVALID_CHECKSUM:
	            		message = getString(R.string.checksum_error);
	            		break;
	            		
	            	case E_EMPTY_SECURITY_KEY:
	            		message = getString(R.string.empty_security_key);
	            		break;
	            		
	            	case E_MOBILE_NOT_SUPPORTED:
	            		message = getString(R.string.mobile_not_supported);
	            		break;
	            		
//	            	case E_CHECKSUM_NOT_INFORMED:
//	            		//message = 
//	            		break;
//	            		
//	            	case E_INVALID_TIMESTAMP:
//	            		//message = 
//	            		break;
//	            		
//	            	case E_MISSING_PARAM_MEETINGID:
//	            		//message = 
//	            		break;
//	            		
//	            	case E_MISSING_PARAM_FULLNAME:
//	            		//message = 
//	            		break;
//	            		
//	            	case E_MISSING_PARAM_PASSWORD:
//	            		//message = 
//	            		break;
//	            		
//	            	case E_MISSING_PARAM_TIMESTAMP:
//	            		//message = 
//	            		break;
//	            		
//	            	case E_INVALID_URL:
//	            		//message = 
//	            		break;	            			               			   				
				}
				return message;
			}
	    };

		final Thread updateThread = new Thread(new Runnable() {
			@Override
			public void run() { //.
				
				getBigBlueButton().createJoinService(serverUrl, serverPassword);
				
				if (getBigBlueButton().getJoinService() == null) {
					if (progressDialog.isShowing()) progressDialog.dismiss();
					showToast(R.string.login_unsupported_server);
					return;
				}
				
				int returnCode = getBigBlueButton().getJoinService().load();
				
				if(returnCode != E_OK)
				{
					log.debug("timestamp failed on android");

					if (progressDialog.isShowing()) 
						progressDialog.dismiss();
					
					h.sendEmptyMessage(returnCode);
					return;
				}

				if (Thread.interrupted())
					return;

				final List<Meeting> meetings = getBigBlueButton().getJoinService().getMeetings();

				if (progressDialog.isShowing()) progressDialog.dismiss();

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
						spinnerAdapter.add(getApplicationContext().getResources().getString(R.string.new_meeting));
						
						Spinner spinner = (Spinner) findViewById(R.id.login_spinner);
//NULL pointer          spinner.getChildAt(spinnerAdapter.getCount()-1).setBackgroundResource(R.color.text_selected);
						// select the created meeting in the list
						for (int i = 0; i < spinnerAdapter.getCount(); ++i) {
							if (spinnerAdapter.getItem(i).equals(createdMeeting)) {
								spinner.setSelection(i);
								createdMeeting = "";
								break;
							}
						}
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
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_QR_CODE, Menu.NONE, R.string.qrcode).setIcon(R.drawable.ic_menu_qrcode);
		menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, R.string.menu_about).setIcon(android.R.drawable.ic_menu_info_details);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		switch (item.getItemId()) {
		case MENU_ABOUT:
			new AboutDialog(this).show();
			return true; 
		case MENU_QR_CODE: 
			barcodeHandler.scan(this);
			return true;
		default:			
			return super.onOptionsItemSelected(item);
		}
	}    

	private SharedPreferences getPreferences() {
		return getSharedPreferences("storedPreferences", MODE_PRIVATE);
	}
	
	private void loadUserPreferences(){
		username = getPreferences().getString("username", username);
		serverUrl = getPreferences().getString("serverUrl", "");
		serverPassword = getPreferences().getString("serverPassword", "");
	}
	
	private void storePreferences(String username, String serverUrl, String serverPassword) {
		SharedPreferences.Editor preferenceEditor = getPreferences().edit();
		preferenceEditor.putString("username", username);
		preferenceEditor.putString("serverUrl", serverUrl);
		preferenceEditor.putString("serverPassword", serverPassword);
		preferenceEditor.commit();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (barcodeHandler.handle(requestCode, resultCode, intent)) {
			// handled
		}
	}
	
	private void showTimestampFailedDialog(String message)
	{	
				AlertDialog.Builder timestampFailed = new AlertDialog.Builder(LoginPage.this);
				timestampFailed.setTitle(R.string.failed_to_connect);
				timestampFailed.setMessage(message);				    
				timestampFailed.setNeutralButton(R.string.ok, null);
				timestampFailed.show();	
	}
}
