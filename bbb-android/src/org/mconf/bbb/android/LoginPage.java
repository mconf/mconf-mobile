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
import java.util.Map;

import org.mconf.bbb.api.JoinService;
import org.mconf.bbb.api.Meeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

	private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

	public static final String SERVER_CHOSED ="org.mconf.bbb.android.Client.SERVER_CHOSED";
	public static final int MENU_QR_CODE = Menu.FIRST;
	public static final int MENU_ABOUT = Menu.FIRST + 1;

	SharedPreferences preferencesFile;
	Map<String,String> storedPreferences;

	private ArrayAdapter<String> spinnerAdapter;
	private boolean moderator;
	private String username = "Android";
	private String serverUrl = "";
	private String createdMeeting = "";
	private Context context = this;

	BroadcastReceiver serverChosed = new BroadcastReceiver(){ 
		public void onReceive(Context context, Intent intent)
		{ 
			Bundle extras = intent.getExtras();
			serverUrl=extras.getString("serverURL");
			Button serverView = (Button) findViewById(R.id.server);
			serverView.setText(serverUrl);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		//get Username and server if already saved on the preferences file
		setPreferencesFile();
		setUserPreferences();

		final EditText editName = (EditText) findViewById(R.id.login_edittext_name);
		editName.setText(username);
		Button serverView = (Button) findViewById(R.id.server);
		if(serverUrl.length()>3)
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

							if (!getBigBlueButton().getJoinService().createMeeting(createdMeeting)) {
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
		join.setOnClickListener( new OnClickListener()
		{
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
				//				if (!getBigBlueButton().getJoinService().join(meetingId, username, moderator)) {
				//                	Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_cant_join) + ": " + getBigBlueButton().getJoinService().getJoinedMeeting().getMessage(), Toast.LENGTH_SHORT).show();
				//                	return;
				//                }

				updatePreferences(username, serverUrl);

				Intent myIntent = new Intent(getApplicationContext(), Client.class);
				myIntent.putExtra("username", username);
				myIntent.putExtra("moderator", moderator);
				myIntent.putExtra("serverUrl", serverUrl);
				myIntent.putExtra("meetingId", meetingId);
				startActivity(myIntent);

				finish();
			}


		}
		);
		//button to change the server
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

		final Thread updateThread = new Thread(new Runnable() {
			@Override
			public void run() {
				int r = getBigBlueButton().getJoinService().load(serverUrl);
				switch (r) {
				case JoinService.E_LOAD_HTTP_REQUEST:
					progressDialog.dismiss();
					showToast(R.string.login_cant_contact_server);
					return;
				case JoinService.E_LOAD_PARSE_MEETINGS:
					progressDialog.dismiss();
					showToast(R.string.login_unsupported_server);
					return;

				default:
					break;
				}
				if (Thread.interrupted())
					return;

				final List<Meeting> meetings = getBigBlueButton().getJoinService().getMeetings();

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
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); 
			try {
				startActivityForResult(intent, 0);
			} catch (ActivityNotFoundException e) {
				showDownloadDialog();
			}
		default:			
			return super.onOptionsItemSelected(item);
		}
	}    

	public SharedPreferences getPreferencesFile() {
		return preferencesFile;
	}

	public void setUserPreferences(){
		if(!storedPreferences.isEmpty())
		{
			this.username = this.storedPreferences.get("username");
			this.serverUrl = this.storedPreferences.get("serverURL");
		}
	}

	@SuppressWarnings("unchecked")
	public void setPreferencesFile() {
		if(this.getSharedPreferences("storedPreferences", MODE_PRIVATE)!=null)
			this.preferencesFile = this.getSharedPreferences("storedPreferences", MODE_PRIVATE);
		else
		{
			SharedPreferences.Editor serverEditor = preferencesFile.edit();
			serverEditor.commit(); 
			this.preferencesFile = this.getSharedPreferences("storedPreferences", MODE_PRIVATE);
		}
		this.storedPreferences = (Map<String, String>) preferencesFile.getAll();
	}

	public void updatePreferences(String username, String serverURL)
	{
		SharedPreferences.Editor preferenceEditor = preferencesFile.edit();
		if(!preferencesFile.getString("username", "").equals(username))
		{
			preferenceEditor.remove("username");
			preferenceEditor.putString("username", username);

		}
		if(!preferencesFile.getString("serverURL", "").equals(serverURL))
		{
			preferenceEditor.remove("serverURL");
			preferenceEditor.putString("serverURL", serverURL);
		}
		preferenceEditor.commit();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {

				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

				Uri meetingAdress = Uri.parse(contents);
				Intent joinAndLogin = new Intent(getApplicationContext(), Client.class);
				joinAndLogin.addCategory("android.intent.category.BROWSABLE");
				joinAndLogin.setData(meetingAdress);
				startActivity(joinAndLogin);
				finish();
			} else if (resultCode == RESULT_CANCELED) {
				//\TODO Handle cancel
			}
		}
	}

	void showDownloadDialog()
	{
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context);
		downloadDialog.setTitle(R.string.install_bar_code);
		downloadDialog.setMessage(R.string.bar_code_no_found);
		downloadDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {
				Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		downloadDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int i) {}
		});
		downloadDialog.show();

	}

}
