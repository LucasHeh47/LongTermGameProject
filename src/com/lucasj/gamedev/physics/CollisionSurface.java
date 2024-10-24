package com.lucasj.gamedev.physics;

import java.awt.Graphics;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;

public class CollisionSurface {
    
    private Vector2D position;  // Top-left corner of the surface
    private int width;
    private int height;
    private Game game;
    private Vector2D screenPosition;
    
    public CollisionSurface(Game game, Vector2D position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.game = game;
        this.screenPosition = game.getCamera().screenToWorldPosition(position);
        game.getCollisionSurfaces().add(this);
    }
    
    public void update(double deltaTime) {
		Vector2D cameraPos = game.getCamera().getWorldPosition();
		screenPosition = this.position.subtract(cameraPos);
    }
    
    public void render(Graphics g) {
    	g.fillRect((int)this.getScreenPosition().getX(),
    			(int)this.getScreenPosition().getY(),
    			this.getWidth(),
    			this.getHeight());
    }
    
    public Vector2D getScreenPosition() {
    	return screenPosition;
    }
    
    public Vector2D getPosition() {
        return position;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}