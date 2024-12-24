package com.lucasj.gamedev.game.skills;

import com.lucasj.gamedev.essentials.Game;

public abstract class Skill {
	
	protected Game game;
	protected int manaCost;
	
	public Skill(Game game, int manaCost) {
		this.game = game;
		this.manaCost = manaCost;
	}
	
	public int getManaCost() {
		return this.manaCost;
	}

}
