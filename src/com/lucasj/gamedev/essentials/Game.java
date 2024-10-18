package com.lucasj.gamedev.essentials;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.events.MouseClickEventListener;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.enemy.Zombie;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.gamemodes.waves.WavesManager;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.physics.CollisionSurface;
import com.lucasj.gamedev.settings.SettingsManager;
import com.lucasj.gamedev.utils.GraphicUtils;
import com.lucasj.gamedev.world.map.MapManager;
import com.lucasj.gamedev.world.particles.ParticleEmitter;

@SuppressWarnings("unused")
public class Game implements MouseClickEventListener{

    private InputHandler input;
    public List<Entity> instantiatedEntities;
    public List<Entity> toAddEntities;
    public List<Entity> toRemoveEntities;
    private EntityCollisionEvent collisionEvent;

	public List<ParticleEmitter> activeParticles;
    GameState gameState;
    private SettingsManager settings;
    private Dimension screen;
    private MapManager mapm;
    private GraphicUtils gUtils;
    private WavesManager wavesManager;
    
    private Player p;

    // Button variables declared at class level
    private int playButtonX, playButtonY, playWidth;
    private int exitButtonX, exitButtonY, exitWidth;

    public Game(InputHandler input, GraphicUtils gUtils, SettingsManager settings, Dimension screen) {
        this.settings = settings;
        this.screen = screen;
        instantiatedEntities = new ArrayList<Entity>();
        toAddEntities = new ArrayList<Entity>();
        toRemoveEntities = new ArrayList<Entity>();
        this.gUtils = gUtils;
        mapm = new MapManager();
        wavesManager = new WavesManager(this);
        
        this.input = input;
        input.addMouseClickListener(this);
        
        gameState = GameState.mainmenu;
        
        activeParticles = new ArrayList<>();
        
        p = (Player) new Player(this, input).setTag("Player");
        p.setPosition(new Vector2D(settings.getIntSetting("width") / 2, settings.getIntSetting("height") / 2));
        p.instantiate();
        
    }

    public void update(double deltaTime) {
        if (gameState == GameState.game) { // -------------------------------------------------------------------------------- Game State
            int parallelThreshold = 1000;
//            System.out.println("In: " + instantiatedEntities.size());
//            System.out.println("Adding: " + toAddEntities.size());
//            System.out.println("Removing: " + toRemoveEntities.size());
            instantiatedEntities.addAll(toAddEntities);
            instantiatedEntities.removeAll(toRemoveEntities);
            toRemoveEntities.clear();
            toAddEntities.clear();
            if (instantiatedEntities.size() <= 0) return;
            if (instantiatedEntities.size() > parallelThreshold) {
                instantiatedEntities.parallelStream().forEach(entity -> {
                	entity.update(deltaTime);
                });
            } else {
                instantiatedEntities.forEach(entity -> {
                	entity.update(deltaTime);
                });
            }
            
            activeParticles.forEach(particle -> {
            	particle.update();
            });

            checkEntityCollisions();
            wavesManager.update(deltaTime);
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(gameState == GameState.mainmenu) {
	        // Check if the "Play" button was clicked
	        if (x >= playButtonX - 20 && x <= playButtonX + playWidth + 20 &&
	            y >= playButtonY - 40 && y <= playButtonY + 10) {
	            // Switch to game state
	            gameState = GameState.game;
	        }
	
	        // Check if the "Exit" button was clicked
	        if (x >= exitButtonX - 20 && x <= exitButtonX + exitWidth + 20 &&
	            y >= exitButtonY - 40 && y <= exitButtonY + 10) {
	            // Exit the game or perform exit logic
	            System.exit(0);  // Close the application
	        }
        }
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (gameState == GameState.mainmenu) {
            // Cast to Graphics2D for more control over rendering

            // Set font and color for the title
            g2d.setFont(new Font("Arial", Font.BOLD, 50));  // Large font for the title
            g2d.setColor(Color.WHITE);

            // Draw the title of the game "Game"
            String title = "Game";
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (getWidth() - titleWidth) / 2, 100);  // Centered horizontally, Y = 100

            // Set font and color for the buttons
            g2d.setFont(new Font("Arial", Font.PLAIN, 30));  // Medium font for buttons
            g2d.setColor(Color.LIGHT_GRAY);

            // Update "Play" button variables
            String playText = "Play";
            playWidth = g2d.getFontMetrics().stringWidth(playText);
            playButtonX = (getWidth() - playWidth) / 2;
            playButtonY = 200;  // Y-position for the "Play" button
            g2d.fillRect(playButtonX - 20, playButtonY - 40, playWidth + 40, 50);  // Draw button rectangle
            g2d.setColor(Color.BLACK);  // Text color
            g2d.drawString(playText, playButtonX, playButtonY);  // Draw "Play" text

            // Update "Exit" button variables
            String exitText = "Exit";
            exitWidth = g2d.getFontMetrics().stringWidth(exitText);
            exitButtonX = (getWidth() - exitWidth) / 2;
            exitButtonY = 300;  // Y-position for the "Exit" button
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(exitButtonX - 20, exitButtonY - 40, exitWidth + 40, 50);  // Draw button rectangle
            g2d.setColor(Color.BLACK);  // Text color
            g2d.drawString(exitText, exitButtonX, exitButtonY);  // Draw "Exit" text

            // Optionally, draw button borders or more styling for clarity
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(playButtonX - 20, playButtonY - 40, playWidth + 40, 50);  // Border for Play button
            g2d.drawRect(exitButtonX - 20, exitButtonY - 40, exitWidth + 40, 50);  // Border for Exit button
        }

        if (gameState == GameState.game) { // -------------------------------------------------------------------------------- Game State
            mapm.render(g);
            instantiatedEntities.forEach(entity -> {
                entity.render(g);
            });
            wavesManager.render(g);
        }
        gUtils.drawVignette(g2d, getWidth(), getHeight(), 0, 0, 0, 180);
    }
    
