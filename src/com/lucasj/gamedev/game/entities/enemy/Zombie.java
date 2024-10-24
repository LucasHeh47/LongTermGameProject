package com.lucasj.gamedev.game.entities.enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Zombie extends Enemy {
	
	public static void initializeClass(){
		System.out.println("Zombie static block loaded...");
		registerEnemyType(Zombie.class, new EnemyWavesData(0, 5));
		System.out.println("Initialized Zombie Class");
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
	public Zombie(Game game, Vector2D position, int maxHealth, int movementSpeed, int size, String tag) {
		super(game, position, new Vector2D(0, 0), maxHealth, movementSpeed, size, tag);
	}
	
	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect((int)screenPosition.getX(), (int) screenPosition.getY(), 
				size, size);
		super.render(g);
	}

	public void entityDeath() {
		super.entityDeath();

	}

	@Override
	public void onMouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		
		Vector2D mousePos = new Vector2D(e.getX(), e.getY());
		
	}

	@Override
	public void onMouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void setCashDrop() {
		this.cashDrop[0] = 25;
		this.cashDrop[1] = 50;
	}

	@Override
	void setDamageMultiplier() {
		this.damageMultiplier = 1.0f;
	}

}
