package com.lucasj.gamedev.events.collectibles;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.entities.player.Player;

public class CoinCollectedEvent implements GameEvent {

	private int amountCollected;
	private Player playerCollected;
	
	public CoinCollectedEvent(int amountCollected, Player playerCollected) {
		this.amountCollected = amountCollected;
		this.playerCollected = playerCollected;
	}
	
	public int getAmountCollected() {
		return this.amountCollected;
	}

	public Player getPlayerCollected() {
		return playerCollected;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}

}
