package com.lucasj.gamedev.game.entities.projectiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Bullet extends Projectile {

	/**
	 * 
	 * @param game
	 * @param sender
	 * @param position
	 * @param velocity
	 * @param size
	 * @param tag
	 * @param timeToLive
	 * @param damage
	 */
	public Bullet(Game game, Entity sender, Vector2D position, Vector2D velocity, int size,
			String tag, float timeToLive, int damage) {
		super(game, sender, position, velocity, 0, size, tag, timeToLive, damage);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.blue);
		g2d.fillOval((int)position.getX(), (int)position.getY(), 
				size, size);
		
	}

	@Override
	public void entityDeath() {
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		if(e.getCollider() == this.getSender()) return;
		System.out.println(e.getCollider().getTag());
		e.getCollider().takeDamage(this.getDamage());
		System.out.println("Damage Dealt: " + this.getDamage());
		System.out.println("Health: " + e.getCollider().getHealth() + "/" + e.getCollider().getMaxHealth());
		System.out.println("Collision");
		die();
	}

	@Override
	public void updateProjectile(double deltaTime) {
		this.position = position.add(velocity);
	}

}