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
	
	private BufferedImage[] images;
	private int currentWalkingImage = 1; // 1 = down 2 = up = 3 = left 4 = right
	private float animationSpeed = 0.1f;
	private int animationTick = 1;
	private long lastAnimationUpdate;
	
	public static void initializeClass(){
		registerEnemyType(Zombie.class, new EnemyWavesData(0, 5));
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
		images = new BufferedImage[5];
		for(int i = 0; i < 5; i++) {
			Debug.log(this, "Art/Enemies/Zombie/zombie" + i+1 + ".png");
			images[i] = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Enemies/Zombie/zombie" + (i+1) + ".png", new Vector2D(0, 0), new Vector2D(32, 32));
		}
	}
	
	public void update(double deltaTime) {
		super.update(deltaTime);
		if((System.currentTimeMillis() - lastAnimationUpdate)/1000.0 > animationSpeed) {
			animationTick++;
			if (animationTick > 5) {
			    animationTick = 1; // Reset to first frame if it exceeds available frames
			}
			lastAnimationUpdate = System.currentTimeMillis();
		}
		if(!isMoving) animationTick = 1;
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Image img = images[animationTick-1];
		
		int imageWidth = img.getWidth(null);
	    int imageHeight = img.getHeight(null);
	    
	    double x = this.getScreenPosition().getX();
	    double y = this.getScreenPosition().getY();

	    AffineTransform transform = new AffineTransform();
	    transform.translate(x - imageWidth / 2.0, y - imageHeight / 2.0);
	    
	    double scaleX = size / (double) imageWidth;
	    double scaleY = size / (double) imageHeight;
	    transform.scale(scaleX, scaleY);
	    
	    transform.rotate(this.angleToPlayer, imageWidth / 2.0, imageHeight / 2.0);
		
		g2d.drawImage(img, transform, null);
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
