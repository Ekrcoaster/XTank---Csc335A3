/*
 * Author: Ethan Rees
 * This is the default tank, pretty generic
 */
package battle.tanks;

import battle.bullets.Bullet;
import battle.bullets.GenericBullet;

public class GenericTank extends Tank {

	public GenericTank(String id) {
		super(id);
		this.health = 10;
		this.moveSpeed = 5;
		this.rotateSpeed = 3;
		this.size = 15;
		this.bulletSpeedCooldown = 0.5;
	}

	@Override
	public Bullet shoot(IntPoint origin, double direction) {
		return new GenericBullet(origin.x, origin.y, direction);
	}
}
