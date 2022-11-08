/*
 * Author: Ethan Rees
 * This class represents a collider rect, it will calculate the math required for collisions
 */
package battle.map;

public class ColliderRect {

	protected double x1, y1, x2, y2;

	public ColliderRect(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/*
	 * When given a point, check if this point is inside myself
	 */
	public boolean isInside(double x, double y) {
		return isInside(x, y, 0, 0);
	}
	/*
	 * When given a rectangle, check if this rectangle overlaps with myself
	 */
	public boolean isInside(double x, double y, double width, double height) {
		return (x > this.x1 || x+width > this.x1) && 
				(y > this.y1 || y+height > this.y1) && 
				(x < this.x2 || x+width < this.x2) && 
				(y < this.y2 || y+height < this.y2);
	}

	/*
	 * This will check if a rect is inside of myself, if so it'll give the proper position to make sure
	 * it gets shifted out of the way
	 */
	public ColliderHitPoint snapToEdgePoint(ColliderHitPoint p, double width, double height) {
		// no worries here, just return its og position
		if(!isInside(p.x, p.y, width, height))
			return p;

		// uh oh, it overlaps a bit, now try to find the problematic part
		// 0: north, 1: east, 2: south, 3:west
		double[] overlapDepths = {
				Math.min(y1 - (p.y + height), 0), // (-infinity, 0] north, how much the rect is intersecting on north
				Math.min(p.x - x2, 0), // (-infinity, 0] east, how much the rect is intersecting on east
				Math.min(p.y - y2, 0), // (-infinity, 0] south, how much the rect is intersecting on south
				Math.min(x1 - (p.x + width), 0), // (-infinity, 0] north, how much the rect is intersecting on north
		};

		// figure out the closest way to get the shape out of myself
		int largestIndex = 0;
		double largestValue = overlapDepths[0];
		for(int i = 1; i < overlapDepths.length; i++) {
			if(overlapDepths[i] > largestValue) {
				largestIndex = i;
				largestValue = overlapDepths[i];
			}
		}

		// how much should be shifted
		double shiftAmount = overlapDepths[largestIndex];
		// for North and East, it should ADD to the position
		if(largestIndex == 1 || largestIndex == 2)
			shiftAmount = 0-shiftAmount;

		// y is the best option
		if(largestIndex % 2 == 0) {
			p.y += shiftAmount;
		} else {
			p.x += shiftAmount;
		}

		p.hit = true;

		return p;
	}

	/*
	 * This will check if a rect is inside of myself, if so it'll give the proper position to make sure
	 * it gets shifted out of the way
	 */
	public ColliderHitPoint snapToEdgePoint(double x, double y, double width, double height) {
		return snapToEdgePoint(new ColliderHitPoint(x, y), width, height);
	}
}