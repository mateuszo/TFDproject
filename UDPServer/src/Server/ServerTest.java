package Server;

import java.io.IOException;


	public class ServerTest{
		
//		private static Process replica0;         
//		private static Process replica1;         
//		private static Process replica2;         
//		private static Process replica3;         
//		private static String[] command = new String[5];
		
		public static void main( String args[] ){
			int replicaId = 2; // to run replicas simply change this number and run this few times
//			System.out.println("Starting the servers");                         
//			command[0] = "java";                         
//			command[1] = "-cp";                         
//			command[2] = "bin/SMaRt.jar:lib/slf4j-api-1.5.8.jar:lib/slf4j-jdk14-1.5.8.jar:lib/netty-3.1.1.GA.jar:lib/commons-codec-1.5.jar";                         
//			command[3] = "UDPserver.id";                         
//			command[4] = "0";                         
//			
//			try {
//				replica0 = new ProcessBuilder(command).redirectErrorStream(true).start();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}			 
			
			
			UDPserver application1 = new UDPserver( replicaId ); // create server
			
			//application1.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			
			//application.waitForPackets(); // run server application
			} // end main
		} // end class ServerTest