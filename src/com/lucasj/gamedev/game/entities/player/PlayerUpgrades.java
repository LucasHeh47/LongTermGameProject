package com.lucasj.gamedev.game.entities.player;

import com.lucasj.gamedev.essentials.Game;

public class PlayerUpgrades {
    
    private final int DEFAULT_MAX_HEALTH = 100;
    private final float DEFAULT_HEALTH_REGEN = 0.01f;
    private final float DEFAULT_DAMAGE_MULTIPLIER = 1.0f;
    private final float DEFAULT_MOVEMENT_SPEED_MULTIPLIER = 1.0f;
    
    private float upgradedMaxHealth = 0;
    private boolean hasHealthRegen = false;
    private float upgradedHealthRegen = 0;
    private float upgradedShieldRegen = 0;
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
    }

    // Getters
    public float getMaxHealth() {
        return DEFAULT_MAX_HEALTH + upgradedMaxHealth;
    }

    public boolean hasHealthRegen() {
        return hasHealthRegen;
    }

    public float getHealthRegen() {
        return DEFAULT_HEALTH_REGEN + upgradedHealthRegen;
    }

    public float getDamageMultiplier() {
        return DEFAULT_DAMAGE_MULTIPLIER + upgradedDamageMultiplier;
    }

    public float getMovementSpeedMultiplier() {
        return DEFAULT_MOVEMENT_SPEED_MULTIPLIER + upgradedMovementSpeedMultiplier;
    }
}

