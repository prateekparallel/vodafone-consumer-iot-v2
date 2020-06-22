package com.vodafone.uk.iot.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vodafone.uk.iot.beans.DeviceDetails;

class IOTUtilTest {

	@Test
	void shouldReturnCorrectDateFormat() {
		Long tstmp = 1582605077000L;
		assertEquals("25/02/2020 04:31:17", IOTUtil.convertToDate(tstmp));
	}

	@Test
	void shouldReturn_Active_Status_When_AirplaneMode_OFF() {

		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("OFF");
		assertEquals("Active", IOTUtil.getStatus(deviceInfo));
	}

	@Test
	void shouldReturn_Inactive_Status_When_AirplaneMode_ON() {

		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("ON");
		assertEquals("Inactive", IOTUtil.getStatus(deviceInfo));
	}

	@Test
	void shouldReturn_All_Battery_Status_Correct() {
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
	void shouldReturn_Location_Identified_When_AirplaneMode_OFF() {

		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("OFF");
		assertEquals("SUCCESS: Location identified.", IOTUtil.getDescription(deviceInfo));
	}

	@Test
	void shouldReturn_Location_Notavailable_When_AirplaneMode_ON() {

		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("ON");
		assertEquals("SUCCESS: Location not available: Please turn off airplane mode",
				IOTUtil.getDescription(deviceInfo));
	}

	@Test
	void shouldReturn_Longitude_Latitude_When_AirplaneMode_OFF() {

		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("OFF");
		deviceInfo.setLatitude(51.5185);
		deviceInfo.setLongitude(-0.1736);
		assertEquals("51.5185", IOTUtil.getLatitude(deviceInfo));
		assertEquals("-0.1736", IOTUtil.getLongitude(deviceInfo));
	}

	@Test
	void shouldNotReturn_Longitude_Latitude_When_AirplaneMode_ON() {

		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setAirplaneMode("ON");
		deviceInfo.setLatitude(51.5185);
		deviceInfo.setLongitude(-0.1736);
		assertEquals("", IOTUtil.getLatitude(deviceInfo));
		assertEquals("", IOTUtil.getLongitude(deviceInfo));
	}

	@Test
	void shouldReturnCorrectValueForGPSData() {

		double lat1 = 45.5187;
		double lng1 = -12.52001;
		double lat2 = 45.5187;
		double lng2 = -12.52001;

		assertEquals(IOTUtil.getDistanceBetween(lat1, lng1, lat2, lng2), 0);

		lat2 = 45.5185;
		lng2 = -12.52040;

		assertNotEquals(IOTUtil.getDistanceBetween(lat1, lng1, lat2, lng2), 0);
	}

	@Test
	void shouldReturnInActive_WhenAllGPSDataSame() {
		List<DeviceDetails> dlist = new LinkedList<DeviceDetails>();
		dlist.add(getDeviceDetail(45.5187, -12.52001));
		dlist.add(getDeviceDetail(45.5187, -12.52001));
		dlist.add(getDeviceDetail(45.5187, -12.52001));
		assertEquals(IOTUtil.getStatus(dlist), "Inactive");
	}

	@Test
	void shouldReturnNA_WhenAllGPSDistanceNotSignificant() {
		List<DeviceDetails> dlist = new LinkedList<DeviceDetails>();
		dlist.add(getDeviceDetail(45.5185, -12.52010));
		dlist.add(getDeviceDetail(45.5186, -12.52005));
		dlist.add(getDeviceDetail(45.5187, -12.52001));
		assertEquals(IOTUtil.getStatus(dlist), "N/A");
	}

	@Test
	void shouldReturnActive_WhenAllGPS_Are_in_Significant_Distance_Apart() {
		List<DeviceDetails> dlist = new LinkedList<DeviceDetails>();
		dlist.add(getDeviceDetail(45.5180, -12.52020));
		dlist.add(getDeviceDetail(45.5184, -12.52011));
		dlist.add(getDeviceDetail(45.5187, -12.52001));
		assertEquals(IOTUtil.getStatus(dlist), "Active");
	}

	@Test
	void shouldReturnInActive_WhenOneGPSDataSame_Not_Enough_Missing() {
		List<DeviceDetails> dlist = new LinkedList<DeviceDetails>();
		dlist.add(getDeviceDetail(45.5180, -12.52041));
		dlist.add(getDeviceDetail(45.5187, -12.52001));
		// dlist.add(getDeviceDetail(45.5187,-12.52001));
		assertEquals(IOTUtil.getStatus(dlist), "N/A");
	}

	@Test
	void shouldReturnInActive_WhenOneGPSData_Missing() {
		List<DeviceDetails> dlist = new LinkedList<DeviceDetails>();
		dlist.add(getDeviceDetail(45.5180, -12.52041));
		dlist.add(getDeviceDetail(null, -12.520021));
		dlist.add(getDeviceDetail(45.5187, -12.52001));
		assertEquals(IOTUtil.getStatus(dlist), "Inactive");
	}

	@Test
	void shouldReturnInActive_WhenOneGPSData_Missing2() {
		List<DeviceDetails> dlist = new LinkedList<DeviceDetails>();
		dlist.add(getDeviceDetail(45.5180, null));
		dlist.add(getDeviceDetail(45.5185, -12.52041));
		dlist.add(getDeviceDetail(45.5187, -12.52001));
		assertEquals(IOTUtil.getStatus(dlist), "Inactive");
	}

	@Test
	void shouldReturnInActive_WhenOneGPSData_Missing3() {
		List<DeviceDetails> dlist = new LinkedList<DeviceDetails>();
		dlist.add(getDeviceDetail(45.5183, -12.52001));
		dlist.add(getDeviceDetail(45.5185, -12.52001));
		dlist.add(getDeviceDetail(null, -12.52001));
		assertEquals(IOTUtil.getStatus(dlist), "Inactive");
	}

	@Test
	void shouldValidate_All_Delimiter_Correctly() {

		assertTrue(IOTUtil.isValidDelimiter(','));
		assertTrue(IOTUtil.isValidDelimiter(';'));
		assertTrue(IOTUtil.isValidDelimiter('|'));
		assertTrue(IOTUtil.isValidDelimiter(':'));
		assertTrue(IOTUtil.isValidDelimiter('	'));

		assertFalse(IOTUtil.isValidDelimiter(' '));
		assertFalse(IOTUtil.isValidDelimiter('A'));
		assertFalse(IOTUtil.isValidDelimiter('9'));
		assertFalse(IOTUtil.isValidDelimiter(null));

	}

	private static DeviceDetails getDeviceDetail(Double lat, Double lon) {
		DeviceDetails dd = new DeviceDetails();
		dd.setLatitude(lat);
		dd.setLongitude(lon);
		return dd;
	}
}
