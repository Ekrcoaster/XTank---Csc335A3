package battle.tanks;

import java.awt.Color;
import java.awt.Graphics;

import battle.bullets.Bullet;
import battle.bullets.SturdyBullet;
import scenes.BattleScene;

public class SturdyTank extends Tank {

	public SturdyTank(String id, String name, boolean isServerControlled, BattleScene scene) {
		super(id, name, isServerControlled, scene);
		this.health = 26;
		this.moveSpeed = 2.4;
		this.rotateSpeed = 3;
		this.bulletSpeedCooldown = 0.5;
		this.size = 25;
	}

	@Override
	public Bullet shoot(IntPoint origin, double direction) {
		return new SturdyBullet(scene, id, origin.x, origin.y, direction);
	}
	
	@Override
	protected void drawTankBody(Graphics g, Color color) {
		super.drawTankBody(g, color);

		g.setColor(dimColor(color, 0.4));
		double smallerSquareSize = 0.65;
		drawRotatedPoly(new double[][] {
			{-size * smallerSquareSize, -size * smallerSquareSize},
			{size * smallerSquareSize, -size * smallerSquareSize},
			{size * smallerSquareSize, size * smallerSquareSize},
			{-size * smallerSquareSize, size * smallerSquareSize}
		}, direction + 45, g);
	}

	@Override
	protected void drawTankGun(Graphics g, Color color, double gunLength) {
		g.setColor(color);
		
		drawRotatedPoly(new double[][] {
			{-size * 0.5, -size - size * gunLength},
			{size * 0.5, -size - size * gunLength},
			{size * 0.5, size - size},
			{-size * 0.5, size - size}
		}, direction, g);
	}

	@Override
	public String getType() {
		return "sturdy";
	}

}
