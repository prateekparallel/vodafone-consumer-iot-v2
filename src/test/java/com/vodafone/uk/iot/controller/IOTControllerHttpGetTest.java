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
import com.vodafone.uk.iot.service.IOTDataService;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class IOTControllerHttpGetTest extends IOTHttpMockMvcTest{
	
	@LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    IOTDataService iOTDataService;
    
    @BeforeEach
    public void init() throws IOException {
    	iOTDataService.loadCSVFile(creatAndGetCSVFile());
    }

      
    @Test
    public void shouldReturnOKStatusCode_WhenFoundADevice() throws Exception {    	
    	
    	String url = getBaseUrl() + "/iot/v2/event";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    	builder.queryParam("ProductId", "WG11155638");
    	builder.queryParam("tstmp", "1582605077000");
    	
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), 
    			DeviceInfoResponse.class);
   
    	DeviceInfoResponse dr = (DeviceInfoResponse) responseEntity.getBody();
    	assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    	assertEquals("WG11155638", dr.getId());
    	assertEquals("CyclePlusTracker", dr.getName());
    	assertEquals("25/02/2020 04:31:17", dr.getDatetime());
    	assertEquals("-0.1736", dr.getLongitude());
    	assertEquals("51.5185", dr.getLatitude());
    	assertEquals("Active", dr.getStatus());
    	assertEquals("Full", dr.getBattery());
    	assertEquals("SUCCESS: Location identified.", dr.getDescription());
       
    }
    
    @Test
    public void shouldReturnNearerDeviceRecordInPast_WhenSendingEmptyTimeStamp() throws Exception {
    	
    	String url = getBaseUrl() + "/iot/v2/event";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    	builder.queryParam("ProductId", "WG11155638");
    	//builder.queryParam("tstmp", "");
    	
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), 
    			DeviceInfoResponse.class);
   
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
    
    //This test scenario is not available in document provided to me
    //If a user search for a record with a timestamp which is smaller then all existing 
    //timestamp in the database then not record will be found as no past record nearer to 
    //that timestamp will be available in the system. So not found will be send from server
    @Test
    public void shouldReturnBadRequestStatus_WhenNoPastTimeStampAvailable() throws Exception {
    	
    	String url = getBaseUrl() + "/iot/v2/event";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    	builder.queryParam("ProductId", "WG11155638");
    	builder.queryParam("tstmp", "1582605076000");//smaller then existing timestamp 1582605077000
    	
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), 
    			IOTResponse.class);
    	
    	IOTResponse iotResp = (IOTResponse)responseEntity.getBody();
   
    	assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    	assertEquals("ERROR: No Device information available in near past", iotResp.getDescription());
    }
    
    
    @Test
    public void shouldReturnBadRequestStatus_WhenNoLatitudeOrLongitudeAvailable() throws Exception {
    	    	
    	String url = getBaseUrl() + "/iot/v2/event";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    	builder.queryParam("ProductId", "6900233111");
    	builder.queryParam("tstmp", "1582612875000");
    	
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), 
    			IOTResponse.class);
    	
    	IOTResponse iotResp = (IOTResponse)responseEntity.getBody();
   
    	assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    	assertEquals("ERROR: Device could not be located", iotResp.getDescription());
    }
   
    
    @Test
    public void shouldReturnOkStatus_WhenAirPlaneModeON() throws Exception {
    	
    	String url = getBaseUrl() + "/iot/v2/event";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    	builder.queryParam("ProductId", "6900233111");
    	builder.queryParam("tstmp", "1582605615000");
    	
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), 
    			DeviceInfoResponse.class);
   
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
    public void shouldReturnNotFoundstStatus_WhenDataNotAvailableForAProductID() throws Exception {
    	
    	String url = getBaseUrl() + "/iot/v2/event";
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    	builder.queryParam("ProductId", "6900233122");
    	builder.queryParam("tstmp", "1582612875000");
    	
    	ResponseEntity<?> responseEntity = restTemplate.getForEntity(builder.toUriString(), 
    			IOTResponse.class);
    	
    	IOTResponse iotResp = (IOTResponse)responseEntity.getBody();
 
    	assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    	assertEquals("ERROR: Id <6900233122> not found", iotResp.getDescription());       
    }    
   
    
    public String getBaseUrl() throws UnknownHostException {
    	String baseUrl = "http://" + InetAddress.getLocalHost().getHostName() + ":" + port;
    	return baseUrl;
    }

}
