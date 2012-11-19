
package Server;

import java.io.IOException;


	public class ServerTestAll
		{
		private static Process replica0;
		private static Process replica1;
		private static Process replica2;
		private static String[] command = new String[4];
		
			public static void main( String args[] ){
				command[0] = "java";                         
				command[1] = "-jar";                         
				command[2] = "RunTest.jar";                         
				command[3] = "0";                         
                    
			try {
				replica0 = new ProcessBuilder(command).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			command[3] = "1";
			try {
				replica1 = new ProcessBuilder(command).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			command[3] = "2";
			try {
				replica2 = new ProcessBuilder(command).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} // end main

	} // end class ServerTest