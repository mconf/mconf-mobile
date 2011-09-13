/*
 * GT-Mconf: Multiconference system for interoperable web and mobile
 * http://www.inf.ufrgs.br/prav/gtmconf
 * PRAV Labs - UFRGS
 * 
 * This file is part of Mconf-Mobile.
 *
 * Mconf-Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mconf-Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mconf-Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mconf.bbb.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutDialog extends AlertDialog {

	public AboutDialog(Context context) {
		super(context);
		
		final SpannableString s = new SpannableString(context.getText(R.string.about).toString()
				.replace("${VERSION}", getVersion(context))
				.replace("${APP_NAME}", context.getText(R.string.app_name).toString()));
		Linkify.addLinks(s, Linkify.WEB_URLS);

		final TextView message = new TextView(context);
		message.setText(s);
		message.setMovementMethod(LinkMovementMethod.getInstance());
		message.setLinksClickable(true);
		message.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		message.setPadding(10, 0, 10, 0);
		message.setLinkTextColor(Color.YELLOW);
		
		final ScrollView screen = new ScrollView(context);
		screen.addView(message);
		
		setView(screen);
		setTitle(R.string.menu_about);
		setIcon(R.drawable.hurricane_transparent);
		setCancelable(true);
		setButton(BUTTON_POSITIVE, context.getText(R.string.close), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AboutDialog.this.cancel();
			}
		});
	}

	public static String getVersion(Context context) {
	    final String unknown = "Unknown";
	
	    if (context == null) {
	            return unknown;
	    }
	
	    try {
		    String ret = context.getPackageManager()
		               .getPackageInfo(context.getPackageName(), 0)
		               .versionName;
		    if (ret.contains(" + "))
		            ret = ret.substring(0,ret.indexOf(" + "))+"b";
		    return ret;
		    } catch(NameNotFoundException ex) {}
	
	    return unknown;
	}
}