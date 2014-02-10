package edu.mit.media.inm.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotifyReceiver extends BroadcastReceiver {
	private static String TAG = "NotifyReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "AlarmManager Received.");
		NotifyUtil nu = new NotifyUtil(context);
		nu.sendNotification();
	}

	public void SetAlarm(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, NotifyReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
				AlarmManager.INTERVAL_DAY, pi);
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, NotifyReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}