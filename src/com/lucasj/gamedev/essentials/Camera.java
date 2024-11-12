package com.lucasj.gamedev.essentials;

import java.util.Random;

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
	
	private float shakeTime = 0; // Duration for the shake effect
	private float shakeTimer = 0; // Timer to track elapsed shake time
	private float shakeIntensity = 0; // Intensity of the shake
	private boolean shaking = false;
	private Random random = new Random();
	private Vector2D originalPosition;

	public Camera(Game game, Vector2D viewport, Vector2D worldPosition) {
		this.game = game;
		this.viewport = viewport;
		this.worldPosition = worldPosition;
		this.originalPosition = worldPosition;
		scaleX = (double) game.getWidth() / BASE_WIDTH;
		scaleY = (double) game.getHeight() / BASE_HEIGHT;
		scale = Math.min(scaleX, scaleY);
	}
	
	public void update(double deltaTime) {
		
		// Move camere movement from player into here
		
		if (shakeTimer > 0 && shaking) {
			shakeTimer -= deltaTime;
			// Apply shake offset based on the intensity
			double offsetX = (random.nextDouble() * 2 - 1) * shakeIntensity;
			double offsetY = (random.nextDouble() * 2 - 1) * shakeIntensity;
			worldPosition = originalPosition.add(new Vector2D(offsetX, offsetY));

			// Reduce intensity over time for a smooth fade-out effect
			shakeIntensity *= 0.9;
		}
		if(shaking && shakeTimer <= 0) {
			shaking = false;
			worldPosition = originalPosition;
		}
	}
	
	// Camera shake method
		public void shake(float time, float intensity) {
			this.shakeTime = time;
			this.shaking = false;
			this.shakeIntensity = intensity;
			this.shakeTimer = time;
			this.originalPosition = worldPosition;
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
		return getWorldPosition().add(screenPos);
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
