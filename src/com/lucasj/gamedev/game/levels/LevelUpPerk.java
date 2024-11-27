package com.lucasj.gamedev.game.levels;

public enum LevelUpPerk {

	SniperMutantModOnPierce(), // Snipers can cause mutant mod on pierce rather than on impact
	ExtraFlameWeaponTier(), // Flame deals extra damage based on weapon tier
	StunPermSlow(), // Stun permanently slows the enemy
	StunPermReduceDamage(), // Stun reduces damage the enemy deals
	CoinSpeedBoost(), // Collecting coins grant a quick and small speed boost
	NoOneTap(), // When you have full hp and get a hit that deals on your damage, you have 1 hp instead
	ExtraMission(), // Extra mission per round
	
	
	Mythic(), // Level 25
	Divine(), // Level 40
	Ethereal(); // Level 65
	
	
	
}
