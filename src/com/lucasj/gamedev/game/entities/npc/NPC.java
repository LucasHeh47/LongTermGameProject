package com.lucasj.gamedev.game.entities.npc;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Button;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.NameTag;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;

public class NPC extends Entity implements MouseClickEventListener, MouseMotionEventListener {

	private boolean isOpen = false;
	private boolean isHovered = false;
	
	private String name;
	
	private List<Button> buttons;
	
	private NameTag nameTag;
	
	public NPC(Game game, Vector2D position, int size, String name) {
		super(game, position, new Vector2D(0, 0), 999, 0, size, null);
		this.name = name;
		isOpen = false;
		nameTag = new NameTag(game, name, game.getCamera().worldToScreenPosition(position));
		game.getInput().addMouseClickListener(this);
		game.getInput().addMouseMotionListener(this);
	}

	@Override
	public List<Render> render() {
		this.nameTag.setPosition(new Vector2D(this.screenPosition.getX() + size/2, this.screenPosition.getYint()));
		List<Render> renders = new ArrayList<>();
		renders.addAll(super.render());
		
		renders.add(new Render(Layer.Enemy, g -> {
			g.setColor(Color.CYAN);
			g.fillRect((int)screenPosition.getX(), (int) screenPosition.getY(), 
					size, size);
			
			if(isHovered) {
				g.setColor(Color.WHITE);
				g.drawRect((int)screenPosition.getX(), (int) screenPosition.getY(), 
						size, size);
			}
		}));
		
		renders.add(nameTag.render());
		return renders;
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
	    if (e.getButton() == MouseEvent.BUTTON1) {
	        
	        
	    	Vector2D mousePos = new Vector2D(e.getX(), e.getY());
	        
	        // Check collision
	        if (this.isCollidingWithScreen(mousePos)) {
	        	game.getWavesManager().getNPCManager().closeAll();
	            isOpen = true;
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

	@Override
	public void onMouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		Vector2D mousePos = new Vector2D(e.getX(), e.getY());
        
		this.isHovered = (this.isCollidingWithScreen(mousePos));
		
	}

	@Override
	public void onMouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
