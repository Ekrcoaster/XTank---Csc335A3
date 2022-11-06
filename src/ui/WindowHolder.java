/*
 * Author: Ethan Rees
 * This class will setup the default window and will allow easily changing out the _main panel to different
 * ui scenes
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import _main._Settings;

public class WindowHolder {
	public static JFrame frame;
	public static JPanel active;
	
	public static JPanel setPanel(JPanel panel) {
		if(frame == null) {
			frame = new JFrame();
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize((int)_Settings.windowSize.getWidth(), (int)_Settings.windowSize.getHeight());
			frame.setPreferredSize(_Settings.windowSize);
			frame.setVisible(true);
			frame.setResizable(false);
	        
			frame.setLayout(new BorderLayout());
		}
		
		frame.getContentPane().removeAll();
		active = panel;
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		frame.pack();
		
		return panel;
	}
}
