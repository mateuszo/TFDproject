package Message;

public class Heartbeat extends Message {
	public long sendTime;
public int fromString(String[] data) {
        
        // check if there are enough elements to decode...
        if (data.length != 2) {
            
            return 1;
        }
        
        // ...if first is int
        try {
          sendTime = Long.parseLong(data[1]);
          
        } catch (NumberFormatException e) {
          
          return 1;
        }
        
        
        return 0;
    }
    
    public String toString() {        
        return "HEARTBEAT;" + sendTime ;
    }
}