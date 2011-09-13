package org.mconf.bbb.android;

import org.acra.ACRA;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;

public class ReportCrashDialog extends AlertDialog.Builder implements OnClickListener {
	private Context context = null;

	public ReportCrashDialog(Context context) {
		super(context);
		
		this.context  = context;
		setTitle(R.string.report_crash_title);
		setMessage(R.string.report_crash_message);
		setPositiveButton(R.string.yes, this);
		setNegativeButton(R.string.no, this);
		setCancelable(false);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Editor editor = ACRA.getACRASharedPreferences().edit();
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			// it's enabled by default
			// editor.putBoolean(ACRA.PREF_DISABLE_ACRA, false);
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			editor.putBoolean(ACRA.PREF_DISABLE_ACRA, true);
			break;
		}
		editor.putBoolean("acra.remember." + getVersionCode(), true);
		editor.commit();
	}

	private int getVersionCode() {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0 ).versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}
		
	@Override
	public AlertDialog show() {
		if (((BigBlueButton) context.getApplicationContext()).isDebug()
				|| ACRA.getACRASharedPreferences().getBoolean("acra.remember." + getVersionCode(), false))
			return null;
		else
			return super.show();
	}
}
