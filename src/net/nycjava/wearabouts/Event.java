package net.nycjava.wearabouts;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class Event {
	final String name;
	final int id;
	final LatLng latLong;
	final Date start;
	final Date end;

	public Event(String name, int id, LatLng latLong, Date start, Date end) {
		super();
		this.name = name;
		this.id = id;
		this.latLong = latLong;
		this.start = start;
		this.end = end;
	}
}
