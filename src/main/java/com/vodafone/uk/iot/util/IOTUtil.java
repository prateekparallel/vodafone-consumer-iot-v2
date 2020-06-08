package com.vodafone.uk.iot.util;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.vodafone.uk.iot.beans.DeviceDetails;

/**
 * This is a Util class which provide various utility methods
 * @author prate
 *
 */
public class IOTUtil {
	
	
	public static String convertToDate(Long timeStampMili) {
		
		String date = LocalDateTime.ofInstant(
		        Instant.ofEpochMilli(timeStampMili), ZoneId.systemDefault()
		).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

		return date;
	}
	
	
	public static String getStatus(DeviceDetails deviceInfo) {
		if(deviceInfo != null && deviceInfo.getAirplaneMode() != null 
				&& deviceInfo.getAirplaneMode().equals("OFF")) {
			
			return "Active";
		}
		return "Inactive";
	}
	
	
	public static String getBatteryStatus(DeviceDetails deviceInfo) {
		
		if (deviceInfo != null && deviceInfo.getBattery() != null ) {
				
			if(deviceInfo.getBattery() >= .98) {
				return "Full";
			}
			else if(deviceInfo.getBattery() >= .60) {
				return "High";			
			}
			else if(deviceInfo.getBattery() >= .40) {
				return "Medium";
			}
			else if(deviceInfo.getBattery() >= .10) {
				return "Low";
			}
			
		}
		
		return "Critical";
	}
		
	/***
	 * @param deviceInfo
	 * @return device location
	 */
	public static String getDescription(DeviceDetails deviceInfo) {
		
		if(deviceInfo != null && deviceInfo.getAirplaneMode() != null 
				&& deviceInfo.getAirplaneMode().equals("OFF")) {
			
			return "SUCCESS: Location identified.";
		}
		
		return "SUCCESS: Location not available: Please turn off airplane mode";
		
	}
	
	
	public static String getLatitude(DeviceDetails deviceInfo) {
		
		if(deviceInfo != null && deviceInfo.getLatitude() != null
				&& deviceInfo.getAirplaneMode().equals("OFF")){
		
			return deviceInfo.getLatitude().toString();
		}
		
		return "";
	}
	

	
	public static String getLongitude(DeviceDetails deviceInfo) {
		
		if(deviceInfo != null  &&  deviceInfo.getLongitude() != null
				&& deviceInfo.getAirplaneMode().equals("OFF")) {
			
			return deviceInfo.getLongitude().toString();
		}
		
		return "";
		
	}
	

}
