package com.lucasj.gamedev.game.entities.player;

import com.lucasj.gamedev.essentials.Game;

public class PlayerUpgrades {
    
    private final int DEFAULT_MAX_HEALTH = 100;
    private final float DEFAULT_HEALTH_REGEN = 0.01f;
    private final float DEFAULT_DAMAGE_MULTIPLIER = 1.0f;
    private final float DEFAULT_MOVEMENT_SPEED_MULTIPLIER = 1.0f;
    
    private int upgradedMaxHealth = 0;
    private boolean hasHealthRegen = false;
    private float upgradedHealthRegen = 0;
    private float upgradedDamageMultiplier = 0;
    private float upgradedMovementSpeedMultiplier = 0;
    
    // How much you get from each upgrade
    private int maxHealthUpgrade = 5;
    private float healthRegenUpgrade = 0.01f;
    private float damageMultiplierUpgrade = 0.075f;
    private float movementSpeedMultiplier = 0.05f;
    
    private Player player;
    private Game game;
    
    public PlayerUpgrades(Game game, Player p) {
    	this.player = p;
    	this.game = game;
    	this.upgradedMaxHealth = DEFAULT_MAX_HEALTH;
    	this.upgradedHealthRegen = DEFAULT_HEALTH_REGEN;
    	this.upgradedDamageMultiplier = DEFAULT_DAMAGE_MULTIPLIER;
    	this.upgradedMovementSpeedMultiplier = DEFAULT_MOVEMENT_SPEED_MULTIPLIER;
    	
    }

    public boolean upgrade(String type) {
    	if(player.removeGem(1)) {
    		System.out.println("Purchasing: " + type);
    		if(type.equals("regen")) {
    			this.upgradedHealthRegen += this.healthRegenUpgrade;
    		} else if(type.equals("damage")) {
    			this.upgradedDamageMultiplier += this.damageMultiplierUpgrade;
    		} else if(type.equals("movement")) {
    			this.upgradedMovementSpeedMultiplier += this.movementSpeedMultiplier;
    		} else if(type.equals("maxHealth")) {
    			this.upgradedMaxHealth += this.maxHealthUpgrade;
    			this.player.setMaxHealth(this.upgradedMaxHealth);
    		}
    	} else {
    		System.out.println("Not enough to purchase");
    		return false;
    	}
    	return true;
    }
    
    public boolean unlockHealthRegen() {
    	if(hasHealthRegen) return true;
    	if(player.removeGem(5)) {
    		hasHealthRegen = true;
    		return true;
    	}
    	return false;
    }
    
    // Getters
    public int getMaxHealth() {
        return upgradedMaxHealth;
    }

    public boolean hasHealthRegen() {
        return hasHealthRegen;
    }

    public float getHealthRegen() {
        return upgradedHealthRegen;
    }

    public float getDamageMultiplier() {
        return upgradedDamageMultiplier;
    }

    public float getMovementSpeedMultiplier() {
        return upgradedMovementSpeedMultiplier;
    }
}

