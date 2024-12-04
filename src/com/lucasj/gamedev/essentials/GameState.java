package com.lucasj.gamedev.essentials;

public enum GameState {
	mainmenu(),
	wavesmenu(),
	levelmenu(),
	waves(),
	wavesGameOver();
	
	public boolean is(GameState...s) { // First attempt at this parameter shit
		for (GameState state : s) {
			if (state == this) return true;
		}
		return false;
	}
}
