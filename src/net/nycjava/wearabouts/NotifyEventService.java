package net.nycjava.wearabouts;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.LocationClient;

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
		startDate = getDateInTimeZone(startDate, "EDT");
		Date endDate = new Date(intentIn.getLongExtra(IntentExtraConstants.EVENT_END,0));
		endDate = getDateInTimeZone(endDate, "EDT");
		String imageurl = intentIn.getStringExtra(IntentExtraConstants.IMAGE_URL);
		
		Intent openMapIntent = new Intent(
				android.content.Intent.ACTION_VIEW,
				Uri.parse(String.format("geo:0,0?q=%f,%f (%s)", lat, lon, name))
				);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openMapIntent, 0);

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

		// User dismisses it.
		builder.setAutoCancel(false);

		/**
		 *Build the notification's appearance.
		 * Set the large icon, which appears on the left of the notification. In this
		 * sample we'll set the large icon to be the same as our app icon. The app icon is a
		 * reasonable default if you don't have anything more compelling to use as an icon.
		 */
		if(imageurl==null || imageurl.length()==0) {
			builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		} else {
			Bitmap bitmap = getBitmap(imageurl);
			if(bitmap!=null) {
				builder.setLargeIcon(bitmap);
			}
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
		SimpleDateFormat dt = new SimpleDateFormat("hh:mm"); 
		builder.setContentTitle("WearAbouts");
		builder.setContentText(dt.format(startDate) + "\n" + name);
		builder.setSubText("Tap for Map location");
		builder.setVibrate(new long[] {0, 100, 50, 100} );


		/**
		 * Send the notification. This will immediately display the notification icon in the
		 * notification bar.
		 */
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, builder.build());

	}

	private Bitmap getBitmap(String imageurl) {
		Bitmap bitmap=null;		
		try {
	        URL url = new URL(imageurl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        bitmap = BitmapFactory.decodeStream(input);
	        bitmap = getResizedBitmap(bitmap, 128, 128);
	        return bitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return bitmap;
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
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
	public static Date getDateInTimeZone(Date currentDate, String timeZoneId) {
        TimeZone tz = TimeZone.getTimeZone(timeZoneId);
        Date convDate = new Date(currentDate.getTime()+ tz.getOffset(currentDate.getTime()));
        return convDate;
    }
}
