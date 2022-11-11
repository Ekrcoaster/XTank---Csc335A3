/*
 * Author: Ethan Rees
 * This will draw a tank type onto the screen, as a selectable panel! It does this by getting an instance of a tank,
 * isolating it and making it functionless, then using its render function to render to the screen
 */
package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

import battle.tanks.Tank;

public class TankTypePreviewUI extends JPanel {
	
	Tank renderInstance;
	public TankTypePreviewUI(String name, String description, Tank renderInstance, int x, int y, int width, int height) {
		this.renderInstance = renderInstance;	
		// setup panel
		setBounds(x, y, width, height);
		setLayout(null);
		setSelected(false);
		setFocusable(true);

		// tank name
		JLabel label = new JLabel(name);
	    label.setFont(new Font("TimesRoman", Font.BOLD, 20));
		label.setBounds(height, 10, width - height, 25);
		add(label);
		
		// tank description
		JLabel descLabel = new JLabel("<html>" + description + "</html>");
		descLabel.setFont(getFont().deriveFont(Font.PLAIN));
		descLabel.setBounds(height, 25, width - height, height-25);
		add(descLabel);
		
		// the tank preview window
		int margin = 10;
		renderInstance.setX((height-margin*2) / 2);
		renderInstance.setY((height-margin) / 2);
		renderInstance.setSize(15);
		TankPreview preview = new TankPreview(renderInstance, height-margin);
		preview.setBounds(margin, margin, height-margin*2, height-margin*2);
		add(preview);
	}
	
	/*
	 * Set if it should be rendered as selected or not
	 */
	public void setSelected(boolean selected) {
		setBorder(BorderFactory.createLineBorder(selected ? Color.blue : Color.black, selected ? 4 : 2));
	}
	
	public String getType() {
		return renderInstance.getType();
	}
}

/*
 * This class is the class that draws the display, 
 * it overrides the jpanel and it just renders the tank into the space
 */
class TankPreview extends JPanel {
	Tank tank;
	int size;
	public TankPreview(Tank tank, int size) {
		this.tank = tank;
		this.size = size;
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, size, size);
		tank.render(g);
	}
}