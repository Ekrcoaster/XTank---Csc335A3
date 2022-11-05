/*
 * Author: Ethan Rees
 * This represents a generic bullet, it is automatically packed with all of the basic bullet features,
 * such as moving in a straight line!
 */
package battle.bullets;

import java.awt.Graphics;

public abstract class Bullet {
	public double direction;
	public double speed;
	public double x, y;
	
	double cachedSin, cachedCos;
	
	public Bullet(double x, double y, double direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.speed = 0;
		
		cachedSin = Math.sin(Math.toRadians(direction));
		cachedCos = Math.cos(Math.toRadians(direction));
	}
	
	public void update() {
		x += cachedSin * speed;
		y -= cachedCos * speed;
	}
	
	public abstract void render(Graphics g);
}
