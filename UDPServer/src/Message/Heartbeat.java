package Message;

public class Heartbeat extends Message {
	
	public int v; // view number
	public int k; // commit number - op number of last committed request
	public long sendTime; // timestamp
	
	public int fromString(String[] data) {
        
        // check if there are enough elements to decode...
        if (data.length != 4) {
            
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
          k = Integer.parseInt(data[2]);
          
        } catch (NumberFormatException e) {
          
          return 1;
        }
        
        // ...if third is int
        try {
          sendTime = Long.parseLong(data[3]);
          
        } catch (NumberFormatException e) {
          
          return 1;
        }
        
        
        return 0;
    }
    
    public String toString() {        
        return "HEARTBEAT;" + v + ";" + k + ";" + sendTime ;
    }
}