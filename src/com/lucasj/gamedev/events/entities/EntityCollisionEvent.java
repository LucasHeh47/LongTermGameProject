package com.lucasj.gamedev.events.entities;

import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;

public class EntityCollisionEvent {
	
	private Entity entity;
	private Entity collider;
	
	private Vector2D collisionPoint;
	
	// Constructor
    public EntityCollisionEvent(Entity entity, Entity collider, Vector2D collisionPoint) {
        this.entity = entity;
        this.collider = collider;
        this.collisionPoint = collisionPoint;
    }
	public Entity getEntity() {
		return entity;
	}
	public Entity getCollider() {
		return collider;
	}
	public Vector2D getCollisionPoint() {
		return collisionPoint;
	}

}
