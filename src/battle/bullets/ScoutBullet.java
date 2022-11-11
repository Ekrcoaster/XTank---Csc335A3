/*
 * Author: Ethan Rees
 * Scout bullets are very strong but cannot travel far at all
 */
package battle.bullets;

import java.awt.Color;
import java.awt.Graphics;

import battle.map.ColliderHitPoint;
import battle.tanks.Tank;
import scenes.BattleScene;

public class ScoutBullet extends Bullet {

	int maxDistance;
	public ScoutBullet(BattleScene scene, String ownerID, double x, double y, double direction) {
		super(scene, ownerID, x, y, direction);
		this.damage = 7;
		this.speed = 8;
		this.maxDistance = 200;
		this.collisionRadius = 5;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.red);
		int bulletSize = (int)(5 * (1-(distanceTravelled / maxDistance)));
		g.fillOval((int)x, (int)y, bulletSize, bulletSize);
	}
	
	@Override
	public void update() {
		super.update();
		if(distanceTravelled > maxDistance)
			destroy();
	}

	@Override
	public void onMapCollision(ColliderHitPoint point) {
		destroy();
	}

	@Override
	public void onTankCollision(Tank tank) {
		destroy();
	}

	@Override
	public String getType() {
		return "scout";
	}
}
