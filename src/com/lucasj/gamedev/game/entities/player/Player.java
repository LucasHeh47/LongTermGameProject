package com.lucasj.gamedev.game.entities.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.InputHandler;
import com.lucasj.gamedev.events.KeyboardEventListener;
import com.lucasj.gamedev.events.MouseClickEventListener;
import com.lucasj.gamedev.events.MouseMotionEventListener;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.projectiles.Bullet;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Player extends Entity implements MouseClickEventListener, MouseMotionEventListener, KeyboardEventListener{

	private boolean WASD[] = {false, false, false, false};
	
	private Vector2D mousePosition = new Vector2D(0, 0);
	
	private float playerRotation = 0.0f;
	
	InputHandler input;
	
	private boolean playerAttacking = false;
	private float bulletSpeed = 40.0f;
	private float attackSpeed = 0.2f;
	private long lastAttack = 0;
	
	public Player(Game game, InputHandler input) {
		super(game);
		this.input = input;
		this.size = 50;
		this.movementSpeed = 1000;
		importance = 1;
		input.addKeyboardListener(this);
		input.addMouseClickListener(this);
		input.addMouseMotionListener(this);
	}

	@Override
	public void update(double deltaTime) {
		move(deltaTime);
		if(playerAttacking && (System.currentTimeMillis() - lastAttack)/1000.0 > attackSpeed) attack(deltaTime);
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

	    // Create a rectangle representing the player's body
	    Rectangle rect = new Rectangle((int) this.getPosition().getX() - size, 
	                                   (int) this.getPosition().getY() - size, 
	                                   size * 2, size * 2);

	    // Create a Path2D for the player
	    Path2D.Double path = new Path2D.Double();
	    path.append(rect, false);

	    // Create the AffineTransform for rotating around the center of the player
	    AffineTransform t = new AffineTransform();

	    // Move the object to the player's position, rotate, then move back
	    t.rotate(playerRotation, this.getPosition().getX(), this.getPosition().getY());

	    // Apply the transformation
	    path.transform(t);

	    // Render the player
	    g2d.setColor(Color.BLACK);
	    g2d.fill(path);
	}
	
	private void move(double deltaTime) {
	    Vector2D currentPos = this.position;
	    Vector2D posUpdate = new Vector2D(0, 0);
	    
	    if(WASD[0]) {
	    	posUpdate.addY(-1);
	    }
	    if(WASD[1]) {
	    	posUpdate.addX(-1);
	    }
	    if(WASD[2]) {
	    	posUpdate.addY(1);
	    }
	    if(WASD[3]) {
	    	posUpdate.addX(1);
	    }
	    posUpdate = posUpdate.normalize();
	    this.position = currentPos.add(posUpdate.multiply(movementSpeed*deltaTime));
		float dx = (float) (mousePosition.getX() - this.getPosition().getX());
		float dy = (float) (mousePosition.getY() - this.getPosition().getY());

		playerRotation = (float) Math.atan2(dy, dx);
	}
	
	private void attack(double deltaTime) {
		float dx = (float) (mousePosition.getX() - this.getPosition().getX());
		float dy = (float) (mousePosition.getY() - this.getPosition().getY());
		Vector2D vel = new Vector2D(dx, dy).normalize();
		Vector2D bulletVelocity = vel.multiply(bulletSpeed*100*deltaTime);
		Bullet b = new Bullet(game, this, this.position, bulletVelocity, 10, null, 2, 10);
		b.instantiate();
		lastAttack = System.currentTimeMillis();
	}

	@Override
	public void entityDeath() {
		
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		playerAttacking = true;
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		playerAttacking = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
        	WASD[0] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_A) {
        	WASD[1] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_S) {
        	WASD[2] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_D) {
        	WASD[3] = true;
        }
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
        	WASD[0] = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_A) {
        	WASD[1] = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_S) {
        	WASD[2] = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_D) {
        	WASD[3] = false;
        }
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
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
	public void onMouseDragged(MouseEvent e) {
		mousePosition = new Vector2D(e.getX(), e.getY());
		float dx = (float) (mousePosition.getX() - this.getPosition().getX());
		float dy = (float) (mousePosition.getY() - this.getPosition().getY());

		playerRotation = (float) Math.atan2(dy, dx);
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		mousePosition = new Vector2D(e.getX(), e.getY());
		float dx = (float) (mousePosition.getX() - this.getPosition().getX());
		float dy = (float) (mousePosition.getY() - this.getPosition().getY());

		playerRotation = (float) Math.atan2(dy, dx);
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		
	}
	
}
