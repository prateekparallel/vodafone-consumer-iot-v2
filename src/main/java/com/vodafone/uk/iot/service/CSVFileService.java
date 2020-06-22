package com.vodafone.uk.iot.service;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.vodafone.uk.iot.beans.DeviceDetails;
import com.vodafone.uk.iot.constant.IOTConstant;
import com.vodafone.uk.iot.db.service.IOTRepository;
import com.vodafone.uk.iot.response.IOTResponse;
import com.vodafone.uk.iot.util.IOTUtil;

/***
 * 
 * @author Abhijit Pritam Dutta
 * This is a service class and this class provide the service to load a csv file 
 * and store each line of the file into a java POJO and generate a list of POJO
 * and store in the memory
 */

@Service
public class CSVFileService implements IOTFileService{

	@Autowired
	IOTRepository iotRepository;
	
	
	/***
	 * This method will load a csv file into the memory 
	 * if unsuccessful it will return appropriate error message
	 * if a file is empty or not found it will not clear the old records
	 * they will be still available. Also I am not cleaning up the container
	 * for a second successful load (cleaning up old records) for performance reason.
	 * Let gc take the full responsibility for cleaning up of garbages in background.
	 * @param filePath
	 * @return IOTResponse
	 */
	//not two load operation can be allowed at the same time
	@Override
	public synchronized Optional<IOTResponse> loadCSVFile(String filePath, Character delimiter){
		
		List<DeviceDetails> deviceDetailList = null;		
			
		if(IOTUtil.isValidDelimiter(delimiter) == false) {
			delimiter = ','; //very common delimiter of a csv file
		}
		
		IOTResponse resp = new IOTResponse();
		
		//file will be close automatically after use or in a exception
		try(Reader reader = new FileReader(filePath)){

			deviceDetailList = readAll(reader, delimiter);
			
			if(deviceDetailList == null || deviceDetailList.isEmpty()) {
				resp.setDescription(IOTConstant.ERROR_EMPTY_FILE);
				return Optional.of(resp);
			}	
		
		}catch (FileNotFoundException e) {
			resp.setDescription(IOTConstant.ERROR_FILE_NOT_FOUND);
			System.out.println("File - " + filePath + " not found - " + e.getMessage());
			return  Optional.of(resp);
		}catch(Exception e) {
			deviceDetailList = null;
			resp.setDescription(IOTConstant.ERROR_TECHNICAL_EXCEP + " - " + e.getMessage());
			System.out.println("Exception occured while processing file - " + filePath 
					+ " reason - " + e.getMessage());
			return Optional.of(resp);
		}
				
		if(iotRepository.saveOrUpdate(deviceDetailList) == 1) {
			resp.setDescription(IOTConstant.DATA_REFRESHED);
		}
		else {
			deviceDetailList = null;
			resp.setDescription("ERROR: A technical exception occurred - Failed to load DB"
					+ " - Check csv File and Delimiter");
			System.out.println("Exception occured while Loading records in DB - ");
			return Optional.of(resp);
		}
		
		
		return Optional.of(resp);
		
	}		
	
	
	/**
	 * This method will load the csv into the java pojo DeviceDetails.class
	 * @param reader
	 * @return DeviceDetails List
	 */ 
	private List<DeviceDetails> readAll(Reader reader, char separator){
		
		HeaderColumnNameMappingStrategy<DeviceDetails> cpms = new HeaderColumnNameMappingStrategy<DeviceDetails>();
		cpms.setType(DeviceDetails.class);
		CsvToBean<DeviceDetails> csvToBean = new CsvToBeanBuilder<DeviceDetails>(reader)
				   .withSeparator(separator)
			       .withType(DeviceDetails.class)
			       .withMappingStrategy(cpms)
			       .build();
		return csvToBean.parse();
	}		
}
