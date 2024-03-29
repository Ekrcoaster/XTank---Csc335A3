/*
 * Author: Ethan Rees
 * This class acts as the scenes game controller. It updates the tanks, creates the thread,
 * and sets up the UI.
 */
package scenes;

import java.awt.Color;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import _main.Boot;
import battle.bullets.BombBullet;
import battle.bullets.Bullet;
import battle.bullets.GenericBullet;
import battle.bullets.MagicBullet;
import battle.bullets.ScoutBullet;
import battle.bullets.SturdyBullet;
import battle.map.*;
import battle.tanks.*;
import network.Client;
import network.Message;
import network.NetworkListener;
import network.Server;
import ui.*;

public class BattleScene extends Scene implements NetworkListener {

	public boolean isServer;
	public BattleBoardUI ui;
	public String playerID;
	
	public HashMap<String, Tank> players;
	public ArrayList<Bullet> bullets;
	public BattleMap map;
	
	public boolean threadExit;
	Thread gameTickThread;
	
	ArrayList<Renderable> itemsToRender;
	
	boolean gameOver;
	Tank winnerTank;
	int timeOnWinnerScreen;
	boolean clientExited;
	
	public BattleScene(String playerID, String playerName, String playerTankType, ArrayList<String> otherPlayerIDs, ArrayList<String> otherPlayerNames, ArrayList<String> otherPlayerTankTypes, boolean isServer, String mapName) {
		this.players = new HashMap<String, Tank>();
		this.bullets = new ArrayList<Bullet>();
		this.itemsToRender = new ArrayList<Renderable>();
		this.playerID = playerID;
		this.isServer = isServer;
		this.gameOver = false;
		this.clientExited = false;
		
		// create the map
		this.map = new BattleMap(mapName, Boot.windowSize.width - 15, Boot.windowSize.height - 40);
		
		// create the client's tank (if this instance is even a client)
		if(playerID != null)
			players.put(playerID, createTank(playerID, playerName, playerTankType, false)); 
		
		// create the other player's tanks
		for(int i = 0; i < otherPlayerIDs.size(); i++) {
			Tank newTank = createTank(otherPlayerIDs.get(i), otherPlayerNames.get(i), otherPlayerTankTypes.get(i), true);
			players.put(otherPlayerIDs.get(i), newTank);
		}
		
	}

	/*
	 * This is called on startup
	 */
	@Override
	public void init() {
		threadExit = false;
		
		// create the scenes board UI
		ui = new BattleBoardUI(this);
		WindowManager.setPanel(ui);
		
		// add the listeners
		if(playerID == null) {
			Server.server.addListener(this);
			Server.server.acceptingNewPlayers = false;
		} else { // if not we are a client
			Client.client.addListener(this);
		}
		
		// add all tanks to render queue
		for(Tank tank : players.values()) {
			addToRenderQueue(tank);
		}
		
		// add the renderables from the map
		for(Renderable item : map.getRenderables()) {
			addToStartRenderQueue(item);
		}
		
		startGameLoop();

		// create some dummy tanks if the player is alone
		if(players.size() == 1) {
			ui.sendNotification("You are playing alone, so here are some dummy tanks to keep you company", Color.green, false);
			createDummyTank("Dummy Tank 1", "magic", 100, 450, 2);
			createDummyTank("Dummy Tank 2", "sturdy", 750, 350, 1);
			createDummyTank("Dummy Tank 3", "generic", 400, 500, 0);
		}
		
		if(playerID == null)
			ui.sendNotification("You are the server spectating!", Color.orange, false);
	}
	
	/*
	 * This will begin a game loop in a new thread
	 */
	private void startGameLoop() {
		// create the _main scenes tick thread, this will call update/render based on the fps
		gameTickThread = new Thread(() -> {
			
			// FPS MANAGER
			// this will keep the game running at a steady fps
			// get the last time, the fps tick ratio, and the delta
			long lastTime = System.nanoTime();
			double ratio = 1000000000.0 / (double)Boot.BATTLE_FPS;
			double delta = 0;
			
			// place the player at a random position
			if(playerID != null)
				placeTankAtRandomPosition(players.get(playerID));

			// run as fast as humanly possible
			while(!threadExit) {

				// Calculate the delta (how much time has passed between every tick)
				long currentTime = System.nanoTime();
				delta += (currentTime - lastTime) / ratio;
				lastTime = currentTime;

				// if more than a delta tick has passed, then actually update the screen
				// this COULD be an if statement, but using a while lets the scenes catchup incase it lags
				while(delta >= 1) {
					update();
					ui.render(itemsToRender);
					delta--;
				}
			}
		});
		gameTickThread.start();
	}

