package com.vodafone.uk.iot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vodafone.uk.iot.beans.CSVLocation;
import com.vodafone.uk.iot.service.IOTDataService;

@Controller
@RequestMapping("/v2")
public class IOTController {
	
	@Autowired
	IOTDataService dataService;
	
	@PostMapping(path="/event",consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> loadCSVFile(@RequestBody CSVLocation csvLocation) {//throws IOTException {
		
		return dataService.loadCSVFile(csvLocation.getFilepath());
	}
	
	
	@GetMapping(path="/event", produces = "application/json")
	public ResponseEntity<?> getDeviceInfo(@RequestParam(value="ProductId",required = true) 
	String ProductId, @RequestParam(value="tstmp",required = false) Long tstmp ){
		
		return dataService.getDeviceInfo(ProductId, tstmp);
	}	

}
