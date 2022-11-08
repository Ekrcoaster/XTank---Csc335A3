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
	BattleScene scene;
	public String ownerID;
	public double direction;
	public double speed;
	public double x, y;
	public double damage;
	
	double cachedSin, cachedCos;
	
	public Bullet(BattleScene scene, String ownerID, double x, double y, double direction) {
		this.scene = scene;
		this.ownerID = ownerID;
		this.x = x;
		this.y = y;
		this.speed = 0;
		this.damage = 0;
		setDirection(direction);
	}
	
	public void setDirection(double direction) {
		cachedSin = Math.sin(Math.toRadians(direction));
		cachedCos = Math.cos(Math.toRadians(direction));
		
		this.direction = direction;
	}
	
	protected void updateCollisions() {
		// calculate collisions for the map
		ColliderHitPoint point = scene.map.calculateCollisions(x, y, 0, 0);
		if(point.hit)
			onMapCollision(point);
		
	}
	
	protected void updateMovement() {
		x += cachedSin * speed;
		y -= cachedCos * speed;
	}
	
	public void update() {
		updateMovement();
		updateCollisions();
	}
	
	protected void destroy() {
		scene.destroyBullet(this);
	}
	
	public abstract void render(Graphics g);
	public abstract void onMapCollision(ColliderHitPoint point);
	public abstract void onTankCollision(Tank tank);
	public abstract String getType();
}
