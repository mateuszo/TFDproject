package Server;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Message.Heartbeat;
import Message.Message;
import Message.Prepare;
import Message.PrepareOk;
import Message.Reply;
import Message.Request;
import Message.Util;
import Server.VRstate.ClientTab;


//group number: tfd013

public class UDPserver extends JFrame {
	 
	private static final long serialVersionUID = 1L;
	private JTextArea displayArea; // displays packets received
	private JTextField enterField; // for entering messages
	private DatagramSocket socket; // socket to connect to client
	//private String config[][]={{"localhost","1024"},{"localhost","1025"},{"localhost","1026"}};
	private int[] ports = {1020,1021,1022};
	private VRstate state;
	
	//Variables for the timers
	private long timeout; // set the timeout value
	private long lastSend; //time when last send occured
	private long lastReceive; // time when last receive occured
	private long transmissionDelay; //Transmission delay time
	
	 	 //constructor
	 public UDPserver(int id) {
		 
		 super( "Replica"+id );
		 
		 int rep_id=id; //id of the replica  id=0 indicates the primary
		 state = new VRstate(id);
		 state.client_table = new HashMap<Integer,ClientTab>();
		 state.log = new ArrayList();
		 state.prepareOk_counter = new HashMap<Integer,Integer>();	//30-10-2012 - RO
		 
		 //timer values initialization
		 timeout = 5000; // choose timeout value: T [milliseconds]
		 transmissionDelay = 100; //choose delta [milliseconds]
		 lastSend = System.currentTimeMillis(); //initializing
		 lastSend = System.currentTimeMillis();
		  
		 
		 enterField = new JTextField( "Type command here" );
		 enterField.addActionListener(
			new ActionListener()
				{
					public void actionPerformed( ActionEvent event )
					{
						//command
						String command = event.getActionCommand();
						executeCommand(command);
					} // end actionPerformed
				} // end inner class
			); // end call to addActionListener
			
		 add( enterField, BorderLayout.NORTH );
		 
		 displayArea = new JTextArea(); // create displayArea
		 add( new JScrollPane( displayArea ), BorderLayout.CENTER );
		 setSize( 400, 300 ); // set size of window
		 setVisible( true ); // show window
		
		 try {	// create DatagramSocket for sending and receiving packets
			 socket = new DatagramSocket( ports[rep_id] );
		 }	// end try
		 catch ( SocketException socketException ) {
			 socketException.printStackTrace();
			 System.exit( 1 );
		 } // end catch
		 
		 
		 if(id==0){ //starts heartbeat timer for primary only
			 heartbeatTimer.start(); // starts heartbeat timer
		 }
		 else{
			 watchTimer.start(); //start watch dog/failure detector on replicas
		 }
		 
		 this.waitForPackets(); //starts listening
	 }

	 // end UDPserver constructor
		
	 //***************************
	 //*	WAIT FOR PACKETS     *
	 //***************************
	 
