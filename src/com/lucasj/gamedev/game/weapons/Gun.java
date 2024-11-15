package com.lucasj.gamedev.game.weapons;

import java.awt.Image;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;

public abstract class Gun {

	protected float damage;
	protected float projectileSpeed;
	protected float fireRate;
	protected int clipSize;
	protected int currentClip;
	protected float range;
	protected GunType gunType;
	protected float reloadSpeed;
	protected boolean isAutomatic;
	protected Game game;
	protected Player p;
	protected float bloom = 0;
	protected int pierce = 0;
	
	protected Image UIImage;
	
	protected String gunFireSound;
	
	protected Tier tier = Tier.Common;
	
	protected boolean isReloading = false;
	protected long reloadStartTime;
	
	public Gun(Game game, Player p, float damage, float projectileSpeed, float fireRate, int clipSize, float range, GunType gunType, float reloadSpeed, boolean isAutomatic, float bloom, String fireSoundDir, Image ui) {
		this.game = game;
		this.p = p;
        this.damage = damage;
        this.projectileSpeed = projectileSpeed;
        this.fireRate = fireRate;
        this.clipSize = clipSize;
        this.currentClip = clipSize;
        this.range = range;
        this.gunType = gunType;
        this.reloadSpeed = reloadSpeed;
        this.isAutomatic = isAutomatic;
        this.UIImage = ui;
        this.bloom = bloom;
        this.gunFireSound = fireSoundDir;
    }
	
	public void fire() {
		
	}
	public void update() {
		if(isReloading && (System.currentTimeMillis() - reloadStartTime)/1000.0 >= this.getReloadSpeed()) {
			isReloading = false;
			this.currentClip = this.getClipSize();
		}
	}
	public void reload() {
		this.isReloading = true;
		reloadStartTime = System.currentTimeMillis();
	}
	
	public void upgrade() {
		this.tier = this.tier.upgrade();
	}
	
	public Tier getNextTier() {
		return this.tier.upgrade();
	}

	public float getDamage() {
		return damage * this.tier.getDamageMultiplier();
	}

	public float getProjectileSpeed() {
		return projectileSpeed * this.tier.getVelocityMultiplier();
	}

	public float getBloom() {
		return bloom * this.tier.getBloomMultiplier();
	}


	public float getFireRate() {
		return fireRate * this.tier.getFireRateMultiplier();
	}

	public int getClipSize() {
		return clipSize;
	}

	public int getCurrentClip() {
		return currentClip;
	}
	
	public void useRound() {
		this.currentClip--;
		if(this.currentClip == 0) this.reload();
	}

	public float getRange() {
		return range;
	}

	public GunType getGunType() {
		return gunType;
	}

	public float getReloadSpeed() {
		return reloadSpeed * this.tier.getReloadSpeedMultiplier();
	}

	public boolean isAutomatic() {
		return isAutomatic;
	}

	public Tier getTier() {
		return tier;
	}

	public boolean isReloading() {
		return isReloading;
	}

	public int getPierce() {
		return (int) (pierce * this.tier.getPierceMultiplier());
	}

	public Image getUIImage() {
		return UIImage;
	}

	public String getGunFireSound() {
		return gunFireSound;
	}
	
}
