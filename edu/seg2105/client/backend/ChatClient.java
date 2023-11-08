// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  //instance variable for the login id
  String loginID;
  
  //instance variable to check if user is already logged in
  boolean isLoggedIn = false;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.loginID = loginID;
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
		
		//handle initial login
		if(msg.toString().startsWith("#login")) {
			String[] input  = msg.toString().split(" ");
			
			if(input.length>1) {
				loginID = input[1];
				clientUI.display(loginID + " has logged in");
			}
		}
		else {
			 clientUI.display(msg.toString());
		}
	
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
		  try
		    {
		    	if(message.startsWith("#")) {
		    		handleCommand(message);
		    	}
		    	else {
		    		clientUI.display(loginID + "> " + message);
		    		sendToServer(message);
		    		
		    	}
		    	
		    }
		    catch(IOException e)
		    {
		      clientUI.display
		        ("Could not send message to server.  Terminating client.");
		      quit();
		    }
	  	  
  }
  
  private void handleCommand(String command){
	  if(command.equals("#quit")) {
		  quit();
		  clientUI.display("Client terminated");
	  }
	  else if(command.equals("#logoff")) {
		  try {
			  closeConnection();
		  }
		  catch(IOException e) {
			  clientUI.display("Input / output exception occured");
		  }
	  }
	  else if(command.startsWith("#sethost")) {
		  if(!isConnected()) {
			try {
				String[]input = command.split(" ");
				if(input.length == 2) {
					String h = input[1];
					this.setHost(h);
					System.out.println("Host set to " + h);
				}
			}
			catch(Exception e) {
				clientUI.display("Could not set port. Try inputting in the format: #sethost <host>");
			}
		  }
		  else {
			  clientUI.display("Only available if client is logged off");
		  }
	  }
	  else if(command.startsWith("#setport")) {
		  if(!isConnected()) {
			  try {
				  
				  String[] input = command.split(" ");
				  if(input.length == 2) {
					  int p = Integer.parseInt(input[1]);
					  this.setPort(p);
					  System.out.println("Port set to " + p);
				  }
			  }
			  catch(Exception e) {
				  clientUI.display("Could not set port. Try inputting in the format: #setport <port>");
			  }
		  }
		  else {
			  clientUI.display("Only available if client is logged off");
		  }
	  }
	  else if(command.startsWith("#login")) {
		  if(!isConnected()) {
			  
			  if(isLoggedIn) {
				  clientUI.display(loginID + " is already logged in");
			  }
			  else {
				  try {
					  String[] input = command.split(" ");
					  String loginID = input[1];
					  openConnection();
					  System.out.println(loginID + " has logged in");
					  isLoggedIn = true;
				  }
				  catch(IOException e){
					  clientUI.display("Input / output exception occured");
				  } 
			  }
			  
		  }
		  else {
			  clientUI.display("Only available if not connected already");
		  }
	  }
	  else if(command.equals("#gethost")) {
		  clientUI.display(getHost());
	  }
	  else if(command.equals("#getport")) {
		  clientUI.display(String.valueOf(getPort()));
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  //getter for user ID
  public String getID() {
	  return this.loginID;
  }
  
  /**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		clientUI.display("The server is shut down");
  		quit();
	}
  	

	/**
	 * Implements the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  		clientUI.display("Connection closed");
	}
  	
  	/**
	 * implements the hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished() {
		try {
			sendToServer("#login "+ loginID);
		}
		catch(IOException e) {
			clientUI.display("Unable to send login to server");
		}
	}
}
//End of ChatClient class
