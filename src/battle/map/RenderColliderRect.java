/*
 * Author: Ethan Rees
 * This class is the same as a collider rect, but this time it can be rendered!
 */
package battle.map;

import java.awt.Color;
import java.awt.Graphics;

import ui.Renderable;

public class RenderColliderRect extends ColliderRect implements Renderable {

	public Color color;
	public RenderColliderRect(double x1, double y1, double x2, double y2, Color color) {
		super(x1, y1, x2, y2);
		this.color = color;
	}

	public void render(Graphics g) {
		g.setColor(color);
		int buffer = 5;
		g.drawRect((int)Math.round(x1)+5, (int)Math.round(y1)+5, (int)Math.round(x2-x1)-buffer*2, (int)Math.round(y2-y1)-buffer*2);
		g.drawRect((int)Math.round(x1), (int)Math.round(y1), (int)Math.round(x2-x1), (int)Math.round(y2-y1));
	}
}
