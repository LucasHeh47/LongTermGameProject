package com.lucasj.gamedev.events.input;

import com.lucasj.gamedev.essentials.controls.Controls;
import com.lucasj.gamedev.events.GameEvent;

public class ControllerEvent implements GameEvent {

	private Controls key;
	private boolean pressed;
	
	public ControllerEvent(Controls key, boolean pressed) {
		this.setKey(key);
		this.pressed = pressed;
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
	
	public boolean getPressed() {
		return pressed;
	}
	
}
