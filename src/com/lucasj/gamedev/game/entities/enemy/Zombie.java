package com.lucasj.gamedev.game.entities.enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class Zombie extends Enemy {

	private BufferedImage[][] walking;
	private int currentWalkingImage = 1; // 1 = down 2 = up = 3 = left 4 = right
	private float animationSpeed = 0.1f;
	private int animationTick = 1;
	private long lastAnimationUpdate;
	
	public static void initializeClass(){
		registerEnemyType(Zombie.class, new EnemyWavesData(0, 15));
    }

	public Zombie(Game game) {
		super(game);

		walking = new BufferedImage[4][4];
		for(int i = 0; i < 4; i++) {
			for (int j = 0; j<4; j++) {
				walking[i][j] = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Enemies/Zombie/zombie.png", new Vector2D(i*16, j*16), new Vector2D(16, 16));
			}
		}
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
		
		walking = new BufferedImage[4][4];
		for(int i = 0; i < 4; i++) {
			for (int j = 0; j<4; j++) {
				walking[i][j] = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Enemies/Zombie/zombie.png", new Vector2D(i*16, j*16), new Vector2D(16, 16));
			}
		}
	}
	
	public void update(double deltaTime) {
		super.update(deltaTime);
		
		if(this.getAggrod() == null) {
			currentWalkingImage = 1;
		} else {
			Vector2D playerPos = this.getAggrod().getPosition();
		    Vector2D pos = this.position;
	
		    Vector2D direction = playerPos.subtract(pos);
	
		    // Determine the direction based on the angle of the direction vector
		    if (Math.abs(direction.getX()) > Math.abs(direction.getY())) {
		        // Horizontal movement
		        if (direction.getX() > 0) {
		            currentWalkingImage = 4; // Moving right
		        } else {
		            currentWalkingImage = 3; // Moving left
		        }
		    } else {
		        // Vertical movement
		        if (direction.getY() > 0) {
		            currentWalkingImage = 1; // Moving down
		        } else {
		            currentWalkingImage = 2; // Moving up
		        }
		    }
		}
		
		
		float checkAnimationSpeed = animationSpeed;
		if((System.currentTimeMillis() - lastAnimationUpdate)/1000.0 > checkAnimationSpeed) {
			animationTick++;
			if (animationTick > 4) {
			    animationTick = 1; // Reset to first frame if it exceeds available frames
			}
			lastAnimationUpdate = System.currentTimeMillis();
		}
		if(!isMoving) animationTick = 1;
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Image img = walking[currentWalkingImage-1][animationTick-1];
	    
	    int x = (int) this.getScreenPosition().getX();
	    int y = (int) this.getScreenPosition().getY();
		
		g2d.drawImage(img, x, y, this.size, this.size, null);
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

	@Override
	public void setHealthMultiplier() {
		return;
	}

	@Override
	public void setMovementSpeedMultiplier() {
		return;
	}

}
