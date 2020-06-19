package com.vodafone.uk.iot.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vodafone.uk.iot.beans.CSVLocation;
import com.vodafone.uk.iot.constant.IOTConstant;
import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.service.IOTDataService;
import com.vodafone.uk.iot.service.IOTDeviceInfoService;

@Controller
@RequestMapping("/v2")
public class IOTController {
	
	@Autowired
	IOTDataService dataService;
	
	@Autowired
	IOTDeviceInfoService deviceInfoService;
	
	
	@PostMapping(path="/event",consumes = "application/json", produces = "application/json")
	public ResponseEntity<IOTResponse> loadCSVFile(@RequestBody CSVLocation csvLocation) {//throws IOTException {
		
		Optional<IOTResponse> resp = dataService.loadCSVFile(csvLocation.getFilepath());
		IOTResponse iotResponse = resp.get();
		
		if(iotResponse.getDescription().equals(IOTConstant.DATA_REFRESHED)) {
			return ResponseEntity.ok(iotResponse);
		}
		else if(iotResponse.getDescription().equals(IOTConstant.ERROR_EMPTY_FILE)) {
			return ResponseEntity.badRequest().body(iotResponse);
		}
		else if(iotResponse.getDescription().equals(IOTConstant.ERROR_TECHNICAL_EXCEP)) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(iotResponse);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(iotResponse);
	}
	
	
	@GetMapping(path="/event", produces = "application/json")
	public ResponseEntity<?> getDeviceInfo(@RequestParam(value="ProductId",required = true) 
	String ProductId, @RequestParam(value="tstmp",required = false) Long tstmp ){
		
		Optional<?> resp = deviceInfoService.getDeviceInfo(ProductId, tstmp);
		
		if (resp.get() instanceof DeviceInfoResponse ){
			return ResponseEntity.ok(resp.get());
		}
		//if it is not a DeviceInfoResponse definitely it is IOTResponse
		
		IOTResponse iotResponse = (IOTResponse)resp.get();
		
		HttpStatus httpstatus = HttpStatus.NOT_FOUND;
		
		if(iotResponse.getDescription().equals(IOTConstant.ERROR_DEVICE_NOT_LOCATED)) {
			httpstatus = HttpStatus.BAD_REQUEST;
		}
		
		return ResponseEntity.status(httpstatus).body(resp.get());

	}	
	

}
