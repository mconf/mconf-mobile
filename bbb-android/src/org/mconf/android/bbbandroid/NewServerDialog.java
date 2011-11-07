package org.mconf.android.bbbandroid;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewServerDialog extends Dialog {
	private OnInformationEntered externalListener = null;
	
	public NewServerDialog(final Context context, String currentID, String currentUrl, String currentPassword) {
		super(context);
		setTitle(R.string.new_edit_server);
		setContentView(R.layout.new_server_dialog);
		
		final EditText editTextPassword =(EditText) findViewById(R.id.server_salt);
		editTextPassword.setText(currentPassword);
		final EditText editTextID =(EditText) findViewById(R.id.server_Id);
		editTextID.setText(currentID);
		final EditText editTextUrl =(EditText) findViewById(R.id.server_Url);
		editTextUrl.setText(currentUrl);
		
		final Button buttonOk = (Button) findViewById(R.id.ok_password);
		buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (externalListener != null)
					externalListener.onInformation(editTextID.getText().toString(), editTextUrl.getText().toString(), editTextPassword.getText().toString());
				dismiss();
			}
		});
	}
	
	public interface OnInformationEntered {
		public void onInformation(String ID, String Url, String password);
	}
	
	

	public void setOnInformationEntered(OnInformationEntered onInformationEntered) {
		this.externalListener  = onInformationEntered;
		
	}



	

}
