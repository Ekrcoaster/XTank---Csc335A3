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
		this.damage = 1;
		this.speed = 5;
		this.collisionRadius = 5;
		this.pullinSpeed = 800;
		
		bounceCooldown = 0;
		recalculateTarget();
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.blue);
		int bulletSize = (int)(Math.random()*3)+5;
		g.fillOval((int)(x-bulletSize*0.5), (int)(y-bulletSize*0.5), bulletSize, bulletSize);
	}
	
	@Override
	public void update() {

		if(target != null && target.isDead())
			recalculateTarget();
		
		if(target != null) {
			double desiredDirection = Math.atan2(target.getY()-y, target.getX() - x);
			desiredDirection = Math.toDegrees(desiredDirection)+90;
			double difference = shortestAngle(desiredDirection, direction);
			
			double timeInfluence = Math.min(1, distanceTravelled / pullinSpeed);
			if(timeInfluence == 1)
				setDirection(desiredDirection);
			else
				setDirection(direction + difference * 0.5 * timeInfluence);
		} else if(distanceTravelled >pullinSpeed){
			destroy();
		}
		
		if(bounceCooldown > 0)
			bounceCooldown--;
		
		super.update();
	}
	
	void recalculateTarget() {
		target = null;
		double distance = 9999;
		for(Tank tank : scene.players.values()) {
			double d = calculateDistance(tank);
			if(d < distance && !tank.getID().equals(ownerID) && !tank.isDead()) {
				target = tank;
				distance = d;
			}
		}
	}
	
	double shortestAngle(double a, double b) {
		if(Math.abs(a - b) > 180) {
			if(a > b)
				b += 360;
			else
				a += 360;
		}
		return a - b;
	}
	
	double calculateDistance(Tank tank) {
		return Math.sqrt((tank.getX() - x)*(tank.getX() - x) + (tank.getY() - y)*(tank.getY() - y));
	}

	@Override
	public void onMapCollision(ColliderHitPoint point) {
		x = point.x;
		y = point.y;
		if(bounceCooldown <= 0) {
			setDirection(180 - direction);
			damage *= 0.9;
		}
		else
			destroy();
		bounceCooldown = 5;
		//destroy();
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
