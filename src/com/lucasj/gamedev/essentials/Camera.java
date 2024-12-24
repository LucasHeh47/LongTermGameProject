package com.lucasj.gamedev.essentials;

import java.util.Random;

import com.lucasj.gamedev.game.entities.Entity;
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
	
	private double currentLerpFactor = 2.5;  // Start at normal speed
	private Entity objectFollowing;

	public Camera(Game game, Vector2D viewport, Vector2D worldPosition) {
		this.game = game;
		this.viewport = viewport;
		this.worldPosition = worldPosition;
		this.originalPosition = worldPosition;
		scaleX = (double) game.getWidth() / BASE_WIDTH;
		scaleY = (double) game.getHeight() / BASE_HEIGHT;
		scale = Math.min(scaleX, scaleY);
		this.objectFollowing = game.getPlayer();
	}
	
	public void update(double deltaTime) {
		
		if(!this.game.getWavesManager().hasGameStarted()) return;
		if(this.objectFollowing == null) {
			this.objectFollowing = game.getPlayer();
			return;
		}

	    // 1. Determine the target factor based on whether the player is dashing
	    boolean isDashing = game.getPlayer().isDashing();
	    double targetLerpFactor = 2.5;
	    if(this.game.getPlayer().isSprinting()) targetLerpFactor = 3;
	    if(this.game.getPlayer().isDashing()) targetLerpFactor = 5.0;

	    // 2. Interpolate the lerpFactor itself to avoid a sudden jump
	    //    'factorLerpSpeed' controls how fast we move from current to target.
	    double factorLerpSpeed = 3.0;

	    // A simple linear interpolation per frame:
	    currentLerpFactor = currentLerpFactor
	        + (targetLerpFactor - currentLerpFactor)
	        * (factorLerpSpeed * deltaTime);

	    // 3. Compute the fraction 't' for camera movement using the newly updated currentLerpFactor
	    double t = 1.0 - Math.exp(-currentLerpFactor * deltaTime);

	    // 4. Find the desired (clamped) camera position
	    Vector2D desiredCamPos = objectFollowing.getPosition().add(objectFollowing.getSize()/2).subtract(viewport.divide(2));
	    
	    desiredCamPos = clampToWorld(desiredCamPos);

	    // 5. Lerp camera position based on 't'
	    Vector2D interpolatedPos = lerp(this.worldPosition, desiredCamPos, t);

	    // 6. Handle camera shake if needed
	    Vector2D finalPos = applyCameraShakeIfAny(interpolatedPos, deltaTime);

	    // 7. Update the camera position
	    this.worldPosition = finalPos;
	}

	// Example of a clamp method
	private Vector2D clampToWorld(Vector2D camPos) {
	    Vector2D worldSize = game.getMapManager().getWorldSize();
	    double x = Math.max(0, Math.min(camPos.getX(), worldSize.getX() - viewport.getX()));
	    double y = Math.max(0, Math.min(camPos.getY(), worldSize.getY() - viewport.getY()));
	    return new Vector2D(x, y);
	}

	// Basic lerp
	private Vector2D lerp(Vector2D from, Vector2D to, double alpha) {
	    double nx = from.getX() + alpha * (to.getX() - from.getX());
	    double ny = from.getY() + alpha * (to.getY() - from.getY());
	    return new Vector2D(nx, ny);
	}

	// (Optional) apply your shake logic or just return 'interpolatedPos' 
	private Vector2D applyCameraShakeIfAny(Vector2D interpolatedPos, double deltaTime) {
	    if (shaking && shakeTimer > 0) {
	        shakeTimer -= deltaTime;
	        double offsetX = (random.nextDouble() * 2 - 1) * shakeIntensity;
	        double offsetY = (random.nextDouble() * 2 - 1) * shakeIntensity;
	        shakeIntensity *= 0.9; // decay
	        return interpolatedPos.add(new Vector2D(offsetX, offsetY));
	    } else if (shaking && shakeTimer <= 0) {
	        shaking = false;
	    }
	    return interpolatedPos;
	}

	
	// Camera shake method
		public void shake(float time, float intensity) {
			this.shakeTime = time;
			this.shaking = true;
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
