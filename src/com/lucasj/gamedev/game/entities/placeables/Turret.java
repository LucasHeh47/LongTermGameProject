package com.lucasj.gamedev.game.entities.placeables;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.events.player.PlayerAttackEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.entities.projectiles.Bullet;
import com.lucasj.gamedev.game.gamemodes.waves.WavesManager;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class Turret extends Placeable {
	
	private long lastTick;
	
	private double angle;
	private Image img;
	
	private Enemy nearestEnemy;
	
	private long lastAttack;
	private float attackSpeed = 0.07f;
	private float bulletSpeed = 75;
	private double rotationSpeed = 2;
	private float bloom = 0.25f;
	
	/***
	 * @param game
	 * @param player
	 * @param position
	 * @param velocity
	 * @param secondsAlive
	 * @param size
	 * @param tag
	 */
	public Turret(Game game, Player player, Vector2D position, int maxHealth, int size, String tag) {
		super(game, player, position, new Vector2D(), maxHealth, size, tag);
		lastTick = System.currentTimeMillis();
		this.img = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Placeables/turret.png", new Vector2D(0, 0), new Vector2D(32, 32));
	}
	
	public void update(double deltaTime) {
		super.update(deltaTime);
		this.takeDamage((float) ((System.currentTimeMillis() - lastTick) / 1000.0));
        lastTick = System.currentTimeMillis();
        if (nearestEnemy == null || !nearestEnemy.isAlive()) {
            this.findNearestEnemy();
        }
        if (nearestEnemy != null) {
            Vector2D direction = nearestEnemy.getPosition().subtract(this.getPosition()).normalize();
            double targetAngle = Math.atan2(direction.getY(), direction.getX());

            // Smoothly rotate towards the target angle
            angle += rotationSpeed * deltaTime * (targetAngle - angle);
            if (Math.abs(targetAngle - angle) < 0.01) { // Small tolerance to prevent overshooting
                angle = targetAngle;
            }

            if ((System.currentTimeMillis() - lastAttack) / 1000.0 >= attackSpeed) {
                attack(deltaTime);
            }
        }

        super.update(deltaTime);
	}
	
	private void attack(double deltaTime) {
	    float dx = (float) (Math.cos(angle));
	    float dy = (float) (Math.sin(angle));

	    // Implement the gun's fire() method with bloom effect
	    Vector2D vel = new Vector2D(dx, dy).normalize();

	    // Apply bloom by introducing random deviations to the velocity
	    double bloomX = (Math.random() - 0.5) * bloom; // Random deviation between -0.375 and 0.375
	    double bloomY = (Math.random() - 0.5) * bloom;

	    // Add the bloom to the normalized direction
	    Vector2D bloomedVelocity = new Vector2D(vel.getX() + bloomX, vel.getY() + bloomY).normalize();
	    Vector2D bulletVelocity = bloomedVelocity.multiply(bulletSpeed * 25);

	    int damage = (int) (10 * player.getPlayerUpgrades().getDamageMultiplier() * 1 + (game.getWavesManager().getWave() * WavesManager.HEALTH_GROWTH_RATE));
	    game.getAudioPlayer().playSound("GunFire/Turret/turret_shoot.wav", this.position);
	    Bullet b = new Bullet(game, this, this.position.add(new Vector2D(this.getSize()).divide(3)), bulletVelocity, 10, null, 2, damage);
	    b.setTag(tag);
	    b.instantiate();
	    
	    PlayerAttackEvent e = new PlayerAttackEvent(b, damage);
	    b.setPlayerAttackEvent(e);
	    game.getEventManager().dispatchEvent(e);
	    lastAttack = System.currentTimeMillis();
	}
	
	public void onEntityCollision(EntityCollisionEvent e) {
		
	}

	public void render(Graphics g) {
	    super.render(g);
	    Graphics2D g2d = (Graphics2D) g;

	    // Draw the light gray circle for the turret base
	    g2d.setColor(Color.LIGHT_GRAY);
	    if(player.getPlacingMode()) g2d.setColor(new Color(Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 200));
	    g2d.drawImage(SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Placeables/turret_base.png", new Vector2D(0, 0), new Vector2D(32, 32)), (int) this.getScreenPosition().getX(), (int) this.getScreenPosition().getY(), this.getSize(), this.getSize(), null);

	    // Translate to the center of the circle to set the pivot point for rotation
	    g2d.translate((int) this.getScreenPosition().getX() + this.getSize() / 2, (int) this.getScreenPosition().getY() + this.getSize() / 2);

	    double skew = Math.PI*1.5;
	    
	    // Rotate around the center to aim the turret barrel toward the angle
	    g2d.rotate(angle-skew);

	    // Draw the dark gray rectangle for the turret barrel
	    g2d.setColor(Color.BLACK);
	    if(player.getPlacingMode()) g2d.setColor(new Color(Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue(), 200));
	    int rectWidth = 15; // Width of the turret barrel
	    int rectHeight = 60; // Length of the turret barrel
	    g2d.fillRect(-rectWidth / 2, -rectHeight, rectWidth, rectHeight);

	    // Reset the transformation to avoid affecting other rendering
	    g2d.rotate(-angle+skew);
	    g2d.translate(-((int) this.getScreenPosition().getX() + this.getSize() / 2), -((int) this.getScreenPosition().getY() + this.getSize() / 2));
	}

	@Override
	public void entityDeath() {
		this.player.getActivePlaceables().remove(this);
		
	}

	public void findNearestEnemy() {
	    Entity nearestEnemy = null;
	    double closestDistance = Double.MAX_VALUE;
	    Vector2D turretPosition = this.getPosition();

	    for (Entity entity : game.instantiatedEntities.toList()) {
	        // Check if the entity is an enemy and not the turret itself
	        if (entity != this && entity instanceof Enemy) {
	            double distance = turretPosition.distanceTo(entity.getPosition());
	            
	            // Update if this enemy is closer than the previous closest
	            if (distance < closestDistance) {
	                closestDistance = distance;
	                nearestEnemy = entity;
	            }
	        }
	    }
	    this.nearestEnemy = (Enemy) nearestEnemy;
	}

	@Override
	public Image getImage() {
		return this.img;
	}
	
	
}
