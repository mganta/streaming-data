package com.example.streaming;

import java.io.Serializable;
import java.util.Date;

import org.apache.lucene.spatial.util.GeoHashUtils;
import org.apache.solr.schema.LatLonType;

public class EventDataElement implements Serializable,
		Comparable<EventDataElement> {

	private static final long serialVersionUID = 1L;
	private String vin;
	private String make;
	private String model;
	private int year;
	 
	private Date dateTime;
	private int speed;
	private String eventMessage;
	private Double latitude;
	private Double longitude;
	private String geoHash;
	
    private LatLonType latLong;

	public EventDataElement() {
		super();
	}

	public EventDataElement(String vin, String make, String model, int year, Date dateTime, int speed, String eventMessage,
			 String latitude, String longitude) {
		super();

		this.dateTime = dateTime;
		this.vin = vin;
		this.model = model;
		this.make = make;
		this.year = year;
		this.speed = speed;
		this.eventMessage = eventMessage;
		this.latitude = Double.parseDouble(latitude);
		this.longitude = Double.parseDouble(longitude);
		this.geoHash = GeoHashUtils.stringEncode(this.longitude, this.latitude);
		
	}


	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getEventMessage() {
		return eventMessage;
	}

	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}


	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = Double.parseDouble(latitude);
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = Double.parseDouble(longitude);
	}


	public String getGeoHash() {
		return geoHash;
	}

	public void setGeoHash(String geoHash) {
		this.geoHash = geoHash;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public LatLonType getLatLong() {
		return latLong;
		
	}

	public void setLatLong(LatLonType latLong) {
		this.latLong = latLong;
	}

	@Override
	public int compareTo(EventDataElement o) {
	/**	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy HH:mm");
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = sdf.parse(this.dateTime);
			date2 = sdf.parse(o.getDateTime());
		} catch (ParseException e) {
			e.printStackTrace();
		} **/
		return this.dateTime.compareTo(o.getDateTime());
	}

}
