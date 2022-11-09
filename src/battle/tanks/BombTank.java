package battle.tanks;

import java.awt.Color;

import battle.bullets.BombBullet;
import battle.bullets.Bullet;
import battle.bullets.MagicBullet;
import battle.bullets.ScoutBullet;
import scenes.BattleScene;

public class BombTank extends Tank {
	public BombTank(String id, String name, boolean isServerControlled, BattleScene scene) {
		super(id, name, isServerControlled, scene);
		this.moveSpeed = 3;
		this.rotateSpeed = 4;
		this.health = 12;
		this.bulletSpeedCooldown = 10;
		this.size = 23;
	}
	
	@Override public void update() {
		super.update();

		this.moveSpeed = 1.5 + ((bulletActiveCooldown / bulletSpeedCooldown))*4;
	}

	@Override
	public Bullet shoot(IntPoint origin, double direction) {
		return new BombBullet(scene, id, origin.x, origin.y, direction);
	}

	@Override
	public String getType() {
		return "bomb";
	}
}
