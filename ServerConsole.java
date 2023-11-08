import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;
import ocsf.server.ConnectionToClient;

public class ServerConsole implements ChatIF{
	
	//instance of the schoserver
	private EchoServer echo;
	
	//scanner instance
	Scanner fromConsole; 
	
	//connection to client instance
	ConnectionToClient c;
	
	//default port
	final public static int DEFAULT_PORT = 5555;
	
	public ServerConsole(EchoServer e) 
	  {
	  
	    echo = e;   
	    
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
	  }
	
	//message displays in server console
	@Override
	public void display(String message) 
	  {
	    System.out.println("SERVER MSG> " + message);
	    echo.sendToAllClients("SERVER MSG> " + message);
	    
	  }
	
	public void accept() 
	  {
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        
	        if(message.startsWith("#")) {
	        	 handleCommand(message);
	        }
	        else {
	        	display(message);
	        }
	       
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }
	
	  private void handleCommand(String command) {
		  if(command.equals("#quit")) {
			  display("Server quitting");
			  echo.sendToAllClients("SERVER MSG> Server quitting");
			  echo.stopListening();
			  System.exit(0);
			  
		  }
		  else if(command.equals("#stop")) {
			  echo.stopListening();
		  }
		  else if(command.equals("#close")) {
			  try {
				  echo.close();
				  echo.stopListening();
			  }
			  catch(IOException e) {
				  display("Could not close server");
			  }
		  }
		  else if(command.startsWith("#setport")) {
			  if(!echo.isListening()) {
				  String [] input = command.split(" ");
				 
				  if(input.length == 2) {
					  int p = Integer.parseInt(input[1]);
					  echo.setPort(p);
					  display("Port set to "+ p);
				  }
				  else {
					  display("Could not read input, enter in format #setport <port>");
				  }
			  }
			  else {
				  display("Can only use if server is closed");
			  }
		  }
		  else if(command.equals("#start")) {
			  if(!echo.isListening()) {
				  try {
					  echo.listen();
				  }
				  catch(Exception e) {
					  display("ERROR - Could not listen for clients!");
				  }
			  }
			  else {
				  display("Only valid if server is stopped");
			  }
		  }
		  else if(command.equals("#getport")) {
			  display("Port: " + echo.getPort());
		  }
		  
	  }
	
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
		
	    EchoServer es = new EchoServer(port);
	    ServerConsole sc = new ServerConsole(es);
	    
	    try 
	    {
	      sc.echo.listen(); //Start listening for connections
	      sc.accept(); //handle messages and commands 
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
	  }
	
	
}
