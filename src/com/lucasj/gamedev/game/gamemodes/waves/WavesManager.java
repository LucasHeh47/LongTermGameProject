package com.lucasj.gamedev.game.gamemodes.waves;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.npc.NPC;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.world.particles.ParticleEmitter;

public class WavesManager {

	private int wave = 0;
	private int enemiesKilledThisWave;
	private int enemiesThisWave;
	private int enemiesSpawnedThisWave;
	private boolean betweenWaves = true;
	private long intermissionTimer;
	private int intermissionTick = 0;
	private int intermissionLength = 3;
	
	private Game game;
	private WavesEnemySpawner enemySpawner;
	
	private long lastSpawn;
	private float spawnRate;
	private Random rand = new Random();
	
	// Base values
    private final int BASE_HEALTH = 25;
    private final int BASE_ENEMIES = 7; // default: 7

    // Growth rates
    private final double HEALTH_GROWTH_RATE = 0.15;
    private final double ENEMY_GROWTH_RATE = 0.12;
	
	public WavesManager(Game game) {
		this.game = game;
		enemySpawner = new WavesEnemySpawner(game);
		spawnRate = 1f;
	}
	
	public void startWaves() {
		wave = 0;
		game.instantiatedEntities.clear();
		game.instantiatedEntitiesOnScreen.clear();
		game.toAddEntities.clear();
		game.toRemoveEntities.clear();
		game.instantiatedCollectibles.clear();
		newWave();
	}
	
	public void newWave() {
		System.out.println("Starting wave");
		wave++;
		intermissionTimer = System.currentTimeMillis();
		enemySpawner.calculateSpawnableEnemies();
		betweenWaves = true;
		enemiesSpawnedThisWave = 0;
		enemiesKilledThisWave = 0;
		enemiesThisWave = getEnemyCount(wave);
	}
	
	public void update(double deltaTime) {
		if(game.isPaused()) return;
	    int parallelThreshold = 1000;

	    // Update all collision surfaces
	    game.getCollisionSurfaces().forEach(surf -> surf.update(deltaTime));

	    // Update collectibles
	    game.instantiatedCollectibles.removeAll(game.toRemoveCollectibles);
	    game.toRemoveCollectibles.clear();
	    game.instantiatedCollectibles.forEach(collectible -> collectible.update(deltaTime));

	    // Add new entities and remove entities marked for removal
	    game.instantiatedEntities.addAll(game.toAddEntities);
	    game.instantiatedEntities.removeAll(game.toRemoveEntities);
	    game.toRemoveEntities.clear();
	    game.toAddEntities.clear();

	    if (game.instantiatedEntities.isEmpty()) return;

	    // Clear the quadtree and reinsert all entities
	    game.getQuadtree().clear();
	    game.instantiatedEntities.forEach(entity -> game.getQuadtree().insert(entity));

	    // Use parallel stream if the number of entities exceeds the threshold
	    if (game.instantiatedEntities.size() > parallelThreshold) {
	        game.instantiatedEntities.parallelStream().forEach(entity -> entity.update(deltaTime));
	    } else {
	        game.instantiatedEntities.forEach(entity -> {
	            entity.update(deltaTime);
	            
	            // Manage entities on screen
	            boolean isOutOfBounds = entity.getPosition().getX() < 0 ||
	                                    entity.getPosition().getX() > game.getWidth() ||
	                                    entity.getPosition().getY() < 0 ||
	                                    entity.getPosition().getY() > game.getHeight();

	            if (isOutOfBounds) {
	                game.instantiatedEntitiesOnScreen.remove(entity);
	            } else {
	                game.instantiatedEntitiesOnScreen.add(entity);
	            }
	        });
	    }

	    // Update particles
	    game.activeParticles.forEach(ParticleEmitter::update);

	    // Check for entity collisions using quadtree optimization
	    game.checkEntityCollisions();

	    // Intermission logic between waves
	    if (betweenWaves) {
	        if ((System.currentTimeMillis() - intermissionTimer) / 1000.0 >= 1) {
	            intermissionTick++;
	            intermissionTimer = System.currentTimeMillis();
	        }
	        if (intermissionTick == intermissionLength) {
	            betweenWaves = false;
	            intermissionTick = 0;
	            System.out.println("Wave " + wave + " started");
	        }
	    } else if (enemiesSpawnedThisWave < enemiesThisWave && (System.currentTimeMillis() - lastSpawn) / 1000.0 > spawnRate) {
	        enemySpawner.spawnEnemy(getEnemyHealth(wave), new Vector2D(rand.nextInt(game.getWidth()), rand.nextInt(game.getHeight())));
	        enemiesSpawnedThisWave++;
	        lastSpawn = System.currentTimeMillis();
	    }

	    // Check if the wave should transition to the next
	    if (enemiesKilledThisWave == enemiesThisWave) {
	        if(this.wave % 5 == 0) game.getPlayer().addGem(2);
	        else game.getPlayer().addGem(1);
	        newWave();
	    }
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		if(betweenWaves) {
			int titleWidth = g2d.getFontMetrics().stringWidth(Integer.toString(intermissionLength-intermissionTick));
			
			g2d.setColor(Color.black);
			Font currentFont = g2d.getFont(); // Get the current font
	        Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), 50); // Increase size to 20

	        // Set the new font for the Graphics2D object
	        g2d.setFont(newFont);
			
	        g2d.drawString(Integer.toString(intermissionLength-intermissionTick), (game.getWidth() - titleWidth) / 2, 100);
		}

		g2d.setFont(game.font.deriveFont(64f)); // Derive the font size explicitly as a float
		
		// Measure the width of the money string to center it if needed
	    int titleWidth = g2d.getFontMetrics().stringWidth("$" + Integer.toString(this.wave));

	    g2d.setColor(Color.black);
	    g2d.drawString("Wave: " + Integer.toString(this.wave), 520, game.getHeight() - 125);
		
	}
	
	public void killedEnemy() {
		enemiesKilledThisWave++;
	}
	
	public int getEnemyHealth(int wave) {
        return (int) (BASE_HEALTH * Math.pow(1 + HEALTH_GROWTH_RATE, wave - 1));
    }

    public int getEnemyCount(int wave) {
    	double num = Math.pow(1 + ENEMY_GROWTH_RATE, (double) (wave-1));
        int amount = (int) (BASE_ENEMIES * num);
        return amount;
    }

	public int getWave() {
		return wave;
	}

}
