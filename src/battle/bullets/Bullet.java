/*
 * Author: Ethan Rees
 * This represents a generic bullet, it is automatically packed with all of the basic bullet features,
 * such as moving in a straight line!
 */
package battle.bullets;

import java.awt.Graphics;

import battle.map.ColliderHitPoint;
import battle.tanks.Tank;
import scenes.BattleScene;
import ui.Renderable;

public abstract class Bullet implements Renderable {
	public String ownerID;
	public double direction;
	public double speed;
	public double x, y;
	public double damage;
	public double collisionRadius;
	
	protected double distanceTravelled;
	double cachedSin, cachedCos;
	BattleScene scene;
	
	public Bullet(BattleScene scene, String ownerID, double x, double y, double direction) {
		this.scene = scene;
		this.ownerID = ownerID;
		this.x = x;
		this.y = y;
		this.speed = 0;
		this.damage = 0;
		this.collisionRadius = 0;
		this.distanceTravelled = 0;
		setDirection(direction);
	}

	/*
	 * This gets called every frame
	 */
	public void update() {
		updateMovement();
		updateCollisions();
	}
	
	/*
	 * This gets called every time collisions should be updated
	 */
	protected void updateCollisions() {
		// calculate collisions for the map
		ColliderHitPoint point = scene.map.calculateCollisions(x, y, 0, 0);
		if(point.hit)
			onMapCollision(point);
	}
	
	/*
	 * This gets called everytime movement should be updated
	 */
	protected void updateMovement() {
		x += cachedSin * speed;
		y -= cachedCos * speed;
		distanceTravelled += speed;
	}

	/*
	 * a method to destroy this bullet
	 */
	protected void destroy() {
		scene.destroyBullet(this);
	}

	/*
	 * When given a direction, set it, but also cache the sine and cosine for optimization!
	 */
	public void setDirection(double direction) {
		cachedSin = Math.sin(Math.toRadians(direction));
		cachedCos = Math.cos(Math.toRadians(direction));
		
		this.direction = direction;
	}
	
	// abstract methods
	public abstract void render(Graphics g);
	public abstract void onMapCollision(ColliderHitPoint point);
	public abstract void onTankCollision(Tank tank);
	public abstract String getType();
}
