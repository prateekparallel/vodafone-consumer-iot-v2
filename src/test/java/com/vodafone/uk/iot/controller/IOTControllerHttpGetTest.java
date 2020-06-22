package com.vodafone.uk.iot.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.service.IOTFileService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_COLON_SEP;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_PIPE_SEP;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_Semi_Colon_SEPA;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_TAB_SEP;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class IOTControllerHttpGetTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	IOTFileService fileService;

	@Test
	public void shouldReturnOKStatusCode_WhenFoundADevice() throws Exception {

		fileService.loadCSVFile(creatAndGetCSVFile(), ',');

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155638");
		builder.queryParam("tstmp", "1582605077000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		DeviceInfoResponse dr = (DeviceInfoResponse) responseEntity.getBody();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("WG11155638", dr.getId());
		assertEquals("CyclePlusTracker", dr.getName());
		assertEquals("25/02/2020 04:31:17", dr.getDatetime());
		assertEquals("-0.1736", dr.getLongitude());
		assertEquals("51.5185", dr.getLatitude());
		assertEquals("Inactive", dr.getStatus());
		assertEquals("Full", dr.getBattery());
		assertEquals("SUCCESS: Location identified.", dr.getDescription());

	}

	@Test
	public void shouldReturnNearerDeviceRecordInPast_WhenSendingEmptyTimeStamp() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155638");
		// builder.queryParam("tstmp", "");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		DeviceInfoResponse dr = (DeviceInfoResponse) responseEntity.getBody();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("WG11155638", dr.getId());
		assertEquals("CyclePlusTracker", dr.getName());
		assertEquals("25/02/2020 04:38:17", dr.getDatetime());
		assertEquals("-0.17538", dr.getLongitude());
		assertEquals("51.5185", dr.getLatitude());
		assertEquals("Active", dr.getStatus());
		assertEquals("High", dr.getBattery());
		assertEquals("SUCCESS: Location identified.", dr.getDescription());

	}

	// Because of part 3 - below test cases should be check for both
	// CyclePlusTracker and GeneralTracker

	// Test CyclePlusTracker

	// This test scenario is not available in document provided to me
	// If a user search for a record with a timestamp which is smaller then all
	// existing
	// timestamp in the database then no record will be found as no past record
	// nearer to
	// that timestamp will be available in the system. So not found will be send
	// from server
	@Test
	public void shouldReturnNotFoundStatus_WhenNoPastTimeStampAvailable_WG() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155638");
		builder.queryParam("tstmp", "1582605076000");// smaller then existing timestamp 1582605077000

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), IOTResponse.class);

		IOTResponse iotResp = (IOTResponse) responseEntity.getBody();

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertEquals("ERROR: No Device information available in near past", iotResp.getDescription());
	}

	@Test
	// Test GeneralTracker
	public void shouldReturnNotFoundStatus_WhenNoPastTimeStampAvailable_69() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "6900001001");
		builder.queryParam("tstmp", "1582605252000");// smaller then existing timestamp 1582605253000

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), IOTResponse.class);

		IOTResponse iotResp = (IOTResponse) responseEntity.getBody();

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertEquals("ERROR: No Device information available in near past", iotResp.getDescription());
	}

	@Test
	// Test for device - GeneralTracker
	public void shouldReturnBadRequestStatus_WhenNoLatitudeOrLongitudeAvailable_69() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "6900233111");
		builder.queryParam("tstmp", "1582612875000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), IOTResponse.class);

		IOTResponse iotResp = (IOTResponse) responseEntity.getBody();

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertEquals("ERROR: Device could not be located", iotResp.getDescription());
	}

	@Test
	// Test for device - CyclePlusTracker
	public void shouldReturnBadRequestStatus_WhenNoLatitudeOrLongitudeAvailable_WG() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155804");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), IOTResponse.class);

		IOTResponse iotResp = (IOTResponse) responseEntity.getBody();

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertEquals("ERROR: Device could not be located", iotResp.getDescription());
	}

	@Test
	// Test for device - GeneralTracker
	public void shouldReturnOkStatus_And_Location_not_available_WhenAirPlaneModeON_69() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "6900233111");
		builder.queryParam("tstmp", "1582605615000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		DeviceInfoResponse dr = (DeviceInfoResponse) responseEntity.getBody();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("6900233111", dr.getId());
		assertEquals("GeneralTracker", dr.getName());
		assertEquals("25/02/2020 04:40:15", dr.getDatetime());
		assertEquals("", dr.getLongitude());
		assertEquals("", dr.getLatitude());
		assertEquals("Inactive", dr.getStatus());
		assertEquals("Low", dr.getBattery());
		assertEquals("SUCCESS: Location not available: Please turn off airplane mode", dr.getDescription());

	}

	@Test
	// Test for device - CyclePlusTracker
	public void shouldReturnOkStatus_And_Location_not_available_WhenAirPlaneModeON_WG() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155841");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		DeviceInfoResponse dr = (DeviceInfoResponse) responseEntity.getBody();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("WG11155841", dr.getId());
		assertEquals("CyclePlusTracker", dr.getName());
		assertEquals("25/02/2020 04:37:17", dr.getDatetime());
		assertEquals("", dr.getLongitude());
		assertEquals("", dr.getLatitude());
		assertEquals("Inactive", dr.getStatus());
		assertEquals("High", dr.getBattery());
		assertEquals("SUCCESS: Location not available: Please turn off airplane mode", dr.getDescription());

	}

	@Test
	// Test for device - GeneralTracker
	public void shouldReturnNotFoundstStatus_WhenDataNotAvailableForAProductID_69() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "6900233122");
		builder.queryParam("tstmp", "1582612875000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), IOTResponse.class);

		IOTResponse iotResp = (IOTResponse) responseEntity.getBody();

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertEquals("ERROR: Id <6900233122> not found", iotResp.getDescription());
	}

	@Test
	// Test for device - CyclePlusTracker
	public void shouldReturnNotFoundstStatus_WhenDataNotAvailableForAProductID_WG() throws Exception {
		fileService.loadCSVFile(creatAndGetCSVFile(), ',');
		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155691");
		builder.queryParam("tstmp", "1582612875000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), IOTResponse.class);

		IOTResponse iotResp = (IOTResponse) responseEntity.getBody();

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertEquals("ERROR: Id <WG11155691> not found", iotResp.getDescription());
	}

	@Test
	// There are three sets of Longitude and Latitude Information for this device
	// and their movement are also significant hence it's status should be Active
	// test case scenario
	// "1582605317000,10008,WG11155801,45.5185,-12.52035,0.11,ON,OFF"
	// "1582605377000,10009,WG11155801,45.5186,-12.52027,0.83,ON,OFF"
	// "1582605437000,10010,WG11155801,45.5187,-12.52001,0.82,ON,OFF"
	void shouldReturnCorrectTrackingInformationOfADevice_WithStatus_Active() throws IOException {

		fileService.loadCSVFile(creatAndGetCSVFile_TAB_SEP(), '	');

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155801");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) responseEntity.getBody();
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

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155800");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) responseEntity.getBody();
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

		final String csvFile = creatAndGetCSVFile_COLON_SEP();

		fileService.loadCSVFile(csvFile, ':');

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155638");
		builder.queryParam("tstmp", "1582605257000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) responseEntity.getBody();
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

		final String csvFile = creatAndGetCSVFile_PIPE_SEP();

		fileService.loadCSVFile(csvFile, '|');

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155805");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) responseEntity.getBody();
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

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155803");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) responseEntity.getBody();
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

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155804");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), IOTResponse.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		IOTResponse iotResp = (IOTResponse) responseEntity.getBody();

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

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155803");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) responseEntity.getBody();
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

		String url = getBaseUrl() + "/iot/v2/event";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("ProductId", "WG11155806");
		builder.queryParam("tstmp", "1582605437000");

		ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), DeviceInfoResponse.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) responseEntity.getBody();
		assertEquals(deviceInfoResponse.getId(), "WG11155806");
		assertEquals(deviceInfoResponse.getName(), "CyclePlusTracker");
		assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:37:17");
		assertEquals(deviceInfoResponse.getLongitude(), "-12.52001");
		assertEquals(deviceInfoResponse.getLatitude(), "45.5187");
		assertEquals(deviceInfoResponse.getStatus(), "Inactive");
		assertEquals(deviceInfoResponse.getBattery(), "High");
		assertEquals(deviceInfoResponse.getDescription(), "SUCCESS: Location identified.");

	}

	public String getBaseUrl() throws UnknownHostException {
		String baseUrl = "http://" + InetAddress.getLocalHost().getHostName() + ":" + port;
		return baseUrl;
	}

}
