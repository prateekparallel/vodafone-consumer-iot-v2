package com.vodafone.uk.iot.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class IOTTESTUtil {
	
	//The unit test case should not fail in various environment or machine
    //due to not available of data.csv file hence generated file dynamically
    public static String creatAndGetCSVFile() throws IOException {
    	
    	final String csvFile = System.getProperty("user.dir") + "/" + "data.csv";
    	
//    	File newCsvFile = new File(csvFile);
//    	
//    	if(newCsvFile.length() > 0) {
//    		return csvFile;
//    	}
    			
		FileWriter fileWriter = new FileWriter(csvFile);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.println("DateTime,EventId,ProductId,Latitude,Longitude,Battery,Light,AirplaneMode");
	    printWriter.println("1582605077000,10001,WG11155638,51.5185,-0.1736,0.99,OFF,OFF");
	    printWriter.println("1582605137000,10002,WG11155638,51.5185,-0.1736,0.99,OFF,OFF");
	    printWriter.println("1582605197000,10003,WG11155638,51.5185,-0.1736,0.98,OFF,OFF");
	    printWriter.println("1582605257000,10004,WG11155638,51.5185,-0.1736,0.98,OFF,OFF");
	    printWriter.println("1582605257000,10005,6900001001,40.73061,-73.935242,0.11,N/A,OFF");
	    printWriter.println("1582605258000,10006,6900001001,40.73071,-73.935242,0.1,N/A,OFF");
	    printWriter.println("1582605259000,10007,6900001001,40.73081,-73.935242,0.1,N/A,OFF");
	    printWriter.println("1582605317000,10008,WG11155800,45.5185,-12.52029,0.11,ON,OFF");
	    printWriter.println("1582605377000,10009,WG11155800,45.5186,-12.52027,0.1,ON,OFF");
	    printWriter.println("1582605437000,10010,WG11155800,45.5187,-12.52025,0.09,ON,OFF");
	    printWriter.println("1582605497000,10011,WG11155638,51.5185,-0.17538,0.95,OFF,OFF");
	    printWriter.println("1582605557000,10012,6900001001,40.73081,-73.935242,0.1,N/A,OFF");
	    printWriter.println("1582605615000,10013,6900233111,,,0.1,N/A,ON");
	    printWriter.println("1582612875000,10014,6900233111,,,0.1,N/A,OFF");
	    printWriter.print("1582605253000,10015,6900001001,41.73061,-74.935242,0.11,N/A,OFF");
	    printWriter.close();
	    
	    return csvFile;
    }
    
public static String creatAndGetBigCSVFile() throws IOException {
    	
    	final String csvFile = System.getProperty("user.dir") + "/" + "data.csv";
    	
//    	File newCsvFile = new File(csvFile);
//    	
//    	if(newCsvFile.length() > 0) {
//    		return csvFile;
//    	}
    			
		FileWriter fileWriter = new FileWriter(csvFile);
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.println("DateTime,EventId,ProductId,Latitude,Longitude,Battery,Light,AirplaneMode");
	    Long tstmp = 1582605077000L;
	    
	    for(int i =0; i<1000000; i++) {
	    	printWriter.println(tstmp.toString() + ",10001,WG11155638,51.5185,-0.1736,0.99,OFF,OFF");
	    	tstmp++;
	    }
	    
	    printWriter.close();
	    
	    return csvFile;
    }
    

}
