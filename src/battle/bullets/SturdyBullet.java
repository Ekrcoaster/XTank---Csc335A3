/*
 * Author: Ethan Rees
 * Almost a near copy of generic bullets, but a bit stronger
 */
package battle.bullets;

import java.awt.Color;
import java.awt.Graphics;

import battle.map.ColliderHitPoint;
import battle.tanks.Tank;
import scenes.BattleScene;

public class SturdyBullet extends Bullet {

	public SturdyBullet(BattleScene scene, String ownerID, double x, double y, double direction) {
		super(scene, ownerID, x, y, direction);
		this.speed = 6;
		this.damage = 2;
		this.collisionRadius = 10;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillOval((int)Math.round(x)-5, (int)Math.round(y)-5, 10, 10);
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
	public String getType() { return "sturdy"; }
}
