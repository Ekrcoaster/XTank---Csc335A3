/*
 * Author: Ethan Rees
 * This scene holds the join screen, it will collect players and when the server is ready, they
 * can begin the game.
 */
package scenes;

import java.io.File;
import java.rmi.server.UID;
import java.util.ArrayList;

import _main.Boot;
import network.Client;
import network.Message;
import network.NetworkListener;
import network.Server;
import ui.DropdownField;
import ui.JoinSceneUI;
import ui.WindowManager;

public class JoinScene extends Scene implements NetworkListener {
	
	JoinSceneUI ui;
	boolean client, server;
	
	public ArrayList<String> playerIDs;
	public ArrayList<String> playerNames;
	public ArrayList<String> playerTankTypes;
	
	public String myPlayerID;
	public String myPlayerName;
	public String myPlayerTank;
	
	public String mapName;
	
	public JoinScene(boolean client, boolean server, String playerName, String mapName) {
		this.client = client;
		this.server = server;

		this.myPlayerID = null;
		this.myPlayerName = playerName;
		this.myPlayerTank = Boot.defaultTankType;
		
		this.mapName = mapName;
	}

	@Override
	public void init() {
		ui = new JoinSceneUI(client, server, this, mapName);
		playerIDs = new ArrayList<String>();
		playerNames = new ArrayList<String>();
		playerTankTypes = new ArrayList<String>();
		WindowManager.setPanel(ui);
		
		// if we are a client
		if(client) {
			Client.client.addListener(this);
			
			// ask for the player list to chatch up
			Client.client.sendMessage("playerList");

			// ask for my ID just to make sure
			Client.client.sendMessage("myID");
			
		// if we are a server ONLY
		} else if(server) {
			Server.server.addListener(this);
			Server.server.acceptingNewPlayers = true;

			// because we are already the server, we can just ask for the player list ourselves
			populatePlayerList(Server.server.constructPlayerList());
		}
	}

	/*
	 * Pass the information along and begin the scene
	 */
	public void beginGame() {
		// remove the null players who accidentally slip in
		for(int i = 0; i < playerIDs.size(); i++) {
			if(playerIDs.get(i) == null) {
				playerIDs.remove(i);
				playerNames.remove(i);
				playerTankTypes.remove(i);
				i--;
			}
		}
		
		enterBattleScene(mapName);
		
		Server.server.sendMessageToAllBut("start battle " + mapName, myPlayerID);
	}
	
	public void returnToTitle() {
		Boot.closeAllNetworks();
		SceneManager.setScene(new TitleScene());
	}
	
	/*
	 * This will enter the battle scene
	 */
	public void enterBattleScene(String map) {
		// if my player ID is real, it is gonna be in the list of playerIDs, so remove it
		
		if(playerIDs.contains(myPlayerID)) {
			int index = playerIDs.indexOf(myPlayerID);
			playerIDs.remove(index);
			playerNames.remove(index);
			playerTankTypes.remove(index);
		}
		
		
		BattleScene scene = new BattleScene(myPlayerID, myPlayerName, myPlayerTank, playerIDs, playerNames, playerTankTypes, server, map);
		SceneManager.setScene(scene);
	}

	/*
	 * This will take in the server generated list of existing players and will decode them
	 * into the playerIDs list and the playerNames list
	 */
	public void populatePlayerList(String listReceived) {
		String[] pieces = listReceived.split(" ");
		playerIDs = new ArrayList<String>();
		playerNames = new ArrayList<String>();
		playerTankTypes = new ArrayList<String>();
		
		ui.clearPlayerNames();
		
		// if there is just 1 entree, ignore because its not valid
		if(pieces.length < 2) return;
		
		// odd entrees are IDs, even entrees are names
		for(int i = 0; i < pieces.length; i++) {
			if(i % 3 == 0)
				playerIDs.add(pieces[i]);
			else if(i % 3 == 1) {
				playerNames.add(pieces[i]);
				ui.addPlayerName(pieces[i]);
			} else {
				playerTankTypes.add(pieces[i]);
				ui.updatePlayerName(playerTankTypes.size() - 1, pieces[i], true);
			}
		}
	}

	@Override
	public void onMessage(Message message) {
		// join on the server side
		if(message.is("join") && !client) {
			playerIDs.add(message.fromID);
			playerNames.add(message.getArg(0));
			playerTankTypes.add(Boot.defaultTankType);
			ui.addPlayerName(message.getArg(0));
		}
		
		// join on the client side (for other clients already connected)
		if(message.is("joined")) {
			playerIDs.add(message.getArg(0));
			playerNames.add(message.getArg(1));
			playerTankTypes.add(Boot.defaultTankType);
			ui.addPlayerName(message.getArg(1));
		}
		
		// if the server responded with the player list
		if(message.is("retPlayerList")) {
			populatePlayerList(message.joinedArgs());
		}
		
		// if the server told us to go to the battle scene
		if(message.is("start")) {
			if(message.getArg(0).equals("battle")) {
				enterBattleScene(message.getArg(1));
			}
		}
		
		// set the ID
		if(message.is("retMyID")) {
			myPlayerID = message.getArg(0);
			myPlayerTank = playerTankTypes.get(playerIDs.indexOf(myPlayerID));
			// make sure this value isn't null anymore
			Client.client.id = myPlayerID;
		}
		
		// player changed tank type
		if(message.is("rTankType")) {
			int index = playerIDs.indexOf(message.getArg(0));
			playerTankTypes.set(index, message.getArg(1));
			ui.updatePlayerName(index, message.getArg(1), message.getArg(0).equals(myPlayerID));
		}
		
		// player changed tank type
		if(message.is("sTankType")) {
			int index = playerIDs.indexOf(message.fromID);
			playerTankTypes.set(index, message.getArg(0));
			ui.updatePlayerName(index, message.getArg(0), message.fromID.equals(myPlayerID));
			if(message.fromID.equals(myPlayerID))
				myPlayerTank = message.getArg(0);
		}
		
		// a map has been changed
		if(message.is("map")) {
			ui.setMap(message.getArg(0));
			mapName = message.getArg(0);
		}

		// if a client has exited, remove them from the list
		if(message.is("clientExit") || message.is("aClientExited")) {
			String clientID = message.fromID == null ? message.getArg(0) : message.fromID;
			int index = playerIDs.indexOf(clientID);

			if(index > -1) {
				ui.removePlayer(index);
				playerIDs.remove(index);
				playerNames.remove(index);
				playerTankTypes.remove(index);
			}
		}
	}

	@Override
	public void exit() {
		if(server && Server.server != null) {
			Server.server.removeListener(this);
		} else if(client && Client.client != null) {
			Client.client.removeListener(this);
		}
	}
	
	@Override
	public void onSentMessage(Message message) { }
	@Override
	public String getID() {return "join";}
}