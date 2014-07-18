package net.nycjava.wearabouts;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootUpReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO add wake lock here

		List<Event> events = new LocalEventsFetcher().getLocalEvents();
		new GeoFenceCreator().createGeoFences(context, events);

		// TODO release wake lock here
	}
}
