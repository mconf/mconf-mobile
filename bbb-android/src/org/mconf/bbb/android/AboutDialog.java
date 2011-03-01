package org.mconf.bbb.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutDialog extends AlertDialog {

	public AboutDialog(Context context) {
		super(context);
		
		final SpannableString s = new SpannableString(context.getText(R.string.about));
		Linkify.addLinks(s, Linkify.WEB_URLS);

		final TextView message = new TextView(context);
		message.setText(s);
		message.setMovementMethod(LinkMovementMethod.getInstance());
		message.setLinksClickable(true);
		message.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		message.setPadding(10, 0, 10, 0);
		
		final ScrollView screen = new ScrollView(context);
		screen.addView(message);
		
		setView(screen);
		setTitle(R.string.menu_about);
		setIcon(R.drawable.icon_bbb);
		setCancelable(true);
		setButton(BUTTON_POSITIVE, "Close", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AboutDialog.this.cancel();
			}
		});
	}
}
