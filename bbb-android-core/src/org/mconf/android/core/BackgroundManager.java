package org.mconf.android.core;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

// this class requires the GET_TASKS permission
public class BackgroundManager {
	public static boolean isApplicationBroughtToBackground(final Context context) { // returns true if the
				// application is going to the background, false otherwise. 
				// Can be called inside an activity's onPause method or inside a SurfaceView's 
				// onSurfaceDestroyed method to discover if the activity/surface is being
				// paused/destroyed because a new activity from the application is starting, or because the 
				// application is going to the background.
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
	}
}