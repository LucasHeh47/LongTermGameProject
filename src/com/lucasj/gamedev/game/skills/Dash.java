package com.lucasj.gamedev.game.skills;

import com.lucasj.gamedev.essentials.Game;

public class Dash extends Skill {

	private int manaCost = 20;
	private int dashDistance = 1000;
	private float sprintMultiplier = 4;
	private int distanceTravelled = 0;
	private boolean isActive;
	
	public Dash(Game game) {
		super(game, 10);
	}

	public int getDashDistance() {
		return dashDistance;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		if(!isActive) {
			this.distanceTravelled = 0;
		}
		this.isActive = isActive;
	}

	public int getDistanceTravelled() {
		return distanceTravelled;
	}

	public void setDistanceTravelled(int distanceTravelled) {
		this.distanceTravelled = distanceTravelled;
	}

	public float getSprintMultiplier() {
		return sprintMultiplier;
	}

	public void setSprintMultiplier(float sprintMultiplier) {
		this.sprintMultiplier = sprintMultiplier;
	}

}
