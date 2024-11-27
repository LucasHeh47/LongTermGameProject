package com.lucasj.gamedev.game.gamemodes.waves;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityDamagedEvent;
import com.lucasj.gamedev.events.entities.EntityDamagedEventListener;
import com.lucasj.gamedev.events.entities.EntityDeathEvent;
import com.lucasj.gamedev.events.entities.EntityDeathEventListener;
import com.lucasj.gamedev.events.waves.WaveEndEvent;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.npc.NPCManager;
import com.lucasj.gamedev.game.gamemodes.waves.missions.Mission;
import com.lucasj.gamedev.mathutils.Vector2D;

public class WavesManager implements EntityDamagedEventListener, EntityDeathEventListener {

	private int wave = 0;
	private int enemiesKilledThisWave;
	private int enemiesThisWave;
	private int enemiesSpawnedThisWave;
	private boolean betweenWaves = true;
	private long intermissionTimer;
	private int intermissionTick = 0;
	private int intermissionLength = 1;
	
	private Game game;
	private WavesEnemySpawner enemySpawner;
	
	private MissionManager missionManager;
	
	private boolean hasGameStarted = false;
	
	private NPCManager npcManager;
	
	private long lastSpawn;
	private float spawnRate;
	private Random rand = new Random();
	
	// Base values
    private final int BASE_HEALTH = 50;
    private final int BASE_ENEMIES = 7; // default: 7

    // Growth rates
    public static final double HEALTH_GROWTH_RATE = 0.15;
    public static final double ENEMY_GROWTH_RATE = 0.12;
	
	public WavesManager(Game game) {
		this.game = game;
		this.missionManager = new MissionManager(game, this);
		spawnRate = 0.1f;
		game.getEventManager().addListener(this, EntityDamagedEvent.class);
		game.getEventManager().addListener(this, EntityDeathEvent.class);
	}
	
	public void startWaves() {
		this.hasGameStarted = true;
		wave = 0;
		this.enemiesKilledThisWave = 0;
		this.enemiesSpawnedThisWave = 0;
		Mission.activeMission = null;
		enemySpawner = new WavesEnemySpawner(game);
		game.instantiatedEntities.clear();
		game.instantiatedEntitiesOnScreen.clear();
		game.instantiatedCollectibles.clear();
		npcManager = new NPCManager(game, game.getPlayer());
		npcManager.instantiateNPCs();
	}
	
	public void newWave() {
		wave++;
		intermissionTimer = System.currentTimeMillis();
		enemySpawner.calculateSpawnableEnemies();
		betweenWaves = true;
		this.enemiesSpawnedThisWave = 0;
		this.enemiesKilledThisWave = 0;
		this.enemiesThisWave = getEnemyCount(wave);
		this.missionManager.setCanStartMission(true);
	}
	
