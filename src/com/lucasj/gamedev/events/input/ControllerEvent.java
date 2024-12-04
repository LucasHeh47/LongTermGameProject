package com.lucasj.gamedev.events.input;

import com.lucasj.gamedev.essentials.controls.Controls;
import com.lucasj.gamedev.events.GameEvent;

public class ControllerEvent implements GameEvent {

	private Controls key;
	
	public ControllerEvent(Controls key) {
		this.setKey(key);
	}

	public Controls getKey() {
		return key;
	}

	public void setKey(Controls key) {
		this.key = key;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}
	
}
