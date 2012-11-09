package Message;

import java.util.Arrays;
import java.util.List;


public class StartView extends Message {

     public int v; 				// view number
     public List<Request> l; 			// log 							ATENCION: must be fixed to correct type
     public int n; 				// op number
     public int k; 				// commit number
     


     // returns 1 on error, 0 on success
     public int fromString(String[] data) {

    	 String[] data_copy= new String[data.length-3];

          // check if there are enough elements to decode...
          //if (data.length != 5) {

            //    return 1;
          //}

          // ...if first is int
          try {
             v = Integer.parseInt(data[1]);

          } catch (NumberFormatException e) {

             return 1;
          }

          // ...if second is List????
          
          //r = new Request();
		  
		  //String[] forRequest = {data[4], data[5], data[6], data[7]};

            //r.fromString(forRequest);
          
          //try {
             for (int idx=2;idx<data.length;idx++){
            	 data_copy[idx-2]=data[idx];
             }
             
             //l = Arrays.asList((List<Request>) data_copy);

          //} catch (NumberFormatException e) {

            // return 1;
          //}

       // ...if third is int
          try {
             n = Integer.parseInt(data[4]);

           } catch (NumberFormatException e) {

             return 1;
            }

          
          // ...if fourth is int
          try {
             k = Integer.parseInt(data[5]);

           } catch (NumberFormatException e) {

             return 1;
            }

          
          return 0;
     }

     public String toString() {

          return "STARTVIEW;" + v + ";" + l + ";" + n + ";" + k;
     }
}