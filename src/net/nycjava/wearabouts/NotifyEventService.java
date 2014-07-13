package net.nycjava.wearabouts;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.location.LocationClient;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class NotifyEventService extends IntentService {

	public static final String TAG = "WearAbouts:NotifyEventService";
	
	public NotifyEventService() {
		super("NotifyEventService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(LocationClient.hasError(intent)) {
            // Get the error code
            int errorCode = LocationClient.getErrorCode(intent);

            // Get the error message
            String errorMessage = LocationServiceErrorMessages.getErrorString(this, errorCode);

            // Log the error
            Log.e(TAG,"Bad:" + errorMessage);

		}
//        else {
			showNotification(intent);
//		}
	}
	
	public void showNotification(Intent intentIn) {

		/** Create an intent that will be fired when the user clicks the notification.
		 * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
		 * notification service can fire it on our behalf.
		 */

		double lat = intentIn.getDoubleExtra(IntentExtraConstants.EVENT_LATITUDE,0);
		double lon = intentIn.getDoubleExtra(IntentExtraConstants.EVENT_LONGITUDE,0);
		String name = intentIn.getStringExtra(IntentExtraConstants.EVENT_NAME);
		Date startDate = new Date(intentIn.getLongExtra(IntentExtraConstants.EVENT_START,0));
		Date endDate = new Date(intentIn.getLongExtra(IntentExtraConstants.EVENT_END,0));
		String imageurl = intentIn.getStringExtra(IntentExtraConstants.IMAGE_URL);
		
		Intent openMapIntent = new Intent(
				android.content.Intent.ACTION_VIEW,
				Uri.parse(String.format("geo:0,0?q=%f,%f (%s)", lat, lon, name))
				);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openMapIntent, 0);

		//
		// BEGIN_INCLUDE (build_notification)
		/**
		 * Use NotificationCompat.Builder to set up our notification.
		 */
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		/** Set the icon that will appear in the notification bar. This icon also appears
		 * in the lower right hand corner of the notification itself.
		 *
		 * Important note: although you can use any drawable as the small icon, Android
		 * design guidelines state that the icon should be simple and monochrome. Full-color
		 * bitmaps or busy images don't render well on smaller screens and can end up
		 * confusing the user.
		 */
		builder.setSmallIcon(R.drawable.ic_stat_notification);

		// Set the intent that will fire when the user taps the notification.
		builder.setContentIntent(pendingIntent);

		// Set the notification to auto-cancel. This means that the notification will disappear
		// after the user taps it, rather than remaining until it's explicitly dismissed.
		builder.setAutoCancel(true);

		/**
		 *Build the notification's appearance.
		 * Set the large icon, which appears on the left of the notification. In this
		 * sample we'll set the large icon to be the same as our app icon. The app icon is a
		 * reasonable default if you don't have anything more compelling to use as an icon.
		 */
		if(imageurl==null || imageurl.length()==0) {
			builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		}
		/**
		 * Set the text of the notification. This sample sets the three most commononly used
		 * text areas:
		 * 1. The content title, which appears in large type at the top of the notification
		 * 2. The content text, which appears in smaller text below the title
		 * 3. The subtext, which appears under the text on newer devices. Devices running
		 *    versions of Android prior to 4.2 will ignore this field, so don't use it for
		 *    anything vital!
		 */
		SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss"); 
		builder.setContentTitle("WearAbouts");
		builder.setContentText("Event: " + name + " Starts: " + dt.format(startDate) + " Ends: " + dt.format(endDate));
		builder.setSubText("Tap for Map location");

		// END_INCLUDE (build_notification)


		/**
		 * Send the notification. This will immediately display the notification icon in the
		 * notification bar.
		 */
		NotificationManager notificationManager = (NotificationManager) getSystemService(
				NOTIFICATION_SERVICE);
		notificationManager.notify(0, builder.build());

	}

/*
	private void showNotification(Intent intent) {
		Log.d(TAG,"in showNotification");
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this)
				.setContentTitle("Local Event!")
				.setContentText(
						intent.getStringExtra(IntentExtraConstants.EVENT_NAME))
				.setLargeIcon(
						BitmapFactory.decodeResource(getResources(),
								R.drawable.ic_launcher));

//		Intent openMapIntent = new Intent(
//				android.content.Intent.ACTION_VIEW,
//				Uri.parse(String.format(
//						"geo:0,0?q=%f,%f (%s)",
//						intent.getDoubleExtra(IntentExtraConstants.EVENT_LATITUDE,0),
//						intent.getDoubleExtra(IntentExtraConstants.EVENT_LONGITUDE,0),
//						intent.getStringExtra(IntentExtraConstants.EVENT_NAME))));
//
//		PendingIntent showOnMapPendingIntent = PendingIntent.getBroadcast(this, 0,
//				openMapIntent, PendingIntent.FLAG_ONE_SHOT
//						| PendingIntent.FLAG_CANCEL_CURRENT);
//		
//		builder.setContentIntent(showOnMapPendingIntent);
		// Notifications are issued by sending them to the
		// NotificationManager system service.
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds an anonymous Notification object from the builder, and
		// passes it to the NotificationManager
		mNotificationManager.notify(0, builder.build());
		
		// Create the action
//		NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_action_locate,
//		                getString(R.string.label), showOnMapPendingIntent)
//		                .build();
		
//		NotificationManager mNotificationManager =
//			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//			// mId allows you to update the notification later on.
//		
//			mNotificationManager.notify(mId, mBuilder.build());
//			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//			builder.setContentIntent(resultPendingIntent);
//			NotificationManager mNotificationManager =
//			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//			mNotificationManager.notify(id, builder.build());			
//			
			
			
//		Notification notification =
//		        new NotificationCompat.Builder(this)
//				.extend(new WearableExtender().addAction(action))	
//				.build();
//		NotificationManagerCompat.from(this).notify(0, notification);
	}
	*/
}
