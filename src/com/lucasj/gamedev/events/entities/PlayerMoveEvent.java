package com.lucasj.gamedev.events.entities;

import com.lucasj.gamedev.mathutils.Vector2D;

public class PlayerMoveEvent {

	private Vector2D positionBefore;
	private Vector2D positionChange;
	private Vector2D positionAfter;
	
	public PlayerMoveEvent(Vector2D before, Vector2D change, Vector2D after) {
		this.positionAfter = after;
		this.positionChange = change;
		this.positionBefore = before;
	}

	public Vector2D getPositionBefore() {
		return positionBefore;
	}

	public Vector2D getPositionChange() {
		return positionChange;
	}

	public Vector2D getPositionAfter() {
		return positionAfter;
	}

}
