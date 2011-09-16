package org.mconf.bbb.android;

import android.app.AlertDialog;
import android.content.Context;

public class CloseDialog extends AlertDialog.Builder {
	
	public CloseDialog(final Context context) {
		super(context);
		setTitle(R.string.back_pressed);
		setMessage(R.string.back_pressed_action);
	}

}