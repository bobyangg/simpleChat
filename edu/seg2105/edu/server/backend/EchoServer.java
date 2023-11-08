package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  String loginKey = "loginID";
  
  String isLoggedIn;
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF sc;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {	
	  
	  	 //server handles client login
	     if(msg.toString().startsWith("#login")) {
	    	 
	    	 if(client.getInfo(isLoggedIn)!= "logged in") {
	    		 handleLoginInfo(msg.toString(), client);
	    	 }
	    	 else {
	    		 System.out.println(client.getInfo(loginKey) + " is already logged in");
	    	 }
	     }
	     else {
	    	 System.out.println("Message received: " + msg.toString() + " from " + client.getInfo(loginKey));
			 this.sendToAllClients(msg);
	     }
  }
  
  //method to handle case where #login is called
  public void handleLoginInfo(String msg, ConnectionToClient client) {
	  
	  String[] cmd = msg.split(" ");
	  
	  //since message being sent to server at connection is #login <loginID>, retrieve the loginID string and set it in the hashmap
	  if(cmd.length > 1) {
		  String login = cmd[1];
		  client.setInfo(loginKey, login);
		  client.setInfo(isLoggedIn, "logged in");
		  System.out.println("Message recieved: "+ msg + " from null");
		  System.out.println(login + " has logged in");
		  try {
			client.sendToClient(login + " has logged in");
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	  
	 
  }
  
  /**
   * This method terminates the server.
   */
  public void quit()
  {
    try
    {
      close();
    }
    catch(IOException e) {}
    System.exit(0);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Implements the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("User has connected to server");
  }
  
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println("User has disconnected from server");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  /*
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
  */
  
}
//End of EchoServer class
