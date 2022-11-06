/*
 * Author: Ethan Rees
 * This scene holds the join screen, it will collect players and when the server is ready, they
 * can begin the game.
 */
package scenes;

import java.util.ArrayList;

import main.Client;
import main.Server;
import network.Message;
import network.NetworkListener;
import ui.JoinSceneUI;
import ui.WindowHolder;

public class JoinScene extends Scene implements NetworkListener {
	
	JoinSceneUI ui;
	boolean client, server;
	
	public ArrayList<String> playerIDs;
	public ArrayList<String> playerNames;
	
	public JoinScene(boolean client, boolean server) {
		this.client = client;
		this.server = server;
	}

	@Override
	public void init() {
		ui = new JoinSceneUI(client, server, this);
		playerIDs = new ArrayList<String>();
		playerNames = new ArrayList<String>();
		WindowHolder.setPanel(ui);
		
		if(server) {
			Server.server.addListener(this);
			
			// because we are already the server, we can just ask for the player list ourselves
			populatePlayerList(Server.server.constructPlayerList());
		}
		else if(client) {
			Client.client.addListener(this);
			
			// since this is just the client, ask for the player list to catch up
			Client.client.sendMessage("playerList");
		}
	}

	
	public void beginGame() {
		// TODO this is where the game begins, somehow
	}

	@Override
	public void onMessage(Message message) {
		if(message.is("join")) {
			playerIDs.add(message.getArg(0));
			playerNames.add(message.getArg(1));
			ui.addPlayerName(message.getArg(1));
		}
		
		if(message.is("retPlayerList")) {
			populatePlayerList(message.joinedArgs());
		}
	}

	
	public void populatePlayerList(String listReceived) {
		String[] pieces = listReceived.split(" ");
		for(int i = 0; i < pieces.length; i++) {
			if(i % 2 == 0)
				playerIDs.add(pieces[i]);
			else {
				playerNames.add(pieces[i]);
				ui.addPlayerName(pieces[i]);
			}
		}
	}

	@Override
	public void exit() { }
	
	@Override
	public void onSentMessage(Message message) { }
}