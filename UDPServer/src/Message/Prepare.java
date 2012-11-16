package Message;

public class Prepare extends Message {

	 public int v; 		// view number
	 public int n; 		// op number
	 public int k; 		// commit number
	 public Request r; 		// request from client


	 // returns 1 on error, 0 on success
	 public int fromString(String[] data) {

		  // check if there are enough elements to decode...
		  if (data.length != 8) {

				return 1;
		  }

		  // ...if first is int
		  try {
			 v = Integer.parseInt(data[1]);

		  } catch (NumberFormatException e) {

			 return 1;
		  }

		  // ...if second is int
		  try {
			 n = Integer.parseInt(data[2]);

		  } catch (NumberFormatException e) {

			 return 1;
		  }

          // ...if third is int
          try {
             k = Integer.parseInt(data[3]);
            
           } catch (NumberFormatException e) {
            
             return 1;
            }
        
		  r = new Request();
		  
		  String[] forRequest = {data[4], data[5], data[6], data[7]};

            r.fromString(forRequest);

		  return 0;
	 }

	 public String toString() {


		  return "PREPARE" + ";" + v + ";" + n + ";" + k + ";" + r.toString();
	 }
}