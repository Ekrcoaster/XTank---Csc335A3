/*
 * Author: Ethan Rees
 * This represents a tank, it is built with all of the default methods are variables
 * that are needed to do basic tank functionality
 */
package battle.tanks;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import _main.Boot;
import battle.bullets.Bullet;
import battle.map.ColliderHitPoint;
import battle.map.ColliderRect;
import network.Client;
import network.NetworkListener;
import scenes.BattleScene;
import ui.Renderable;

public abstract class Tank implements Renderable, Comparable<Tank> {
	// network
	BattleScene scene;
	public boolean isServerControlled;
	
	// tank data
	protected String id, name;
	protected double x, y;
	protected double direction; //degrees
	protected double size;
	protected boolean isDead;
	protected double health, maxHealth;
	protected double moveSpeed, rotateSpeed;
	protected double bulletSpeedCooldown; // seconds
	public double damageDealt;

	// private tank data
	double cachedSin, cachedCos;
	protected double bulletActiveCooldown;
	
	public Tank(String id, String name, boolean isServerControlled, BattleScene scene) {
		this.isServerControlled = isServerControlled;
		this.scene = scene;
		this.id = id;
		this.name = name;
		
		this.x = 0;
		this.y = 0;
		this.isDead = false;
		setDirection(0);
		this.size = 0;
		this.health = 0;
		this.maxHealth = 0;
		this.moveSpeed = 0;
		this.rotateSpeed = 0;
		this.bulletSpeedCooldown = 0;
		this.bulletActiveCooldown = 0;
		this.damageDealt = 0;
	}
	
	// - - - - - - - - - - - -
	//  MOVEMENT AND CONTROLS
	// - - - - - - - - - - - -

	/*
	 * The _main update, update all of the bullets and cooldowns
	 */
	public void update() {
		if(isDead)
			return;
		
		if(health > maxHealth)
			maxHealth = health;
		
		if(isServerControlled)
			return;
		
		if(bulletActiveCooldown > 0)
			bulletActiveCooldown -= 1.0 / Boot.BATTLE_FPS;
	}
	
	/*
	 * This will only get called if this tank is the player, it updates controls
	 */
	public void updateControls(HashSet<Integer> keysDown) {
		if(isDead)
			return;
		
		if(keysDown.contains(KeyEvent.VK_LEFT) || keysDown.contains(KeyEvent.VK_KP_LEFT)) {
			turn(-rotateSpeed);
			saveDirectionToServer();
		}

		if(keysDown.contains(KeyEvent.VK_RIGHT) || keysDown.contains(KeyEvent.VK_KP_RIGHT)) {
			turn(rotateSpeed);
			saveDirectionToServer();
		}

		if(keysDown.contains(KeyEvent.VK_UP) || keysDown.contains(KeyEvent.VK_KP_UP)) {
			move(moveSpeed);
			savePositionToServer();
		}

		if(keysDown.contains(KeyEvent.VK_DOWN) || keysDown.contains(KeyEvent.VK_KP_DOWN)) {
			move(-moveSpeed);
			savePositionToServer();
		}
		if(keysDown.contains(KeyEvent.VK_SPACE) && bulletActiveCooldown <= 0) {
			Bullet shot = shoot(rotatePoint(0, -size*2, direction).offset(x, y), direction);
			scene.bullets.add(shot);
			scene.addToRenderQueue(shot);
			bulletActiveCooldown = bulletSpeedCooldown;
			sendMessage("sBullet " + shot.getType() + " " + x + " " + y + " " + direction);
		}
	}

	/*
	 * Turn the tank a certain amount
	 */
	public void turn(double amount) {
		setDirection(direction + amount);
	}
	
	/*
	 * Move the tank a certian amount
	 */
	public void move(double amount) {
		// move (x,y) using sin and cos, this will move it forward in the desired direction
		x += cachedSin * amount;
		y -= cachedCos * amount;
		
		setPosToCollisionCheck(x, y);
	}
	
