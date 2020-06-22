package com.vodafone.uk.iot.constant;

/***
 * 
 * @author Abhijit P Dutta
 * In real life scenario I will load all the below information
 * from a centralised config server/file or from a DB. So that if we make any changes in 
 * statement in future, it will not impact the application and no need for compile the codes too.
 */

public class IOTConstant {
	
	final public static String ERROR_EMPTY_FILE = "ERROR: CSV file is empty or corrupt";
	
	final public static String ERROR_FILE_NOT_FOUND = "ERROR: no data file found";
	
	final public static String ERROR_TECHNICAL_EXCEP = "ERROR: A technical exception occurred";	
	
	final public static String ERROR_DB_FAILURE = "ERROR: A technical exception occurred - Failed to load DB";
	
	final public static String ERROR_NO_DEVICE_IN_PAST = "ERROR: No Device information available in near past";

 	final public static String ERROR_DEVICE_NOT_LOCATED = "ERROR: Device could not be located";
	
	final public static String DATA_REFRESHED = "data refreshed";
	
	final public static String ERROR_DB_EMPTY = "ERROR: Database not yet loaded";

}
