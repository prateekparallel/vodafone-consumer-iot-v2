package com.vodafone.uk.iot.service;

import java.util.Optional;


public interface IOTDeviceInfoService {
	
	public Optional<?> getDeviceInfo(String productId, Long tstmp);

}
