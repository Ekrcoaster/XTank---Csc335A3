/*
 * Author: Ethan Rees
 * This file will open and create a server!
 */

package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JPanel;

import _main.Boot;

/*
 * This class will create a network and create the client thread connections. It can also send and receive messages
 */
public class Server extends NetworkActivityCaller implements MessageNode {
	
	// a static connection to the network reference. This is done because there is only 1 network ever running at once.
	public static Server server;

	// there is no need for order, since the players are all given IDs
	// map each client to an ID
	public HashMap<String, ServerClientConnection> players;
	private ServerClientConnection nextPotentialPlayer;
	
	ExecutorService pool;
	ServerSocket listener;
	int port;
	
	boolean exit;
	public boolean acceptingNewPlayers;
	
	/*
	 * When the network is created, startup the network and thread pool
	 */
	public Server(int port) throws IOException {
		server = this;
		players = new HashMap<String, ServerClientConnection>();
		this.port = port;

		listener = new ServerSocket(port);
		
		pool = Executors.newFixedThreadPool(200);
		pool.execute(() -> newPlayerListenerServerThread());
		
		exit = false;
		acceptingNewPlayers = true;
		if(Boot.createDebugConsoles)
			new ConsoleDebugWindow(this, this);
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
		while(!exit) {
			if(acceptingNewPlayers) {
				try {
	            	// setup the listener to the next one (if it exists)
	            	// then execute once the listener has joined!
					Socket socket = null;
					try {
						socket = listener.accept();
					} catch (SocketException e){ }
					if(socket != null) {

		            	nextPotentialPlayer.setSocket(socket);
		            	pool.execute(nextPotentialPlayer);
		            	
		            	// nice, someone connected. Add the created potential to the player list, then create a new potential slot
		            	players.put(nextPotentialPlayer.getID(), nextPotentialPlayer);
		        		newPotentialSlot();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
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
		
		nextPotentialPlayer = new ServerClientConnection(id, null);
	}

	/*
	 * this is called once a player has joined, add in a new slot!
	 */
	public boolean playerAttemptConnect(String connectedID) {
		return acceptingNewPlayers;
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
	 * Send the message to all clients BUT the one
	 */
	public void sendMessageToAllBut(String message, String exceptClientID) {
		for(ServerClientConnection player : players.values()) {
			if(!player.getID().equals(exceptClientID))
				player.sendMessage(message);
		}
	}

	/*
	 * One of the clients has sent me a message!
	 */
	@Override
	public void messageReceived(Message message) {
		callListenersOnMessage(message);
	}
	
	/*
	 * This will close a server
	 */
	public void close() {
		exit = true;
		acceptingNewPlayers = false;
		for(ServerClientConnection player : players.values()) {
			player.close();
		}
		Server.server = null;
		try {
			listener.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Return the id of the network (its network)
	 */
	@Override
	public String getID() {
		return "network";
	}
	
	public String constructPlayerList() {
		String build = "";
		
		for(ServerClientConnection player : players.values())
			build += player.getID() + " " + player.getName() + " " + player.getTankType() + " ";
		
		return build;
	}
	
	public int getPort() {
		return port;
	}
}