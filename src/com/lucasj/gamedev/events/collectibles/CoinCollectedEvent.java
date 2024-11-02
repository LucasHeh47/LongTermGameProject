package com.lucasj.gamedev.events.collectibles;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.entities.player.Player;

public class CoinCollectedEvent implements GameEvent {

	private int amountCollected;
	private Player player;
	
	public CoinCollectedEvent(int amountCollected, Player playerCollected) {
		this.amountCollected = amountCollected;
		this.player = playerCollected;
	}
	
	public int getAmountCollected() {
		return this.amountCollected;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}

}
