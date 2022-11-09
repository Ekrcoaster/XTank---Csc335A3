package battle.tanks;

import java.awt.Color;

import battle.bullets.Bullet;
import battle.bullets.MagicBullet;
import battle.bullets.ScoutBullet;
import scenes.BattleScene;

public class MagicTank extends Tank {
	public MagicTank(String id, String name, boolean isServerControlled, BattleScene scene) {
		super(id, name, isServerControlled, scene);
		this.moveSpeed = 5;
		this.rotateSpeed = 7;
		this.health = 3;
		this.bulletSpeedCooldown = 0.2;
		this.size = 16;
	}

	@Override
	public Bullet shoot(IntPoint origin, double direction) {
		return new MagicBullet(scene, id, origin.x, origin.y, direction);
	}

	@Override
	public String getType() {
		return "magic";
	}
}
