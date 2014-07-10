package net.nycjava.wearabouts;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;

public class NotifyEventService extends IntentService {

	public NotifyEventService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		showNotification(intent);
	}

	private void showNotification(Intent intent) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this)
				.setContentTitle("Local Event!")
				.setContentText(
						intent.getStringExtra(IntentExtraConstants.EVENT_NAME))
				.setLargeIcon(
						BitmapFactory.decodeResource(getResources(),
								R.drawable.ic_launcher));

		Intent openMapIntent = new Intent(
				android.content.Intent.ACTION_VIEW,
				Uri.parse(String.format(
						"geo:0,0?q=%s,%s (%s)",
						intent.getStringExtra(IntentExtraConstants.EVENT_LATITUDE),
						intent.getStringExtra(IntentExtraConstants.EVENT_LONGITUDE),
						intent.getStringExtra(IntentExtraConstants.EVENT_NAME))));

		PendingIntent showOnMapPendingIntent = PendingIntent.getBroadcast(this, 0,
				openMapIntent, PendingIntent.FLAG_ONE_SHOT
						| PendingIntent.FLAG_CANCEL_CURRENT);
		
		// Create the action
		NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_action_locate,
		                getString(R.string.label), showOnMapPendingIntent)
		                .build();
		
		Notification notification =
		        new NotificationCompat.Builder(this)
				.extend(new WearableExtender().addAction(action))	
				.build();
		NotificationManagerCompat.from(this).notify(0, notification);
	}
}
