package com.lucasj.gamedev.game.gamemodes.waves;

import java.awt.Graphics;
import java.util.Random;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;

public class WavesManager {

	int wave = 0;
	
	private Game game;
	private WavesEnemySpawner enemySpawner;
	
	private long lastSpawn;
	private float spawnRate;
	Random rand = new Random();
	
	public WavesManager(Game game) {
		this.game = game;
		enemySpawner = new WavesEnemySpawner(game);
		spawnRate = 3;
	}
	
	public void update(double deltaTime) {
		
		if((System.currentTimeMillis() - lastSpawn)/1000.0 > spawnRate) {
			enemySpawner.spawnEnemy(new Vector2D(rand.nextInt(game.getWidth()), rand.nextInt(game.getHeight())));
			System.out.println("spawn!");
			lastSpawn = System.currentTimeMillis();
		}
		
	}
	
	public void render(Graphics g) {
		
		
		
	}

}
