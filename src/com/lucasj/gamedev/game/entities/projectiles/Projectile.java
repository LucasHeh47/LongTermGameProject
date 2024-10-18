package com.lucasj.gamedev.game.entities.projectiles;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;

public abstract class Projectile extends Entity {

	private long timeCreated;
	private double timeToLive;
	
	private int damage;
	private Entity sender;
	
	public Projectile(Game game, Entity sender, Vector2D position, Vector2D velocity, int maxHealth, int size,
			String tag, float timeToLive, int damage) {
		super(game, position, velocity, maxHealth, 0, size, tag);
		timeCreated = System.currentTimeMillis();
		this.timeToLive = timeToLive;
		this.sender = sender;
		this.damage = damage;
		importance = 3;
	}
	public Projectile(Game game, Entity sender, Vector2D position, Vector2D velocity, int size,
			String tag, float timeToLive, int damage) {
		super(game, position, velocity, 0, 0, size, tag);
		timeCreated = System.currentTimeMillis();
		this.timeToLive = timeToLive * 1000;
		this.sender = sender;
		this.damage = damage;
		importance = 3;
	}
	
	public void update(double deltaTime) {
		updateProjectile(deltaTime);
		if(timeToLive != -1) {
			if((System.currentTimeMillis() - timeCreated)/1000.0 >= timeToLive) {
				this.die();
			}
		}
	}
	
	/**
	 * Called each frame before projectile is checked for TTL
	 * 
	 * @param deltaTime
	 */
	public abstract void updateProjectile(double deltaTime);
	
	public Entity getSender() {
		return sender;
	}
	
	public int getDamage() {
		return damage;
	}

}
