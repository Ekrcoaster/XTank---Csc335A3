/*
 * Author: Ethan Rees
 * This class will setup the default window and will allow easily changing out the _main panel to different
 * ui scenes
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import _main.Boot;

public class WindowManager {
	public static JFrame frame;
	public static JPanel active;
	
	public static JPanel setPanel(JPanel panel) {
		if(frame == null) {
			frame = new JFrame();
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize((int)Boot.windowSize.getWidth(), (int)Boot.windowSize.getHeight());
			frame.setPreferredSize(Boot.windowSize);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setTitle("Csc 335 - X Tank Game (Ethan Rees)");
	        
			frame.setLayout(new BorderLayout());
		}
		
		frame.getContentPane().removeAll();
		active = panel;
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		frame.pack();
		
		return panel;
	}
	
	public static void closeWindow() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
