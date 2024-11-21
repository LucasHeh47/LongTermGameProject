package com.lucasj.gamedev.game.entities.player.multiplayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class OnlinePlayer extends Entity implements PlayerMP {

	private BufferedImage[][] walking;
	private int currentWalkingImage = 1; // 1 = down 2 = up = 3 = left 4 = right
	private float animationSpeed = 0.1f;
	private int animationTick = 1;
	private long lastAnimationUpdate;

	private String username;
	private Color color;
	
	public OnlinePlayer(Game game, Color color, String username, Vector2D position) {
		super(game, position, new Vector2D(0, 0), 100, 0, 64, username);
		this.color = color;
		this.username = username;
		
		walking = new BufferedImage[4][4];
		for(int i = 0; i < 4; i++) {
			for (int j = 0; j<4; j++) {
				walking[i][j] = SpriteTools.getSprite(SpriteTools.assetPackDirectory + "Actor/Characters/Boy/SeparateAnim/Walk.png", new Vector2D(i*16, j*16), new Vector2D(16, 16));
			}
		}
		lastAnimationUpdate = System.currentTimeMillis();
	}
	
	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		Debug.log(this, position);
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		Graphics2D g2d = (Graphics2D) g;
		
		
		// Health bar
		g.setColor(Color.black);
		int barWidth = (int) (size + (size * 0.075));
		int barHeight = (int) (size * 0.05);
		int barX = (int) (screenPosition.getX()+(size/2) - (barWidth / 2));
		int barY = (int) (screenPosition.getY() - (size * 0.2));
		g.fillRect(barX, barY, barWidth, barHeight);

		// Health portion of the bar
		g.setColor(this.color);
		int healthWidth = (int) (barWidth * ((double) health / maxHealth));
		g.fillRect(barX, barY, healthWidth, barHeight);
		
		
		// Load the image you want to render (e.g., walking sprite)
	    Image img = walking[currentWalkingImage-1][animationTick-1];
	    
	    int imageWidth = img.getWidth(null);
	    int imageHeight = img.getHeight(null);
	    
	    double x = this.getPosition().getX();
	    double y = this.getPosition().getY();

	    AffineTransform transform = new AffineTransform();
	    transform.translate(x - imageWidth / 2.0, y - imageHeight / 2.0);
	    
	    g2d.drawImage(img, (int) this.screenPosition.getX(), (int) this.screenPosition.getY(), size, size, null);
        Debug.log(this, screenPosition);
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entityDeath() {
		// TODO Auto-generated method stub
		
	}

	public int getCurrentWalkingImage() {
		return currentWalkingImage;
	}

	public void setCurrentWalkingImage(int currentWalkingImage) {
		this.currentWalkingImage = currentWalkingImage;
	}

	public String getUsername() {
		return username;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public Entity setPosition(Vector2D pos) {
		this.position = pos;
		return this;
	}

	@Override
	public OnlinePlayer getPlayer() {
		return this;
	}

	@Override
	public void setWalkingImage(int num) {
		this.currentWalkingImage = num;
	}

	@Override
	public Entity setMaxHealth(float num) {
		this.setMaxHealth(num);
		return this;
	}
	
}
