package com.vodafone.uk.iot.service;

import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.service.IOTDataService;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileServiceTest {
	
	@Autowired
	IOTDataService fileService;
	
	@Test
	void shouldReturn_Data_Refreshed_Msg_OnSuccessfullLoadingOfCSVFile() throws IOException{
		
		final String csvFile = creatAndGetCSVFile();			    
	    
	    Optional<IOTResponse> resp = fileService.loadCSVFile(csvFile);
	    
	    IOTResponse iotResp = resp.get();
	   
	    assertEquals(iotResp.getDescription(), "data refreshed");
        
	}
	
	@Test
	void shouldReturnNotFoundHttpResponse_WhenCSVFileNotFound() throws IOException{
		
		//below file does not exist
		final String csvFile = System.getProperty("user.dir") + "/" + "NotACSV.csv";		
	    
		Optional<IOTResponse> resp = fileService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.get();

	    assertEquals(iotResp.getDescription(), "ERROR: no data file found");
        
	}
	
	@Test
	void shouldReturnBadRequestHttpResponse_WhenCSVFileDoesNotHaveAnyRecordsOrcorrupt() throws IOException{
		
		String csvFile = System.getProperty("user.dir") + "/" + "Empty.csv";
		
		FileWriter fileWriter = new FileWriter(csvFile);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.println("DateTime,EventId,ProductId,Latitude,Longitude,Battery,Light,AirplaneMode");
	    printWriter.close();
	    
	    Optional<IOTResponse> resp = fileService.loadCSVFile(csvFile);
	    IOTResponse iotResp =  (IOTResponse) resp.get();
	  
	    assertEquals(iotResp.getDescription(), "ERROR: CSV file is empty or corrupt");
        
	}	

}
