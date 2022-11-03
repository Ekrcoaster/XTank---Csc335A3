package main;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import network.*;

/*
 * This is the client, it can be ran and if so, 
 * it'll create the client and then create the network if one is not found. This can be disabled!
 * 
 * The client handles the connections from the network
 */
public class Client implements MessageNode {
    public static Client client;
    
    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    public ConsoleDebugWindow debugConsoleDialogue;
    
    public String serverAddress;
    public int port;
    public String id;

    public Client() throws Exception
    {
    	this.port = _Settings.defaultPort;
    	this.serverAddress = _Settings.defaultServerAdress;
    	
    	// if the network wasn't able to be connected to, maybe it doesn't exist yet? So create it
    	if(!attemptConnect() && _Settings.clientCreatesServer) {
    		// try to create the network
    		try {
        		Server.main(null);
    		} catch (Exception e) {
    		}
    		// then try to reconnect
    		attemptConnect();
    	}
    	
    	if(_Settings.createDebugConsoles)
    		debugConsoleDialogue = new ConsoleDebugWindow(this);

    	// tell the network I'd like to join!
        sendMessage("join");
        startMessageListener();
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
	}

	@Override
	public void messageReceived(MessageNode from, String message) {
		if(debugConsoleDialogue != null)
			debugConsoleDialogue.addMessage("Server", message);
	}

	@Override
	public String getID() {
		return id;
	}

    /**
     * The main thread of the client will listen for messages from the network. The
     * first message will be a "WELCOME" message in which we receive our mark. Then
     * we go into a loop listening for any of the other messages, and handling each
     * message appropriately. The "VICTORY", "DEFEAT", "TIE", and
     * "OTHER_messageListenerER_LEFT" messages will ask the user whether or not to messageListener another
     * game. If the answer is no, the loop is exited and the network is sent a "QUIT"
     * message.
     */
    public void startMessageListener() throws Exception 
    {
        try 
        {
        	// send the client the ID
        	id = in.nextLine();
        	if(debugConsoleDialogue != null) debugConsoleDialogue.setID(id);
        	
            while (in.hasNextLine()) 
            {
                messageReceived(null, in.nextLine());
            }
            
            sendMessage("exit");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            socket.close();
            if(debugConsoleDialogue != null) debugConsoleDialogue.exit();
        }
    }

    // runs the client
    public static void main(String[] args) throws Exception 
    {
    	client = new Client();
    }
}
