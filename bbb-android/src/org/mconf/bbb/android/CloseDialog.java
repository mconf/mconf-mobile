package org.mconf.bbb.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class CloseDialog extends AlertDialog.Builder{

CloseDialog(final Context context)
{
super(context);
setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int id) {


Intent intent = new Intent(Client.SEND_TO_BACK);
((Activity) context).sendBroadcast(intent);
((Activity) context).moveTaskToBack(true);
((Client)context).showBackgroundNotification();
dialog.cancel();
}
});
setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int id) {
Intent intent = new Intent(Client.QUIT);
((Activity) context).sendBroadcast(intent);
dialog.cancel();
}
});
setTitle(R.string.quit_dialog);
setMessage(R.string.if_no_send_to_back);
}

}