	 // wait for packets to arrive, display data and echo packet to client
	 public void waitForPackets() {
		 while ( true ) {
			 try {	// receive packet, display contents, return copy to client
				 byte data[] = new byte[ 100 ]; // set up packet
					
				 displayMessage("\n--------------------------------------------------\nwaiting for the packet\n");
				 
				 DatagramPacket receivePacket = new DatagramPacket( data, data.length );
				 
				 socket.receive( receivePacket ); // wait to receive packet
					
				 // display information from received packet
				 String received_msg = new String( receivePacket.getData(), 0, receivePacket.getLength() );
					
				 Message test = Util.fromString(received_msg); // creating object from message
				
				 lastReceive = System.currentTimeMillis(); //update lats receive timestamp
				 
				 displayMessage( "\n\tPacket received:" + 
						 "\nFrom host: "+ receivePacket.getAddress() +
						 "\nHost port: "+ receivePacket.getPort() +
						// "\nLength: "+ receivePacket.getLength() +
						 "\nContaining: " + received_msg +
						 "\nType: "+ test.getClass().getName() );
				 
		//*******************************		 
		//		ACTION RECOGNITION
		//*******************************
				 //checks the type of the received messages and runs appropriate Action
				 if(test.getClass().getName().equals("Message.Request")){
					 
					 displayMessage("\n\tRequest received!");
					 processRequest((Request) test, receivePacket.getAddress(), receivePacket.getPort());
						
					//sendPrepareToReplicas( receivePacket );
				}
				else if (test.getClass().getName().equals("Message.Prepare")){
						
					displayMessage("\n\tPrepare received!");
					processPrepare((Prepare) test);
						
				}
				else if (test.getClass().getName().equals("Message.PrepareOk")){
						
					displayMessage("\n\tPrepareOK received!");
					processPrepareOk((PrepareOk) test, receivePacket);		//30-10-2012 - RO
						
				}
				else if (test.getClass().getName().equals("Message.Heartbeat")){ // problem here - doesn't detect the type
					Heartbeat heart = (Heartbeat)test;
					displayMessage("\nHeartbeat received: " + heart.sendTime);
				}
				else{
					displayMessage("\n\tInvaild message received!");  // if the message type is invalid
				}
			} // end try
			catch ( IOException ioException ){
				displayMessage( ioException.toString() + "\n" );
				ioException.printStackTrace();
			} // end catch
		} // end while
	}// end method waitForPackets
		
		 
	//********************************************************************* 
	//								SENDERS
	//********************************************************************* 

	// echo packet to client
	private void sendPacketToClient( DatagramPacket receivePacket ) throws IOException {
			
		displayMessage( "\n\nEcho data to client..." );
			
		//create packet to send
		DatagramPacket sendPacket = new DatagramPacket(
				receivePacket.getData(), receivePacket.getLength(),
				receivePacket.getAddress(), receivePacket.getPort() );
		
		socket.send( sendPacket );		// send packet to client
		displayMessage( "Packet sent\n" );
	
	} // end method sendPacketToClient
		
	//Send PREPARE to replicas
	private void sendPrepareToReplicas (Request received_req) throws IOException {
			
		displayMessage("\n\nSending Prepare to all replicas.");
		//for loop should be here
		//create prepare message
		Prepare prepare_msg = new Prepare();
		prepare_msg.r = received_req; //take message from the client 
		prepare_msg.v = state.view_number; //send view number
		prepare_msg.n = state.op_number; //and current op_number
		prepare_msg.k = state.commit_number;
			
		//generate message
		String message = prepare_msg.toString();
		displayMessage( "\nPrepare:"+ message );
		byte data[] = message.getBytes();
			
		DatagramPacket sendPacket1 = new DatagramPacket( 
				data, data.length,
				InetAddress.getByName("localhost"), ports[1] ); //should be taken from config
			
		socket.send( sendPacket1 ); // send packet to the first replica
		
		DatagramPacket sendPacket2 = new DatagramPacket(  
				data, data.length,
				InetAddress.getByName("localhost"), ports[2] ); //should be taken from config
			
		socket.send( sendPacket2 ); // send packet to the second replica
			
		lastSend = System.currentTimeMillis(); // updates last Send time
		
		displayMessage( "\nPrepare sent\n" );
	
	} //end prepare sender
		
	//Send PREPAREOK to primary
	private void sendPrepareOkToPrimary (Prepare prep_msg) throws IOException {
		
		displayMessage("\nSending PrepareOK  to primary.");
					
		//generate message
		PrepareOk prepareOk_msg = new PrepareOk();
		prepareOk_msg.i = state.rep_number; 	//id of replica
		prepareOk_msg.n = prep_msg.n;			//op number
		prepareOk_msg.v = state.view_number;	//view number
			
		String message = prepareOk_msg.toString();
		byte data[] = message.getBytes();
			
		DatagramPacket sendPacket1 = new DatagramPacket( data, data.length,
					InetAddress.getByName("localhost"), ports[0] ); //should be taken from config
			
		socket.send( sendPacket1 ); // send packet to the primary

		displayMessage( "\nPrepareOK sent" );
	
	} //end prepareOk sender
		
