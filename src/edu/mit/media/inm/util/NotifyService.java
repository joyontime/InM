package edu.mit.media.inm.util;

import java.sql.Date;
import java.util.Calendar;
import java.util.Random;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This service is started when an Alarm has been raised
 * 
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 * 
 * @author paul.blundell
 */
public class NotifyService extends Service {
	private static String TAG = "NotifyService";

	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		NotifyService getService() {
			return NotifyService.this;
		}
	}

	// Unique id to identify the notification.
	private static final int NOTIFICATION = 123;
	// Name of an intent extra we can use to identify if this service was started to create a notification	
	public static final String INTENT_NOTIFY = "edu.mit.media.inm.service.INTENT_NOTIFY";
	// The system notification manager
	private NotificationManager mNM;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate()");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Received start id " + startId + ": " + intent);
		
		// If this service was started by out AlarmTask intent then we want to show our notification
		if(intent.getBooleanExtra(INTENT_NOTIFY, false))
			showNotification();
		
		// We don't care if this service is stopped as we have already delivered our notification
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Creates a notification and shows it in the OS drag-down status bar
	 */
	private void showNotification() {		
		Log.d(TAG, "Showing notification");
		NotificationCompat.Builder mBuilder = new NotificationCompat
				.Builder(this)
				.setSmallIcon(R.drawable.ic_alert)
				.setContentTitle("What's On Your Mind?")
				.setContentText("Check InMind for a new prompt!");

		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.putExtra("Prompt", true);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, NOTIFICATION,
				resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		
		mNM.notify(NOTIFICATION, mBuilder.build());
		
		Calendar c = Calendar.getInstance();
		Random random = new Random();
		int day_hour = 9 + random.nextInt(6);
		int night_hour = 15 + random.nextInt(6);
		c.setTimeInMillis(System.currentTimeMillis() + 1000 * 20);
		/*
    	c.set(Calendar.HOUR_OF_DAY, 15);
    	c.set(Calendar.MINUTE, 0);
    	c.set(Calendar.SECOND, 0);
    	
    	if (c.getTime().before(new Date(System.currentTimeMillis()))){
			c.roll(Calendar.DATE, true);
	    	c.set(Calendar.HOUR_OF_DAY, day_hour);
    	} else {
	    	c.set(Calendar.HOUR_OF_DAY, night_hour);
    	}
    	*/
		Log.d(TAG, "Next notification: " + c.getTime().toString());
    	new AlarmTask(this, c).run();
		
		// Stop the service when we are finished
		stopSelf();
	}
}