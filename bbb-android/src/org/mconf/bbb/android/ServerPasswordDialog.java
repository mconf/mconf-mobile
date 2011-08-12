package org.mconf.bbb.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class ServerPasswordDialog extends Dialog implements OnClickListener{
	private Context context;
	EditText password;
	String serverPassword;
	Button ok;
	public ServerPasswordDialog(final Context context) {
		super(context);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		password = new EditText(context);
		password.setLayoutParams(params);
		this.context=context;
		setTitle(R.string.server_password);
		setContentView(R.layout.password_dialog);
		ok = (Button) findViewById(R.id.ok_password);
		ok.setOnClickListener(this);
		password =(EditText) findViewById(R.id.server_password);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==ok)
		{
			if(password.getText().toString().length()>1)
			{
				serverPassword=password.getText().toString();
				Intent passwordInputed;
				if(ServerChoosing.changePassword ==true)
				{
					passwordInputed = new Intent(ServerChoosing.PASSWORD_CHANGED);
					
				}
				else
				{
					passwordInputed= new Intent(ServerChoosing.PASSWORD_INPUTED);
				
				}
				passwordInputed.putExtra("serverPassword", serverPassword);
				context.sendBroadcast(passwordInputed);
				dismiss();

			}
			else
			{
				Toast.makeText(context, R.string.server_password, Toast.LENGTH_SHORT).show();
			}
		}


	}





}