	/*
	 * The update method, this updates all tanks, handles movements, etc
	 */
	private void update() {
		
		// this is called once a tank has won the game, it spins the tank for a second, then goes to the results screen
		if(gameOver) {
			if(winnerTank != null)
				winnerTank.setDirection(winnerTank.getDirection() + 5);
			timeOnWinnerScreen++;
			
			// time is up, swap the scene
			if(timeOnWinnerScreen > 130) {
				ResultsScene scene = new ResultsScene(map.getMapName(), playerID == null ? null : players.get(playerID).getName());
				SceneManager.setScene(scene);
				if(isServer) {
					Server.server.sendMessage("start results");
					String results = generateDataForResults();
					Server.server.sendMessage("results " + results);
					if(playerID == null)
						scene.processResults(results);
				}
			}
			return;
		}
		
		// update the tanks
		for(Tank tank : players.values()) {
			if(!tank.isServerControlled) {
				// if the tank is dead, all them to press L to leave the game
				if(tank.isDead() && ui.getKeysDown().contains(KeyEvent.VK_L) && !clientExited) {
					clientExited = true;
					Boot.closeAllNetworks();
					SceneManager.setScene(new TitleScene());
				}
				
				tank.updateControls(ui.getKeysDown());
			}
			tank.update();
		}
		
		// update the bullets and calculate tank damage
		for(int i = 0; i < bullets.size(); i++) {
			Bullet bullet = bullets.get(i);
			bullet.update();
			
			// check if this bullet is colliding with any tank
			Tank collidedWith = getCollidedTank(bullet.x, bullet.y, bullet.collisionRadius);
			
			// if so, tell the tank, then calculate the damage
			if(collidedWith != null && !collidedWith.getID().equals(bullet.ownerID)) {
				bullet.onTankCollision(collidedWith);
				
				// if we are the server, send it out to the client
				if(isServer) {
					collidedWith.damage(bullet.damage);
					updateTankDamageNotification(collidedWith);
					checkForEndOfGame();
					players.get(bullet.ownerID).damageDealt += bullet.damage;
					Server.server.sendMessage("health " + collidedWith.getID() + " " + collidedWith.getHealth());
					Server.server.sendMessage("bulletDamage " + bullet.ownerID + " " + players.get(bullet.ownerID).damageDealt);
				}
			}
		}
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

			// handle if a player has left mid game, kill them instantly
			if(message.is("clientExit")) {
				String clientID = message.fromID == null ? message.getArg(0) : message.fromID;
				players.get(clientID).setHealth(0);
				Server.server.sendMessage("health " + clientID + " " + 0);
				checkForEndOfGame();
			}
				
		} else {
			// server -> client position update
			if(message.is("rPos"))
				updateTankPos(message.getArg(0), message.doubleArg(1), message.doubleArg(2));
			
			// server -> client direction update
			if(message.is("rDir"))
				updateTankDir(message.getArg(0), message.doubleArg(1));
			
			// server -> client a bullet has been shot
			if(message.is("rBullet"))
				bullets.add(createBullet(message.getArg(0), message.getArg(1), message.doubleArg(2), message.doubleArg(3), message.doubleArg(4)));
		
			// a tank's health has been updated
			if(message.is("health"))
				updateTankHealth(message.getArg(0), message.doubleArg(1));
			
			// a tank's bullet damage has been updated
			if(message.is("bulletDamage"))
				updateTankBulletDamage(message.getArg(0), message.doubleArg(1));
			
			if(message.is("start") && message.getArg(0).equals("results"))
				SceneManager.setScene(new ResultsScene(map.getMapName(), players.get(playerID).getName()));
			
			if(message.is("aClientExited"))
				updateTankHealth(message.getArg(0), 0);
		}
		
		
	}
	
	/*
	 * This will speed up reflecting a command sent from a client to server, to the rest of the clients!
	 */
	private void sendClientServerMessageToOthers(String commandPrefix, Message m) {
		Server.server.sendMessageToAllBut(commandPrefix + " " + m.fromID + " " + m.joinedArgs(), m.fromID);
	}
	
	/*
	 * This is called for server updates to a tank's position!
	 */
	private void updateTankPos(String id, double x, double y) {
		if(id.equals(playerID))
			return;
		Tank tank = players.get(id);
		if(tank != null) {
			players.get(id).setX(x);
			players.get(id).setY(y);
		}
	}
	
	/*
	 * This is called for server updates to a tank's direction
	 */
	private void updateTankDir(String id, double dir) {
		if(id.equals(playerID))
			return;
		Tank tank = players.get(id);
		if(tank != null) {
			players.get(id).setDirection(dir);
		}
	}
	
	/*
	 * This is called for a server's updates to a tank's health
	 */
	private void updateTankHealth(String id, double newHealth) {
		Tank tank = players.get(id);
		if(tank != null) {
			tank.setHealth(newHealth);
			updateTankDamageNotification(tank);
			checkForEndOfGame();
		}
	}
	
	/*
	 * This is called when damage has happened to a tank, update the notification at the bottom
	 */
	void updateTankDamageNotification(Tank tank) {
		// good english
		String name = tank.getName() + " was ";
		if(tank.getID().equals(playerID))
			name = "you were ";
		
		// the 2 notifications
		if(tank.isDead()) {
			ui.sendNotification(name + "killed! " + getTanksAlive().size() + " tanks remain!", Color.red, false);
		} else {
			ui.sendNotification(name + "shot!", Color.white, false);
		}
	}
	
	/*
	 * This is called every time a player's health has changed, check to see if we should end the game
	 */
	void checkForEndOfGame() {
		// figure out how many players alive and who is alive
		ArrayList<Tank> tanks = getTanksAlive();
		Tank alive = tanks.size() == 0 ? null :tanks.get(0);
		
		// if there is only 0-1 tanks alive (0 should be impossible, but just incase)
		if(tanks.size() <= 1) {
			if(alive != null) {
				ui.sendNotification(alive.getName() + " won!", null, true);
				alive.damageDealt += 25;
				winnerTank = null;
			} else {
				ui.sendNotification("Somehow, no one won!", null, true);
			}
			gameOver = true;
		}
	}
	
	public void onTankKilled(Tank tank) {
		removeFromRenderQueue(tank);
		addToStartRenderQueue(tank);
	}
	
	/*
	 * Called for server's update on tanks' damage
	 */
	private void updateTankBulletDamage(String id, double newBulletDamage) {
		Tank tank = players.get(id);
		if(tank != null) {
			players.get(id).damageDealt = newBulletDamage;
		}
	}

	/*
	 * This will place a tank at a random position
	 */
	public void placeTankAtRandomPosition(Tank tank) {
		double x = ((Math.random() * Boot.windowSize.getWidth() * 0.75)+  Boot.windowSize.getWidth() * 0.15);
		double y =((Math.random() * Boot.windowSize.getHeight() * 0.75) +  Boot.windowSize.getHeight() * 0.15);
		
		// check for collisions
		tank.setPosToCollisionCheck(x, y);
		tank.savePositionToServer();
	}
	
	/*
	 * This will create a tank using the strigified type, this is useful to transfer and reconstruct the different
	 * tank types over the server
	 */
	private Tank createTank(String playerID, String name, String type, boolean serverControlled) {
		Tank tank;
		
		switch(type) {
			case "bomb": tank = new BombTank(playerID, name, serverControlled, this); break;
			case "magic": tank = new MagicTank(playerID, name, serverControlled, this); break;
			case "scout": tank = new ScoutTank(playerID, name, serverControlled, this); break;
			case "sturdy": tank = new SturdyTank(playerID, name, serverControlled, this); break;
			default: tank = new GenericTank(playerID, name, serverControlled, this); break;
		}
		
		tank.setX(500);
		tank.setY(500);
		return tank;
	}

	/*
	 * This will create a dummy no AI tank for singleplayer
	 */
	void createDummyTank(String name, String type, double x, double y, int index) {
		Tank a =createTank(name.toLowerCase().replace(" ", "_"), name, type, true);
		a.setPosToCollisionCheck(x, y);
		a.damageDealt = index;
		players.put(name.toLowerCase().replace(" ", "_"), a);
		addToRenderQueue(a);
	}
	
	/*
	 * This will create a bullet using the stringified type, helpful for server sync
	 */
	private Bullet createBullet(String playerID, String bulletType, double x, double y, double direction) {
		Bullet bullet;
		switch(bulletType) {
			case "bomb": bullet = new BombBullet(this, playerID, x, y, direction); break;
			case "magic": bullet = new MagicBullet(this, playerID, x, y, direction); break;
			case "scout": bullet = new ScoutBullet(this, playerID, x, y, direction); break;
			case "sturdy": bullet = new SturdyBullet(this, playerID, x, y, direction); break;
			default: bullet = new GenericBullet(this, playerID, x, y, direction); break;
		}
		
		addToRenderQueue(bullet);
		return bullet;
	}
	
	/*
	 * This will destroy a bullet
	 */
	public void destroyBullet(Bullet bullet) {
		bullets.remove(bullet);
		removeFromRenderQueue(bullet);
	}
	
	public void addToRenderQueue(Renderable item) {
		itemsToRender.add(item);
	}

	public void addToStartRenderQueue(Renderable item) {
		itemsToRender.add(0, item);
	}

	public void removeFromRenderQueue(Renderable item) {
		itemsToRender.remove(item);
	}
	
	/*
	 * This will find the tank that collides with x/y/radius, if one exists
	 */
	public Tank getCollidedTank(double x, double y, double radius) {
		for(Tank tank : players.values()) {
			ColliderRect rect = tank.getColliderRect(3 + (int)radius);
			if(rect != null && rect.isInside(x, y))
				return tank;
		}
		return null;
	}
	
	/*
	 * Get the tanks alive
	 */
	public ArrayList<Tank> getTanksAlive() {
		ArrayList<Tank> tanks = new ArrayList<Tank>();
		for(Tank tank : players.values()) {
			if(!tank.isDead())
				tanks.add(tank);
		}
		return tanks;
	}
	
	/*
	 * This will return the results to the results screen
	 */
	String generateDataForResults() {
		String text = "";
		for(Tank tank : players.values()) {
			text += tank.toEncoded() + " ";
		}
		return text;
	}
	
	@Override
	public void exit() {
		threadExit = true;
	}

	@Override
	public void onSentMessage(Message message) {}

	@Override
	public String getID() {return "battle";}
}
