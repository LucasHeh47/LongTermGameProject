package com.lucasj.gamedev.game.gamemodes.waves;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.mathutils.Vector2D;

public class WavesEnemySpawner {

	private Game game;
	private int totalSpawnWeight = 0;
    private List<Class<? extends Enemy>> spawnableEnemies = new ArrayList<>();
    private Random random = new Random();
	
    public WavesEnemySpawner(Game game) {
        this.game = game;

        Enemy.getEnemyTypes().forEach(enemyType -> {
            try {
                // Use reflection to get the static method getWavesData() from the class
                Enemy.EnemyWavesData wavesData = (Enemy.EnemyWavesData) enemyType
                    .getMethod("getWavesData")
                    .invoke(null);  // Pass null because it's a static method
                
                // Use the wavesData as needed
                if (wavesData != null && wavesData.spawnRate != -1) {
                    spawnableEnemies.add(enemyType);
                    totalSpawnWeight += wavesData.spawnRate;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
	
    public void spawnEnemy(Vector2D position) {
        if (spawnableEnemies.isEmpty()) {
            System.out.println("No enemies available to spawn.");
            return;
        }

        // Generate a random number between 0 and totalSpawnWeight
        int randomWeight = random.nextInt(totalSpawnWeight) + 1;
        int cumulativeWeight = 0;

        // Iterate through the enemies to find the one that matches the random weight
        for (Class<? extends Enemy> enemyType : spawnableEnemies) {
            try {
                // Get the waves data for this enemy type
                Enemy.EnemyWavesData wavesData = (Enemy.EnemyWavesData) enemyType
                    .getMethod("getWavesData")
                    .invoke(null);

                cumulativeWeight += wavesData.spawnRate;

                // If the random number falls within the cumulative weight, spawn this enemy
                if (randomWeight <= cumulativeWeight) {
                	
                	// Calculuate Damage based on wave --------------
                	
                    spawn(enemyType, position);
                    break;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void spawn(Class<? extends Enemy> enemyType, Vector2D position) {
        try {
            // Example spawning logic
            Enemy enemy = new Enemy.Builder(game, position, enemyType).setHealth(100).setMovementSpeed(100).setSize(50).setTag(null).build();

            System.out.println("Spawned enemy: " + enemyType.getSimpleName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
