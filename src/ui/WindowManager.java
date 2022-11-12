/*
 * Author: Ethan Rees
 * This class will setup the default window and will allow easily changing out the _main panel to different
 * ui scenes
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import _main.Boot;

public class WindowManager {
	public static JFrame frame;
	public static JPanel active;
	static Container container;
	
	/*
	 * Set the active panel
	 */
	public static JPanel setPanel(JPanel panel) {
		// if the frame doesn't exist yet, create it
		if(frame == null) {
			frame = new JFrame();
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize((int)Boot.windowSize.getWidth(), (int)Boot.windowSize.getHeight());
			frame.setPreferredSize(Boot.windowSize);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setTitle("Csc 335 - XTank Game (Ethan Rees)");
	        
			frame.setLayout(new BorderLayout());
			
			container = frame.getContentPane();
			
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent event) {
					Boot.closeAllNetworks();
				}
			});
		}
		
		// otherwise replace the contents
		container.removeAll();
		container.repaint();
		active = panel;
		container.add(panel, BorderLayout.CENTER);
		
		frame.pack();
		
		return panel;
	}
	
	/*
	 * Close the window
	 */
	public static void closeWindow() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
