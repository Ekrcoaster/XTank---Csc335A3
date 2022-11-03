package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import network.*;

/*
 * This class will create a network and create the client thread connections. It can also send and receive messages
 */
public class Server implements MessageNode {
	
	// a static connection to the network reference. This is done because there is only 1 network ever running at once.
	public static Server server;

	// there is no need for order, since the players are all given IDs
	// map each client to an ID
	public HashMap<String, ServerClientConnection> players;
	private ServerClientConnection nextPotentialPlayer;
	
	// used for debugging messages sent
	public ConsoleDebugWindow debugConsole;
	
	ExecutorService pool;
	ServerSocket listener;
	int port;
	
	/*
	 * When the network is created, startup the network and thread pool
	 */
	public Server() {
		players = new HashMap<String, ServerClientConnection>();
		port = _Settings.defaultPort;
		
		// attempt to create the network
		try {
			listener = new ServerSocket(port);
			System.out.println("Server started on port " + port + "\nIP: " + InetAddress.getLocalHost());
			
			pool = Executors.newFixedThreadPool(200);
			pool.execute(() -> newPlayerListenerServerThread());
		} catch (Exception e) {
			System.out.println("Server failed to start!");
			e.printStackTrace();
			return;
		}
		
		if(_Settings.createDebugConsoles)
			debugConsole = new ConsoleDebugWindow(this);
	}

	/*
	 * This is called once the network threads have been created, it will forever listen to
	 * new players joining!
	 */
	private void newPlayerListenerServerThread() {
		// setup the potential slot
		newPotentialSlot();
		
		// begin the listening loop for the network's connected client class
		// this loop will forever listen for new players, if ones connect, it'll add them to the player list
		while(true) {
            try {
            	
            	// setup the listener to the next one (if it exists)
            	// then execute once the listener has joined!
            	nextPotentialPlayer.setSocket(listener.accept());
            	pool.execute(nextPotentialPlayer);
            	
            	// nice, someone connected. Add the created potential to the player list, then create a new potential slot
            	players.put(nextPotentialPlayer.getID(), nextPotentialPlayer);
        		newPotentialSlot();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * this will add a potential player slot to the list
	 */
	public void newPotentialSlot() {
		String id = "";
		String possibleCharacters = "1234567890abcdefghijklmnopqrstuvwxyz";
		// generate a 8 long string of random characters
		for(int i = 0; i < 8; i++)
			id += possibleCharacters.charAt((int)(Math.random() * possibleCharacters.length()));
		
		nextPotentialPlayer = new ServerClientConnection(id);
	}

	/*
	 * this is called once a player has joined, add in a new slot!
	 */
	public boolean playerAttemptConnect(String connectedID) {
		return true;
	}
	
	/*
	 * Send all of the clients a message
	 */
	@Override
	public void sendMessage(String message) {
		for(ServerClientConnection player : players.values())
			player.sendMessage(message);
	}

	/*
	 * One of the clients has sent me a message!
	 */
	@Override
	public void messageReceived(MessageNode from, String message) {
		if(debugConsole != null)
			debugConsole.addMessage(from.getID(), message);
	}

	/*
	 * Return the id of the network (its network)
	 */
	@Override
	public String getID() {
		return "network";
	}
	
	/*
	 * This class can be run alone, in which it'll create the network and start it
	 */
	public static void main(String[] args) throws IOException {
		server = new Server();
	}
}