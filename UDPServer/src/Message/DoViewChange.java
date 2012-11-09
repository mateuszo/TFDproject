package Message;



public class DoViewChange extends Message {

     public int v; 				// view number
     public String l; 			// log 							ATENCION: must be fixed to correct type
     public int last_v; 		// last view with normal state
     public int n; 				// op number
     public int k; 				// commit number
     public int i; 				// replica id


     // returns 1 on error, 0 on success
     public int fromString(String[] data) {


          // check if there are enough elements to decode...
          if (data.length != 7) {

                return 1;
          }

          // ...if first is int
          try {
             v = Integer.parseInt(data[1]);

          } catch (NumberFormatException e) {

             return 1;
          }

          // ...if second is List????
          try {
             l = data[2];

          } catch (NumberFormatException e) {

             return 1;
          }

       // ...if third is int
          try {
             last_v = Integer.parseInt(data[3]);

           } catch (NumberFormatException e) {

             return 1;
            }
          
       // ...if fourth is int
          try {
             n = Integer.parseInt(data[4]);

           } catch (NumberFormatException e) {

             return 1;
            }

          
          // ...if fifth is int
          try {
             k = Integer.parseInt(data[5]);

           } catch (NumberFormatException e) {

             return 1;
            }

       // ...if sixth is int
          try {
             i = Integer.parseInt(data[6]);

           } catch (NumberFormatException e) {

             return 1;
            }
          
          return 0;
     }

     public String toString() {

          return "DOVIEWCHANGE;" + v + ";" + l + ";" + last_v + ";" + n + ";" + k + ";" + i;
     }
}