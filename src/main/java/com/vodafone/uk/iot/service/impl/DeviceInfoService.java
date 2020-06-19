package com.vodafone.uk.iot.service.impl;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vodafone.uk.iot.beans.DeviceDetails;
import com.vodafone.uk.iot.beans.ProductList;
import com.vodafone.uk.iot.constant.IOTConstant;
import com.vodafone.uk.iot.db.DeviceDetailDB;
import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.service.IOTDeviceInfoService;
import com.vodafone.uk.iot.util.IOTUtil;

public class DeviceInfoService implements IOTDeviceInfoService{
	
	@Autowired
	DeviceDetailDB deviceDetailDB;
	
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
	@Override
	public Optional<?> getDeviceInfo(String productId, Long tstmp){
		
		if(tstmp == null) {
			Instant instant = Instant.now();
			tstmp = instant.toEpochMilli();
		}		
		
		final Long t_stmp = tstmp;
			
		List<DeviceDetails> deviceDetailList = deviceDetailDB.getDeviceList(productId);
		
		if(deviceDetailList == null) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription("ERROR: Id <" + productId + "> not found");
			return Optional.of(resp);
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
	private Optional<?> generateIOTResponse(Optional<DeviceDetails> deviceDetails){
		
		if(deviceDetails.isEmpty()) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription(IOTConstant.ERROR_NO_DEVICE_IN_PAST);
			return Optional.of(resp);
		}
		
		DeviceDetails deviceInfo = deviceDetails.get();
		
		if(deviceInfo.getAirplaneMode().equals("OFF")
				&& (deviceInfo.getLatitude() == null || deviceInfo.getLongitude() == null)) {
			IOTResponse resp = new IOTResponse();
			resp.setDescription(IOTConstant.ERROR_DEVICE_NOT_LOCATED);
			return Optional.of(resp);
			//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
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
		return Optional.of(deviceInfoResponse);
		
//		String json =   """
//			{ 
//					   "id":"%s",
//					   "name":"%s",
//					   "datetime":"%s",
//					   "long":"%s",
//					   "lat":"%s",
//					   "status":"%s",
//					   "battery":"%s",
//					   "description":"%s"
//			}
//
//            """;
//		
//		json.formatted(
//				deviceInfo.getProductId(),
//				ProductList.getProductType(deviceInfo.getProductId()),
//				IOTUtil.convertToDate(deviceInfo.getDateTime()),
//				IOTUtil.getLongitude(deviceInfo),
//				IOTUtil.getLatitude(deviceInfo),
//				IOTUtil.getStatus(deviceInfo),
//				IOTUtil.getBatteryStatus(deviceInfo),
//				IOTUtil.getDescription(deviceInfo));
	}	

}
