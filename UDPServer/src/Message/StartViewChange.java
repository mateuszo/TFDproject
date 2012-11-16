package Message;


public class StartViewChange extends Message {

     public int v; 				// view number
     public int i; 				// replica id


     // returns 1 on error, 0 on success
     public int fromString(String[] data) {


          // check if there are enough elements to decode...
          if (data.length != 3) {

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
             i = Integer.parseInt(data[2]);

           } catch (NumberFormatException e) {

             return 1;
            }
          
          return 0;
     }

     public String toString() {

          return "STARTVIEWCHANGE;" + v + ";" +  i;
     }
}