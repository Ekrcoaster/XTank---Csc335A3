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
import scenes.BattleScene;
import battle.bullets.*;
import battle.map.BattleMap;
import battle.map.RenderColliderRect;

public class BattleBoardUI extends JPanel implements KeyListener{
	
	BufferStrategy bufferStrategy;
	Graphics graphics;
	HashSet<Integer> keysDown;
	ArrayList<Renderable> renderQueue;
	
	BattleScene scene;
	
	public BattleBoardUI(BattleScene scene) {
		this.renderQueue = new ArrayList<Renderable>();
		this.keysDown = new HashSet<Integer>();
		this.scene = scene;
		setLayout(new BorderLayout(0, 0));
		
		setFocusable(true);
		
		requestFocusInWindow();
		requestFocus();

		addKeyListener(this);
	}

	public void render(ArrayList<Renderable> renderQueue) {
		this.renderQueue = renderQueue;
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
		
		for(Renderable item : renderQueue) {
			item.render(g);
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
		if(e.getKeyChar() == 'e') {
			for(Renderable item : scene.map.getRenderables()) {
				scene.removeFromRenderQueue(item);
			}
			scene.map = new BattleMap(scene.map.getMapName(), scene.map.getMapWidth(), scene.map.getMapHeight());

			for(Renderable item : scene.map.getRenderables()) {
				scene.addToStartRenderQueue(item);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown.remove(e.getKeyCode());
	}
}
