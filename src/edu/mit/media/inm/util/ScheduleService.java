package edu.mit.media.inm.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ScheduleService extends Service {
	private static String TAG = "ScheduleService";
	public ArrayList<Calendar> alarms;

	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		ScheduleService getService() {
			return ScheduleService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ScheduleService", "Received start id " + startId + ": " + intent);
		
		// We want this service to continue running until it is explicitly stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Show an alarm for a certain date when the alarm is called it will pop up a notification
	 */
	public void setAlarm(Calendar c) {
		// This starts a new thread to set the alarm
		// You want to push off your tasks onto a new thread to free up the UI to carry on responding
		new AlarmTask(this, c).run();
	}
	
	public void checkAlarms(){
		if (alarms == null){
			alarms = new ArrayList<Calendar>();
		}
		
		// Remove Alarms that already ran.
		while (true){
			if (alarms.size()>0){
				if (alarms.get(0).getTimeInMillis() < System.currentTimeMillis()){
					Log.d(TAG, "Removing: " + alarms.get(0).getTime().toString());
					alarms.remove(0);
				} else {
					break;
				}
			} else {
				break;
			}
		}

		if (alarms.size() < 1){
			Calendar c = Calendar.getInstance();
			Random random = new Random();
			int day_hour = 9 + random.nextInt(6);
			int night_hour = 15 + random.nextInt(6);
			c.setTimeInMillis(System.currentTimeMillis());
	    	c.set(Calendar.HOUR_OF_DAY, 15);
	    	c.set(Calendar.MINUTE, 0);
	    	c.set(Calendar.SECOND, 0);
	    	
	    	if (c.getTime().before(new Date(System.currentTimeMillis()))){
    			c.roll(Calendar.DATE, true);
    	    	c.set(Calendar.HOUR_OF_DAY, day_hour);
	    	} else {
    	    	c.set(Calendar.HOUR_OF_DAY, night_hour);
	    	}
			Log.d(TAG, "Next Notification: " + c.getTime().toString());
	    	alarms.add(c);
	    	new AlarmTask(this, c).run();
		}
	}
}