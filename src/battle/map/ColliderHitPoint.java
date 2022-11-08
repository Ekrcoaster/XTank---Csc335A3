/*
 * Author: Ethan Rees
 * This class represents the collision hit point, it has the new position, and some info about the hit
 */
package battle.map;

public class ColliderHitPoint {
	public double x, y;
	public boolean hit;
	public ColliderHitPoint(double x, double y) {
		this.x = x;
		this.y = y;
		this.hit = false;
	}
	@Override
	public String toString() {
		return "(" + this.x + ", " + y + ")";
	}
}
