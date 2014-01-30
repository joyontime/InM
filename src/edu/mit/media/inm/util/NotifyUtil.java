package edu.mit.media.inm.util;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotifyUtil {
	private final Context ctx;
	public static final int dummy_notify = 0;

	public NotifyUtil(Context ctx) {
		this.ctx = ctx;
	}

	public void sendNotification() {
		NotificationCompat.Builder mBuilder = new NotificationCompat
				.Builder(ctx)
				.setSmallIcon(R.drawable.bookmark)
				.setContentTitle("What's On Your Mind?")
				.setContentText("Check InMind for a new prompt!");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(ctx, MainActivity.class);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0,
				resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(dummy_notify, mBuilder.build());
	}
}