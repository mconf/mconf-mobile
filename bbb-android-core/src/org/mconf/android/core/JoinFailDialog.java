package org.mconf.android.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

class JoinFailDialog extends AlertDialog.Builder {
Context context;
	public JoinFailDialog(Context context) {
		super(context);
		this.context=context;
		setMessage(R.string.login_cant_join);
		initListener();
	}

	public JoinFailDialog(Context context, String message) {
		super(context);
		setTitle(R.string.login_cant_join);
		setMessage(message);
		initListener();
	}

	private void initListener() {
		setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
//				if (getGlobalContext().getLaunchedBy() == BigBlueButton.LAUNCHED_BY_APPLICATION) {
//					Intent login = new Intent(getGlobalContext(), LoginPage.class);
//					startActivity(login);
//				}
				if (((BigBlueButton) context).getLaunchedBy() == BigBlueButton.LAUNCHED_USING_URL)
					((BigBlueButtonActivity) context).finish();
			}
		});
	}

}