package net.nycjava.wearabouts;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.JsonReader;

import com.google.android.gms.maps.model.LatLng;

public class LocalEventsFetcher {
	// TODO get today's date and time and use NYC long/lat and range of about 25 miles
	private final static String URL_PATTERN = "http://api.seatgeek.com/2/events?lat=40.783767&lon=-73.965118&range=1mi&datetime_local.gt=%s&datetime_local.lt=%s";
	private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final int MAXIMUM_NUMBER_OF_HOURS_BEFORE_EVENT_STARTS = 3;
	private static final int MAXIMUM_NUMBER_OF_HOURS_AFTER_EVENT_STARTS = 1;
	
	public List<Event> getLocalEvents() {
		try {
			SimpleDateFormat localFormat = new SimpleDateFormat(ISO_FORMAT);
			
			Calendar timeWindowStartCalendar = Calendar.getInstance();
			timeWindowStartCalendar.add(Calendar.HOUR, -MAXIMUM_NUMBER_OF_HOURS_BEFORE_EVENT_STARTS);
			Date timeWindowStartDate = timeWindowStartCalendar.getTime();
			String timeWindowStartLocalString = localFormat.format(timeWindowStartDate);
			
			Calendar timeWindowEndCalendar = Calendar.getInstance();
			timeWindowEndCalendar.add(Calendar.HOUR, MAXIMUM_NUMBER_OF_HOURS_AFTER_EVENT_STARTS);
			Date timeWindowEndDate = timeWindowEndCalendar.getTime();
			String timeWindowEndLocalString = localFormat.format(timeWindowEndDate);
			
			HttpClient httpclient = new DefaultHttpClient();
			
			String url = String.format(URL_PATTERN, timeWindowStartLocalString, timeWindowEndLocalString);
			HttpResponse response = httpclient.execute(new HttpGet(url));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
			
			HttpEntity entity = response.getEntity();
            String jsonString = EntityUtils.toString(entity);
            
			List<Event> events = new ArrayList<Event>();

			JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
            try {
				jsonReader.beginObject();
				while(jsonReader.hasNext()) {
					if (jsonReader.nextName().equals("events")) {
						jsonReader.beginArray();
						while(jsonReader.hasNext()) {
							String name = null;
							int id = 0;
							double latitude = 0;
							double longitude = 0;

							jsonReader.beginObject();
							while(jsonReader.hasNext()) {
								String propertyName = jsonReader.nextName();
								if (propertyName.equals("id")) {
									id = jsonReader.nextInt();
								} else if (propertyName.equals("title")) {
									name = jsonReader.nextString();
								} else if (propertyName.equals("venue")) {
				        			jsonReader.beginObject();
				        			while(jsonReader.hasNext()) {
				        				String venuePropertyName = jsonReader.nextName();
				        				if (venuePropertyName.equals("location")) {
				        					jsonReader.beginObject();
				                			while(jsonReader.hasNext()) {
				                				String locationPropertyName = jsonReader.nextName();
				                				if (locationPropertyName.equals("lat")) {
				                					latitude = jsonReader.nextDouble();
				                				} else if (locationPropertyName.equals("lon")) {
				                					longitude = jsonReader.nextDouble();
				                				} else {
				                					jsonReader.skipValue();
				                				}
				                			}
				                			jsonReader.endObject();
				        				} else {
				        					jsonReader.skipValue();
				        				}
				        			}
				        			jsonReader.endObject();
								} else {
									jsonReader.skipValue();
								}
							}
							jsonReader.endObject();
							
							events.add(new Event(name, id, new LatLng(latitude, longitude)));
						}
						jsonReader.endArray();
					} else {
						jsonReader.skipValue();
					}
				}
				jsonReader.endObject();
			} finally {
				if (jsonReader != null) {
					jsonReader.close();
				}
			}

            return events;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
