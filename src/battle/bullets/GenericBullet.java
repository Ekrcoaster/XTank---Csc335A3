/*
 * Author: Ethan Rees
 * This is the default bullet, pretty generic
 */
package battle.bullets;

import java.awt.Color;
import java.awt.Graphics;

import battle.map.ColliderHitPoint;
import battle.tanks.Tank;
import scenes.BattleScene;

public class GenericBullet extends Bullet {

	public GenericBullet(BattleScene scene, String ownerID, double x, double y, double direction) {
		super(scene, ownerID, x, y, direction);
		this.speed = 10;
		this.damage = 1;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect((int)Math.round(x), (int)Math.round(y), 5, 5);
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
	public String getType() { return "generic"; }

}
