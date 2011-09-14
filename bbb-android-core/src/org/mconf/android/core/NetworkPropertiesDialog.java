package org.mconf.android.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class NetworkPropertiesDialog extends AlertDialog.Builder{
	
	public NetworkPropertiesDialog(final Context context)
	{
		super(context);
		setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       });
		setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
	           }
	       });
		setTitle(R.string.no_connection);
		setMessage(R.string.open_properties);
	}

}
