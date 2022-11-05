/*
 * Author: Ethan Rees
 * This class will setup the default window and will allow easily changing out the main panel to different
 * ui scenes
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class WindowHolder {
	public static JFrame frame;
	public static JPanel active;
	
	
	public static void setPanel(JPanel panel) {
		frame = new JFrame();
		
		Dimension size = new Dimension(900, 600);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize((int)size.getWidth(), (int)size.getHeight());
		frame.setPreferredSize(size);
		frame.setVisible(true);
		frame.setResizable(true);
        
		frame.setLayout(new BorderLayout());
        
		frame.getContentPane().removeAll();
		active = panel;
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		frame.pack();
	}
}
