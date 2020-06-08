package com.vodafone.uk.iot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class IOTHttpMockMvcTest {
	private MockMvc mockMvc;
	
	@Autowired
	protected WebApplicationContext webApplicationContext;
	
	
	public MockMvc getMockMvc() {
		if (mockMvc == null)
			return mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		return mockMvc;
	}	
}

