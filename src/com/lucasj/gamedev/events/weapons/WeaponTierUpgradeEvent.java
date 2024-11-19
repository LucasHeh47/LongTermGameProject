package com.lucasj.gamedev.events.weapons;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.weapons.Tier;

public class WeaponTierUpgradeEvent implements GameEvent {

	private Tier newTier;
	private Player player;
	
	public WeaponTierUpgradeEvent(Player player, Tier tier) {
		this.newTier = newTier;
		this.player = player;
	}
	
	
	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}


	public Tier getNewTier() {
		return newTier;
	}


	public Player getPlayer() {
		return player;
	}

}
