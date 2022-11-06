/*
 * Author: Ethan Rees
 * This represents a tank, it is built with all of the default methods are variables
 * that are needed to do basic tank functionality
 */
package battle.tanks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.HashSet;

import battle.bullets.Bullet;
import scenes.BattleScene;

public abstract class Tank {
	protected String id;
	protected int health;
	protected double x, y;
	protected double direction; //degrees
	protected double size;
	protected double moveSpeed, rotateSpeed;
	protected double bulletSpeedCooldown; // seconds
	
	private double bulletActiveCooldown;
	double cachedSin, cachedCos;
	public HashSet<Bullet> shotBullets;
	
	public Tank(String id) {
		this.id = id;
		this.x = 0;
		this.y = 0;
		this.direction = 0;
		this.health = 0;
		this.size = 0;
		this.moveSpeed = 0;
		this.rotateSpeed = 0;
		this.shotBullets = new HashSet<Bullet>();
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
	}
	
	/*
	 * What should happen if a bullet is shot?
	 */
	public abstract Bullet shoot(IntPoint origin, double direction);
	
	/*
	 * This will damage the tank and kill it <- TODO
	 */
	public void damage(int health) {
		this.health -= health;
	}
	
	/*
	 * The main update, update all of the bullets and cooldowns
	 */
	public void update() {
		for(Bullet bullet : shotBullets) {
			bullet.update();
		}
		
		if(bulletActiveCooldown > 0)
			bulletActiveCooldown -= 1.0 / BattleScene.FPS;
	}
	
	/*
	 * This will only get called if this tank is the player, it updates controls
	 */
	public void updateControls(HashSet<Integer> keysDown) {

		if(keysDown.contains(KeyEvent.VK_LEFT) || keysDown.contains(KeyEvent.VK_KP_LEFT)) {
			turn(-rotateSpeed);
		}

		if(keysDown.contains(KeyEvent.VK_RIGHT) || keysDown.contains(KeyEvent.VK_KP_RIGHT)) {
			turn(rotateSpeed);
		}

		if(keysDown.contains(KeyEvent.VK_UP) || keysDown.contains(KeyEvent.VK_KP_UP)) {
			move(moveSpeed);
		}

		if(keysDown.contains(KeyEvent.VK_DOWN) || keysDown.contains(KeyEvent.VK_KP_DOWN)) {
			move(-moveSpeed);
		}
		if(keysDown.contains(KeyEvent.VK_SPACE) && bulletActiveCooldown <= 0) {
			shotBullets.add(shoot(rotatePoint(0, -size*2).offset(x, y), direction));
			bulletActiveCooldown = bulletSpeedCooldown;
		}
	}
		
	// -------------------
	//      rendering
	// -------------------
	
	public void render(Graphics g) {
		g.setColor(Color.white);
		drawRotatedPolygon(new IntPoint[] {
				rotatePoint(-size * 0.3, -size - size).offset(x, y),
				rotatePoint(size * 0.3, -size - size).offset(x, y),
				rotatePoint(size * 0.3, size - size).offset(x, y),
				rotatePoint(-size * 0.3, size - size).offset(x, y)
		}, g);
		drawRotatedPolygon(new IntPoint[] {
				rotatePoint(-size, -size).offset(x, y),
				rotatePoint(size, -size).offset(x, y),
				rotatePoint(size, size).offset(x, y),
				rotatePoint(-size, size).offset(x, y)
		}, g);
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

	public void setDirection(double direction) {
		this.direction = direction;
		cachedSin = Math.sin(Math.toRadians(direction));
		cachedCos = Math.cos(Math.toRadians(direction));
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
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