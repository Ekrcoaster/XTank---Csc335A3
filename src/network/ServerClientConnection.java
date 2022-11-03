package network;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import main.*;

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
	
	public ServerClientConnection(String id) {
		this.id = id;
	}
	
	/*
	 * This is called once the client connects to my slot
	 */
	@Override
	public void run() {
		try 
		{
			System.out.println(id + " asking");
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
			sendMessage(id);
			
			while (getInput().hasNextLine()) 
			{
				String command = getInput().nextLine();
				
				// handle an exit command
				if (command.startsWith("exit"))
					return;

				Server.server.messageReceived(this, command);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/*
	 * This will send the message to the computer of the player
	 */
	@Override
	public void sendMessage(String message) {
		// send it out the actual client
		if(getOutput() != null)
			getOutput().println(message);
	}
	@Override
	public void messageReceived(MessageNode from, String message) {
		// because this object is server side, we don't care at all what is recieved by the server, so its just ginored
		
	}
	@Override
	public String getID() {
		return id;
	}

	public void setInput(Scanner input) { this.input = input;}
	public void setOutput(PrintWriter output) {this.output = output;}
	public Scanner getInput() {return input;}
	public PrintWriter getOutput() {return output;}
	
}
