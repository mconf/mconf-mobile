package org.mconf.bbb.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class NetworkPropertiesDialog extends AlertDialog.Builder{
	
	NetworkPropertiesDialog(final Context context)
	{
		super(context);
		this.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   dialog.cancel();
	           }
	       });
		this.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
	           }
	       });
	this.setTitle(R.string.no_connection);
	this.setMessage(R.string.open_properties);
	}

}
