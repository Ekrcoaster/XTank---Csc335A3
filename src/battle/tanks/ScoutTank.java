package battle.tanks;

import java.awt.Color;
import java.awt.Graphics;

import battle.bullets.Bullet;
import battle.bullets.ScoutBullet;
import scenes.BattleScene;

public class ScoutTank extends Tank {

	public ScoutTank(String id, String name, boolean isServerControlled, BattleScene scene) {
		super(id, name, isServerControlled, scene);
		this.moveSpeed = 6;
		this.rotateSpeed = 6;
		this.health = 4;
		this.bulletSpeedCooldown = 2;
		this.size = 10;
	}
	
	@Override
	public Color getColor() {
		return dimColor(super.getColor(), 0.5);
	}

	@Override
	public Bullet shoot(IntPoint origin, double direction) {
		return new ScoutBullet(scene, id, origin.x, origin.y, direction);
	}

	@Override
	public String getType() {
		return "scout";
	}
}
