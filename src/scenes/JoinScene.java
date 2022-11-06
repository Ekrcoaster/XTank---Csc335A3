/*
 * Author: Ethan Rees
 * This scene holds the join screen, it will collect players and when the server is ready, they
 * can begin the game.
 */
package scenes;

import java.util.ArrayList;

import network.Client;
import network.Message;
import network.NetworkListener;
import network.Server;
import ui.JoinSceneUI;
import ui.WindowHolder;

public class JoinScene extends Scene implements NetworkListener {
	
	JoinSceneUI ui;
	boolean client, server;
	
	public ArrayList<String> playerIDs;
	public ArrayList<String> playerNames;
	
	public String myPlayerID;
	public String myPlayerName;
	
	public JoinScene(boolean client, boolean server, String playerID, String playerName) {
		this.client = client;
		this.server = server;

		this.myPlayerID = playerID;
		this.myPlayerName = playerName;
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

	/*
	 * Pass the information along and begin the scene
	 */
	public void beginGame() {
		// if my player ID is real, it is gonna be in the list of playerIDs, so remove it
		if(myPlayerID != null) {
			System.out.println(myPlayerID);
			int index = playerIDs.indexOf(myPlayerID);
			playerIDs.remove(index);
			playerNames.remove(index);
		}
		BattleScene scene = new BattleScene(myPlayerID, myPlayerName, playerIDs, playerNames);
		SceneManager.setScene(scene);
	}

	@Override
	public void onMessage(Message message) {
		if(message.is("join")) {
			System.out.println(message);
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