	//Send REPLY to client
	//private void sendReplyToClient (PrepareOk prepareOk_msg, int req_cli) throws IOException {
	private void sendReplyToClient (PrepareOk prepareOk_msg, Request request_msg) throws IOException {
		//reply sender - 30-10-2012 - RO
		String cli_add;
		int cli_port;
			
		//generate message
		Reply reply_msg = new Reply();
			 
		reply_msg.v = prepareOk_msg.v; 	//view number
		//reply_msg.s = state.op_number; 	//current op_number
		reply_msg.s = request_msg.s; 		//original seq from request
		//reply_msg.x = state.log.listIterator(state.log.lastIndexOf());
		//reply_msg.x ="final reply";		//????????
		reply_msg.x = request_msg.op + "- reply";		//????????
			
		String message = reply_msg.toString();
		displayMessage( "\nSending reply to client!" );
		byte data[] = message.getBytes();
			
		//cli_add=state.client_table.get(req_cli).c_add;
		//cli_port=state.client_table.get(req_cli).c_port;
		cli_add=state.client_table.get(request_msg.c).c_add;
		cli_port=state.client_table.get(request_msg.c).c_port;
		//displayMessage( "\nIP: " + cli_add + "Port: " + cli_port );
		DatagramPacket sendPacket1 = null;
		
		sendPacket1 = new DatagramPacket( data, data.length, InetAddress.getByName(cli_add), cli_port );
			
		socket.send( sendPacket1 );
		displayMessage( "\nReply sent" );
			
	}// end reply sender
	
	
	//heartbeat sender
	private void sendHeartbeat() throws IOException{
		Heartbeat heart = new Heartbeat();
		heart.sendTime = System.currentTimeMillis();
		String message = heart.toString();
		
		byte data[] = message.getBytes();
		displayMessage( "\nSending Heartbeat: " + message);
		
		DatagramPacket sendPacket1 = new DatagramPacket( 
				data, data.length,
				InetAddress.getByName("localhost"), ports[1] ); //should be taken from config
			
		socket.send( sendPacket1 ); // send packet to the first replica
		
		DatagramPacket sendPacket2 = new DatagramPacket(  
				data, data.length,
				InetAddress.getByName("localhost"), ports[2] ); //should be taken from config
			
		socket.send( sendPacket2 ); // send packet to the second replica
		
		displayMessage( "\nHeartbeat sent!" );
		
		
	}
		
	//********************************************************************* 
	//							END OF SENDERS
	//*********************************************************************	
		
		
	//********************************************************************* 
	//							MESSAGE PROCESSORS
	//*********************************************************************	
		
	//request processor - processes request message and sends Prepare messages
	//private void processRequest(Request req_msg){		//30-10-2012 - RO
	private void processRequest(Request req_msg, InetAddress cli_add, int cli_port){	
		
		Request request = req_msg;
		ClientTab cli_tab = state.new ClientTab();			//initialize client table
			
		if(state.client_table.containsKey(request.c)){ 		//checks if client table exists for this client
			cli_tab =  state.client_table.get(request.c);	//loads client table
		}
		else{ 		//if not put new client tab into state
			cli_tab.c_id = request.c;
			cli_tab.c_add=cli_add.getHostName();   
			cli_tab.c_port=cli_port;
			state.client_table.put(request.c, cli_tab);
		}
			
		if(request.s>cli_tab.recent){ //if the request has newer sequence number than the most recent 
				
			state.log.add(request); //add request to the log
			cli_tab.recent = request.s; //updates the sequence number in the client table
			state.client_table.put(request.c, cli_tab); // update the VR state
			try{
				sendPrepareToReplicas(request); //sendPrepareToReplicas caused by the request
				//TODO
				//Replicas should somehow obtain the clients addres 
				//it could be done by adding new fields to Request or Prepare message
			}
			catch ( IOException ioException ){
				displayMessage( ioException.toString() + "\n" );
				ioException.printStackTrace();
			}
			
			state.op_number++; //increment the op_number
			
		}
		else{
			//drop request, resent reply
		}
	} //end request processor
		
