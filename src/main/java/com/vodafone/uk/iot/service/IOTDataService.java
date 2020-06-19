package com.vodafone.uk.iot.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vodafone.uk.iot.response.IOTResponse;

/***
 * 
 * @author Abhijit P Dutta
 * This  class provides all the get and post services for this
 * example project.
 */

public interface IOTDataService {
	
	public Optional<IOTResponse> loadCSVFile(String filePath);
	
}
