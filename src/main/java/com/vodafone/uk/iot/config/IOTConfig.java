package com.vodafone.uk.iot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vodafone.uk.iot.service.impl.FileService;
import com.vodafone.uk.iot.service.IOTDataService;
import com.vodafone.uk.iot.service.IOTDeviceInfoService;
import com.vodafone.uk.iot.service.impl.DeviceInfoService;

@Configuration
public class IOTConfig {

	@Bean
	IOTDataService fileService()
	{
	    return new FileService();
	}
	
	@Bean
	IOTDeviceInfoService deviceInfoService()
	{
	    return new DeviceInfoService();
	}
	
}
