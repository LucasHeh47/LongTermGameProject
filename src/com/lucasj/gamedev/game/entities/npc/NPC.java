package com.lucasj.gamedev.game.entities.npc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Button;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;

public class NPC extends Entity implements MouseClickEventListener{

	private boolean isOpen = false;
	
	private List<Button> buttons;
	
	public NPC(Game game, Vector2D position, int size) {
		super(game, position, new Vector2D(0, 0), 999, 0, size, null);
		isOpen = false;
		game.getInput().addMouseClickListener(this);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.CYAN);
		g.fillRect((int)screenPosition.getX(), (int) screenPosition.getY(), 
				size, size);
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
		
	}

	@Override
	public void onMousePressed(MouseEvent e) {
	    // Check if the left mouse button is pressed
	    if (e.getButton() == MouseEvent.BUTTON3) {
	        
	        
	    	Vector2D mousePos = new Vector2D(e.getX(), e.getY());
	        System.out.println("Mouse Position: " + mousePos.toString());
	        System.out.println("NPC Position: " + position);
	        
	        // Check collision
	        if (this.isCollidingWithScreen(mousePos)) {
	        	game.getWavesManager().getNPCManager().closeAll();
	            isOpen = true;
	            System.out.println("NPC clicked!");
	        }
	    }
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean isOpen() {
		return isOpen;
	}
	
	public void close() {
		isOpen = false;
	}

}
