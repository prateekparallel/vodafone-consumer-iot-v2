package com.vodafone.uk.iot.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vodafone.uk.iot.beans.DeviceDetails;
import com.vodafone.uk.iot.db.IOTDataBase;
import com.vodafone.uk.iot.exception.IOTException;

/***
 * 
 * @author Abhijit P Dutta
 * A extra layer to do transaction with database. If we change the DB in near future  (example Oracle)
 * we just need to modify or replace this implementation. No more impact into the entire
 * application as transaction will be done through IOTRepository interface reference
 */

@Service
public class IOTRepositoryService implements IOTRepository{
	
	@Autowired
	IOTDataBase iotDB;

	@Override
	public int saveOrUpdate(List<DeviceDetails> deviceDetailList) {
		
		return iotDB.add(deviceDetailList);
	}

	@Override
	public List<DeviceDetails> getDeviceList(String productId) throws IOTException {
		
		return iotDB.get(productId);
	}

}
