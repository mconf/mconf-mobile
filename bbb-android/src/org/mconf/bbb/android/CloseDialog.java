package org.mconf.bbb.android;

import android.app.AlertDialog;
import android.content.Context;

public class CloseDialog extends AlertDialog.Builder {
	
	public CloseDialog(final Context context) {
		super(context);
		setTitle(R.string.quit_dialog);
		setMessage(R.string.if_no_send_to_back);
	}

}