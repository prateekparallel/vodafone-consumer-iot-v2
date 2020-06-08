package com.vodafone.uk.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.vodafone.uk.iot" })
public class VodafoneConsumerIotV2Application {

	public static void main(String[] args) {
		SpringApplication.run(VodafoneConsumerIotV2Application.class, args);
	}

}
