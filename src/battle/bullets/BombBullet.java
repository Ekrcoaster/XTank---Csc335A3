package battle.bullets;

import java.awt.Color;
import java.awt.Graphics;

import battle.map.ColliderHitPoint;
import battle.tanks.Tank;
import scenes.BattleScene;

public class BombBullet extends Bullet {

	public BombBullet(BattleScene scene, String ownerID, double x, double y, double direction) {
		super(scene, ownerID, x-10, y-10, direction);
		this.speed = 6;
		this.damage = 10;
		this.collisionRadius = 20;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(((int)(distanceTravelled / 10)) % 2 == 0 ? Color.gray : Color.white);
		g.fillOval((int)Math.round(x), (int)Math.round(y), 20, 20);
	}

	@Override
	public String getType() { return "bomb"; }

	@Override
	public void onMapCollision(ColliderHitPoint point) {
		destroy();
	}

	@Override
	public void onTankCollision(Tank tank) {
		// balancing
		if(tank.getType().equals("sturdy"))
			this.damage = 20;
		destroy();
	}
}
