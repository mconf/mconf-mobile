package org.mconf.android.mconfmobile;

import java.util.List;

import org.mconf.android.core.AboutDialog;
import org.mconf.android.core.BarcodeHandler;
import org.mconf.android.core.BigBlueButtonActivity;
import org.mconf.android.core.Client;
import org.mconf.android.core.ReportCrashDialog;
import org.mconf.web.Authentication;
import org.mconf.web.MconfWebImpl;
import org.mconf.web.MconfWebItf;
import org.mconf.web.Room;
import org.mconf.web.Space;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends BigBlueButtonActivity {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(LoginPage.class);

	private static final int MENU_QR_CODE = Menu.FIRST;
	private static final int MENU_ABOUT = Menu.FIRST + 1;

	private static final int E_AUTHENTICATED = 0;
	private static final int E_NOT_AUTHENTICATED = 1;
	private static final int E_CANNOT_CONTACT_SERVER = 2;
	
	private final String DEFAULT_SERVER = "https://mconf.org";
	private Authentication auth = null;
	private String authUsername, authPassword;
	private ArrayAdapter<String> spinnerAdapter;
	private Room selectedRoom = null;
	private MconfWebItf mconf = new MconfWebImpl();
	private BarcodeHandler barcodeHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		new ReportCrashDialog(this).show();

		loadCredentials();
		
//		final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
//		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

		final Spinner spinnerRooms = (Spinner) findViewById(R.id.spinnerRooms);
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRooms.setAdapter(spinnerAdapter);
		
		spinnerRooms.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					
					final ProgressDialog progressDialog = new ProgressDialog(LoginPage.this);
					progressDialog.setTitle(R.string.wait);
					progressDialog.setMessage(getResources().getString(R.string.login_updating));
					
					final Thread updateThread = new Thread(new Runnable() {
						@Override
						public void run() {
							
							boolean hasError = false;
							int errorId = 0;
							
							int r = checkAuthentication();
							switch (r) {
								case E_AUTHENTICATED:
									storeCredentials();
									List<Room> rooms = null;
									try {
										rooms = mconf.getRooms(auth);
									} catch (Exception e) {
										e.printStackTrace();
										errorId = R.string.login_cant_contact_server; hasError = true;
									}
									
									if (!progressDialog.isShowing())
										return;
									
									if (!hasError) {
										if (rooms.isEmpty()) {
											errorId = R.string.no_rooms; hasError = true;
										} else {
											progressDialog.dismiss();
											openRoomsDialog(rooms);
											return;
										}
									}
									break;
								case E_NOT_AUTHENTICATED:
									errorId = R.string.invalid_password; hasError = true;
									break;
								case E_CANNOT_CONTACT_SERVER:
									errorId = R.string.login_cant_contact_server; hasError = true;
									break;
							}
							
							if (progressDialog.isShowing()) {
								progressDialog.dismiss();
								if (hasError)
									makeToast(errorId);
							}
						}

						private void openRoomsDialog(final List<Room> rooms) {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									RoomsDialog dialog = new RoomsDialog(LoginPage.this, rooms);
									dialog.setOnSelectRoomListener(new RoomsDialog.OnSelectRoomListener() {
										
										@Override
										public void onSelectRoom(Room room) {
											selectedRoom = room;
											spinnerAdapter.clear();
											spinnerAdapter.add(room.getName());
											spinnerAdapter.notifyDataSetChanged();
										}
									});
									
									dialog.show();
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
					
					return true;
				} else
					return false;
			}
		});
		
		final Button buttonJoin = (Button) findViewById(R.id.buttonJoin);
		buttonJoin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final ProgressDialog progressDialog = new ProgressDialog(LoginPage.this);
				progressDialog.setCancelable(false);
				progressDialog.setMessage(getResources().getString(R.string.wait));
				progressDialog.show();
				
				new Thread(new Runnable() {
					
					private void showToast(final int id) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(LoginPage.this, id, Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					@Override
					public void run() {
						int r = checkAuthentication();
						switch (r) {
							case E_NOT_AUTHENTICATED:
								progressDialog.dismiss();
								showToast(R.string.invalid_password);
								return;
							case E_CANNOT_CONTACT_SERVER:
								progressDialog.dismiss();
								showToast(R.string.login_cant_contact_server);
								return;
						}
						
						if (selectedRoom == null) {
							progressDialog.dismiss();
							showToast(R.string.select_room);
							return;
						}
						
						String path = null;
						try {
							path = mconf.getJoinUrl(auth, selectedRoom.getPath());
						} catch (Exception e) {
							progressDialog.dismiss();
							if (selectedRoom.getOwner().getClass() == Space.class
									&& ((Space) selectedRoom.getOwner()).isPublic()
									&& !((Space) selectedRoom.getOwner()).isMember())
								showToast(R.string.no_running_meeting);
							else
								showToast(R.string.login_cant_contact_server);
							return;
						}
						if (path == null
								|| path.length() == 0) {
							progressDialog.dismiss();
							showToast(R.string.cant_join);
							return;
						}
						
		                Intent intent = new Intent(getApplicationContext(), Client.class);
		                intent.setAction(Intent.ACTION_VIEW);
		                intent.setData(new Uri.Builder()
		                		.scheme(getResources().getString(R.string.protocol))
		                		.appendEncodedPath(path.replace(getResources().getString(R.string.protocol) + ":/", ""))
		                		.build());
		                startActivity(intent);
						progressDialog.dismiss();
	                }
				}).start();
            }
		});

		final TextView account = (TextView) findViewById(R.id.textViewDontHaveAccount);
		account.setText(Html.fromHtml("<a href=\"" + DEFAULT_SERVER + "\">" + getResources().getString(R.string.dont_have_account) + "</a>"));
		account.setMovementMethod(LinkMovementMethod.getInstance());
		account.setLinkTextColor(Color.YELLOW);
		
		barcodeHandler = new BarcodeHandler(getApplicationContext());
	} 
	
	private int checkAuthentication() {
		final EditText editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

		if (auth == null
				|| !auth.isAuthenticated()
				|| !authUsername.equals(editTextUsername.getText().toString())
				|| !authPassword.equals(editTextPassword.getText().toString())) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					spinnerAdapter.clear();
					spinnerAdapter.notifyDataSetChanged();
				}
			});

			authUsername = editTextUsername.getText().toString();
			authPassword = editTextPassword.getText().toString();
			auth = null;
			try {
				auth = new Authentication(DEFAULT_SERVER, authUsername, authPassword);
			} catch (Exception e) {
				e.printStackTrace();
				return E_CANNOT_CONTACT_SERVER;
			}
		}
		return (auth.isAuthenticated()? E_AUTHENTICATED: E_NOT_AUTHENTICATED);
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
			barcodeHandler.scan();
			return true;
		default:			
			return super.onOptionsItemSelected(item);
		}
	}    

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (barcodeHandler.handle(requestCode, resultCode, intent)) {
			// handled
		}
	}
	
}
