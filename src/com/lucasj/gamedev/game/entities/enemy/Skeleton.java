package com.lucasj.gamedev.game.entities.enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Skeleton extends Enemy {
	
	public static void initializeClass(){
		System.out.println("Skeleton static block loaded...");
		registerEnemyType(Skeleton.class, new EnemyWavesData(3, 3));
		System.out.println("Initialized Skeleton Class");
    }

	public Skeleton(Game game, Vector2D position, int maxHealth, int movementSpeed, int size,
			String tag) {
		super(game, position, new Vector2D(0, 0), maxHealth, movementSpeed, size, tag);
		// TODO Auto-generated constructor stub
	}
	
	public void render(Graphics g) {
		g.setColor(Color.gray);
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
		// TODO Auto-generated method stub

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
	void setCashDrop() {
		this.cashDrop[0] = 30;
		this.cashDrop[1] = 55;
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	void setDamageMultiplier() {
		this.damageMultiplier = 1.3f;
	}

}