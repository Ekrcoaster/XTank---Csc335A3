/*
 * Author: Ethan Rees
 * This is the class that holds the UI and rendering for the battle gameplay,
 * it us updated and calls all of the drawing
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JPanel;

import battle.tanks.Tank;
import battle.bullets.*;

public class BattleBoardUI extends JPanel implements KeyListener{
	
	BufferStrategy bufferStrategy;
	Graphics graphics;
	HashSet<Integer> keysDown;
	Collection<Tank> tanks;
	HashSet<Bullet> bullets;
	
	public BattleBoardUI() {
		this.tanks = new ArrayList<Tank>();
		this.bullets = new HashSet<Bullet>();
		this.keysDown = new HashSet<Integer>();
		setLayout(new BorderLayout(0, 0));
		
		setFocusable(true);
		
		requestFocusInWindow();
		requestFocus();

		addKeyListener(this);
	}

	public void render(Collection<Tank> tanks, HashSet<Bullet> bullets) {
		this.tanks = tanks;
		this.bullets = bullets;
		if(!isFocusOwner()) {
			requestFocusInWindow();
			requestFocus();
		}
		
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for(Tank tank : tanks) {
			tank.render(g);
		}
		for(Bullet bullet : bullets) {
			bullet.render(g);
		}
	}
	
	public HashSet<Integer> getKeysDown() {
		return this.keysDown;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		keysDown.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown.remove(e.getKeyCode());
	}
}
