package com.lucasj.gamedev.game.entities.placeables;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;

public abstract class Placeable extends Entity {
	
	protected long startTime;
	
	protected Player player;
	protected int enemiesKilled = 0;
	protected float damage;

	private boolean placed;
	
	public Placeable(Game game, Player player, Vector2D position, Vector2D velocity, int maxHealth, int size, String tag) {
		super(game, position, velocity, maxHealth, 0, size, tag);
		this.player = player;
	}
	
	@Override
	public boolean takeDamage(float dmg) {
		if(this instanceof Turret) super.takeDamage(dmg);
		return false;
	}
	
	public void render(Graphics g) {
		// Define the fixed health bar width and height
		int healthBarWidth = 40;  // Fixed width for all entities
		int healthBarHeight = 6;  // Fixed height for all entities

		// Calculate position based on entity size
		int healthBarX = (int) (screenPosition.getX() + (size/2) - (healthBarWidth / 2));
		int healthBarY = (int) (screenPosition.getY() - (size * 0.05));

		// Draw the background of the health bar (black)
		g.setColor(Color.black);
		g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

		// Draw the health bar based on the entity's current health (green)
		g.setColor(Color.green);
		g.fillRect(healthBarX, healthBarY, (int) (healthBarWidth * ((double) health / maxHealth)), healthBarHeight);
	}
	
	public abstract Image getImage();
	
	public void instantiate(Vector2D position) {
		this.position = position;
		this.player.getActivePlaceables().add(this);
		startTime = System.currentTimeMillis();
	}
	
	public void uninstantiate(Vector2D position) {
		this.position = position;
		this.player.getActivePlaceables().remove(this);
		startTime = 0;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public int getEnemiesKilled() {
		return enemiesKilled;
	}

	public void setEnemiesKilled(int enemiesKilled) {
		this.enemiesKilled = enemiesKilled;
	}

	public boolean isPlaced() {
		return placed;
	}

	public void setPlaced(boolean placed) {
		this.placed = placed;
	}
}
