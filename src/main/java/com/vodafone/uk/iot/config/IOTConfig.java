package com.vodafone.uk.iot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vodafone.uk.iot.service.DeviceInfoService;
import com.vodafone.uk.iot.db.service.IOTRepository;
import com.vodafone.uk.iot.db.service.IOTRepositoryService;
import com.vodafone.uk.iot.service.CSVFileService;
import com.vodafone.uk.iot.service.IOTFileService;
import com.vodafone.uk.iot.service.IOTDeviceInfoService;

@Configuration
public class IOTConfig {

	@Bean
	IOTFileService fileService()
	{
	    return new CSVFileService();
	}
	
	@Bean
	IOTDeviceInfoService deviceInfoService()
	{
	    return new DeviceInfoService();
	}
	
	@Bean
	IOTRepository iotRepository() {
		return new IOTRepositoryService();
	}
	
}
