package com.lucasj.gamedev.game.entities.projectiles.data;

import java.util.Random;

public class BulletMutantModData {
	
	private int generation;
	private boolean willSplit = false;
	
	public BulletMutantModData(int generation) {
		this.generation = generation;
		if(generation == 1) willSplit = true;
		else {
			Random rand = new Random();
			willSplit = rand.nextInt(100) >= 33;
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
