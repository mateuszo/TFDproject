package Message;

import java.util.List;


public class StartView extends Message {

     public int v; 				// view number
     public List<Request> l; 			// log 							ATENCION: must be fixed to correct type
     public int n; 				// op number
     public int k; 				// commit number
     


     // returns 1 on error, 0 on success
     public int fromString(String[] data) {

    	  // check if there are enough elements to decode...
          if (data.length < 5) {

                return 1;
          }

          // ...if first is int
          try {
             v = Integer.parseInt(data[1]);

          } catch (NumberFormatException e) {

             return 1;
          }

          // ...if second is List
          
          String str_req= new String();
          
          try {
        	  for (int idx=2;idx<data.length-2;idx++){
        		  str_req=str_req + ";" + data[idx];
        	  }
             
        	  //l = Util.tempLog(str_req);
        	  l = Util.tempLog(str_req);
          }
          catch (Exception e) {
        	  return 1;
          }
          
       // ...if third is int
          try {
             n = Integer.parseInt(data[data.length-2]);

           } catch (NumberFormatException e) {

             return 1;
            }

          
          // ...if fourth is int
          try {
             k = Integer.parseInt(data[data.length-1]);

           } catch (NumberFormatException e) {

             return 1;
            }

          
          return 0;
     }

     public String toString() {

          return "STARTVIEW;" + v + ";" + l + ";" + n + ";" + k;
     }
     
//     //Extracts the log from the startview msg to a new log
//     public List<Request> tempLog(String str_request) {
//    	 
//    	 String[] arrayOfRequests = str_request.split(", ");
//    	 Message request_msg;
//    	 List<Request> temp_log = new ArrayList();
//    	 
//    	 for (int idx=0;idx<arrayOfRequests.length;idx++){
//    		 arrayOfRequests[idx]=arrayOfRequests[idx].replace(";[", "");	//takes out the [
//    		 arrayOfRequests[idx]=arrayOfRequests[idx].replace("]", "");	//takes out the ]
//    		 request_msg = Util.fromString(arrayOfRequests[idx]);			//tranforms the string in message
//    		 temp_log.add((Request) request_msg);							//adds to the temporary log
//    	 }
//    	
//    	return temp_log;
//		      	 
//     }
}