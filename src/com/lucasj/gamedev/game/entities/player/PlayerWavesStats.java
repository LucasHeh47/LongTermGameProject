package com.lucasj.gamedev.game.entities.player;

public class PlayerWavesStats {
	
	public int wave;
	public int enemiesKilled;
	public int damageDealt;
	public int damageTaken;
	public int moneyEarned;
	public int moneySpent;
	public int gemsEarned;
	public int gemsSpent;
	public float accuracy = calculateAccuracy();
	public int startingLevel;
	public int finalLevel;
	public int totalExp;
	
	private int shotsTaken;
	private int shotsLanded;
	
	public PlayerWavesStats() {
		this.startingLevel = Player.getGlobalStats().getLevel();
	}
	
	private float calculateAccuracy() {
		if (shotsTaken == 0) return 0;
		return shotsLanded/shotsTaken;
	}
	
	public void shoot() {
		shotsTaken++;
	}
	
	public void shotLanded() {
		shotsLanded++;
	}
}
