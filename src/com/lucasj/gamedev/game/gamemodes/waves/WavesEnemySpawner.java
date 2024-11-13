package com.lucasj.gamedev.game.gamemodes.waves;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.enemy.Enemy.EnemyWavesData;
import com.lucasj.gamedev.mathutils.Vector2D;

public class WavesEnemySpawner {

	private Game game;
	private int totalSpawnWeight = 0;
    private List<Class<? extends Enemy>> spawnableEnemies = new ArrayList<>();
    private Random random = new Random();
	
    public WavesEnemySpawner(Game game) {
        this.game = game;
        calculateSpawnableEnemies();
    }
    
    public void calculateSpawnableEnemies() {
    	spawnableEnemies.clear();
    	totalSpawnWeight = 0;
    	Enemy.getEnemyTypes().forEach(enemyType -> {
            try {
                EnemyWavesData wavesData = Enemy.getWavesData(enemyType);
                
                // Use the wavesData as needed
                System.out.println(wavesData.startRound);
                if (wavesData != null && wavesData.spawnRate != -1 && wavesData.startRound <= game.getWavesManager().getWave()) {
                    spawnableEnemies.add(enemyType);
                    totalSpawnWeight += wavesData.spawnRate;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
	
    public void spawnEnemy(int health, Vector2D position) {
        if (spawnableEnemies.isEmpty()) {
            System.out.println("No enemies available to spawn.");
            return;
        }

        // Generate a random number between 0 and totalSpawnWeight
        int randomWeight = random.nextInt(totalSpawnWeight) + 1;
        int cumulativeWeight = 0;

        System.out.println("Spawning");
        // Iterate through the enemies to find the one that matches the random weight
        for (Class<? extends Enemy> enemyType : spawnableEnemies) {
            try {
            	EnemyWavesData wavesData = Enemy.getWavesData(enemyType);

                cumulativeWeight += wavesData.spawnRate;

                // If the random number falls within the cumulative weight, spawn this enemy
                if (randomWeight <= cumulativeWeight) {
                	
                    spawn(enemyType, position, health);
                    break;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void spawn(Class<? extends Enemy> enemyType, Vector2D position, int health) {
        try {
            // Example spawning logic
        	System.out.println("Spawned");
            Enemy enemy = new Enemy.Builder(game, position, enemyType).setHealth(health).setMovementSpeed(6).setSize(25).setTag(null).build();
            enemy.setAggroRange(1500);
            enemy.instantiate();
            System.out.println("Spawned enemy: " + enemyType.getSimpleName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
