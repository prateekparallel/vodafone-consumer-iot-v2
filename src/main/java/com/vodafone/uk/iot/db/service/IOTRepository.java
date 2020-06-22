/**
 * 
 */
package com.vodafone.uk.iot.db.service;

import java.util.List;

import com.vodafone.uk.iot.beans.DeviceDetails;
import com.vodafone.uk.iot.exception.IOTException;

/**
 * @author Abhijit P Dutta
 *
 * This is repository interface to retrieve and save device list for this application
 */
public interface IOTRepository {
	
	public int saveOrUpdate(List<DeviceDetails> deviceDetailList);
	
	public List<DeviceDetails> getDeviceList(String productId) throws IOTException;	

}
