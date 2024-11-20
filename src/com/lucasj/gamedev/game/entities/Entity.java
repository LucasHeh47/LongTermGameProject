package com.lucasj.gamedev.game.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEventListener;
import com.lucasj.gamedev.events.entities.EntityDamagedEvent;
import com.lucasj.gamedev.events.entities.EntityDeathEvent;
import com.lucasj.gamedev.game.entities.npc.NPC;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.utils.RandomStringGenerator;

public abstract class Entity implements EntityCollisionEventListener {
	
	protected Vector2D velocity;
	
	protected Vector2D screenPosition;
	protected Vector2D position;
	protected float maxHealth;
	protected float health = maxHealth;
	protected int movementSpeed = 3;
	protected int size = 25;
	protected String tag;
	public int importance;
	protected boolean isAlive;
	
	private Entity killer;
	
	protected Game game;
	
	public Entity(Game game) {
		this.game = game;
		maxHealth = 100;
		isAlive = true;
		position = new Vector2D(0, 0);
		velocity = new Vector2D(0, 0);
		this.screenPosition = new Vector2D(0, 0);
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
		this.screenPosition = new Vector2D(0, 0);
		if(tag == null) tag = RandomStringGenerator.generateRandomString(32, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
		this.tag = tag;
		isAlive = true;
	}
	
	public void instantiate() {
		game.instantiatedEntities.add(this);
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
	
	public final boolean isCollidingWithEntity() {
	    for (Entity entity : game.instantiatedEntities.toList()) {
	        if (entity != this && isCollidingWith(entity)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public final boolean isCollidingWith(Entity other) {
	    double thisLeft = this.getScreenPosition().getX();
	    double thisRight = thisLeft + this.getSize();
	    double thisTop = this.getScreenPosition().getY();
	    double thisBottom = thisTop + this.getSize();

	    double otherLeft = other.getScreenPosition().getX();
	    double otherRight = otherLeft + other.getSize();
	    double otherTop = other.getScreenPosition().getY();
	    double otherBottom = otherTop + other.getSize();

	    boolean isColliding = thisLeft < otherRight && thisRight > otherLeft
	        && thisTop < otherBottom && thisBottom > otherTop;

	    return isColliding;
	}
	
	public final boolean isCollidingWith(Vector2D pos, Vector2D size) {
		double thisLeft = this.getPosition().getX();
	    double thisRight = thisLeft + this.getSize();
	    double thisTop = this.getPosition().getY();
	    double thisBottom = thisTop + this.getSize();

	    double otherLeft = pos.getX();
	    double otherRight = otherLeft + size.getX();
	    double otherTop = pos.getY();
	    double otherBottom = otherTop + size.getY();

	    // Check if the rectangles overlap
	    return thisLeft < otherRight && thisRight > otherLeft
	        && thisTop < otherBottom && thisBottom > otherTop;
	}
	
	public final boolean isCollidingWith(Vector2D point) {
		// Get the entity's position and size
	    double entityX = this.getPosition().getX();
	    double entityY = this.getPosition().getY();
	    double entityWidth = this.getSize();
	    double entityHeight = this.getSize(); // Assuming the entity is a square

	    // Check if the point's coordinates are within the entity's bounds
	    return point.getX() >= entityX 
	        && point.getX() <= entityX + entityWidth
	        && point.getY() >= entityY
	        && point.getY() <= entityY + entityHeight;
	}

	public final boolean isCollidingWithScreen(Vector2D point) {
		// Get the entity's position and size
	    double entityX = this.getScreenPosition().getX();
	    double entityY = this.getScreenPosition().getY();
	    double entityWidth = this.getSize();
	    double entityHeight = this.getSize(); // Assuming the entity is a square

	    // Check if the point's coordinates are within the entity's bounds
	    return point.getX() >= entityX 
	        && point.getX() <= entityX + entityWidth
	        && point.getY() >= entityY
	        && point.getY() <= entityY + entityHeight;
	}
	
	public void update(double deltaTime) {
		screenPosition = game.getCamera().worldToScreenPosition(position);
	}
	
	public void render(Graphics g) {
		 Graphics2D g2d = (Graphics2D) g; // Cast to Graphics2D for better rendering control

	    // Set the shadow color with transparency
	    g2d.setColor(new Color(56, 56, 56, 120));

	    // Calculate shadow size and position based on entity size and position
	    int shadowWidth = (int) (this.size * 0.8); // Shadow slightly smaller than entity size
	    int shadowHeight = (int) (this.size * 0.2); // Flattened shadow
	    int shadowX = (int) this.screenPosition.getX() + (this.size - shadowWidth) / 2; // Centered under entity
	    int shadowY = (int) (this.screenPosition.getY() + this.size * 0.9); // Slightly below the entity

	    // Draw the shadow
	    g2d.fillOval(shadowX, shadowY, shadowWidth, shadowHeight);
	}
	public abstract void entityDeath();
	
	public final void die() {
		entityDeath();
		isAlive = false;
		EntityDeathEvent e = new EntityDeathEvent(this, killer);
		game.getEventManager().dispatchEvent(e);
		game.instantiatedEntities.remove(this);
		System.out.println("KILLING ENTITY: " + this.getTag() + " | " + this.getClass().getSimpleName());
	}
	
	public boolean takeDamage(float dmg) {
		if(this instanceof NPC) return false;
		this.setHealth(this.getHealth()-dmg);
		EntityDamagedEvent e = new EntityDamagedEvent(this, this.killer, dmg);
		game.getEventManager().dispatchEvent(e);
		if(health <= 0) {
			this.die();
			return true;
		}
		return false;
	}
	
	public Vector2D getPosition() {
		return position;
	}

	public Entity setPosition(Vector2D position) {
		this.position = position;
		return this;
	}
	
	public Vector2D getScreenPosition() {
		return screenPosition;
	}

	public Entity setScreenPosition(Vector2D position) {
		this.screenPosition = position;
		return this;
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public Entity setMaxHealth(float maxHealth) {
		this.maxHealth = maxHealth;
		return this;
	}

	public float getHealth() {
		return health;
	}

	public Entity setHealth(float health) {
		this.health = health;
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

	public boolean isAlive() {
		return isAlive;
	}

	public void setKiller(Entity killer) {
		this.killer = killer;
	}
}
