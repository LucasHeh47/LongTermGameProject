package com.lucasj.gamedev.game.entities.player;

import java.io.Serializable;

import com.lucasj.gamedev.os.GameData;

public class PlayerStats implements Serializable{

	private static final long serialVersionUID = -4937487621824812897L;
	private int totalEnemiesKilled;
	private int totalDamageDealt;
	
	private int currentXP;
	private int xpToNextLevel = 200;
	private int totalXP;
	private int level;
    private float xpMultiplier;
	
	public PlayerStats() {
        this.currentXP = 0;
        this.xpToNextLevel = 200;
        this.totalXP = 0;
        this.level = 1;
        this.xpMultiplier = 1.0f;
        calculateXPToNextLevel();
	}
	
	private void calculateXPToNextLevel() {
        double base = 200;
        double growthFactor = 1.2; // 1 + 0.2
        double exponent = (level - 1) / 2.0;
        this.xpToNextLevel = (int)(base * Math.pow(growthFactor, exponent));
    }

    // Add XP and check if the player levels up
    public void addXP(int amount) {
        int xpGained = (int)(amount * xpMultiplier);
        currentXP += xpGained;
        totalXP += xpGained;
        System.out.println("Added xp: " + currentXP + " / " + xpToNextLevel + " At lvl: " + level);
        // Level up logic
        while (currentXP >= xpToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        currentXP -= xpToNextLevel;
        level++;
        calculateXPToNextLevel();
    }
	
    public void load(GameData gameData) {
        PlayerStats loadedStats = gameData.loadPlayerStats();
        if (loadedStats != null) {
            this.totalEnemiesKilled = loadedStats.totalEnemiesKilled;
            this.totalDamageDealt = loadedStats.totalDamageDealt;
            this.currentXP = loadedStats.currentXP;
            this.xpToNextLevel = loadedStats.xpToNextLevel;
            this.totalXP = loadedStats.totalXP;
            this.level = loadedStats.level;
            this.xpMultiplier = loadedStats.xpMultiplier;
            System.out.println("Player stats loaded successfully.");
        }
    }

    public void save(GameData gameData) {
        gameData.savePlayerStats(this);
    }

	public int getTotalEnemiesKilled() {
		return totalEnemiesKilled;
	}

	public int getTotalDamageDealt() {
		return totalDamageDealt;
	}

	public int getCurrentXP() {
		return currentXP;
	}

	public int getXpToNextLevel() {
		return xpToNextLevel;
	}

	public int getTotalXP() {
		return totalXP;
	}

	public int getLevel() {
		return level;
	}

	public float getXpMultiplier() {
		return xpMultiplier;
	}

}
