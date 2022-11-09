package ui;

import java.awt.Color;
import java.awt.Graphics;

public interface Renderable {
	public void render(Graphics g);

	/* a nice handy function any renderable would appreciate*/
	public default Color dimColor(Color color, double percentage) {
		return new Color((int)(color.getRed()*percentage), (int)(color.getGreen() * percentage), (int)(color.getBlue() * percentage));
	}
}
