package Message;

import java.util.ArrayList;
import java.util.List;


public class Util {

	 // returns object
	 public static Message fromString(String message) {
        
        String[] data = message.split(";");
        
        Message obj;
        
        if (data[0].equals("REQUEST")) {
            
            obj = new Request();
            
        } else if (data[0].equals("PREPARE")) {
            
            obj = new Prepare();
        
        } else if (data[0].equals("PREPAREOK")) {
    
            obj = new PrepareOk();
        } else if (data[0].equals("REPLY")) {
            
            obj = new Reply();
        } else if (data[0].equals("STARTVIEWCHANGE")) {
        	
        	obj = new StartViewChange();
        } else if (data[0].equals("STARTVIEW")) {
            
            obj = new StartView();
        } else if (data[0].equals("HEARTBEAT")){
        	
        	obj = new Heartbeat(); 
        } else if(data[0].equals("DOVIEWCHANGE")){
        	
        	obj = new DoViewChange();
        }else {
            
            obj = new Other();
        }
        
        if (obj.fromString(data) != 0) {
            
            obj = new Other();
        }
        
        return obj;
	 }
	    //Extracts the log from the startview msg to a new log
	 
	    public static List<Request> tempLog(String str_request) {
	   	 
	    	String[] arrayOfRequests = str_request.split(", ");
	   	 	Message request_msg;
	   	 	List<Request> temp_log = new ArrayList<Request>();
	   	 
	   	 	for (int idx=0;idx<arrayOfRequests.length;idx++){
	   	 		arrayOfRequests[idx]=arrayOfRequests[idx].replace(";[", "");	//takes out the [
	   	 		arrayOfRequests[idx]=arrayOfRequests[idx].replace("]", "");		//takes out the ]
	   	 		request_msg = fromString(arrayOfRequests[idx]);					//Transforms the string in message
	   	 		temp_log.add((Request) request_msg);							//adds to the temporary log
	   	 	}
	   	
	   	 	return temp_log;
			      	 
	    }
}