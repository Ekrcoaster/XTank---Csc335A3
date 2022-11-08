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

import _main._Settings;
import battle.bullets.Bullet;
import battle.map.ColliderHitPoint;
import battle.map.ColliderRect;
import network.Client;
import network.NetworkListener;
import scenes.BattleScene;
import ui.Renderable;

public abstract class Tank implements Renderable {
	BattleScene scene;
	public boolean isServerControlled;
	
	protected String id, name;
	protected double x, y;
	protected double direction; //degrees
	protected double size;
	protected boolean isDead;

	protected double health, maxHealth;
	protected double moveSpeed, rotateSpeed;
	protected double bulletSpeedCooldown; // seconds

	double cachedSin, cachedCos;
	private double bulletActiveCooldown;
	
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
		
		var result = calculateCollisions();
		x = result.x + size;
		y = result.y + size;
	}
	
	public ColliderHitPoint calculateCollisions() {
		return scene.map.calculateCollisions(x - size, y - size, size*2, size*2);
	}
	
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
	
	public void kill() {
		isDead = true;
	}
	
	/*
	 * The _main update, update all of the bullets and cooldowns
	 */
	public void update() {
		if(isServerControlled)
			return;
		
		if(isDead)
			return;
		
		if(bulletActiveCooldown > 0)
			bulletActiveCooldown -= 1.0 / _Settings.BATTLE_FPS;
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
			Bullet shot = shoot(rotatePoint(0, -size*2).offset(x, y), direction);
			scene.bullets.add(shot);
			scene.addToRenderQueue(shot);
			bulletActiveCooldown = bulletSpeedCooldown;
			sendMessage("sBullet " + shot.getType() + " " + x + " " + y + " " + direction);
		}
	}

	protected void sendMessage(String message) {
		Client.client.sendMessage(message);
	}
	
	public void savePositionToServer() {
		sendMessage("sPos " + x + " " + y);
	}
	
	public void saveDirectionToServer() {
		sendMessage("sDir " + direction);
	}

	// -------------------
	//      rendering
	// -------------------
	
	public void render(Graphics g) {
		Color tankColor = getColor();
		if(isDead) tankColor = new Color(40, 40, 40);
		double gunLength = 1-(bulletActiveCooldown / (double)bulletSpeedCooldown)*0.4+0.3;
		g.setColor(tankColor);
		drawRotatedPolygon(new IntPoint[] {
				rotatePoint(-size * 0.3, -size - size * gunLength).offset(x, y),
				rotatePoint(size * 0.3, -size - size * gunLength).offset(x, y),
				rotatePoint(size * 0.3, size - size).offset(x, y),
				rotatePoint(-size * 0.3, size - size).offset(x, y)
		}, g);
		drawRotatedPolygon(new IntPoint[] {
				rotatePoint(-size, -size).offset(x, y),
				rotatePoint(size, -size).offset(x, y),
				rotatePoint(size, size).offset(x, y),
				rotatePoint(-size, size).offset(x, y)
		}, g);
		
		renderHoverUI(g, rotatePoint(0, size*1.5).offset(x, y), tankColor);
	}
	
	protected void renderHoverUI(Graphics g, IntPoint origin, Color color) {
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
	 * Given a x/y, rotate it by the current direction
	 */
	public IntPoint rotatePoint(double x, double y) {
		return new IntPoint(
				x * cachedCos - y * cachedSin,
				y * cachedCos + x * cachedSin);
	}
	
	/*
	 * Draw a rotated polygon
	 */
	public void drawRotatedPolygon(IntPoint[] points, Graphics g) {
		int[] xPoints = new int[points.length];
		int[] yPoints = new int[points.length];
		for(int i = 0; i < points.length; i++) {
			xPoints[i] = points[i].x;
			yPoints[i] = points[i].y;
		}
		g.drawPolygon(xPoints, yPoints, points.length);
	}
	
	// -------------------
	//  getters / setters
	// -------------------

	public ColliderRect getColliderRect(int padding) {
		if(isDead)
			return null;
		return new ColliderRect(x - size - padding, y - size - padding, x + size + padding, y + size + padding);
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

	public double getDirection() {
		return direction;
	}

	public double getSize() {
		return size;
	}
	
	public String getID() {
		return id;
	}

	public void setDirection(double direction) {
		this.direction = direction;
		cachedSin = Math.sin(Math.toRadians(direction));
		cachedCos = Math.cos(Math.toRadians(direction));
	}
	
	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
		if(health <= 0)
			kill();
	}
	
	@Override
	public String toString() {
		return "[tank " + id + " (" + name + "), server: " + isServerControlled + "]";
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

