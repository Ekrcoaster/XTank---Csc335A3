/*
 * Author: Ethan Rees
 * This is the default bullet, pretty generic
 */
package battle.bullets;

import java.awt.Color;
import java.awt.Graphics;

public class GenericBullet extends Bullet {

	public GenericBullet(String ownerID, double x, double y, double direction) {
		super(ownerID, x, y, direction);
		this.speed = 10;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect((int)Math.round(x), (int)Math.round(y), 5, 5);
	}

	@Override
	public String getType() { return "generic"; }
}
