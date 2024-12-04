package com.lucasj.gamedev.game.entities.projectiles.data;

import java.util.Random;

import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.levels.LevelUpPerk;

public class BulletMutantModData {
	
	private int generation;
	private boolean willSplit = false;
	
	public BulletMutantModData(int generation) {
		this.generation = generation;
		if(generation == 1) willSplit = true;
		else {
			Random rand = new Random();
			int chance = 33;
			if(Player.getGlobalStats().hasPerkUnlocked(LevelUpPerk.MutantModChance)) chance = 66;
			willSplit = rand.nextInt(100) <= chance;
		}
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public boolean willSplit() {
		return willSplit;
	}

}