	/*
	 * This will move the tank to a position, but then will check for collisions
	 */
	public void setPosToCollisionCheck(double x, double y) {
		ColliderHitPoint result = calculateCollisions(x, y);
		this.x = result.x + size;
		this.y = result.y + size;
	}
	
	/*
	 * This will check for collisions at a single point, without touching the tank
	 */
	public ColliderHitPoint calculateCollisions(double x, double y) {
		return scene.map.calculateCollisions(x - size, y - size, size*2, size*2);
	}

	/*
	 * Send my current position to the server
	 */
	public void savePositionToServer() {
		sendMessage("sPos " + x + " " + y);
	}

	/*
	 * Send my current rotation to the server
	 */
	public void saveDirectionToServer() {
		sendMessage("sDir " + direction);
	}

	
	
	// - - - - - - - - - - - -
	//    HEALTH AND DAMAGE
	// - - - - - - - - - - - -
	
	/*
	 * What should happen if a bullet is shot?
	 */
	public abstract Bullet shoot(IntPoint origin, double direction);
	
	/*
	 * This will damage the tank and kill it
	 */
	public void damage(double amount) {
		setHealth(health - amount);
	}
	
	/*
	 * Kill the tank
	 */
	public void kill() {
		isDead = true;
		scene.onTankKilled(this);
	}
	
	


	// - - - - - - - - - - - -
	//       RENDERING
	// - - - - - - - - - - - -
	
	/*
	 * Render the tank
	 */
	public void render(Graphics g) {
		Color tankColor = getColor();
		if(isDead) tankColor = new Color(40, 40, 40);
		double gunLength = getBulletCooldownPercent()*0.4+0.3;
		
		// draw the tank
		drawTankBody(g, tankColor);
		drawTankGun(g, tankColor, gunLength);

		// draw the hover UI
		renderHoverUI(g, rotatePoint(0, size*1.5, direction).offset(x, y), tankColor);
	}
	
	/*
	 * Render the tank body, just draw a square that can be rotated
	 */
	protected void drawTankBody(Graphics g, Color color) {
		g.setColor(color);
		
		drawRotatedPoly(new double[][] {
			{-size, -size},
			{size, -size},
			{size, size},
			{-size, size}
		}, direction, g);
	}
	
	/*
	 * Draw a tank gun, just a rect that can be rotated
	 */
	protected void drawTankGun(Graphics g, Color color, double gunLength) {
		g.setColor(color);
		
		drawRotatedPoly(new double[][] {
			{-size * 0.3, -size - size * gunLength},
			{size * 0.3, -size - size * gunLength},
			{size * 0.3, size - size},
			{-size * 0.3, size - size}
		}, direction, g);
	}
	
	/*
	 * Draw the hover UI for the tank, the player's name, healthbar, and more
	 */
	protected void renderHoverUI(Graphics g, IntPoint origin, Color color) {
		// calculate the size of the player's name
		FontMetrics metrics = g.getFontMetrics();
		int width = metrics.stringWidth(name);
		int height = 14;
		
		// draw the background to the label
		g.setColor(new Color(10, 10, 10));
		g.fillRect((int)(origin.x - width*.5), (int)(origin.y - height * 0.7), width, height);
		
		// draw the label
		g.setColor(color);
		g.drawString(name, (int)(origin.x - width*.5), origin.y);
		
		// render the healthbar ONLY if the tank is damaged
		if(health < maxHealth && !isDead) {
			int healthBarWidth = 30;
			g.setColor(Color.red);
			g.fillRect((int)(origin.x - healthBarWidth*.5), (int)(origin.y - height * 0.7)+15, healthBarWidth, 2);

			g.setColor(Color.green);
			g.fillRect((int)(origin.x - healthBarWidth*.5), (int)(origin.y - height * 0.7)+15, (int)(healthBarWidth * (health / maxHealth)), 2);
		}
	}

