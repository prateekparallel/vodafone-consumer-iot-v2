package com.vodafone.uk.iot.db;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.vodafone.uk.iot.beans.DeviceDetails;

@Component
public class DeviceDetailDB {

	// I am storing device information based on its productId
	// it will help us to retrieve the record very quickly
	private Map<String, List<DeviceDetails>> deviceDetails = null;

	public int saveOrUpdateDB(List<DeviceDetails> deviceDetailList) {

		deviceDetails = deviceDetailList.parallelStream().collect(Collectors.groupingBy(DeviceDetails::getProductId));
		
		if(deviceDetailList.isEmpty()) {
			return -1;
		}
		
		return 1;
	}
	
	
	public List<DeviceDetails> getDeviceList(String productId) {
		return deviceDetails.get(productId);
	}

}
