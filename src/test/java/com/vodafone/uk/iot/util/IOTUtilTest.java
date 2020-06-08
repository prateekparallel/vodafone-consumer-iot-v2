package com.vodafone.uk.iot.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.vodafone.uk.iot.beans.DeviceDetails;

class IOTUtilTest {
	
	@Test
	void shouldReturnCorrectDateFormat() {
		 Long tstmp = 1582605077000L;
		 assertEquals("25/02/2020 04:31:17", IOTUtil.convertToDate(tstmp));
	}

	@Test
	void shouldReturn_Active_Status_When_AirplaneMode_OFF(){
		
		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("OFF");
		assertEquals("Active", IOTUtil.getStatus(deviceInfo));
	}
	
	@Test
	void shouldReturn_Inactive_Status_When_AirplaneMode_ON(){
		
		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("ON");
		assertEquals("Inactive", IOTUtil.getStatus(deviceInfo));
	}

	@Test
	void shouldReturn_All_Battery_Status_Correct(){
		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setBattery(.98);
		assertEquals("Full", IOTUtil.getBatteryStatus(deviceInfo));
		
		deviceInfo.setBattery(.60);
		assertEquals("High", IOTUtil.getBatteryStatus(deviceInfo));
		
		deviceInfo.setBattery(.40);
		assertEquals("Medium", IOTUtil.getBatteryStatus(deviceInfo));
		
		deviceInfo.setBattery(.10);
		assertEquals("Low", IOTUtil.getBatteryStatus(deviceInfo));
		
		deviceInfo.setBattery(.05);
		assertEquals("Critical", IOTUtil.getBatteryStatus(deviceInfo));
	}
	
	@Test
	void shouldReturn_Location_Identified_When_AirplaneMode_OFF(){
		
		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("OFF");
		assertEquals("SUCCESS: Location identified.", IOTUtil.getDescription(deviceInfo));
	}
	
	@Test
	void shouldReturn_Location_Notavailable_When_AirplaneMode_ON(){
		
		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("ON");
		assertEquals("SUCCESS: Location not available: Please turn off airplane mode", IOTUtil.getDescription(deviceInfo));
	}
	
	@Test
	void shouldReturn_Longitude_Latitude_When_AirplaneMode_OFF(){
		
		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("OFF");
		deviceInfo.setLatitude(51.5185);
		deviceInfo.setLongitude(-0.1736);
		assertEquals("51.5185", IOTUtil.getLatitude(deviceInfo));
		assertEquals("-0.1736", IOTUtil.getLongitude(deviceInfo));
	}
	
	@Test
	void shouldNotReturn_Longitude_Latitude_When_AirplaneMode_ON(){
		
		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("ON");
		deviceInfo.setLatitude(51.5185);
		deviceInfo.setLongitude(-0.1736);
		assertEquals("", IOTUtil.getLatitude(deviceInfo));
		assertEquals("", IOTUtil.getLongitude(deviceInfo));
	}
}
