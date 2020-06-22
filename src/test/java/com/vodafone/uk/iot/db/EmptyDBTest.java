package com.vodafone.uk.iot.db;

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
import com.vodafone.uk.iot.service.IOTFileService;
import com.vodafone.uk.iot.service.IOTDeviceInfoService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmptyDBTest {

	
	@Autowired
	IOTDeviceInfoService deviceInfoService;

	

	@Test
	void shouldReturnError_Message_Database_not_yet_loaded_WhenDatabaseEmpty(){

		Long tstmp = 1582605077000L;
		
		Optional<?> response = deviceInfoService.getDeviceInfo("WG11155639", tstmp);
		IOTResponse iotResp = (IOTResponse) response.get();
		assertEquals("ERROR: Database not yet loaded", iotResp.getDescription());

	}
	
}
