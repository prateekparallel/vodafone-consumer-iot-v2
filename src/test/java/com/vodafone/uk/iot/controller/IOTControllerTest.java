package com.vodafone.uk.iot.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.uk.iot.beans.CSVLocation;
import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.service.IOTDataService;
import com.vodafone.uk.iot.service.IOTDeviceInfoService;

import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;


@ExtendWith(SpringExtension.class)
@WebMvcTest(IOTController.class)
public class IOTControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IOTDataService fileService;
	
	@MockBean
	private IOTDeviceInfoService deviceInfoService;
	
	@Test
	public void shoulReturnOKStatus_WhenSuccessfullyLoadingCSVFile() throws Exception {
		
		final String csvFile = creatAndGetCSVFile();
		
		IOTResponse resp = new IOTResponse();
		resp.setDescription("data refreshed");
		
		CSVLocation csvLocation = new CSVLocation();
		csvLocation.setFilepath(csvFile);
		
		when(fileService.loadCSVFile(any())).thenReturn(Optional.of(resp));
						
		this.mockMvc
				.perform(
						post("/v2/event").contentType(APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(csvLocation)))
						.andDo(print())
						.andExpect(jsonPath("description").value("data refreshed"))
						.andExpect(status().isOk());
	}
	
	@Test
	public void shoulReturnNotFoundStatus_WhenCSVFile_Not_Found() throws Exception {
		
		final String csvFile = creatAndGetCSVFile();
		
		IOTResponse resp = new IOTResponse();
		resp.setDescription("ERROR: no data file found");
		
		CSVLocation csvLocation = new CSVLocation();
		csvLocation.setFilepath(csvFile);
		
		when(fileService.loadCSVFile(any())).thenReturn(Optional.of(resp));
				
		this.mockMvc
				.perform(
						post("/v2/event").contentType(APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(csvLocation)))
						.andDo(print())
						.andExpect(jsonPath("description").value("ERROR: no data file found"))
						.andExpect(status().isNotFound());
	}	
	
	
	@Test
	public void shouldReturnDeviceInformationWithOKStatus() throws Exception {
		
		DeviceInfoResponse dr = new DeviceInfoResponse();
		dr.setId("WG11155638");
		dr.setName("CyclePlusTracker");
		dr.setBattery("Full");
		dr.setDatetime("25/02/2020 04:31:17");
		dr.setDescription("SUCCESS: Location identified.");
		dr.setLatitude("51.5185");
		dr.setLongitude("-0.1736");
		dr.setStatus("Active");
		
		Mockito.doReturn(Optional.of(dr)).when(deviceInfoService).getDeviceInfo(any(), any());
		
		this.mockMvc.perform(get("/v2/event").param("ProductId","WG11155638")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("id").value("WG11155638"))
				.andExpect(jsonPath("name").value("CyclePlusTracker"))
				.andExpect(jsonPath("datetime").value("25/02/2020 04:31:17"))
				.andExpect(jsonPath("longitude").value("-0.1736"))
				.andExpect(jsonPath("latitude").value("51.5185"))
				.andExpect(jsonPath("status").value("Active"))
				.andExpect(jsonPath("battery").value("Full"))
				.andExpect(jsonPath("description").value("SUCCESS: Location identified."));
	}

}
