package net.nycjava.wearabouts;

import static java.lang.String.format;

import java.util.Collections;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

public class GeoFenceCreator {
	private LocationClient locationClient;

	private String TAG = "WearAbouts";

	public void createGeoFences(final Context context, final List<Event> events) {
		locationClient = new LocationClient(context,
				new GooglePlayServicesClient.ConnectionCallbacks() {
					@Override
					public void onDisconnected() {
						Log.d(TAG, "disconnected");
					}

					@Override
					public void onConnected(Bundle connectionHint) {
						long now = System.currentTimeMillis();
						
						for (Event event : events) {
							final Intent nearbyEventIntent = new Intent(
									context, NotifyEventService.class);
							nearbyEventIntent.putExtra(IntentExtraConstants.EVENT_ID, event.id);
							nearbyEventIntent.putExtra(IntentExtraConstants.EVENT_NAME, event.name);
							nearbyEventIntent.putExtra(IntentExtraConstants.EVENT_LATITUDE, event.latLong.latitude);
							nearbyEventIntent.putExtra(IntentExtraConstants.EVENT_LONGITUDE, event.latLong.longitude);
							nearbyEventIntent.putExtra(IntentExtraConstants.EVENT_START, event.start.getTime());
							nearbyEventIntent.putExtra(IntentExtraConstants.EVENT_END, event.end.getTime());
							final PendingIntent nearbyEventPendingIntent = PendingIntent
									.getService(
											context,
											0,
											nearbyEventIntent,
											PendingIntent.FLAG_ONE_SHOT
													| PendingIntent.FLAG_CANCEL_CURRENT);

							final Geofence geofence = new Geofence.Builder()
									.setRequestId(Integer.toString(event.id))
									.setExpirationDuration(event.end.getTime() - now)
									.setCircularRegion(event.latLong.latitude,
											event.latLong.longitude, 500f)
									.setTransitionTypes(
											Geofence.GEOFENCE_TRANSITION_ENTER)
									.build();

							final LocationClient.OnAddGeofencesResultListener addGeofencesResultListener = new LocationClient.OnAddGeofencesResultListener() {
								@Override
								public void onAddGeofencesResult(
										int statusCode,
										String[] geofenceRequestIds) {
									Log.d(TAG,
											format("onAddGeofencesResult status was %d",
													statusCode));
								}
							};

							locationClient.addGeofences(
									Collections.singletonList(geofence),
									nearbyEventPendingIntent,
									addGeofencesResultListener);
						}
					}
				}, new GooglePlayServicesClient.OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(ConnectionResult result) {
						Log.d(TAG,
								format("onConnectionFailed result was %s",
										result));

					}
				});
		locationClient.connect();
	}
}