	//Prepare processor
	private void processPrepare(Prepare prep_msg){
		
		Request request = prep_msg.r;  // gets request from the prepare message
		ClientTab cli_tab = state.new ClientTab();			//initialize client table
		
		if(prep_msg.n==state.op_number){ //Checks if the op-number is correct
			
			
			state.op_number++; //increment op_number
						
						
			//update client table
			if(state.client_table.containsKey(request.c)){ 		//checks if client table exists for this client
				cli_tab =  state.client_table.get(request.c);	//loads client table
			}
			else{ 		//if not put new client tab into state
				cli_tab.c_id = request.c;
				//cli_tab.c_add=cli_add.getHostName(); // does the replicas need the clients addresses and port?
				//cli_tab.c_port=cli_port;
				state.client_table.put(request.c, cli_tab);
			}
			
			state.log.add(request); //add request to the log
			cli_tab.recent = request.s; //updates the sequence number in the client table
			state.client_table.put(request.c, cli_tab); // update the VR state
			
			try{
				sendPrepareOkToPrimary(prep_msg); //sendPrepareToReplicas caused by the request
			}
			catch ( IOException ioException ){
				displayMessage( ioException.toString() + "\n" );
				ioException.printStackTrace();
			}
		}
		else{
				//wait for the previous Prepare messages
		}
	} // end prepare processor
		
		
	//PrepareOk processor
		