    private void renderSurface(Graphics g, CollisionSurface surface) {
        g.setColor(Color.GREEN);  // Use green for the ground
        g.fillRect((int) surface.getPosition().getX(), 
                   (int) surface.getPosition().getY(),
                   surface.getWidth(),
                   surface.getHeight());
    }
    
    private void checkEntityCollisions() {
        // Loop through all entities and check for collisions
        for (int i = 0; i < instantiatedEntities.size(); i++) {
            Entity entityA = instantiatedEntities.get(i);
            for (int j = i + 1; j < instantiatedEntities.size(); j++) {
                Entity entityB = instantiatedEntities.get(j);

                if (entityA.isCollidingWith(entityB)) {
                	Entity collider;
                	Entity entity;
                	if(entityA.importance > entityB.importance) {
                		collider = entityB;
                		entity = entityA;
                	} else if (entityB.importance > entityA.importance) {
                		collider = entityA;
                		entity = entityB;
                	} else return;
                    EntityCollisionEvent e = new EntityCollisionEvent(
                        entity,
                        collider,
                        new Vector2D(
                            (entityA.getPosition().getX() + entityB.getPosition().getX()) / 2,
                            (entityA.getPosition().getY() + entityB.getPosition().getY()) / 2
                        )
                    );

                    // Notify the listeners of both entities
                    entityA.onEntityCollision(e);
                    entityB.onEntityCollision(e);
                }
            }
        }
    }
    
    private void initializeEnemyTypes() {
    	Enemy.addEnemyType(Zombie.class);
    }

    public int getWidth() {
        return screen.width;
    }

    public int getHeight() {
        return screen.height;
    }

	@Override
	public void onMousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public InputHandler getInput() {
		return input;
	}

    public EntityCollisionEvent getCollisionEvent() {
		return collisionEvent;
	}

	public Player getPlayer() {
		return p;
	}

	public Dimension getScreen() {
		return screen;
	}

	public void setScreen(Dimension screen) {
		settings.setIntSetting("width", (int)screen.getWidth());
		settings.setIntSetting("height", (int)screen.getHeight());
		this.screen = screen;
	}

	public SettingsManager getSettings() {
		return settings;
	}
}
