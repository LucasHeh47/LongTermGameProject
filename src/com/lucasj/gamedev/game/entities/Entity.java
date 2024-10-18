package com.lucasj.gamedev.game.entities;

import java.awt.Graphics;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.utils.RandomStringGenerator;

public abstract class Entity {
	
	protected Vector2D velocity;
	
	protected Vector2D position;
	protected int maxHealth;
	protected int health = maxHealth;
	protected int movementSpeed = 3;
	protected int size = 25;
	protected String tag;
	public int importance;
	
	protected Game game;
	
	public Entity(Game game) {
		this.game = game;
		maxHealth = 100;
		position = new Vector2D(0, 0);
		velocity = new Vector2D(0, 0);
		tag = RandomStringGenerator.generateRandomString(32, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
	}

	public Entity(Game game, Vector2D position, Vector2D velocity, int maxHealth, int movementSpeed, int size, String tag) {
		this.game = game;
		this.position = position;
		this.velocity = velocity;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.movementSpeed = movementSpeed * 1000;
		this.size = size;
		if(tag == null) tag = RandomStringGenerator.generateRandomString(32, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
		this.tag = tag;
	}
	
	public void instantiate() {
		game.toAddEntities.add(this);
	}
	
	public final boolean isPointColliding(Vector2D point) {
	    // Get the entity's position and size
	    double x = position.getX();
	    double y = position.getY();
	    double size = this.size;
	    
	    // Check if the point is within the bounds of the entity's rectangle
	    return (point.getX() >= x && point.getX() <= x + size &&
	            point.getY() >= y && point.getY() <= y + size);
	}
	
	public final boolean isCollidingWith(Entity other) {
	    // Check if this entity is colliding with another entity
	    return this.getPosition().getX() < other.getPosition().getX() + other.getSize()
	        && this.getPosition().getX() + this.getSize() > other.getPosition().getX()
	        && this.getPosition().getY() < other.getPosition().getY() + other.getSize()
	        && this.getPosition().getY() + this.getSize() > other.getPosition().getY();
	}

	public abstract void update(double deltaTime);
	public abstract void render(Graphics g);
	public abstract void entityDeath();
	public abstract void onEntityCollision(EntityCollisionEvent e);
	
	public final void die() {
		entityDeath();
		
		game.toRemoveEntities.add(this);
		System.out.println("KILLING ENTITY: " + this.getTag() + " | " + this.getClass().getTypeName());
	}
	
	public void takeDamage(int dmg) {
		this.setHealth(this.getHealth()-dmg);
	}
	
	public Vector2D getPosition() {
		return position;
	}

	public Entity setPosition(Vector2D position) {
		this.position = position;
		return this;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public Entity setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		return this;
	}

	public int getHealth() {
		return health;
	}

	public Entity setHealth(int health) {
		this.health = health;
		if(health <= 0) this.die();
		return this;
	}

	public int getMovementSpeed() {
		return movementSpeed;
	}

	public Entity setMovementSpeed(int movementSpeed) {
		this.movementSpeed = movementSpeed;
		return this;
	}

	public int getSize() {
		return size;
	}

	public Entity setSize(int size) {
		this.size = size;
		return this;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public Entity setVelocity(Vector2D velocity) {
		this.velocity = velocity;
		return this;
	}


	public String getTag() {
		return tag;
	}

	public Entity setTag(String tag) {
		this.tag = tag;
		return this;
	}
}
