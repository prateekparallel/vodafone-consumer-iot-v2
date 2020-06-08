package com.vodafone.uk.iot.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.vodafone.uk.iot.beans.DeviceDetails;
import com.vodafone.uk.iot.beans.ProductList;
import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.util.IOTUtil;

import org.springframework.http.HttpStatus;

/***
 * 
 * @author Abhijit P Dutta
 * This is the class provides all the get and post services for this
 * example project.
 */

@Service
public class IOTDataService {
	
	//I am storing device information based on its productId
	//it will help us to retrieve the record very quickly
	private  Map<String,List<DeviceDetails>> deviceDetailDB = null;
	
	/***
	 * This method will load a csv file into the memory 
	 * if unsuccessful it will return appropriate error message
	 * if a file is empty or not found it will not clear the old records
	 * they will be still available. Also I am not cleaning up the container
	 * for a second successful load (cleaning up old records) for performance reason.
	 * Let gc take the full responsibility for cleaning up of garbages in background.
	 * @param filePath
	 * @return IOTResponse
	 */
	public ResponseEntity<?> loadCSVFile(String filePath){
		
		List<DeviceDetails> deviceDetailList = null;
		
		IOTResponse resp = new IOTResponse();
		
		try {
			Reader reader = new FileReader(filePath);
		
			deviceDetailList = readAll(reader);
						
			if(deviceDetailList == null || deviceDetailList.isEmpty()) {
				reader.close();
				resp.setDescription("ERROR: CSV file is empty or corrupt");
				return ResponseEntity.badRequest().body(resp);
			}
			
			deviceDetailDB = deviceDetailList.parallelStream()
					.collect(Collectors.groupingBy(DeviceDetails::getProductId));		
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			resp.setDescription("ERROR: no data file found");
			System.out.println("File - " + filePath + " not found - " + e.getMessage());
			return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
		}catch(Exception e) {
			deviceDetailList = null;
			resp.setDescription("ERROR: A technical exception occurred" + e.getMessage());
			System.out.println("Exception occured while processing file - " + filePath 
					+ " reason - " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
		}
		
		resp.setDescription("data refreshed");
		
		return ResponseEntity.ok().body(resp);
		
	}	
	
	/**
	 * This method will load the csv into the java pojo DeviceDetails.class
	 * @param reader
	 * @return DeviceDetails List
	 */ 
	private List<DeviceDetails> readAll(Reader reader){
		
		HeaderColumnNameMappingStrategy<DeviceDetails> cpms = new HeaderColumnNameMappingStrategy<DeviceDetails>();
		cpms.setType(DeviceDetails.class);
		CsvToBean<DeviceDetails> csvToBean = new CsvToBeanBuilder<DeviceDetails>(reader)
			       .withType(DeviceDetails.class)
			       .withMappingStrategy(cpms)
			       .build();
		return csvToBean.parse();
	}
	
	/**
	 * 	Below method will generate a DeviceInfoResponse if a device found
	 *  in the memory based on productId and timestamp for that device.
	 *  If timestamp is not available, it will return the information of a device
	 *  record which is latest in past i.e. near to timestamp provided by the
	 *  caller. Appropriate error response will be send back to call for any failure 
	 * @param productId
	 * @param tstmp
	 * @return
	 */
	public ResponseEntity<?> getDeviceInfo(String productId, Long tstmp){
		
		if(tstmp == null) {
			Instant instant = Instant.now();
			tstmp = instant.toEpochMilli();
		}		
		
		final Long t_stmp = tstmp;
			
		List<DeviceDetails> deviceDetailList = deviceDetailDB.get(productId);
		
		if(deviceDetailList == null) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription("ERROR: Id <" + productId + "> not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
		}
		
		//if the timestamp provided is not a complete match then it should return the 
		//data that is closest to it in the past. If not past timestamp found it will
		//send a error respose to caller
		Optional<DeviceDetails> deviceDetails = Optional.ofNullable(deviceDetailList.parallelStream()
		.filter(dd-> dd.getDateTime() <= t_stmp )
		.min(Comparator.comparingLong(dd ->(Math.abs(dd.getDateTime()  - t_stmp))))
		.orElse(null));
		
		return generateIOTResponse(deviceDetails);

	}
	
	/**
	 * Below private method will generate a device detail response if successful
	 * else a appropriate error response in case there is any error.
	 * @param deviceDetails
	 * @return
	 */
	private ResponseEntity<?> generateIOTResponse(Optional<DeviceDetails> deviceDetails){
		
		if(deviceDetails.isEmpty()) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription("ERROR: No Device information available in near past");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
		}
		
		DeviceDetails deviceInfo = deviceDetails.get();
		
		if(deviceInfo.getAirplaneMode().equals("OFF")
				&& (deviceInfo.getLatitude() == null || deviceInfo.getLongitude() == null)) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription("ERROR: Device could not be located");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
		}
				
		DeviceInfoResponse deviceInfoResponse = new DeviceInfoResponse();
		
		deviceInfoResponse.setId(deviceInfo.getProductId());
		deviceInfoResponse.setName(ProductList.getProductType(deviceInfo.getProductId()));
		deviceInfoResponse.setDatetime(IOTUtil.convertToDate(deviceInfo.getDateTime()));
		deviceInfoResponse.setLongitude(IOTUtil.getLongitude(deviceInfo));
		deviceInfoResponse.setLatitude(IOTUtil.getLatitude(deviceInfo));
		deviceInfoResponse.setStatus(IOTUtil.getStatus(deviceInfo));
		deviceInfoResponse.setBattery(IOTUtil.getBatteryStatus(deviceInfo));
		deviceInfoResponse.setDescription(IOTUtil.getDescription(deviceInfo));
		return ResponseEntity.status(HttpStatus.OK).body(deviceInfoResponse);
	}	
	
}
