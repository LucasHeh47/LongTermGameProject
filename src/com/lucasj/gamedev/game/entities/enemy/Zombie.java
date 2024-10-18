package com.lucasj.gamedev.game.entities.enemy;

import java.awt.Color;
import java.awt.Graphics;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Zombie extends Enemy {

	private static final EnemyWavesData enemyData = new EnemyWavesData(1, 5);
	
	static {
		registerEnemyType(Zombie.class, new EnemyWavesData(1, 5));
    }
	
	/**
	 * 
	 * @param game
	 * @param position
	 * @param maxHealth
	 * @param movementSpeed
	 * @param size
	 * @param tag
	 */
	private Zombie(Game game, Vector2D position, int maxHealth, int movementSpeed, int size, String tag) {
		super(game, position, new Vector2D(0, 0), maxHealth, movementSpeed, size, tag);
	}

	@Override
	public void update(double deltaTime) {
		System.out.println(this.getHealth() + "/" + this.getMaxHealth());
		float dx = (float) (game.getPlayer().getPosition().getX() - this.getPosition().getX());
		float dy = (float) (game.getPlayer().getPosition().getY() - this.getPosition().getY());
		Vector2D vel = new Vector2D(dx, dy).normalize();
		this.velocity = vel.multiply(movementSpeed*deltaTime/50);
		
		this.position = position.add(velocity);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect((int)position.getX(), (int) position.getY(), 
				size, size);
	}

	@Override
	public void entityDeath() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		//System.out.println("Zombie collision | Health Left: " + this.getHealth() + " " + this.getMaxHealth());
	}

}
