package Server;


	public class ServerTest
		{
			public static void main( String args[] )
			{
			int replicaId = 1; // to run replicas simply change this number and run this few times

			UDPserver application1 = new UDPserver( replicaId ); // create server
			
			//application1.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			//application.waitForPackets(); // run server application
			} // end main
		} // end class ServerTest