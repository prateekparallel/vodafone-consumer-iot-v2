package com.vodafone.uk.iot.beans;


import com.opencsv.bean.CsvBindByName;

/***
 * 
 * @author Abhijit P Dutta
 * 
 *This class is to store each raw of the csv file i.e. device's GPS data
 */
public class DeviceDetails {
	
	@CsvBindByName(column = "DateTime")
	private Long dateTime;
	
	@CsvBindByName(column = "EventId")
	private Long eventId; 
	
	@CsvBindByName(column = "ProductId")
	private String productId; 
	
	@CsvBindByName(column = "Latitude")
	private Double latitude; 
	
	@CsvBindByName(column = "Longitude")
	private Double longitude; 
	
	@CsvBindByName(column = "Battery")
	private Double battery;
	
	@CsvBindByName(column = "Light")
	private String light;
	
	@CsvBindByName(column = "AirplaneMode")
	private String airplaneMode;
	
	
	public Long getDateTime() {
		return dateTime;
	}
	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getBattery() {
		return battery;
	}
	public void setBattery(Double battery) {
		this.battery = battery;
	}
	public String getLight() {
		return light;
	}
	public void setLight(String light) {
		this.light = light;
	}
	public String getAirplaneMode() {
		return airplaneMode;
	}
	public void setAirplaneMode(String airplaneMode) {
		this.airplaneMode = airplaneMode;
	}
	
	
	
}

