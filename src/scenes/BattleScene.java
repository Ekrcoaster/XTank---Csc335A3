/*
 * Author: Ethan Rees
 * This class acts as the scenes game controller. It updates the tanks, creates the thread,
 * and sets up the UI.
 */
package scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import _main._Settings;
import battle.bullets.Bullet;
import battle.bullets.GenericBullet;
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
	public HashSet<Bullet> bullets;
	
	public boolean isServer;
	public BattleBoardUI ui;
	private String playerID;
	
	public boolean exit;
	Thread gameTickThread;
	
	public BattleScene(String playerID, String playerName, ArrayList<String> otherPlayerIDs, ArrayList<String> otherPlayerNames, boolean isServer) {
		this.players = new HashMap<String, Tank>();
		this.bullets = new HashSet<Bullet>();
		this.playerID = playerID;
		this.isServer = isServer;
		
		// create the client's tank (if this instance is even a client)
		if(playerID != null)
			players.put(playerID, createTank(playerID, playerName, false)); 
		
		// create the other player's tanks
		for(int i = 0; i < otherPlayerIDs.size(); i++) {
			Tank newTank = createTank(otherPlayerIDs.get(i), otherPlayerNames.get(i), true);
			players.put(otherPlayerIDs.get(i), newTank);
		}
		
		// debug server stuff
		if(isServer) {
			System.out.println("-- All Players: --");
			for(Tank tank : players.values()) {
				System.out.println(tank);
			}
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
		
		if(playerID != null)
			placeTankAtRandomPosition(players.get(playerID));
		
		startGameLoop();
	}
	
	private void startGameLoop() {
		// create the _main scenes tick thread, this will call update/render based on the fps
		gameTickThread = new Thread(() -> {
			// get the last time, the fps tick ratio, and the delta
			long lastTime = System.nanoTime();
			double ratio = 1000000000.0 / (double)_Settings.BATTLE_FPS;
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

	private void update() {
		// update the tanks
		for(Tank tank : players.values()) {
			if(!tank.isServerControlled)
				tank.updateControls(ui.getKeysDown());
			tank.update();
		}
		
		// update the bullets
		for(Bullet bullet : bullets) {
			bullet.update();
		}
	}
	
	private void render() {
		ui.render(players.values(), bullets);
	}
	
	/*
	 * Messages received by the server AND clients!
	 */
	@Override
	public void onMessage(Message message) {
		if(isServer) {
			if(message.fromID == null)
				return;
			boolean sentFromMyself = message.fromID.equals(playerID);
			
			// client -> server position update
			if(message.is("sPos")) {
				if(!sentFromMyself) updateTankPos(message.fromID, message.doubleArg(0), message.doubleArg(1));
				sendClientServerMessageToOthers("rPos", message);
			}
			
			// client -> server direction update
			if(message.is("sDir")) {
				if(!sentFromMyself) updateTankDir(message.fromID, message.doubleArg(0));
				sendClientServerMessageToOthers("rDir", message);
			}
			
			// client -> server sent a bullet
			if(message.is("sBullet")) {
				if(!sentFromMyself) bullets.add(createBullet(message.fromID, message.getArg(0), message.doubleArg(1), message.doubleArg(2), message.doubleArg(3)));
				sendClientServerMessageToOthers("rBullet", message);
			}
				
		} else {
			// server -> client position update
			if(message.is("rPos")) {
				updateTankPos(message.getArg(0), message.doubleArg(1), message.doubleArg(2));
			}
			
			// server -> client direction update
			if(message.is("rDir"))
				updateTankDir(message.getArg(0), message.doubleArg(1));
			
			if(message.is("rBullet"))
				bullets.add(createBullet(message.getArg(0), message.getArg(1), message.doubleArg(2), message.doubleArg(3), message.doubleArg(4)));
		}
	}
	
	/*
	 * This will speed up reflecting a command sent from a client to server, to the rest of the clients!
	 */
	private void sendClientServerMessageToOthers(String commandPrefix, Message m) {
		Server.server.sendMessageToAllBut(commandPrefix + " " + m.fromID + " " + m.joinedArgs(), m.fromID);
	}
	
	private void updateTankPos(String id, double x, double y) {
		if(id.equals(playerID))
			return;
		Tank tank = players.get(id);
		if(tank != null) {
			players.get(id).setX(x);
			players.get(id).setY(y);
		}
	}
	
	private void updateTankDir(String id, double dir) {
		if(id.equals(playerID))
			return;
		Tank tank = players.get(id);
		if(tank != null) {
			players.get(id).setDirection(dir);
		}
	}
	
	public void placeTankAtRandomPosition(Tank tank) {
		tank.setX((Math.random() * _Settings.windowSize.getWidth() * 0.75)+  _Settings.windowSize.getWidth() * 0.15);
		tank.setY((Math.random() * _Settings.windowSize.getHeight() * 0.75) +  _Settings.windowSize.getHeight() * 0.15);
		tank.savePositionToServer();
	}

	private Tank createTank(String playerID, String name, boolean serverControlled) {
		return new GenericTank(playerID, name, serverControlled, this);
	}
	
	private Bullet createBullet(String playerID, String bulletType, double x, double y, double direction) {
		return new GenericBullet(playerID, x, y, direction);
	}
	
	@Override
	public void exit() {
		exit = true;
	}

	@Override
	public void onSentMessage(Message message) {}
}
