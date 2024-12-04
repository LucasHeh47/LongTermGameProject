package com.lucasj.gamedev.game.entities.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.events.level.LevelUpEvent;
import com.lucasj.gamedev.game.levels.LevelUpPerk;
import com.lucasj.gamedev.os.GameData;

public class PlayerStats implements Serializable{

	private static final long serialVersionUID = -4937487621824812897L;
	private int totalEnemiesKilled;
	private int totalDamageDealt;
	
	private List<LevelUpPerk> perksUnlocked = new ArrayList<>();
	
	private int currentXP;
	private int xpToNextLevel = 200;
	private int totalXP;
	private int level;
    private float xpMultiplier;
    
    private int levelTokens = 0;
	
	public PlayerStats() {
        this.currentXP = 0;
        this.xpToNextLevel = 200;
        this.totalXP = 0;
        this.level = 1;
        this.xpMultiplier = 1.0f;
        perksUnlocked = new ArrayList<>();
        calculateXPToNextLevel();
	}
	
	private void calculateXPToNextLevel() {
        double base = 200;
        double growthFactor = 2;
        double exponent = (level - 1) / 2.0;
        this.xpToNextLevel = (int)(base * Math.pow(growthFactor, exponent));
    }

    // Add XP and check if the player levels up
    public boolean addXP(int amount) {
    	boolean leveledUp = false;
        int xpGained = (int)(amount * xpMultiplier);
        currentXP += xpGained;
        totalXP += xpGained;
        //System.out.println("Added xp: " + currentXP + " / " + xpToNextLevel + " At lvl: " + level);
        // Level up logic
        while (currentXP >= xpToNextLevel) {
            levelUp();
            leveledUp = true;
        }
        return leveledUp;
    }

    private void levelUp() {
        currentXP -= xpToNextLevel;
        level++;
        this.levelTokens++;
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
            this.levelTokens = loadedStats.levelTokens;
            this.perksUnlocked = loadedStats.perksUnlocked;
            if(this.perksUnlocked == null) this.perksUnlocked = new ArrayList<>();
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

	public int getLevelTokens() {
		return levelTokens;
	}

	public void setLevelTokens(int levelTokens) {
		this.levelTokens = levelTokens;
	}

	public List<LevelUpPerk> getPerksUnlocked() {
		return perksUnlocked;
	}

	public void setPerksUnlocked(List<LevelUpPerk> perksUnlocked) {
		this.perksUnlocked = perksUnlocked;
	}
	
	public boolean hasPerkUnlocked(LevelUpPerk perk) {
		return this.perksUnlocked.contains(perk);
	}

}
