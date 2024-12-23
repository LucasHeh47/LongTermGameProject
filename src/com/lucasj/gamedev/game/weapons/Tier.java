package com.lucasj.gamedev.game.weapons;

import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.misc.Debug;

public enum Tier {
	Common(1),
	Uncommon(2),
	Rare(3),
	Epic(4),
	Legendary(5),
	Mythic(6),
	Divine(7),
	Ethereal(8);
	
	private int tier;

	Tier(int i) {
		this.tier = i;
	}
	
	private static Tier getLastTier() {
		Tier result = Legendary;
		if(Player.getGlobalStats().getLevel() >= 25) {
			result = Mythic;
		}
		if(Player.getGlobalStats().getLevel() >= 40) {
			result = Divine;
		}
		if(Player.getGlobalStats().getLevel() >= 65) {
			result = Ethereal;
		}
		return result;
	}
	
	public static Tier lastTier() {
		return getLastTier();
	}
	
	public float getDamageMultiplier() {
		// Common: 1x Uncommon: 1.5x Rare: 2.5x Epic: 4x Legendary: 5x Mythic: 7.5x Godly: 10x
		switch (this) {
	        case Common: return 1.0f;
	        case Uncommon: return 1.5f;
	        case Rare: return 2.5f;
	        case Epic: return 4.0f;
	        case Legendary: return 5.0f;
	        case Mythic: return 7.5f;
	        case Divine: return 9.0f;
	        case Ethereal: return 10.0f;
	        default: return 1.0f;
		}
	}
	
	public float getReloadSpeedMultiplier() {
		// Common: 1x Un: 0.9x R: 0.75x Epic: 0.6x Legendary: 0.5x Mythic: 0.35x Godly: 0.25x
		switch (this) {
	        case Common: return 1.0f;
	        case Uncommon: return 0.9f;
	        case Rare: return 0.75f;
	        case Epic: return 0.6f;
	        case Legendary: return 0.5f;
	        case Mythic: return 0.35f;
	        case Divine: return 0.3f;
	        case Ethereal: return 0.25f;
	        default: return 1.0f;
		}
	}
	
	public float getBloomMultiplier() {
		// Common: 1x Un: 0.95x R: 0.85x Epic: 0.75x Legendary: 0.6x Mythic: 0.55x Godly: 0.5x
		switch (this) {
	        case Common: return 1.0f;
	        case Uncommon: return 0.95f;
	        case Rare: return 0.85f;
	        case Epic: return 0.75f;
	        case Legendary: return 0.6f;
	        case Mythic: return 0.55f;
	        case Divine: return 0.5f;
	        case Ethereal: return 0.43f;
	        default: return 1.0f;
		}
	}
	
	public float getVelocityMultiplier() {
		// Common: 1x un: 1.1 R: 1.25x Epic: 1.35x Legendary: 1.45x Mythic: 1.6x Godly: 1.8x
		switch (this) {
	        case Common: return 1.0f;
	        case Uncommon: return 1.1f;
	        case Rare: return 1.25f;
	        case Epic: return 1.35f;
	        case Legendary: return 1.45f;
	        case Mythic: return 1.6f;
	        case Divine: return 1.8f;
	        case Ethereal: return 2.0f;
	        default: return 1.0f;
		}
	}
	
	public float getFireRateMultiplier() {
		// Common: 1x un: 1.2x Rare: 1.6 Epic: 1.85 Legendary: 2x Mythic: 2.2x Godly: 2.4x
		switch (this) {
	        case Common: return 1.0f;
	        case Uncommon: return 0.9f;
	        case Rare: return 0.82f;
	        case Epic: return 0.76f;
	        case Legendary: return 0.6f;
	        case Mythic: return 0.4f;
	        case Divine: return 0.25f;
	        case Ethereal: return 0.2f;
	        default: return 1.0f;
		}
	}
	
	public float getPierceMultiplier() {
		// Common: 1x un: 1.5x Rare: 2x Epic: 2.5x Legendary: 3x Mythic: 5x Godly: 10x
		switch (this) {
	        case Common: return 1.0f;
	        case Uncommon: return 1.5f;
	        case Rare: return 2f;
	        case Epic: return 2.5f;
	        case Legendary: return 3f;
	        case Mythic: return 5f;
	        case Divine: return 10f;
	        case Ethereal: return 15f;
	        default: return 1.0f;
		}
	}
	
	public int getAmmoModMultiplier() {
		// x/100
		// Common: 10 un: 12 Rare: 13 Epic: 14 Legendary: 15 Mythic: 15 Godly: 17
		switch (this) {
        case Common: return 10;
        case Uncommon: return 12;
        case Rare: return 13;
        case Epic: return 14;
        case Legendary: return 15;
        case Mythic: return 15;
        case Divine: return 17;
        case Ethereal: return 20;
        default: return 10;
		}
	}
	
	public Tier upgrade() {
        switch (this) {
            case Common: return Uncommon;
            case Uncommon: return Rare;
            case Rare: return Epic;
            case Epic: return Legendary;
            case Legendary: return Mythic;
            case Mythic: return Divine;
            case Divine: return Ethereal; // Max tier
	        case Ethereal: return Ethereal;
            default: return this;
        }
    }
	
	public int MissionFrenzyDividend() {
		switch (this) {
        case Common: return 1;
        case Uncommon: return 1;
        case Rare: return 2;
        case Epic: return 3;
        case Legendary: return 7;
        case Mythic: return 10;
        case Divine: return 15;
        case Ethereal: return 20;
        default: return 10;
		}
	}

	public float FlamePerkMultiplier() {
		switch (this) {
        case Common: return 1;
        case Uncommon: return 1.2f;
        case Rare: return 1.4f;
        case Epic: return 1.5f;
        case Legendary: return 1.6f;
        case Mythic: return 1.65f;
        case Divine: return 1.75f;
        case Ethereal: return 1.8f;
        default: return 1;
		}
	}
	
}
