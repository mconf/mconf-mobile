package org.mconf.android.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class BarcodeHandler {
	private Context context;
	public static final int REQUEST_CODE = 0;
	
	public BarcodeHandler(Context context)
	{
		this.context = context;
	}

	public void scan(Context context) {		
		this.context = context;
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage("com.google.zxing.client.android");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); 
		try {
			((Activity) context).startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			downloadIt();
		}
	}
	
	private void downloadIt() {
		AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context);
	    downloadDialog.setTitle(R.string.bar_code_no_found);
	    downloadDialog.setMessage(R.string.install_bar_code);
	    downloadDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialogInterface, int i) {
		        Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
		        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		        
				try {
					context.startActivity(intent);
				} catch (ActivityNotFoundException e) {
					//creates a small window to notify there is no market in this device
					AlertDialog.Builder itHasNoMarketDialog = new AlertDialog.Builder(context);
				    itHasNoMarketDialog.setTitle(R.string.market_not_found);
				    itHasNoMarketDialog.setMessage(R.string.you_have_no_market);				    
				    itHasNoMarketDialog.setNeutralButton(R.string.ok, null);
				    itHasNoMarketDialog.show();
				}   
		        
	    	}
	    });
	    downloadDialog.setNegativeButton(R.string.no, null);
	    downloadDialog.show();
	}
	
	public boolean handle(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
	        if (resultCode == Activity.RESULT_OK) {
	        	
	            String contents = intent.getStringExtra("SCAN_RESULT");
	            @SuppressWarnings("unused")
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	            
	            Uri meetingAdress = Uri.parse(contents);
	            if (meetingAdress.getScheme().equals(context.getResources().getString(R.string.protocol))) {
	            	Intent joinAndLogin = new Intent(context.getApplicationContext(), Client.class);
		            joinAndLogin.addCategory("android.intent.category.BROWSABLE");
		            joinAndLogin.setData(meetingAdress);
		            joinAndLogin.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
		            context.startActivity(joinAndLogin);
	            } else {
	            	Toast.makeText(context, R.string.invalid_join_url, Toast.LENGTH_SHORT).show();
	            }
	        } else {
	            // do nothing
	        }
			return true;
		}
		return false;
	}
	
}
