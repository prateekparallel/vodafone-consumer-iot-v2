package com.vodafone.uk.iot.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.vodafone.uk.iot.response.IOTResponse;

/***
 * 
 * @author Abhijit P Dutta
 */

public interface IOTFileService {
	
	public Optional<IOTResponse> loadCSVFile(String filePath, Character separator);
	
}
