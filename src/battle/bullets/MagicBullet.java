/*
 * Author: Ethan Rees
 * This is a magic bullet, it will find the nearest tank and rotate towards it
 */
package battle.bullets;

import java.awt.Color;
import java.awt.Graphics;

import battle.map.ColliderHitPoint;
import battle.tanks.Tank;
import scenes.BattleScene;

public class MagicBullet extends Bullet {
	
	Tank target;
	int bounceCooldown;
	int pullinSpeed;

	public MagicBullet(BattleScene scene, String ownerID, double x, double y, double direction) {
		super(scene, ownerID, x, y, direction);
		this.damage = 0.5;
		this.speed = 5;
		this.collisionRadius = 5;
		this.pullinSpeed = 800;
		
		bounceCooldown = 0;
		recalculateTarget();
	}

	@Override
	public void update() {
		// recalculate the target if the existing target is dead
		if(target != null && target.isDead())
			recalculateTarget();
		
		// if the target is not dead, rotate towards it
		if(target != null) {
			// first calculate the ideal direction (degrees) the bullet should face to meet the target using atan2
			double desiredDirection = Math.atan2(target.getY()-y, target.getX() - x);
			desiredDirection = Math.toDegrees(desiredDirection)+90;
			
			// next, find the difference between my current direction and the desired direction
			double difference = shortestAngle(desiredDirection, direction);
			
			// next, figure out how long i have been travelling for and if it has been longer
			// than the pullin speed, just set the direction to the desired direction
			// this helps prevent bullets from spinning in a circle forever
			double timeInfluence = Math.min(1, distanceTravelled / pullinSpeed);
			if(timeInfluence == 1)
				setDirection(desiredDirection);
			else
				// otherwise, slowly inch towards the desired direction
				setDirection(direction + difference * 0.5 * timeInfluence);
			
		// otherwise, there is no target, so if the bullet has travelled far enough, kill it
		} else if(distanceTravelled >pullinSpeed){
			destroy();
		}
		
		// reduce the bounce cooldown
		if(bounceCooldown > 0)
			bounceCooldown--;
		
		super.update();
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.blue);
		int bulletSize = (int)(Math.random()*4)+5;
		g.fillOval((int)(x-bulletSize*0.5), (int)(y-bulletSize*0.5), bulletSize, bulletSize);
	}
	
	/*
	 * This will scan the map and find the nearest tank that isn't my owner
	 */
	void recalculateTarget() {
		target = null;
		double distance = Double.MAX_VALUE;
		for(Tank tank : scene.players.values()) {
			double d = calculateDistance(tank);
			if(d < distance && !tank.getID().equals(ownerID) && !tank.isDead()) {
				target = tank;
				distance = d;
			}
		}
	}
	
	/*
	 * This will find the difference between 2 angles, it has to be complex
	 * because the difference between 370 and 0 is 10 degrees, for example
	 */
	double shortestAngle(double angleA, double angleB) {
		if(Math.abs(angleA - angleB) > 180) {
			if(angleA > angleB) angleB += 360;
			else angleA += 360;
		}
		return angleA - angleB;
	}
	
	/*
	 * This will just calculate the distance between a given tank and myself using the distance function.
	 * Because we don't care about the real distance, the sqrt is removed to help optimize
	 * This won't effect the code because 6 > 3, just as sqrt(6) > sqrt(3)
	 */
	double calculateDistance(Tank tank) {
		return (tank.getX() - x)*(tank.getX() - x) + (tank.getY() - y)*(tank.getY() - y);
	}

	@Override
	public void onMapCollision(ColliderHitPoint point) {
		x = point.x;
		y = point.y;
		
		// check if we are allowed to bounce, if so, bounce
		if(bounceCooldown <= 0) {
			setDirection(180 - direction);
			damage *= 0.9;
		}
		else // otherwise destroy myself
			destroy();
		
		bounceCooldown = 5;
	}

	@Override
	public void onTankCollision(Tank tank) {
		destroy();
	}

	@Override
	public String getType() {
		return "magic";
	}

}
