package com.vodafone.uk.iot.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import com.vodafone.uk.iot.beans.CSVLocation;
import com.vodafone.uk.iot.response.IOTResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;

import java.net.InetAddress;
import java.net.UnknownHostException;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class IOTControllerHttpPostTest extends IOTHttpMockMvcTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	
	@Test
	public void shouldReturnOKResponseForSuccessfulLoadingOfCSVFile() throws Exception {

		CSVLocation csvLocation = new CSVLocation();
		csvLocation.setFilepath(creatAndGetCSVFile());

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", csvLocation,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("data refreshed", iotResp.getDescription());

	}
	
	@Test
	public void shouldReturnNotFoundResponse_WhenCSVFileIsNotAvailable() throws Exception {

		//below file is not available
		final String csvFile = System.getProperty("user.dir") + "/" + "NotAdata.csv";

		CSVLocation csvLocation = new CSVLocation();
		csvLocation.setFilepath(csvFile);

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", csvLocation,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertEquals("ERROR: no data file found", iotResp.getDescription());

	}


	public String getBaseUrl() throws UnknownHostException {
		String baseUrl = "http://" + InetAddress.getLocalHost().getHostName() + ":" + port;
		return baseUrl;
	}

}
