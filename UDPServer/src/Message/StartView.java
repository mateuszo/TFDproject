package Message;


public class StartView extends Message {

     public int v; 				// view number
     public String l; 			// log 							ATENCION: must be fixed to correct type
     public int n; 				// op number
     public int k; 				// commit number
     


     // returns 1 on error, 0 on success
     public int fromString(String[] data) {


          // check if there are enough elements to decode...
          if (data.length != 5) {

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