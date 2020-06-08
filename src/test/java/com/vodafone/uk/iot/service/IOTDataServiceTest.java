package com.vodafone.uk.iot.service;

import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.response.IOTResponse;


class IOTDataServiceTest {

	@Test
	void shouldReturnOKHttpResponse_OnSuccessfullLoadingOfCSVFile() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();
			    
	    IOTDataService iOTDataService = new IOTDataService();
	    
	    ResponseEntity<?> resp = iOTDataService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.getBody();
	    
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
	    assertEquals(iotResp.getDescription(), "data refreshed");
        
	}
	
	@Test
	void shouldReturnNotFoundHttpResponse_WhenCSVFileNotFound() throws IOException{
		
		//below file does not exist
		final String csvFile = System.getProperty("user.dir") + "/" + "NotACSV.csv";
	    
	    IOTDataService iOTDataService = new IOTDataService();
	    
	    ResponseEntity<?> resp = iOTDataService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.getBody();
	    
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	    assertEquals(iotResp.getDescription(), "ERROR: no data file found");
        
	}
	
	@Test
	void shouldReturnBadRequestHttpResponse_WhenCSVFileDoesNotHaveAnyRecordsOrcorrupt() throws IOException{
		
		String csvFile = System.getProperty("user.dir") + "/" + "Empty.csv";;
		
		FileWriter fileWriter = new FileWriter(csvFile);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.println("DateTime,EventId,ProductId,Latitude,Longitude,Battery,Light,AirplaneMode");
	    printWriter.close();
	    
	    IOTDataService iOTDataService = new IOTDataService();
	    
	    ResponseEntity<?> resp = iOTDataService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.getBody();
	    
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	    assertEquals(iotResp.getDescription(), "ERROR: CSV file is empty or corrupt");
        
	}
	
	
	@Test
	void shouldReturnCorrectTrackingInformationOfADevice_withOKHttpResponse() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();
		
	    IOTDataService iOTDataService = new IOTDataService();
	    
	    ResponseEntity<?> resp = iOTDataService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.getBody();
	    
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
	    assertEquals(iotResp.getDescription(), "data refreshed");
	    
	    Long tstmp = 1582605077000L;
	    
	    resp = iOTDataService.getDeviceInfo("WG11155638", tstmp);
	  	    
	    DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) resp.getBody();
	    assertEquals(deviceInfoResponse.getId(),"WG11155638");
	    assertEquals(deviceInfoResponse.getName(),"CyclePlusTracker");
	    assertEquals(deviceInfoResponse.getDatetime(), "25/02/2020 04:31:17");
	    assertEquals(deviceInfoResponse.getLongitude(),"-0.1736");
	    assertEquals(deviceInfoResponse.getLatitude(),"51.5185");
	    assertEquals(deviceInfoResponse.getStatus(),"Active");
	    assertEquals(deviceInfoResponse.getBattery(),"Full");
	    assertEquals(deviceInfoResponse.getDescription(),"SUCCESS: Location identified.");		
        
	}
	
	@Test
	void shouldReturnDeviceInfo_withOKHttpResponse_WhenNoTimeStampprovided() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();
	
	    IOTDataService iOTDataService = new IOTDataService();
	    
	    ResponseEntity<?> resp = iOTDataService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.getBody();
	    
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
	    assertEquals(iotResp.getDescription(), "data refreshed");
	    
	    resp = iOTDataService.getDeviceInfo("WG11155638", null);
	  	    
	    DeviceInfoResponse dr = (DeviceInfoResponse) resp.getBody();
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
	void shouldReturnNotFoundHttpResponse_WhenProvideWrongProductId() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();
	
	    IOTDataService iOTDataService = new IOTDataService();
	    
	    ResponseEntity<?> resp = iOTDataService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.getBody();
	    
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
	    assertEquals(iotResp.getDescription(), "data refreshed");
	    
	    Long tstmp = 1582605077000L;
	    //testing with wrong product id WG11155639
	    resp = iOTDataService.getDeviceInfo("WG11155639", tstmp);
	    iotResp =  (IOTResponse) resp.getBody();
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	    assertEquals("ERROR: Id <WG11155639> not found", iotResp.getDescription());
        
	}
	
	@Test
	void shouldReturnBadRequestHttpResponse_WhenNoGPSDataAvailable_And_AirPlaneMode_OFF() throws IOException{
		final String csvFile = creatAndGetCSVFile();
	    
	    IOTDataService iOTDataService = new IOTDataService();
	    
	    ResponseEntity<?> resp = iOTDataService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.getBody();
	    
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
	    assertEquals(iotResp.getDescription(), "data refreshed");
	    
	    Long tstmp = 1582612875000L;
	    resp = iOTDataService.getDeviceInfo("6900233111", tstmp);
	    iotResp =  (IOTResponse) resp.getBody();
	    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	    assertEquals("ERROR: Device could not be located", iotResp.getDescription());
        
	}
	

}
