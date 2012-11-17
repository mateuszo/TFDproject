package Server;


// Client that sends and receives packets to/from a server.
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Message.Request;

public class Client extends JFrame
{
	/**
	 * 
	 */
	
	//TODO
	//Clients Timer
	private static final long serialVersionUID = 1L;
	private JTextField enterField; // for entering messages
	private JTextArea displayArea; // for displaying messages
	private DatagramSocket socket; // socket to connect to server
	private int c_id; //clients id
	private int seq_number; //number of the most recent request
	private int view_number; //the view number
	private int serverPort;
	private String serverAddress;
	private String config[][]={{"localhost","1020"},{"localhost","1021"},{"localhost","1022"}};	
	
	// set up GUI and DatagramSocket
	public Client( int id)
		{
		
		super( "Client "+id);
		
		//some configuration here
		c_id = id; //client's id
		seq_number = 1;  //message sequence number
		serverPort = 1021; //server port
		serverAddress = "localhost"; // server IP address
		view_number = 0; // initially the view number is 0
		
		enterField = new JTextField( "Type message here" );
		enterField.setEditable(false);
		enterField.addActionListener(
		new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					//sent request
					String message = event.getActionCommand();
					sendRequest(message);
					
				} // end actionPerformed
			} // end inner class
		); // end call to addActionListener
		
		add( enterField, BorderLayout.NORTH );
			
		displayArea = new JTextArea();
		add( new JScrollPane( displayArea ), BorderLayout.CENTER );
		setSize( 400, 300 ); // set window size
		setVisible( true ); // show window
			
		try // create DatagramSocket for sending and receiving packets
		{
			socket = new DatagramSocket();
		} // end try
		catch ( SocketException socketException )
		{
			socketException.printStackTrace();
			System.exit( 1 );
		} // end catch
		//msgSender.start();  //starts automatic message sender
		this.waitForPackets(); //starts listening
	} // end Client constructor		
		
	// wait for packets to arrive from Server, display packet contents
	public void waitForPackets()
	{
		while ( true )
		{
			try // receive packet and display contents
			{
				byte data[] = new byte[ 100 ]; // set up packet
				DatagramPacket receivePacket = new DatagramPacket(
				data, data.length );
				socket.receive( receivePacket ); // wait for packet
				
				
				// display packet contents
				displayMessage( "\n\tPacket received:" + 
						"\nFrom host: "+ receivePacket.getAddress() +
						"\nHost port: "+ receivePacket.getPort() +
						//"\nLength: "+ receivePacket.getLength() +
						"\nContaining:" + new String( receivePacket.getData(), 0, receivePacket.getLength() ) +"\n");
			} // end try
			catch ( IOException exception )
			{
				displayMessage( exception.toString() + "\n" );
				exception.printStackTrace();
			} // end catch
		}
	} // end wait for packets
	
	
	// manipulates displayArea in the event-dispatch thread
	private void displayMessage( final String messageToDisplay )
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run() // updates displayArea
				{
					displayArea.append( messageToDisplay );
				} // end method run
			} // end inner class
		); // end call to SwingUtilities.invokeLater
	} // end method displayMessage
	
	
	//method for sending requests - generates message, and updates the sequence number
	private void sendRequest( String msg ){ 

		Request message = new Request(); // construction of Request object
		message.c = c_id;
		message.s = seq_number;
		message.op = msg;
		
		String str_msg = message.toString(); //generate string message
		displayArea.append( "\nCLI"+c_id+" Sending packet containing: " + str_msg + "\n" );
				
		byte data[] = str_msg.getBytes(); // convert to bytes
		 
		try{									
			DatagramPacket sendPacket = new DatagramPacket( data, data.length, InetAddress.getByName(
					config[view_number%config.length][0]), Integer.parseInt(config[view_number%config.length][1]) );
			socket.send( sendPacket ); // send packet
			displayArea.append( "Packet sent\n" );
			displayArea.setCaretPosition(
			displayArea.getText().length() );
			seq_number++; //increment seq_number
		}
		catch ( IOException ioException )
		{
			displayMessage( ioException.toString() + "\n" );
			ioException.printStackTrace();
		} // end catch
		
	}
	
	
	//auto message sender
	Thread msgSender = new Thread(){
		public void run(){
			Integer arq =0;
			while(true){
				try{							
					sleep(5000);
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
				finally{
					sendRequest("Automatic Request:" + arq);
					arq++;
				}
			}
		}
	};
} // end class Client