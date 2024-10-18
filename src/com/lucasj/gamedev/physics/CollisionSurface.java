package com.lucasj.gamedev.physics;

import com.lucasj.gamedev.mathutils.Vector2D;

public class CollisionSurface {
    
    private Vector2D position;  // Top-left corner of the surface
    private int width;
    private int height;
    
    public CollisionSurface(Vector2D position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
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