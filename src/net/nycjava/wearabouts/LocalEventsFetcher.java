package net.nycjava.wearabouts;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.maps.model.LatLng;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class LocalEventsFetcher {
	// TODO get today's date and time and use NYC long/lat and range of about 25 miles
	private final static String URL = "http://api.seatgeek.com/2/events?lat=40.783767&lon=-73.965118&range=1mi&datetime_local.gt=2014-07-09T00:00:00&datetime_local.lt=2014-07-10T00:00:00";
	
	public List<Event> getLocalEvents() {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(URL));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
			
			HttpEntity entity = response.getEntity();
            String jsonString = EntityUtils.toString(entity);

            // TODO now process the jsonString here, I (Timothy) can help with that, to create some event description or list 
            // of events or something
            
            return Collections.singletonList(new Event("The Beatnuts with Ana Tijoux", 2128571, new LatLng(40.7736d, -73.9711d)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