	private void processPrepareOk(PrepareOk prepOk_msg, DatagramPacket receivePacket){
			
		Integer nr_ok=0;
		PrepareOk prepareOk = prepOk_msg;
		int req_cli;
			
		if(state.prepareOk_counter.containsKey(prepareOk.n)){ 	//checks if prepareOk counter exists for this client
			nr_ok=state.prepareOk_counter.get(prepareOk.n);
		}
		nr_ok++;					//update prepareOk counter
		state.prepareOk_counter.put(prepareOk.n, nr_ok);
			
		if (nr_ok>=2) {		//check if there are enough prepareOk's
			try {
				state.commit_number=prepareOk.n;				//update commit number
				displayMessage("\nEnough PrepareOK's received!");
				// here should be delivery/commit
				//req_cli=state.log.get(state.log.size()-1).c;	//get request client

				
				//sendReplyToClient(prepOk_msg, req_cli);			//sendReply
				sendReplyToClient(prepOk_msg, getReply(prepareOk.n));			//sendReply
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
					
		}
	}	// end prepareOk processor

	
	//********************************************************************* 
	//							UTIL METHODS
	//*********************************************************************	
		
	// manipulates displayArea in the event-dispatch thread
	private void displayMessage( final String messageToDisplay ){
			SwingUtilities.invokeLater( new Runnable() {
				public void run() { 		// updates displayArea
					displayArea.append( messageToDisplay ); // display message
				} // end method run
			} // end anonymous inner class
			); // end call to SwingUtilities.invokeLater
	} // end method displayMessage
		
	//returns the request message of an op_number
	private Request getReply(int log_idx){
		
		return state.log.get(log_idx);
		
	}
	
	//executes commands given to the server
	private void executeCommand(String command) {
		switch (command){
			case "op": // display op-number
				displayArea.append( "\nCurrent op-number is:" + state.op_number +"\n" ); 
				break;
			case "log": //displays the log
				displayLog(); 
				break;
			case "client-tab":
				displayClientTable();
				break;
			case "info":
				displayMessage( "\n\tInfo:" +
								"\nState:" + state.status +
						 		"\nView number: "+ state.view_number +
						 		"\nReplica number: "+ state.rep_number +
						 		"\nOp number: "+ state.op_number + "\n");
				break;
			default:
				displayArea.append("\nCommand not supported\n");
				break;
		}
		
		
	}
	
	//displays client table
	private void displayClientTable() {
		displayArea.append("\n\tCurrent client table:");
		for (ClientTab value : state.client_table.values()) {
			displayMessage( "\nClient:" + value.c_id +
			 		"\nRecen request: "+ value.recent +
			 		"\nCommited: "+ value.commited + "\n"); //+
			 	//	"\nAddress&port: "+ value.c_add + ":" + value.c_port + "\n");
		}   
		
	}

	//method that displays current log
	private void displayLog() {
		
		displayArea.append( "\n\tCurrent log:" );
		
		Iterator it = state.log.iterator(); //initialize iterator for log list
		int i=0; //
		while (it.hasNext()) {
			
			
		    Request val = (Request) it.next(); //get next request from log
		    //display all information about the request
		    displayMessage( "\nIndex:" + i +
					 		"\nClient: "+ val.c +
					 		"\nSequence no: "+ val.s +
					 		"\nOperation: "+ val.op + "\n");
		    i++;
		}
		
	}
	
	//********************************************************************* 
	//							END UTIL METHODS
	//*********************************************************************	
		
	
	
	
	//*********************************************************************
	//							TIMERS - FAILURE DETECTORS
	//*********************************************************************
	
	
	//heartbeat timer on primary. Sends heartbeat to replicas if no message has been sent within timeout
	Thread heartbeatTimer = new Thread(){
		public void run(){
			long sleepTime = timeout; //time to sleep
			long sendInterval;
			while(true){
				try{
					displayMessage("\nheartbeat timer goes to sleep for: " + sleepTime + " miliseconds");
					sleep(sleepTime); //sleeps for the given time 
					sendInterval = System.currentTimeMillis() - lastSend; //calculates the time period between now and last send 
					displayMessage("\ncurrent time: " + System.currentTimeMillis() + " send interval: " + sendInterval );
					if(sendInterval >= timeout){ //checks if the time was exceeded
						displayMessage("\nTimeout! -- sending Heartbeat");
						sleepTime = timeout;
						try{
							sendHeartbeat(); //sendHeartbeat to replicas						
						}
						catch ( IOException ioException ){
							displayMessage( ioException.toString() + "\n" );
							ioException.printStackTrace();
						}
						
					}
					else{
						displayMessage("\nno timeout");
						sleepTime = timeout - sendInterval;
					}
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}

			}
		}
	}; // end heartbeat timer thread

	
	//watch dog timer on replicas. Checks every (Td + delta) if the message from primary was received. If not starts a view change.
	Thread watchTimer = new Thread(){
		public void run(){
			long timeoutPlusDelay = timeout + transmissionDelay;
			long sleepTime = timeoutPlusDelay; //time to sleep
			long receiveInterval;
			while(true){
				try{
					displayMessage("\nWatchDog timer goes to sleep for: " + sleepTime + " miliseconds");
					sleep(sleepTime); //sleeps for the given time 
					receiveInterval = System.currentTimeMillis() - lastReceive; //calculates the time period between now and last receive 
					displayMessage("\ncurrent time: " + System.currentTimeMillis() + " receive interval: " + receiveInterval );
					if(receiveInterval >= timeoutPlusDelay){ //checks if the time was exceeded
						displayMessage("\nTimeout! Primary is dead!");
						sleepTime = timeoutPlusDelay;
					}
					else{
						displayMessage("\nPrimary is alive!");
						sleepTime = timeoutPlusDelay - receiveInterval;
					}
					
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}

			}
		}
	}; //end watch dog timer thread
	
	
	
}

		

		
	 
		