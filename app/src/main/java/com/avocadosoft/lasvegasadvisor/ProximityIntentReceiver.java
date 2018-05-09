package com.avocadosoft.lasvegasadvisor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.graphics.Color;
import android.location.LocationManager;

public class ProximityIntentReceiver extends BroadcastReceiver
{

	private static final int NOTIFICATION_ID = 1000;
	Context mContext;
	Resources res;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String name = intent.getStringExtra("Name");
		String info = intent.getStringExtra("Info");
		int dealCount = intent.getIntExtra("DealCount", 1);
		Log.i("Avocado", "Extra: " + name);
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		mContext = context;
		
		Boolean entering = intent.getBooleanExtra(key, false);

		/*
		 * 
		 * if (entering) { Log.d(getClass().getSimpleName(), "entering"); } else
		 * { Log.d(getClass().getSimpleName(), "exiting"); }
		 */
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = createNotification();
		
		String notificationText;
		
		if (dealCount == 1)
		{
			notificationText = info + " at " + name;
		}
		else
		{
			notificationText = dealCount + " deals found at " + name;
		}
		
		res = mContext.getResources();
		
		notification.setLatestEventInfo(context, res.getString(R.string.proximityAlertNotificationTitle), notificationText, pendingIntent);

		notificationManager.notify(NOTIFICATION_ID, notification);

	}

	private Notification createNotification()
	{
		Notification notification = new Notification();

		notification.icon = R.drawable.icon;
		notification.when = System.currentTimeMillis();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		Resources res = mContext.getResources();
		SharedPreferences prefs = mContext.getSharedPreferences(res.getString(R.string.preferencesFileName), 0);
		Boolean vibrate = prefs.getBoolean(res.getString(R.string.prefVibrateKeyName), false);
		if (vibrate)
			notification.defaults |= Notification.DEFAULT_VIBRATE;

		notification.defaults |= Notification.DEFAULT_LIGHTS;

		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = 1500;
		notification.ledOffMS = 1500;

		return notification;
	}

}
