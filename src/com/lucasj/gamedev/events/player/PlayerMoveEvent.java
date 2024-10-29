package com.lucasj.gamedev.events.player;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.mathutils.Vector2D;

public class PlayerMoveEvent implements GameEvent {

	private Vector2D positionBefore;
	private Vector2D positionChange;
	private Vector2D positionAfter;
	private boolean isCancelled = false;
	
	public PlayerMoveEvent(Vector2D before, Vector2D change, Vector2D after) {
		this.positionAfter = after;
		this.positionChange = change;
		this.positionBefore = before;
	}
	
	public PlayerMoveEvent() {
		this.positionAfter = null;
		this.positionChange = null;
		this.positionBefore = null;
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

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public void setPositionBefore(Vector2D positionBefore) {
		this.positionBefore = positionBefore;
	}

	public void setPositionChange(Vector2D positionChange) {
		this.positionChange = positionChange;
	}

	public void setPositionAfter(Vector2D positionAfter) {
		this.positionAfter = positionAfter;
	}

}
