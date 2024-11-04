package com.lucasj.gamedev.events.player;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.entities.player.Player;

public class PlayerDamageTakenEvent implements GameEvent {
	
	private float damageTaken;
	private Player player;
	
	public PlayerDamageTakenEvent(Player player, float damageTaken) {
		this.player = player;
		this.damageTaken = damageTaken;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}

	public float getDamageTaken() {
		return damageTaken;
	}

	public void setDamageTaken(float damageTaken) {
		this.damageTaken = damageTaken;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
