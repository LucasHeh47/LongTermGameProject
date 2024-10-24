package com.lucasj.gamedev.game.entities.npc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.MouseClickEventListener;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;

public class NPC extends Entity implements MouseClickEventListener{

	private boolean isOpen;
	
	public NPC(Game game, Vector2D position, int size) {
		super(game, position, new Vector2D(0, 0), 999, 0, size, null);
		isOpen = false;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.CYAN);
		g.fillOval((int)screenPosition.getX(), (int) screenPosition.getY(), 
				size, size);
		System.out.println("rendering");
	}

	@Override
	public void entityDeath() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean takeDamage(int dmg) {
		return false;
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		if(this.isCollidingWith(new Vector2D(e.getX(), e.getY()))) isOpen = true;
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean isOpen() {
		return isOpen;
	}

}
