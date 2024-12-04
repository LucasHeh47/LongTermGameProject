package com.lucasj.gamedev.game.entities.player;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.weapons.AmmoMod;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.game.weapons.Tier;

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
    private int maxHealthUpgrade = 25;
    private float healthRegenUpgrade = 0.03f;
    private float damageMultiplierUpgrade = 0.33f;
    private float movementSpeedMultiplier = 0.1f;
    
    private int regenUpgradeCount = 0;
    private int damageUpgradeCount = 0;
    private int movementUpgradeCount = 0;
    private int maxHealthUpgradeCount = 0;
    
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
    
    public void reset() {
    	this.upgradedMaxHealth = DEFAULT_MAX_HEALTH;
    	this.upgradedHealthRegen = DEFAULT_HEALTH_REGEN;
    	this.upgradedDamageMultiplier = DEFAULT_DAMAGE_MULTIPLIER;
    	this.upgradedMovementSpeedMultiplier = DEFAULT_MOVEMENT_SPEED_MULTIPLIER;
    }

    public boolean upgrade(String type) {
        int cost = getCost(type);
        if (player.removeGem(cost)) {
            System.out.println("Purchasing: " + type);
            switch (type) {
                case "health":
                    this.upgradedHealthRegen += this.healthRegenUpgrade;
                    regenUpgradeCount++;
                    break;
                case "damage":
                    this.upgradedDamageMultiplier += this.damageMultiplierUpgrade;
                    damageUpgradeCount++;
                    break;
                case "movement":
                    this.upgradedMovementSpeedMultiplier += this.movementSpeedMultiplier;
                    movementUpgradeCount++;
                    break;
                case "maxHealth":
                    this.upgradedMaxHealth += this.maxHealthUpgrade;
                    this.player.setMaxHealth(this.upgradedMaxHealth);
                    maxHealthUpgradeCount++;
                    break;
                default:
                    System.out.println("Unknown upgrade type");
                    return false;
            }
            return true;
        } else {
            System.out.println("Not enough to purchase");
            return false;
        }
    }
    
    public int getCost(String type) {
        switch (type) {
            case "health":
                return regenUpgradeCount + 1;
            case "damage":
                return damageUpgradeCount + 1;
            case "movement":
                return movementUpgradeCount + 1;
            case "maxHealth":
                return maxHealthUpgradeCount + 1;
            default:
                return Integer.MAX_VALUE; // Unknown upgrade type
        }
    }
    
    public boolean unlockHealthRegen() {
    	if(hasHealthRegen) return true;
    	if(player.removeGem(5)) {
    		hasHealthRegen = true;
    		return true;
    	}
    	return false;
    }
    
    public boolean unlockSecondClass() {
    	if(player.getSecondaryGun() != null) return false;
    	
    	if(player.getMoney() >= 10000 && player.getGems() >= 10) {
    		player.removeGem(10);
    		player.removeMoney(10000);
        	player.setPickingSecondary(true);
        	return true;
    	}
    	return false;
    	
    }
    
    public boolean upgradeWeapon() {
    	Gun weapon = this.player.getPrimaryGun();
    	int amount = this.getWeaponUpgradeCost();
		if(player.getMoney() >= amount && amount != -1) {
			weapon.upgrade();
			player.removeMoney(amount);
			return true;
		}
		return false;
    }
    
    public boolean purchaseAmmoMod(AmmoMod mod) {
    	if(player.getPrimaryGun().getAmmoMod() == mod) return false;
    	if(game.getPlayer().getGems() >= 5) {
    		game.getPlayer().getPrimaryGun().setAmmoMod(mod);
    		game.getPlayer().removeGem(5);
    		return true;
    	}
    	return false;
    }
    
    public int getWeaponUpgradeCost() {
    	Gun weapon = this.player.getPrimaryGun();
    	switch(weapon.getTier()) {
    	
    	case Common:
    		return 1500;
    	case Uncommon:
    		return 2500;
    	case Rare:
    		return 5000;
    	case Epic:
    		return 10000;
    	case Legendary:
    		if(Tier.lastTier() == Tier.Legendary) return -1;
    		return 17500;
    	case Mythic:
    		if(Tier.lastTier() == Tier.Mythic) return -1;
    		return 25000;
    	case Divine:
    		if(Tier.lastTier() == Tier.Divine) return -1;
    		return 50000;
    	case Ethereal:
    		return -1;
    		
    	default:
    		return -1;
    	
    	}
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

