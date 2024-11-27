package com.lucasj.gamedev.events.level;

import com.lucasj.gamedev.events.GameEvent;

public class LevelUpEvent implements GameEvent {
	
	private int newLevel;
	
	public LevelUpEvent(int newLevel) {
		this.newLevel = newLevel;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}

	public int getNewLevel() {
		return this.newLevel;
	}
	
}
