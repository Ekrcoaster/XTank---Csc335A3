/*
 * Author: Ethan Rees
 * This acts as almost a phone that can be used to connect to one real life client/player. These
 * are created by the server as new players join and are responsible for communications between
 * that one client and the server
 */

package network;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import _main.*;

/*
 * So this class is the messager from the server to the client. Say there are 5 players on the server,
 * there is gonna be 5 of these classes. Each one of them is responsible for connecting to one client.
 * 
 * This class lives on the server side and is never created if just a client is created. This is because
 * the client doesn't need a class to talk to the client, cause it is a client.
 */
public class ServerClientConnection implements Runnable, MessageNode {
	Scanner input;
	PrintWriter output;
	Socket socket;
	
	String id;
	String name;
	
	public ServerClientConnection(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/*
	 * This is called once the client connects to my slot
	 */
	@Override
	public void run() {
		try 
		{
			// ask the server if it is okay if my player joins
			if(!Server.server.playerAttemptConnect(id)) {
				System.out.println("The server denied your connection!");
				return;
			}
			
			// input = client -> server
			setInput(new Scanner(socket.getInputStream()));
			// output = server -> client
			setOutput(new PrintWriter(socket.getOutputStream(), true));
			
			// once a connection has been established, generate an ID and send it to the player
			sendMessage("id " + id);
			
			while (getInput().hasNextLine()) 
			{
				String command = getInput().nextLine();
				Message message = new Message(command, id);
				
				// handle an exit command
				if (message.is("exit"))
					return;

				// the order here doesn't matter, i chose to tell the server first before myself
				// so the debug consoles look more readable
				Server.server.messageReceived(message);
				messageReceived(message);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/*
	 * This will send the message to the computer of the player
	 */
	@Override
	public void sendMessage(String message) {
		// send it out the actual client
		if(getOutput() != null) {
			getOutput().println(message);
			Server.server.callListenersOnSentMessage(new Message(message, null));
		}
	}
	
	/*
	 * These are all of the messages received by the connected client, useful for commands!
	 */
	@Override
	public void messageReceived(Message message) {
		if(message.is("join")) {
			name = message.getArg(0);
			Server.server.sendMessageToAllBut("joined " + id + " " + name, id);
		}
		
		if(message.is("sPos")) {
			//Server.server.sendMessageToAllBut("rPos " + id + " " + message.joinedArgs(), id);
		}

		if(message.is("sDir")) {
			Server.server.sendMessageToAllBut("rDir " + id + " " + message.joinedArgs(), id);
		}
		
		if(message.is("playerList")) {
			sendMessage("retPlayerList " + Server.server.constructPlayerList());
		}
		
		if(message.is("myID"))
			sendMessage("retMyID " + id);
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public void setInput(Scanner input) { this.input = input;}
	public void setOutput(PrintWriter output) {this.output = output;}
	public Scanner getInput() {return input;}
	public PrintWriter getOutput() {return output;}
	
}
