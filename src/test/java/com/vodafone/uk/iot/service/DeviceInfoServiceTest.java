package com.vodafone.uk.iot.service;

import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;
import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetBigCSVFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.service.IOTDataService;
import com.vodafone.uk.iot.service.IOTDeviceInfoService;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DeviceInfoServiceTest {

	@Autowired
	IOTDataService fileService;
	
	@Autowired
	IOTDeviceInfoService deviceInfoService;
	
	@Test
	void shouldReturnCorrectTrackingInformationOfADevice() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();
	    
		fileService.loadCSVFile(csvFile);
	    Long tstmp = 1582605077000L;
	    
	    Optional<?> response = deviceInfoService.getDeviceInfo("WG11155638", tstmp);
	  	    
	    DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
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
	void shouldReturnDeviceInfo_WhenNoTimeStampprovided() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();
	    
		fileService.loadCSVFile(csvFile);
	    
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
	void shouldReturnIdNotFound_WhenProvideWrongProductId() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();
	    
		fileService.loadCSVFile(csvFile);
	    
	    Long tstmp = 1582605077000L;
	    //testing with wrong product id WG11155639
	    Optional<?> response = deviceInfoService.getDeviceInfo("WG11155639", tstmp);
	    IOTResponse iotResp =  (IOTResponse) response.get();
	    assertEquals("ERROR: Id <WG11155639> not found", iotResp.getDescription());
        
	}
	
	@Test
	void shouldReturn_Device_could_not_be_located_WhenNoGPSDataAvailable_And_AirPlaneMode_OFF() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();
    
		fileService.loadCSVFile(csvFile);
	   
	    Long tstmp = 1582612875000L;
	    Optional<?> response = deviceInfoService.getDeviceInfo("6900233111", tstmp);
	    IOTResponse iotResp =  (IOTResponse) response.get();
	    assertEquals("ERROR: Device could not be located", iotResp.getDescription());
        
	}
	
	
	void performanceTesting() throws IOException{
		
		Instant instant = Instant.now();
	    Long before = instant.toEpochMilli();
		final String csvFile = creatAndGetBigCSVFile();
		instant = Instant.now();
	    Long after = instant.toEpochMilli();
		System.out.println("Time Taken to create the file - " + (after-before));
		instant = Instant.now();
	    before = instant.toEpochMilli();
	    System.out.println("Before Loading the file....");
	    Optional<IOTResponse> resp = fileService.loadCSVFile(csvFile);
	    instant = Instant.now();
	    after = instant.toEpochMilli();
	    System.out.println("Time Taken to load the file - " + (after-before));
	    IOTResponse iotResp = resp.get();
	  
	    assertEquals(iotResp.getDescription(), "data refreshed");
	    
	    Long tstmp = 1582605277000L;
	    instant = Instant.now();
	    before = instant.toEpochMilli();
	    System.out.println("Before :"+ instant.toString() + " In mili :" + before);
	    Optional<?> response = deviceInfoService.getDeviceInfo("WG11155638", tstmp);
	    instant = Instant.now();
	    after = instant.toEpochMilli();
	    System.out.println("After :"+ instant.toString() + " in Mili :" + after);
	    System.out.println("Time taken in mili = " + (after - before)  );
	    DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) response.get();
	    System.out.println(deviceInfoResponse.getDatetime());
	    assertEquals(deviceInfoResponse.getId(),"WG11155638");
	    assertEquals(deviceInfoResponse.getName(),"CyclePlusTracker");	
        
	}
	
}
