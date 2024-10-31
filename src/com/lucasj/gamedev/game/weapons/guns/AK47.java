package com.lucasj.gamedev.game.weapons.guns;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.game.weapons.GunType;

public class AK47 extends Gun {

	public static final float fireRate = 0.5f;
	public static final int clipSize = 30;
	public static final float range = 2.5f;
	public static final float reloadSpeed = 1.0f;
	
	public AK47(Game game, Player p, float damage, float projectileSpeed) {
		super(game, p, damage, projectileSpeed, fireRate, clipSize, range, GunType.AR, reloadSpeed, true);
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void upgrade() {
		// TODO Auto-generated method stub
		
	}
	
	
}
