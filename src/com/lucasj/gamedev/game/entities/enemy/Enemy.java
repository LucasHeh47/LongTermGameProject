package com.lucasj.gamedev.game.entities.enemy;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;

public abstract class Enemy extends Entity {
	
	
	// WAVES DATA --------------------------------
	public static class EnemyWavesData {
		
		public final int startRound;
		public final int spawnRate;
		
		/**
		 * 
		 * @param startRound
		 * @param spawnRate
		 */
		public EnemyWavesData(int startRound, int spawnRate) {
			this.startRound = startRound;
			this.spawnRate = spawnRate;
		}
		
	}
	
	public static class Builder {
        private Game game;
        private Vector2D position;
        private int maxHealth;
        private int movementSpeed;
        private int size;
        private String tag;
        private Class<? extends Enemy> type;

        /**
         * 
         * @param game
         * @param position
         * @param enemy type
         */
        public Builder(Game game, Vector2D position, Class<? extends Enemy> type) {
            this.game = game;
            this.position = position;
            this.type = type;
        }

        // Setter methods for each additional parameter
        public Builder setHealth(int maxHealth) {
            this.maxHealth = maxHealth;
            return this;
        }

        public Builder setMovementSpeed(int movementSpeed) {
            this.movementSpeed = movementSpeed;
            return this;
        }

        public Builder setSize(int size) {
            this.size = size;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Enemy build() {
            // Validate required fields
            if (maxHealth == 0 || movementSpeed == 0 || size == 0 || tag == null) {
                throw new IllegalStateException("All fields must be set before building the enemy");
            }

            try {
                // Find the constructor for the enemy class with the required parameters
                Constructor<? extends Enemy> constructor = type.getConstructor(
                    Game.class, Vector2D.class, int.class, int.class, int.class, String.class
                );

                // Create a new instance of the enemy using the constructor
                return constructor.newInstance(game, position, maxHealth, movementSpeed, size, tag);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Could not create an instance of " + type.getSimpleName());
            }
        }
    }
	
	protected static <T extends Enemy> void registerEnemyType(Class<T> enemyClass, EnemyWavesData data) {
	    registerWavesData(enemyClass, data);
	    addEnemyType(enemyClass);
	}
	
	private static final Map<Class<? extends Enemy>, EnemyWavesData> wavesDataMap = new HashMap<>();
	private static List<Class<? extends Enemy>> enemyTypes = new ArrayList<>();

	public Enemy(Game game, Vector2D position, Vector2D velocity, int maxHealth, int movementSpeed, int size,
			String tag) {
		super(game, position, velocity, maxHealth, movementSpeed, size, tag);
		importance = 2;
		
	}
	
	public static void registerWavesData(Class<? extends Enemy> enemyClass, EnemyWavesData data) {
        wavesDataMap.put(enemyClass, data);
    }

	public static EnemyWavesData getWavesData(Class<? extends Enemy> enemyClass) {
        return wavesDataMap.getOrDefault(enemyClass, null);
    }

	public static List<Class<? extends Enemy>> getEnemyTypes() {
		return Collections.unmodifiableList(enemyTypes);
	}
	
	public static <T extends Enemy> void addEnemyType(Class<T> type) {
		if(Enemy.class.isAssignableFrom(type)) {
			enemyTypes.add(type);
		}
	}

}
