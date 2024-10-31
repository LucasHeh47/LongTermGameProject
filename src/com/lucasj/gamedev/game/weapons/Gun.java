package com.lucasj.gamedev.game.weapons;

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
	
	public Gun(Game game, Player p, float damage, float projectileSpeed, float fireRate, int clipSize, float range, GunType gunType, float reloadSpeed, boolean isAutomatic) {
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
    }
	
	public void fire() {
		
	}
	public abstract void reload();
	public abstract void upgrade();
	
}
