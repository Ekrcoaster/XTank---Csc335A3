/*
 * Author: Ethan Rees
 * This is the class that holds the UI and rendering for the battle gameplay,
 * it us updated and calls all of the drawing
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JPanel;

import _main.Boot;
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
	
	int frame;
	String notification;
	int framesSinceShownBulletCooldown;
	Color notificationColor;
	boolean largeNotification;
	
	public BattleBoardUI(BattleScene scene) {
		this.renderQueue = new ArrayList<Renderable>();
		this.keysDown = new HashSet<Integer>();
		this.scene = scene;
		this.notification = "Ready... Set... Fight!";
		this.frame = 0;
		this.framesSinceShownBulletCooldown = 0;
		this.notificationColor = Color.white;
		this.largeNotification = false;
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
	
	public void sendNotification(String notification, Color color, boolean large) {
		this.notification = notification;
		this.frame = 25;
		this.notificationColor = color;
		this.largeNotification = large;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		// draw backdrop
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// render each renderable
		for(Renderable item : renderQueue) {
			item.render(g);
		}
		
		// draw the leaderboard
		
		// calculate the longest width and add the tanks to the list
		int longestWidth = 0;
		ArrayList<Tank> playList = new ArrayList<Tank>();
		for(Tank tank : scene.players.values()) {
			playList.add(tank);
			double width = getFontMetrics(getFont()).stringWidth(drawLeaderboardLine(scene.players.size(),tank));
			if(width > longestWidth)
				longestWidth = (int)Math.ceil(width);
		}
		
		// sort that list by the tank's damage amount
		Collections.sort(playList);
		
		// draw the leaderboard, draw a black square first
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(0, 0, longestWidth+20, 18 * scene.players.size()+10);
		// draw the players in order
		for(int i = 0; i < playList.size(); i++) {
			Tank tank = playList.get(i);
			g.setColor(tank.isDead() ? Color.gray : Color .white);
			g.drawString(drawLeaderboardLine(i, tank), 10, i * 18 + 20);
		}
		
		// draw the large notification
		if(largeNotification) {
			Font font = new Font("Arial", Font.BOLD, 64);
			int width = getFontMetrics(font).stringWidth(notification);
			g.setFont(font);
			if(frame < 60) {
				g.setColor(frame/5 % 2 == 0 ? Color.black : notificationColor);
			} else {
				g.setColor(notificationColor);
			}
			g.drawString(notification, (int)(Boot.windowSize.width * 0.47 - width * 0.5), (int)(Boot.windowSize.height * 0.5));
		} else {
			// draw the bottom notification
			int width = 100;
			if(frame < 60) {
				g.setColor(frame/5 % 2 == 0 ? Color.black : notificationColor);
			} else {
				g.setColor(notificationColor);
			}
			g.drawString(notification, (int)(Boot.windowSize.width * 0.47 - width * 0.5), (int)(Boot.windowSize.height - 50));
		}
		
		
		// draw the bullet cooldown
		if(scene.playerID != null) {
			Tank clientTank = scene.players.get(scene.playerID);
			
			// if the tank is on cooldown OR it is fading out
			if(clientTank.getBulletCooldownPercent() < 1 || framesSinceShownBulletCooldown > 0) {
				
				// if it is just cooldowning, set the fade frames
				if(clientTank.getBulletCooldownPercent() < 1)
					framesSinceShownBulletCooldown = 60;
				
				// draw the cooldown progress bar
				Color color = new Color(105, 105, 105, (int)(255 * (framesSinceShownBulletCooldown / (double)60)));
				g.setColor(color);
				g.drawString("Cooldown:", Boot.windowSize.width - 130, Boot.windowSize.height - 60);
				g.drawRect(Boot.windowSize.width - 130, Boot.windowSize.height - 55, 100, 5);
				g.fillRect(Boot.windowSize.width - 130, Boot.windowSize.height - 55, (int)(100 * clientTank.getBulletCooldownPercent()), 5);
			}
		}
		
		frame++;
		if(framesSinceShownBulletCooldown > 0)
			framesSinceShownBulletCooldown--;
	}
	
	String drawLeaderboardLine(int i, Tank tank) {
		return (i+1) + ") " + tank.getName() + ": " + tank.damageDealt + (tank.isDead() ? " (dead)" : "");
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
