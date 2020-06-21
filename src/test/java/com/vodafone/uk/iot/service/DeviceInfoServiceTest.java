package com.vodafone.uk.iot.service;

import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetBigCSVFile;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_PIPE_SEP;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_COLON_SEP;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_TAB_SEP;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_Semi_Colon_SEPA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.vodafone.uk.iot.beans.DeviceDetails;
import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.response.IOTResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DeviceInfoServiceTest {

	@Autowired
	IOTFileService fileService;

	@Autowired
	IOTDeviceInfoService deviceInfoService;

	@Test
	// private method test
	void shouldRequrn_DeviceNotAvailableInPast() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Optional<DeviceDetails> deviceDetails = Optional.empty();
		DeviceInfoService dfs = new DeviceInfoService();
		Method generateIOTResponse = DeviceInfoService.class.getDeclaredMethod("generateIOTResponse", Optional.class);
		generateIOTResponse.setAccessible(true);
		Optional<?> response = (Optional<?>) generateIOTResponse.invoke(dfs, deviceDetails);
		IOTResponse iotResp = (IOTResponse) response.get();

		assertEquals("ERROR: No Device information available in near past", iotResp.getDescription());
	}

	@Test
	// private method test
	void shouldRequrn_DeviceNotLocatedWhenGPSDataMissing_And_AirplanModeOFF() throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		DeviceDetails dd = getDeviceDetail("6900233111", null, -12.52001);
		Optional<DeviceDetails> deviceDetails = Optional.of(dd);
		DeviceInfoService dfs = new DeviceInfoService();

		Method generateIOTResponse = DeviceInfoService.class.getDeclaredMethod("generateIOTResponse", Optional.class);
		generateIOTResponse.setAccessible(true);
		Optional<?> response = (Optional<?>) generateIOTResponse.invoke(dfs, deviceDetails);
		IOTResponse iotResp = (IOTResponse) response.get();

		assertEquals("ERROR: Device could not be located", iotResp.getDescription());
	}

	@Test
	// private method test
	void shouldRequrn_DeviceInformation() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		DeviceDetails dd = getDeviceDetail("6900233111", 51.5185, -12.52001);
		Optional<DeviceDetails> deviceDetails = Optional.of(dd);
		DeviceInfoService dfs = new DeviceInfoService();

		Method generateIOTResponse = DeviceInfoService.class.getDeclaredMethod("generateIOTResponse", Optional.class);
		generateIOTResponse.setAccessible(true);
		Optional<?> response = (Optional<?>) generateIOTResponse.invoke(dfs, deviceDetails);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "6900233111");
		assertEquals(deviceInfoResponse.getName(), "GeneralTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52001");
		assertEquals(deviceInfoResponse.getLatitude(), "51.5185");
		assertEquals(deviceInfoResponse.getStatus(), "Active");
		assertEquals(deviceInfoResponse.getBattery(), "High");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");
	}

	@Test
	// private method test
	void shouldRequrn_DeviceNotAvailableInPast_WG() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<DeviceDetails> devList = null;
		DeviceInfoService dfs = new DeviceInfoService();
		Method generateIOTResponse = DeviceInfoService.class.getDeclaredMethod("generateIOTResponse", List.class);
		generateIOTResponse.setAccessible(true);
		Optional<?> response = (Optional<?>) generateIOTResponse.invoke(dfs, devList);
		IOTResponse iotResp = (IOTResponse) response.get();

		assertEquals("ERROR: No Device information available in near past", iotResp.getDescription());
	}

	@Test
	// private method test
	void shouldRequrn_DeviceNotLocatedWhenGPSDataMissing_And_AirplanModeOFF_WG() throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<DeviceDetails> devList = new LinkedList();
		DeviceDetails dd = getDeviceDetail("WG11155638", null, -12.52001);
		devList.add(dd);
		DeviceInfoService dfs = new DeviceInfoService();

		Method generateIOTResponse = DeviceInfoService.class.getDeclaredMethod("generateIOTResponse", List.class);
		generateIOTResponse.setAccessible(true);
		Optional<?> response = (Optional<?>) generateIOTResponse.invoke(dfs, devList);
		IOTResponse iotResp = (IOTResponse) response.get();

		assertEquals("ERROR: Device could not be located", iotResp.getDescription());
	}

	@Test
	// private method test
	void shouldRequrn_DeviceInformation_WG() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		List<DeviceDetails> devList = new LinkedList();
		devList.add(getDeviceDetail("WG11155638", 51.5185, -12.52001));
		devList.add(getDeviceDetail("WG11155638", 51.5180, -12.52031));
		devList.add(getDeviceDetail("WG11155638", 51.5175, -12.52051));
		DeviceInfoService dfs = new DeviceInfoService();

		Method generateIOTResponse = DeviceInfoService.class.getDeclaredMethod("generateIOTResponse", List.class);
		generateIOTResponse.setAccessible(true);
		Optional<?> response = (Optional<?>) generateIOTResponse.invoke(dfs, devList);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155638");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52001");
		assertEquals(deviceInfoResponse.getLatitude(), "51.5185");
		assertEquals(deviceInfoResponse.getStatus(), "Active");
		assertEquals(deviceInfoResponse.getBattery(), "High");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");
	}

	@Test
	// There is only one set of Latitude and Longitude hence its status should be
	// Inactive
	void shouldReturnCorrectTrackingInformationOfADevice() throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');
		Long tstmp = 1582605077000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155638", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155638");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:31:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-0.1736");
		assertEquals(deviceInfoResponse.getLatitude(), "51.5185");
		assertEquals(deviceInfoResponse.getStatus(), "Inactive");
		assertEquals(deviceInfoResponse.getBattery(), "Full");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	@Test
	// Testing with Pipe separated csv file
	void shouldReturnCorrectTrackingInformationOfADevice_For_Device_69() throws IOException {

		final String csvFile = creatAndGetCSVFile_PIPE_SEP();

		fileService.loadCSVFile(csvFile, '|');
		Long tstmp = 1582605615000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("6900233111", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "6900233111");
		assertEquals(deviceInfoResponse.getName(), "GeneralTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:40:15");
		assertEquals(deviceInfoResponse.getLongitude(), "");
		assertEquals(deviceInfoResponse.getLatitude(), "");
		assertEquals(deviceInfoResponse.getStatus(), "Inactive");
		assertEquals(deviceInfoResponse.getBattery(), "Low");
		assertEquals(deviceInfoResponse.getDescription(),
				"SUCCESS: Location not available: Please turn off airplane mode");

	}

	@Test
	// Testing with ':' delimited csv file
	void shouldReturnDeviceInfo_WhenNoTimeStampprovided() throws IOException {

		final String csvFile = creatAndGetCSVFile_COLON_SEP();

		fileService.loadCSVFile(csvFile, ':');

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155638", null);

		DeviceInfoResponse dr = (DeviceInfoResponse) response.get();
		assertEquals("WG11155638", dr.getId());
		assertEquals("CyclePlusTracker", dr.getName());
		assertEquals("25/02/2020 04:38:17", dr.getDatetime());
		assertEquals("-0.17538", dr.getLongitude());
		assertEquals("51.5185", dr.getLatitude());
		assertEquals("Active", dr.getStatus());
		assertEquals("High", dr.getBattery());
		assertEquals("SUCCESS: Location identified.", dr.getDescription());

	}

	@Test
	void shouldReturnIdNotFound_WhenProvideWrongProductId_WG() throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');

		Long tstmp = 1582605077000L;
		// testing with wrong product id WG11155639
		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155639", tstmp);
		IOTResponse iotResp = (IOTResponse) response.get();
		assertEquals("ERROR: Id <WG11155639> not found", iotResp.getDescription());

	}

	@Test
	void shouldReturnIdNotFound_WhenProvideWrongProductId_69() throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');

		Long tstmp = 1582605077000L;
		// testing with wrong product id 6900001221
		Optional<?> response = deviceInfoService.getDeviceInfo("6900001221", tstmp);
		IOTResponse iotResp = (IOTResponse) response.get();
		assertEquals("ERROR: Id <6900001221> not found", iotResp.getDescription());

	}

	@Test
	public void shouldReturnNotFoundStatus_WhenNoPastTimeStampAvailable_WG() throws Exception {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');

		Long tstmp = 1582605076000L;
		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155638", tstmp);
		IOTResponse iotResp = (IOTResponse) response.get();

		assertEquals("ERROR: No Device information available in near past", iotResp.getDescription());
	}

	@Test
	// Test GeneralTracker
	public void shouldReturnNotFoundStatus_WhenNoPastTimeStampAvailable_69() throws Exception {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');

		Long tstmp = 1582605252000L;
		Optional<?> response = deviceInfoService.getDeviceInfo("6900001001", tstmp);
		IOTResponse iotResp = (IOTResponse) response.get();

		assertEquals("ERROR: No Device information available in near past", iotResp.getDescription());
	}

	@Test
	void shouldReturn_Device_could_not_be_located_WhenNoGPSDataAvailable_And_AirPlaneMode_OFF() throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');

		Long tstmp = 1582612875000L;
		Optional<?> response = deviceInfoService.getDeviceInfo("6900233111", tstmp);
		IOTResponse iotResp = (IOTResponse) response.get();
		assertEquals("ERROR: Device could not be located", iotResp.getDescription());

	}

	@Test
	// There are three sets of Longitude and Latitude Information for this device
	// and their movement are also significant hence it's status should be Active
	// test case scenario
	// "1582605317000,10008,WG11155801,45.5185,-12.52035,0.11,ON,OFF"
	// "1582605377000,10009,WG11155801,45.5186,-12.52027,0.83,ON,OFF"
	// "1582605437000,10010,WG11155801,45.5187,-12.52001,0.82,ON,OFF"
	void shouldReturnCorrectTrackingInformationOfADevice_WithStatus_Active() throws IOException {

		final String csvFile = creatAndGetCSVFile_TAB_SEP();

		fileService.loadCSVFile(csvFile, '	');// testing with tab seperated csv file
		Long tstmp = 1582605437000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155801", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155801");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52001");
		assertEquals(deviceInfoResponse.getLatitude(), "45.5187");
		assertEquals(deviceInfoResponse.getStatus(), "Active");
		assertEquals(deviceInfoResponse.getBattery(), "High");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	@Test
	// There are three sets of Longitude and Latitude Information for this device
	// but their movement are not significant hence it's status should be N/A
	// test cases
	// "1582605317000,10008,WG11155800,45.5185,-12.52029,0.11,ON,OFF"
	// "1582605377000,10009,WG11155800,45.5186,-12.52027,0.1,ON,OFF"
	// "1582605437000,10010,WG11155800,45.5187,-12.52025,0.09,ON,OFF"
	void shouldReturnCorrectTrackingInformationOfADevice_WithStatus_NA() throws IOException {

		final String csvFile = creatAndGetCSVFile_Semi_Colon_SEPA();

		fileService.loadCSVFile(csvFile, ';');
		Long tstmp = 1582605437000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155800", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155800");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52025");
		assertEquals(deviceInfoResponse.getLatitude(), "45.5187");
		assertEquals(deviceInfoResponse.getStatus(), "N/A");
		assertEquals(deviceInfoResponse.getBattery(), "Critical");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	// There are three sets of Longitude and Latitude Information for this device
	// but all are same hence it's status should be Inactive
	// test cases scenario
	// "1582605257000,10004,WG11155638,51.5185,-0.1736,0.98,OFF,OFF"
	// "1582605137000,10002,WG11155638,51.5185,-0.1736,0.99,OFF,OFF"
	// "1582605197000,10003,WG11155638,51.5185,-0.1736,0.98,OFF,OFF"
	@Test
	void shouldReturnCorrectTrackingInformationOfDevice_WithStatus_InActive() throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');
		Long tstmp = 1582605257000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155638", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155638");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:34:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-0.1736");
		assertEquals(deviceInfoResponse.getLatitude(), "51.5185");
		assertEquals(deviceInfoResponse.getStatus(), "Inactive");
		assertEquals(deviceInfoResponse.getBattery(), "Full");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	// There are only two sets of Longitude and Latitude Information available for
	// this device
	// hence it's status should be Inactive
	// test cases scenario
	// "1582605377000,10009,WG11155805,45.5186,-12.52027,0.83,ON,OFF"
	// "1582605437000,10010,WG11155805,45.5187,-12.52001,0.82,ON,OFF"
	@Test
	void shouldReturnCorrectTrackingInformationOfDevice_WithStatus_InActive_WhenOneGPS_Info_Missing()
			throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');
		Long tstmp = 1582605437000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155805", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155805");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52001");
		assertEquals(deviceInfoResponse.getLatitude(), "45.5187");
		assertEquals(deviceInfoResponse.getStatus(), "Inactive");
		assertEquals(deviceInfoResponse.getBattery(), "Low");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	@Test
	// There are three sets of Longitude and Latitude Information for this device
	// and their movement are also significant but one past Longitude is missing
	// hence it's status should be Inactive
	// test case scenario
	// "1582605317000,10008,WG11155803,45.5185,-12.52035,0.11,ON,OFF"
	// "1582605377000,10009,WG11155803,45.5186,-12.52027,0.83,ON,OFF"
	// "1582605437000,10010,WG11155803,45.5187,-12.52001,0.82,ON,OFF"
	void shouldReturnCorrectTrackingInformationOfADevice_WithStatus_InActive() throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');
		Long tstmp = 1582605437000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155803", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155803");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52001");
		assertEquals(deviceInfoResponse.getLatitude(), "45.5187");
		assertEquals(deviceInfoResponse.getStatus(), "Inactive");
		assertEquals(deviceInfoResponse.getBattery(), "Medium");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	@Test
	// There are three sets of Longitude and Latitude Information for this device
	// and their movement are also significant but latest Longitude and Latitude are
	// missing hence it's
	// status should be Inactive
	// test case scenario
	// "1582605317000,10008,WG11155804,45.5185,-12.52035,0.11,ON,OFF"
	// "1582605377000,10009,WG11155804,45.5186,-12.52027,0.83,ON,OFF"
	// "1582605437000,10010,WG11155804,,,0.82,ON,OFF"
	void shouldReturn_Device_could_not_be_located_WhenNoLatestGPSDataAvailable_And_AirPlaneMode_OFF()
			throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');
		Long tstmp = 1582605437000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155804", tstmp);

		IOTResponse iotResp = (IOTResponse) response.get();
		assertEquals("ERROR: Device could not be located", iotResp.getDescription());
	}

	@Test
	// There are three sets of Longitude and Latitude Information for this device
	// but one Latitude is missing in past hence it's status should be Inactive
	// test case scenario
	// "1582605317000,10008,WG11155803,45.5185,-12.52035,0.63,ON,OFF"
	// "1582605377000,10009,WG11155803,,-12.52027,0.59,ON,OFF"
	// "1582605437000,10010,WG11155803,45.5187,-12.52001,0.59,ON,OFF"
	void shouldReturnCorrectTrackingInformationOfADevice_WithStatus_Inactive_When_Any_PastGPS_Missing()
			throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');
		Long tstmp = 1582605437000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155803", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155803");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52001");
		assertEquals(deviceInfoResponse.getLatitude(), "45.5187");
		assertEquals(deviceInfoResponse.getStatus(), "Inactive");
		assertEquals(deviceInfoResponse.getBattery(), "Medium");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	@Test
	// There are three sets of Longitude and Latitude Information for this device
	// but one Longitude is missing in past hence it's status should be Inactive
	// test case scenario
	// "1582605317000,10008,WG11155806,45.5185,,0.85,ON,OFF"
	// "1582605377000,10009,WG11155806,45.5186,-12.52027,0.83,ON,OFF"
	// "1582605437000,10010,WG11155806,45.5187,-12.52001,0.82,ON,OFF"
	void shouldReturnCorrectTrackingInformationOfADevice_WithStatus_Inactive_When_Any_Longitude_Missing()
			throws IOException {

		final String csvFile = creatAndGetCSVFile();

		fileService.loadCSVFile(csvFile, ',');
		Long tstmp = 1582605437000L;

		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155806", tstmp);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
		assertEquals(deviceInfoResponse.getId(), "WG11155806");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52001");
		assertEquals(deviceInfoResponse.getLatitude(), "45.5187");
		assertEquals(deviceInfoResponse.getStatus(), "Inactive");
		assertEquals(deviceInfoResponse.getBattery(), "High");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	private static DeviceDetails getDeviceDetail(String devId, Double lat, Double lon) {
		DeviceDetails deviceInfo = new DeviceDetails();
		deviceInfo.setBattery(.90);
		deviceInfo.setDateTime(1582605437000L);
		deviceInfo.setProductId(devId);
		deviceInfo.setAirplaneMode("OFF");
		deviceInfo.setLatitude(lat);
		deviceInfo.setLongitude(lon);
		return deviceInfo;
	}

}
