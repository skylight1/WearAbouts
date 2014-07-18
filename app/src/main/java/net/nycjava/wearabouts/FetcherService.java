package net.nycjava.wearabouts;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class FetcherService extends IntentService {

	static private final String TAG = "WearAbouts:FetcherService";

	public FetcherService() {
		super("FetcherService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		  Log.d(TAG,"onHandleIntent-createGeo");
	      new Thread(new Runnable() {
				
				@Override
				public void run() {
					List<Event> events = new LocalEventsFetcher().getLocalEvents();
					new GeoFenceCreator().createGeoFences(getApplicationContext(), events);
					
					// TODO release wake lock here
				}
			}).start();
	}
}
