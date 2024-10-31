package com.lucasj.gamedev.game.weapons;

public class Ammo {
	public static enum AmmoType {
	
		Mini(), // Pistol, SMG
		Medium(), // AR
		Large(), // Sniper
		Shells(), // Shotgun
		Explosive() // RPG if we add them
		
	}
	
	public static final int maxHeldMiniAmmo = 240;
	public static final int maxHeldMediumAmmo = 510;
	public static final int maxHeldLargeAmmo = 40;
	public static final int maxHeldShotgunShellsAmmo = 80;
	public static final int maxHeldExplosiveAmmo = 16;
	
}
