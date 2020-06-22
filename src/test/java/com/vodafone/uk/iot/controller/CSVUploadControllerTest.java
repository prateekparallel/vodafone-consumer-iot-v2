package com.vodafone.uk.iot.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.uk.iot.beans.CSVDetail;
import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.service.IOTFileService;


import static com.vodafone.uk.iot.util.IOTTESTUtil.creatAndGetCSVFile;

/***
 * 
 * @author Abhijit P Dutta
 *
 * This class is to just check our controller whether all controller apis/methods will 
 * call successfully or not. Hence we are not going to test all condition with mock data
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(CSVUploadController.class)
public class CSVUploadControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IOTFileService fileService;
	
	
	@Test
	public void shoulReturnOKStatus_WhenSuccessfullyLoadingCSVFile() throws Exception {
		
		final String csvFile = creatAndGetCSVFile();
		
		IOTResponse resp = new IOTResponse();
		resp.setDescription("data refreshed");
		
		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(csvFile);
		
		when(fileService.loadCSVFile(any(),any())).thenReturn(Optional.of(resp));
						
		this.mockMvc
				.perform(
						post("/v2/event").contentType(APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(CSVDetail)))
						.andDo(print())
						.andExpect(jsonPath("description").value("data refreshed"))
						.andExpect(status().isOk());
	}
	
	@Test
	public void shoulReturnNotFoundStatus_WhenCSVFile_Not_Found() throws Exception {
		
		final String csvFile = creatAndGetCSVFile();
		
		IOTResponse resp = new IOTResponse();
		resp.setDescription("ERROR: no data file found");
		
		CSVDetail CSVDetail = new CSVDetail();
		CSVDetail.setFilepath(csvFile);
		
		when(fileService.loadCSVFile(any(), any())).thenReturn(Optional.of(resp));
				
		this.mockMvc
				.perform(
						post("/v2/event").contentType(APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(CSVDetail)))
						.andDo(print())
						.andExpect(jsonPath("description").value("ERROR: no data file found"))
						.andExpect(status().isNotFound());
	}	
		

}

