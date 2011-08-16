package org.mconf.bbb.android.mconf;

import java.util.List;

import org.mconf.bbb.android.Client;
import org.mconf.bbb.android.R;
import org.mconf.web.Authentication;
import org.mconf.web.MconfWebAPI;
import org.mconf.web.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class LoginPage extends Activity {
	private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

	private final String DEFAULT_SERVER = "http://mconfmoodle.inf.ufrgs.br";
	private Authentication auth = null;
	private ArrayAdapter<String> spinnerAdapter;
	private List<Room> rooms;

	private String selectedRoom = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_mconf);
		
		loadCredentials();
		
		final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		
		final Spinner spinnerRooms = (Spinner) findViewById(R.id.spinnerRooms);
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRooms.setAdapter(spinnerAdapter);
		
		spinnerRooms.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (auth == null
							|| !auth.isAuthenticated())
						auth = new Authentication(DEFAULT_SERVER, editTextUsername.getText().toString(), editTextPassword.getText().toString());
					if (auth.isAuthenticated()) {
						storeCredentials();
						rooms = MconfWebAPI.getRooms(auth);
						if (rooms.isEmpty()) {
							Toast.makeText(LoginPage.this, R.string.no_rooms, Toast.LENGTH_SHORT).show();
						} else {
							for (Room room : rooms) {
								spinnerAdapter.add(room.getName());
							}
							spinnerAdapter.notifyDataSetChanged();
							spinnerRooms.performClick();
						}
					} else {
						Toast.makeText(LoginPage.this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
					}
					return true;
				} else
					return false;
			}
		});
		
		spinnerRooms.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selectedRoom  = spinnerAdapter.getItem(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		final CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
		final Button buttonJoin = (Button) findViewById(R.id.buttonJoin);
		buttonJoin.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (selectedRoom.length() == 0) {
						Toast.makeText(LoginPage.this, R.string.select_room, Toast.LENGTH_SHORT).show();
						return true;
					}
					
					String path = null;
					for (Room room : rooms) {
						if (room.getName().equals(selectedRoom)) {
							path = room.getPath();
							break;
						}
					}
					if (path == null) {
						Toast.makeText(LoginPage.this, R.string.invalid_room, Toast.LENGTH_SHORT).show();
						return true;
					}
					
					path = MconfWebAPI.getJoinUrl(auth, path);
					if (path == null
							|| path.length() == 0) {
						Toast.makeText(LoginPage.this, R.string.cant_join, Toast.LENGTH_SHORT).show();
						return true;
					}
	                Intent intent = new Intent(getApplicationContext(), Client.class);
	                intent.setAction(Intent.ACTION_VIEW);
	                intent.addCategory("android.intent.category.BROWSABLE");
	                intent.setData(new Uri.Builder()
	                		.scheme(getResources().getString(R.string.protocol))
	                		.appendEncodedPath(path.replace(getResources().getString(R.string.protocol) + ":/", ""))
	                		.build());
	                startActivity(intent);
	     
	                return true;
				}
				return false;
			}
		});
	}
	
	private void loadCredentials() {
		final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		final CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
		
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		checkBoxRememberMe.setChecked(pref.getBoolean("rememberMe", false));
		editTextUsername.setText(pref.getString("username", ""));
		editTextPassword.setText(pref.getString("password", ""));
	}
	
	private void storeCredentials() {
		Editor pref = getPreferences(MODE_PRIVATE).edit();
		
		final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		final CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);

		boolean rememberMe = checkBoxRememberMe.isChecked();
		String username = editTextUsername.getText().toString();
		String password = editTextPassword.getText().toString();
		pref.putBoolean("rememberMe", rememberMe);
		if (rememberMe) {
			pref.putString("username", username);
			pref.putString("password", password);
		} else {
			pref.putString("username", "");
			pref.putString("password", "");
		}
		pref.commit();
	}
}
