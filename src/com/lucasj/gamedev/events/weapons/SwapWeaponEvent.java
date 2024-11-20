package com.lucasj.gamedev.events.weapons;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.weapons.Gun;

public class SwapWeaponEvent implements GameEvent{

	private Gun newPrimary;
	private Gun newSecondary;
	
	public SwapWeaponEvent(Gun newPrimary, Gun newSecondary) {
		this.newPrimary = newPrimary;
		this.newSecondary = newSecondary;
	}
	
	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}

	public Gun getNewPrimary() {
		return newPrimary;
	}

	public Gun getNewSecondary() {
		return newSecondary;
	}
	
}
