/*
 * Author: Ethan Rees
 * This class is the same as a collider rect, but this time it can be rendered!
 */
package battle.map;

import java.awt.Color;
import java.awt.Graphics;

import ui.Renderable;

public class RenderColliderRect extends ColliderRect implements Renderable {

	int time;
	public Color color;
	public RenderColliderRect(double x1, double y1, double x2, double y2, Color color) {
		super(x1, y1, x2, y2);
		this.color = color;
		this.time = 0;
	}

	/*
	 * Render the collider
	 */
	public void render(Graphics g) {
		time += 1;
		
		// draw the main shape
		g.setColor(color);
		g.drawRect((int)Math.round(x1), (int)Math.round(y1), (int)Math.round(x2-x1), (int)Math.round(y2-y1));

		// dim it and draw the shape slightly smaller
		g.setColor(dimColor(color, 0.6));
		int buffer = 5;
		g.drawRect((int)Math.round(x1)+5, (int)Math.round(y1)+5, (int)Math.round(x2-x1)-buffer*2, (int)Math.round(y2-y1)-buffer*2);

		// finally, draw the squigly lines that are inside the shape
		double offset = (Math.sin(time * 0.3 + x1) * 4) - 2;
		for(double y = y1+buffer; y < y2 - 20; y += 20) {

			g.setColor(dimColor(color, 0.3));
			g.drawLine((int)x1+buffer+2, (int)(y+10+offset), (int)x2-buffer-2, (int)(y+15+offset));
		}
	}
}
