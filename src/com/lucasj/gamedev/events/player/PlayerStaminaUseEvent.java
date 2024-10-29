package com.lucasj.gamedev.events.player;

import com.lucasj.gamedev.events.GameEvent;

public class PlayerStaminaUseEvent implements GameEvent {
	
	
	private float amountOfStamina;
	
	public PlayerStaminaUseEvent(float amount) {
		this.amountOfStamina = amount;
	}
	
	public float getAmountOfStaminaUsed() {
		return this.amountOfStamina;
	}
	

	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}

}