	/*
	 * This tankes in a double[][] of coords, and will draw the poly to the degree.
	 */
	protected void drawRotatedPoly(double[][] points, double degree, Graphics g) {
		// convert all of the points to IntPoints and rotate them, then offset them
		IntPoint[] intPoints = new IntPoint[points.length];
		for(int i = 0; i < intPoints.length; i++) {
			intPoints[i] = rotatePoint(points[i][0], points[i][1], degree).offset(x, y);
		}

		// finally, create the positions and draw it to the screen
		int[] xPoints = new int[intPoints.length];
		int[] yPoints = new int[intPoints.length];
		for(int i = 0; i < intPoints.length; i++) {
			xPoints[i] = intPoints[i].x;
			yPoints[i] = intPoints[i].y;
		}
		
		g.drawPolygon(xPoints, yPoints, intPoints.length);
	}
	
	/*
	 * Given a x/y, rotate it by the degree
	 */
	protected IntPoint rotatePoint(double x, double y, double degree) {
		// tiny optimization, if we are using the direction of the tank, just use the sin/cos cached
		double sin = cachedSin; 
		if(degree != direction) sin = Math.sin(Math.toRadians(degree));
		double cos = cachedCos; 
		if(degree != direction) cos = Math.cos(Math.toRadians(degree));
		
		// this rotates using the formula for rotating a x/y by sin/cos
		return new IntPoint(x * cos - y * sin, y * cos + x * sin);
	}

	
	// - - - - - - - - - - - -
	//          UTIL
	// - - - - - - - - - - - -

	/*
	 * This returns the tank's collision box with some padding
	 */
	public ColliderRect getColliderRect(int padding) {
		if(isDead)
			return null;
		return new ColliderRect(x - size - padding, y - size - padding, x + size + padding, y + size + padding);
	}
	
	// compare tanks by damage dealt
	@Override
	public int compareTo(Tank o) {
		return Integer.compare((int)(o.damageDealt*10), (int)(damageDealt*10));
	}
	
	protected Color getColor() {
		if(isServerControlled)
			return Color.blue;
		return Color.yellow;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	/*
	 * Set the direction and cache the sin and cos values
	 */
	public void setDirection(double direction) {
		this.direction = direction;
		cachedSin = Math.sin(Math.toRadians(direction));
		cachedCos = Math.cos(Math.toRadians(direction));
	}
	
	public double getDirection() {
		return direction;
	}

	public void setSize(double size) {
		this.size = size;
	}
	
	public double getSize() {
		return size;
	}
	
	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	/*
	 * This returns a 0-1 scale based on how much of a cooldown is left on the bullet cooldown
	 * 0 = max time
	 * 1 = ready to fire
	 */
	public double getBulletCooldownPercent() {
		return Math.min(1, 1-(bulletActiveCooldown / bulletSpeedCooldown));
	}

	public void setHealth(double health) {
		this.health = health;
		if(health <= 0)
			kill();
	}
	
	public double getHealth() {
		return health;
	}
	
	public boolean isDead() {
		return isDead;
	}

	public abstract String getType();
	
	@Override
	public String toString() {
		return "[tank " + id + " (" + name + "), server: " + isServerControlled + "]";
	}
	
	/*
	 * Unlike toString, this will return the tank in a format that can be sent over the network
	 */
	public String toEncoded() {
		return id + " " + name.replace(" ", "_") + " " + damageDealt + " " + this.getType();
	}
	
	/*
	 * This will send a message to the server
	 */
	protected void sendMessage(String message) {
		Client.client.sendMessage(message);
	}
}

/*
 * This mini class just repesents a screen pixel point, it can be created using a double x/y
 * and will just automatically round. It makes drawing stuff on screen much easier
 */
class IntPoint {
	public int x;
	public int y;
	public IntPoint(double x, double y) {
		this.x = (int)Math.round(x);
		this.y = (int)Math.round(y);
	}
	
	public IntPoint offset(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
}

