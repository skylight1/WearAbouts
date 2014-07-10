package net.nycjava.wearabouts;

import com.google.android.gms.maps.model.LatLng;

public class Event {
	final String name;
	final int id;
	final LatLng latLong;
	
	public Event(String name, int id, LatLng latLong) {
		super();
		this.name = name;
		this.id = id;
		this.latLong = latLong;
	}
}
