/*
 * Author: Ethan Rees
 * This class acts as the scenes game controller. It updates the tanks, creates the thread,
 * and sets up the UI.
 */
package scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import _main._Settings;
import battle.tanks.GenericTank;
import battle.tanks.Tank;
import network.Client;
import network.Message;
import network.NetworkListener;
import network.Server;
import ui.BattleBoardUI;
import ui.WindowHolder;

public class BattleScene extends Scene implements NetworkListener {

	public HashMap<String, Tank> players;
	
	public static final int FPS = 30;
	public BattleBoardUI ui;
	
	private String playerID;
	
	public boolean exit;
	Thread gameTickThread;
	
	public BattleScene(String playerID, String playerName, ArrayList<String> otherPlayerIDs, ArrayList<String> otherPlayerNames) {
		players = new HashMap<String, Tank>();
		this.playerID = playerID;
		if(playerID != null)
			players.put(playerID, new GenericTank(playerID, playerName, false)); 
		
		
		System.out.println("client player: " + players.get(playerID));
		
		for(int i = 0; i < otherPlayerIDs.size(); i++) {
			Tank newTank = new GenericTank(otherPlayerIDs.get(i), otherPlayerNames.get(i), true);

			System.out.println("added player: " + newTank);
			players.put(otherPlayerIDs.get(i), newTank);
		}
	}

	@Override
	public void init() {
		exit = false;
		
		// create the scenes board UI
		ui = new BattleBoardUI();
		WindowHolder.setPanel(ui);
		
		// if we are the server (aka we are spectating)
		if(playerID == null) {
			Server.server.addListener(this);
		} else { // if not we are a client
			Client.client.addListener(this);
		}
		
		placeTankAtRandomPosition(players.get(playerID));
		
		// create the _main scenes tick thread, this will call update/render based on the fps
		gameTickThread = new Thread(() -> {
			// get the last time, the fps tick ratio, and the delta
			long lastTime = System.nanoTime();
			double ratio = 1000000000.0 / (double)FPS;
			double delta = 0;
			
			// run as fast as humanly possible
			while(!exit) {
				
				// calcualte the delta (how much time has passed between every tick)
				long currentTime = System.nanoTime();
				delta += (currentTime - lastTime) / ratio;
				lastTime = currentTime;
				
				// if more than a delta tick has passed, then actually update the screen
				// this COULD be an if statement, but using a while lets the scenes catchup incase it lags
				while(delta >= 1) {
					update();
					render();
					delta--;
				}
			}
		});
		gameTickThread.start();

	}

	@Override
	public void exit() {
		exit = true;
	}
	
	public void update() {
		for(Tank tank : players.values()) {
			if(!tank.isServerControlled)
				tank.updateControls(ui.getKeysDown());
			tank.update();
		}
	}
	
	public void render() {
		ui.render(players.values());
	}
	
	public void placeTankAtRandomPosition(Tank tank) {
		tank.setX((Math.random() * _Settings.windowSize.getWidth() * 0.75)+  _Settings.windowSize.getWidth() * 0.15);
		tank.setY((Math.random() * _Settings.windowSize.getHeight() * 0.75) +  _Settings.windowSize.getHeight() * 0.15);
		tank.savePositionToServer();
	}

	@Override
	public void onMessage(Message message) {
		// client received info about other client's position
		if(message.is("rPos")) {
			updateTankPos(message.getArg(0), message.doubleArg(1), message.doubleArg(2));
		}
		// server receieved info about client's position
		if(message.is("sPos")) {
			updateTankPos(message.fromID, message.doubleArg(0), message.doubleArg(1));
		}
		
		// server -> client direction update
		if(message.is("rDir"))
			updateTankDir(message.getArg(0), message.doubleArg(1));
		
		// client -> server direction update
		if(message.is("sDir"))
			updateTankDir(message.fromID, message.doubleArg(0));
	}

	@Override
	public void onSentMessage(Message message) {}
	
	private void updateTankPos(String id, double x, double y) {
		if(id.equals(playerID))
			return;
		players.get(id).setX(x);
		players.get(id).setY(y);
	}
	
	private void updateTankDir(String id, double dir) {
		if(id.equals(playerID))
			return;
		players.get(id).setDirection(dir);
	}

}
