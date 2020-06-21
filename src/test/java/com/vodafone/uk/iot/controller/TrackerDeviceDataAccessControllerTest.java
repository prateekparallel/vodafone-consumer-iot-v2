package com.vodafone.uk.iot.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.vodafone.uk.iot.response.DeviceInfoResponse;
import com.vodafone.uk.iot.service.IOTDeviceInfoService;


/***
 * 
 * @author Abhijit P Dutta
 *
 * This class is to just check our controller whether all controller apis/methods will 
 * call successfully or not. Hence we are not going to test all condition with mock data
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(TrackerDeviceDataAccessController.class)
public class TrackerDeviceDataAccessControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IOTDeviceInfoService deviceInfoService;
	
		
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