	public void update(double deltaTime) {
		
		if(!hasGameStarted) return;
		if(game.isPaused()) return;
		game.getPlayer().update(deltaTime);
	    // Update all collision surfaces
	    game.getCollisionSurfaces().forEach(surf -> surf.update(deltaTime));

	    // Update collectibles
	    
	    game.instantiatedCollectibles.forEach(collectible -> collectible.update(deltaTime));

	    //if (game.instantiatedEntities.isEmpty()) return; // why is this here

	    // Clear the quadtree and reinsert all entities
	    game.getQuadtree().clear();
	    game.instantiatedEntities.forEach(entity -> game.getQuadtree().insert(entity));
	    
        game.instantiatedEntitiesOnScreen.clear();

        game.instantiatedEntities.forEach(entity -> {
    		Vector2D cameraPosition = game.getCamera().getWorldPosition();
    		Vector2D viewport = game.getCamera().getViewport();
            double leftBound = cameraPosition.getX() - (viewport.getX() / 2);
            double rightBound = cameraPosition.getX() + viewport.getX() * 1.5;
            double topBound = cameraPosition.getY() - (viewport.getY() / 2);
            double bottomBound = cameraPosition.getY() + viewport.getY() * 1.5;
            
            
            // Manage entities on screen
            boolean isOutOfBounds = entity.getPosition().getX() < leftBound ||
                                    entity.getPosition().getX() > rightBound ||
                                    entity.getPosition().getY() < topBound ||
                                    entity.getPosition().getY() > bottomBound;
            

            if (isOutOfBounds) {
                game.instantiatedEntitiesOnScreen.remove(entity);
            } else {
                game.instantiatedEntitiesOnScreen.add(entity);
            }
            entity.update(deltaTime);
            
            
        });

	    // Update particles

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
	        }
	    } else if (enemiesSpawnedThisWave < enemiesThisWave && (System.currentTimeMillis() - lastSpawn) / 1000.0 > spawnRate) {
	        if(game.party != null && game.party.getHost().getUsername().equals(game.username)) {
		    	enemySpawner.spawnEnemy(getEnemyHealth(wave), new Vector2D(rand.nextInt(game.getWidth()), rand.nextInt(game.getHeight())));
		        enemiesSpawnedThisWave++;
		        lastSpawn = System.currentTimeMillis();
		        
		        game.getSocketClient().getPacketManager().syncEnemiesPacket();
		        
	        } else if (game.party == null) {
		    	enemySpawner.spawnEnemy(getEnemyHealth(wave), new Vector2D(rand.nextInt(game.getWidth()), rand.nextInt(game.getHeight())));
		        enemiesSpawnedThisWave++;
		        lastSpawn = System.currentTimeMillis();
	        }
	    }

	    // Check if the wave should transition to the next
	    if (enemiesKilledThisWave >= enemiesThisWave) {
	        WaveEndEvent e = new WaveEndEvent(wave);
	        this.game.getEventManager().dispatchEvent(e);
	        newWave();
	    }
	    
	    this.missionManager.update(deltaTime);
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		if(!this.hasGameStarted && !game.getPlayer().isPickingClass()) {
			g2d.setColor(Color.black);
			g2d.setFont(game.font.deriveFont(256));
			int titleWidth = g2d.getFontMetrics().stringWidth("Waiting For Party...");
	        g2d.drawString("Waiting For Party...", (game.getWidth() - titleWidth) / 2, 100);
	        return;
		}
		if(game.getPlayer().isPickingClass()) return;
		
		if(betweenWaves) {
			int titleWidth = g2d.getFontMetrics().stringWidth(Integer.toString(intermissionLength-intermissionTick));
			
			g2d.setColor(Color.black);
			Font currentFont = g2d.getFont(); // Get the current font
	        Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), 50); // Increase size to 20

	        // Set the new font for the Graphics2D object
	        g2d.setFont(newFont);
			
	        g2d.drawString(Integer.toString(intermissionLength-intermissionTick), (game.getWidth() - titleWidth) / 2, 100);
		}

		g2d.setFont(game.font.deriveFont(58f)); // Derive the font size explicitly as a float
	    g2d.setColor(Color.black);
	    g2d.drawString("Wave: " + Integer.toString(this.wave), 520, game.getHeight() - 160);
//	    g2d.drawString("Left: " + Integer.toString(enemiesThisWave - enemiesKilledThisWave), 520, game.getHeight() - 100);
//	    int enemyCount = (int) game.instantiatedEntities.stream()
//                .filter(entity -> entity instanceof Enemy)
//                .count();
//
//	    g2d.drawString("Active: " + enemyCount, 520, game.getHeight() - 40);
	    
	    this.missionManager.render(g2d);
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

	public NPCManager getNPCManager() {
		return npcManager;
	}

	public boolean hasGameStarted() {
		return hasGameStarted;
	}

	public void setHasGameStarted(boolean hasGameStarted) {
		this.hasGameStarted = hasGameStarted;
	}

	public MissionManager getMissionManager() {
		return missionManager;
	}

	@Override
	public void onEntityDamaged(EntityDamagedEvent e) {
		if(game.party == null || game.party.getHost().getUsername().equals(game.username)) return;
		if(e.getEntity() instanceof Enemy) {
			game.getSocketClient().getPacketManager().playerUpdateEnemyPacket((Enemy) e.getEntity());
		}
	}

	@Override
	public void onEntityDeath(EntityDeathEvent e) {
		if(game.party == null || game.party.getHost().getUsername().equals(game.username)) return;
		if(e.getEntity() instanceof Enemy) {
			game.getSocketClient().getPacketManager().playerKilledEnemyPacket((Enemy) e.getEntity());
		}
	}

}
