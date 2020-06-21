package com.vodafone.uk.iot.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import com.vodafone.uk.iot.beans.CSVDetail;
import com.vodafone.uk.iot.response.IOTResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_COLON_SEP;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_PIPE_SEP;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_Semi_Colon_SEPA;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile_TAB_SEP;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCorruptCSVFile;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class IOTControllerHttpPostTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	
	@Test
	public void shouldReturnOKResponseForSuccessfulLoadingOfCSVFile() throws Exception {

		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(creatAndGetCSVFile());

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("data refreshed", iotResp.getDescription());

	}
	
	@Test
	public void shouldReturnOKResponseForSuccessfulLoadingOf_Pipe_Delimited_CSVFile() throws Exception {

		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(creatAndGetCSVFile_PIPE_SEP());
		CSVDetail.setDelimiter('|');

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("data refreshed", iotResp.getDescription());

	}
	
	@Test
	public void shouldReturnOKResponseForSuccessfulLoadingOf_Colon_Delimited_CSVFile() throws Exception {

		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(creatAndGetCSVFile_COLON_SEP());
		CSVDetail.setDelimiter(':');

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("data refreshed", iotResp.getDescription());

	}
	
	@Test
	public void shouldReturnOKResponseForSuccessfulLoadingOf_TAB_Delimited_CSVFile() throws Exception {

		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(creatAndGetCSVFile_TAB_SEP());
		CSVDetail.setDelimiter('	');

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("data refreshed", iotResp.getDescription());

	}
	
	@Test
	public void shouldReturnOKResponseForSuccessfulLoadingOf_Semi_Colon_Delimited_CSVFile() throws Exception {

		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(creatAndGetCSVFile_Semi_Colon_SEPA());
		CSVDetail.setDelimiter(';');

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertEquals("data refreshed", iotResp.getDescription());

	}
	
	@Test
	public void shouldReturnNotFoundResponse_WhenCSVFileIsNotAvailable() throws Exception {

		//below file is not available
		final String csvFile = System.getProperty("user.dir") + "/" + "NotAdata.csv";

		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(csvFile);

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertEquals("ERROR: no data file found", iotResp.getDescription());

	}
	
	@Test
	public void shouldReturnBadResponse_WhenCSVFileIsEmpty() throws Exception {

		//below file is Empty
		String csvFile = System.getProperty("user.dir") + "/" + "Empty.csv";
		
		FileWriter fileWriter = new FileWriter(csvFile);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.println("DateTime,EventId,ProductId,Latitude,Longitude,Battery,Light,AirplaneMode");
	    printWriter.close();

		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(csvFile);

		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertEquals("ERROR: CSV file is empty or corrupt", iotResp.getDescription());

	}
	
	@Test
	//Technical Error test
	public void shouldReturn_Internel_Server_Error_Status_Code_WhenCSVFileIsCorrupt() throws Exception {

		//below file is Corrupt
		final String csvFile = creatAndGetCorruptCSVFile();

		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(csvFile);
		
		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		System.out.println(iotResp.getDescription());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertTrue(iotResp.getDescription().contains("ERROR: A technical exception occurred"));
		
	}
	
	@Test
	//Technical Error test
	public void shouldReturn_Internel_Server_Error_Status_Code_WhenProvide_Wrong_Delim() throws Exception {

		
		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(creatAndGetCSVFile_Semi_Colon_SEPA());
		CSVDetail.setDelimiter('|');
		
		ResponseEntity<IOTResponse> response = restTemplate.postForEntity(getBaseUrl() + "/iot/v2/event", CSVDetail,
				IOTResponse.class);
		
		IOTResponse iotResp = (IOTResponse)response.getBody();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertTrue(iotResp.getDescription().contains("ERROR: A technical exception occurred"));
		
	}



	public String getBaseUrl() throws UnknownHostException {
		String baseUrl = "http://" + InetAddress.getLocalHost().getHostName() + ":" + port;
		return baseUrl;
	}

}
