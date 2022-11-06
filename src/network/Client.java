/*

 * Author: Ethan Rees
 * This file will open and create a client (and server if desired)!
 */

package network;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import _main._Settings;

/*
 * This is the client, it can be ran and if so, 
 * it'll create the client and then create the network if one is not found. This can be disabled!
 * 
 * The client handles the connections from the network
 */
public class Client extends NetworkActivityCaller implements MessageNode {
    public static Client client;
    
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    public ConsoleDebugWindow debugConsoleDialogue;
    
    public String serverAddress;
    public int port;
    public String id;
    public String name;

    public Client(boolean createServerIfNeeded, String ipAddress, int port, String name) throws Exception
    {
    	client = this;
    	this.port = port;
    	this.serverAddress = ipAddress;
    	this.name = name;
    	
    	// if the network wasn't able to be connected to, maybe it doesn't exist yet? So create it
    	if(!attemptConnect()) {
    		// try to create the network
    		if(createServerIfNeeded) {
        		try {
        			new Server(port);
            		// then try to reconnect
            		if(!attemptConnect())
            			throw new Exception("Unable to connect to myself... for some reason");
        		} catch (Exception e) {
        			throw new Exception("Starting up the server failed!");
        		}
    		} else {
    			throw new Exception("No Server Found");
    		}
    	}
    	
    	if(_Settings.createDebugConsoles)
    		debugConsoleDialogue = new ConsoleDebugWindow(this, this);

    	// tell the network I'd like to join!
        sendMessage("join " + name);
        new Thread(() -> {startMessageListener();}).start();
    }
    
    public boolean attemptConnect() {
    	try {
    		socket = new Socket(serverAddress, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            return true;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
	@Override
	public void sendMessage(String message) {
    	out.println(message);
    	
    	callListenersOnSentMessage(new Message(message, null));
	}

	@Override
	public void messageReceived(Message message) {
		// if the command is "id", it'll tell the client to set the ID to that (only used on startup)
		if(message.is("id"))
			this.id = message.getArg(0);
		
		callListenersOnMessage(message);
	}

	@Override
	public String getID() {
		return id;
	}

    /**
     * The _main thread of the client will listen for messages from the network. The
     * first message will be a "WELCOME" message in which we receive our mark. Then
     * we go into a loop listening for any of the other messages, and handling each
     * message appropriately. The "VICTORY", "DEFEAT", "TIE", and
     * "OTHER_messageListenerER_LEFT" messages will ask the user whether or not to messageListener another
     * scenes. If the answer is no, the loop is exited and the network is sent a "QUIT"
     * message.
     */
    public void startMessageListener()
    {
        try 
        {
            while (in.hasNextLine()) 
            {
                messageReceived(new Message(in.nextLine(), null));
            }
            
            sendMessage("exit");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
        	try {
                socket.close();
        	} catch(Exception e) {
        	}
            if(debugConsoleDialogue != null) debugConsoleDialogue.exit();
        }
    }

    // runs the client
    public static void main(String[] args) throws Exception 
    {
    	new Client(_Settings.clientCreatesServer, _Settings.defaultServerAdress, _Settings.defaultPort, "Testing");
    }
}
