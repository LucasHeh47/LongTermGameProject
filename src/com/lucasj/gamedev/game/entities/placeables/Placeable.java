package com.lucasj.gamedev.game.entities.placeables;

import java.awt.Color;
import java.awt.Graphics;

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
		return false;
	}
	
	public void render(Graphics g) {
		// Health bar
		g.setColor(Color.black);
	    g.fillRect((int) (screenPosition.getX() - (size * 0.25)), (int) (screenPosition.getY() - (size * 0.6)),
	               (int) (size + (size * 0.5)), (int) (size + (size * 0.5)) / 5);

	    g.setColor(Color.green);
	    g.fillRect((int) (screenPosition.getX() - (size * 0.25)), (int) (screenPosition.getY() - (size * 0.6)),
	               (int) ((size + (size * 0.5)) * ((double) health / maxHealth)),
	               (int) (size + (size * 0.5)) / 5);
	}
	
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
