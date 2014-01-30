package edu.mit.media.inm.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NotifyService extends Service {
	private static String TAG = "NotifyService";
	
    NotifyReceiver alarm = new NotifyReceiver();
    
    public void onCreate() {
        super.onCreate();       
    }
    
    public int onStartCommand(Intent intent, int flags, int startid){
    	Log.d(TAG, "NotifyService starting");
        alarm.SetAlarm(this);
        Toast.makeText(this, "Boop", Toast.LENGTH_SHORT).show();
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) 
    {
        return null;
    }
    
    public void onDestroy(){
    	alarm.CancelAlarm(this);
    }
}