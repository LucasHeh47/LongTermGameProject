package com.lucasj.gamedev.essentials;

import com.lucasj.gamedev.mathutils.Vector2D;

public class Camera {

	private Game game;
	private Vector2D viewport;
	private Vector2D worldPosition;
	private static final int BASE_WIDTH = 1920;
	private static final int BASE_HEIGHT = 1080;
	private double scaleX;
	private double scaleY;
	private double scale;

	
	public Camera(Game game, Vector2D viewport, Vector2D worldPosition) {
		this.game = game;
		this.viewport = viewport;
		this.worldPosition = worldPosition;
		scaleX = (double) game.getWidth() / BASE_WIDTH;
		scaleY = (double) game.getHeight() / BASE_HEIGHT;
		scale = Math.min(scaleX, scaleY);
	}
	
	public void recalculateScale() {
		scaleX = (double) game.getWidth() / BASE_WIDTH;
		scaleY = (double) game.getHeight() / BASE_HEIGHT;
		scale = Math.min(scaleX, scaleY);
		
	}

	public Vector2D worldToScreenPosition(Vector2D worldPos) {
		return worldPos.subtract(getWorldPosition());
	}
	
	public Vector2D screenToWorldPosition(Vector2D screenPos) {
		return getWorldPosition().multiply(scale).add(screenPos);
	}
	
	public Vector2D getViewport() {
		return viewport;
	}

	public void setViewport(Vector2D viewport) {
		this.viewport = viewport;
	}

	public Vector2D getWorldPosition() {
		return worldPosition;
	}

	public void setWorldPosition(Vector2D worldPosition) {
		this.worldPosition = worldPosition;
	}

	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public double getScale() {
		return scale;
	}
	
}
