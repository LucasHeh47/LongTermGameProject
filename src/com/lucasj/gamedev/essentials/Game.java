package com.lucasj.gamedev.essentials;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.ui.Menus;
import com.lucasj.gamedev.events.EventManager;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.collectibles.Collectible;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.enemy.Skeleton;
import com.lucasj.gamedev.game.entities.enemy.Zombie;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.entities.projectiles.Projectile;
import com.lucasj.gamedev.game.gamemodes.waves.WavesManager;
import com.lucasj.gamedev.mathutils.Quadtree;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.os.GameData;
import com.lucasj.gamedev.physics.CollisionSurface;
import com.lucasj.gamedev.settings.SettingsManager;
import com.lucasj.gamedev.utils.GraphicUtils;
import com.lucasj.gamedev.world.map.MapManager;
import com.lucasj.gamedev.world.particles.ParticleEmitter;

@SuppressWarnings("unused")
public class Game {

    private InputHandler input;
    public List<Entity> instantiatedEntities;
    public List<Collectible> instantiatedCollectibles;
    public List<Collectible> toRemoveCollectibles;
    public List<Entity> instantiatedEntitiesOnScreen;
    private Quadtree<Entity> entityQuadtree;
    public List<Entity> toAddEntities;
    public List<Entity> toRemoveEntities;
    private EntityCollisionEvent collisionEvent;
    
    private List<CollisionSurface> collisionSurfaces;

	public List<ParticleEmitter> activeParticles;
    private GameState gameState;
    private SettingsManager settings;
    private Dimension screen;
    private MapManager mapm;
    private GraphicUtils gUtils;
    private WavesManager wavesManager;
    private Quadtree quadtree;
    private Camera camera;
    private EventManager eventManager;
    
    public boolean testing = false;
    
    private short renderScreenTick = 0;
    private short lengthToCheckScreen = 5;
    
    private boolean paused;
    
    private Menus menus;
    
    private Player p;
    
    public Font font;

    GameData gameData;
    
    public Game(InputHandler input, GraphicUtils gUtils, SettingsManager settings, Dimension screen) {
    	gameData = new GameData("projectgame", "playerStats.dat");
    	Player.getGlobalStats().load(gameData);
    	eventManager = new EventManager();
    	referenceEnemies();
        this.settings = settings;
        this.screen = screen;
        paused = false;
        gameState = GameState.mainmenu;
        quadtree = new Quadtree(0, new Vector2D(0, 0), new Vector2D(screen.width, screen.height));
        menus = new Menus(this);

        this.input = input;
        input.addMouseClickListener(menus);
        
        camera = new Camera(this, new Vector2D(getWidth(), getHeight()), new Vector2D(0, 0));
        instantiatedEntities = new ArrayList<Entity>();
        instantiatedEntitiesOnScreen = new ArrayList<Entity>();
        entityQuadtree = new Quadtree<>(0, new Vector2D(0, 0), new Vector2D(screen.width, screen.height));
        
        instantiatedCollectibles = new ArrayList<Collectible>();
        toRemoveCollectibles = new ArrayList<Collectible>();
        
        toAddEntities = new ArrayList<Entity>();
        toRemoveEntities = new ArrayList<Entity>();
        this.gUtils = gUtils;
        mapm = new MapManager(this);
        wavesManager = new WavesManager(this);
        
        activeParticles = new ArrayList<>();
        collisionSurfaces = new ArrayList<>();
        CollisionSurface surface = new CollisionSurface(this, new Vector2D(300, 400), 100, 500);
        
        File fontFile = new File(SpriteTools.assetDirectory + "/DanielLinssen/m6x11.ttf");
        try {
            // Load the font file and derive it at a default size
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, 24f);
            // Register the font in the GraphicsEnvironment
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        
    }

    public void update(double deltaTime) {
        if (gameState == GameState.waves) { // -------------------------------------------------------------------------------- Game State
        	this.renderScreenTick++;
        	if (this.renderScreenTick >= this.lengthToCheckScreen) {
        		this.entityQuadtree.setBounds(this.camera.getWorldPosition(), this.camera.getWorldPosition().add(this.camera.getViewport()));
        		Vector2D cameraPosition = camera.getWorldPosition();
        		double viewportWidth = camera.getViewport().getX();
        		double viewportHeight = camera.getViewport().getY();

        		double extendedLeftBound = cameraPosition.getX() - (viewportWidth * camera.getScale() / 2);
        		double extendedRightBound = cameraPosition.getX() + viewportWidth  * camera.getScale() + (viewportWidth * camera.getScale() / 2);
        		double extendedTopBound = cameraPosition.getY() - (viewportHeight * camera.getScale()  / 2);
        		double extendedBottomBound = cameraPosition.getY() + viewportHeight * camera.getScale()  + (viewportHeight * camera.getScale()  / 2);
        		
    	        List<Entity> newOnScreenEntities = new ArrayList<>();

    	        entityQuadtree.retrieve(newOnScreenEntities, new Vector2D(extendedLeftBound, extendedTopBound), 
                        new Vector2D(extendedRightBound, extendedBottomBound));
    	        instantiatedEntitiesOnScreen = newOnScreenEntities; // Only swap after retrieval
    	        this.renderScreenTick = 0;
    	    }
        	
            wavesManager.update(deltaTime);
            
        }
    }
    
    public void instantiatePlayer() {
    	p = (Player) new Player(this, getInput()).setTag("Player").setMaxHealth(100).setHealth(100);
        p.setPosition(new Vector2D(this.getSettings().getIntSetting("width"), this.getSettings().getIntSetting("height")));
        p.instantiate();
    }
    
    public void referenceEnemies() {
    	System.out.println("Initialzing enemy types...");
    	Zombie.initializeClass();
    	Skeleton.initializeClass();
    }

    public void render(Graphics g) {
    	Graphics2D g2d = (Graphics2D) g;
    	menus.render(g);
    	if (gameState == GameState.waves) { // -------------------------------------------------------------------------------- Game State
    		
    		Vector2D cameraPosition = camera.getWorldPosition();
    	    double viewportWidth = camera.getViewport().getX();
    	    double viewportHeight = camera.getViewport().getY();

    	    double extendedLeftBound = cameraPosition.getX() - (viewportWidth / 2);
    	    double extendedRightBound = cameraPosition.getX() + viewportWidth + (viewportWidth / 2);
    	    double extendedTopBound = cameraPosition.getY() - (viewportHeight / 2);
    	    double extendedBottomBound = cameraPosition.getY() + viewportHeight + (viewportHeight / 2);
    		
    		this.getCollisionSurfaces().forEach(surf -> {
            	surf.render(g);
            });
        	this.instantiatedCollectibles.forEach(collectible -> {
            	collectible.render(g);
            });
        	this.instantiatedEntitiesOnScreen.forEach(entity -> {
        		if(!(entity instanceof Player)) entity.render(g);
        		
            });
        	if(p == null) instantiatePlayer(); 
        	p.render(g);
        	this.getWavesManager().render(g);
        }
 

        this.getGraphicUtils().drawVignette(g2d, this.getWidth(), this.getHeight(), 0, 0, 0, 180);
    }
    
    private void renderSurface(Graphics g, CollisionSurface surface) {
        g.setColor(Color.GREEN);  // Use green for the ground
        g.fillRect((int) surface.getPosition().getX(), 
                   (int) surface.getPosition().getY(),
                   surface.getWidth(),
                   surface.getHeight());
    }
    
    public void checkEntityCollisions() {
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
                	} else {
                	    // Handle the case where both entities have the same importance
                	    continue;
                	}
                    EntityCollisionEvent e = new EntityCollisionEvent(
                        entity,
                        collider,
                        new Vector2D(
                            (entityA.getPosition().getX() + entityB.getPosition().getX()) / 2,
                            (entityA.getPosition().getY() + entityB.getPosition().getY()) / 2
                        )
                    );
                    if (entityB instanceof Projectile) {
                        Projectile projectile = (Projectile) entityB;
                        if (projectile.getSender().getClass().equals(entityA.getClass())) {
                            continue;
                        }
                    }
                    // Notify the listeners of both entities
                    System.out.println("Collision with " + entityA.getClass().getSimpleName() + " and " + entityB.getClass().getSimpleName());
                    this.getEventManager().dispatchEvent(e);
                }
            }
        }
    }
    
    private void initializeEnemyTypes() {
    	Enemy.addEnemyType(Zombie.class);
    	Enemy.addEnemyType(Skeleton.class);
    }

    public int getWidth() {
        return screen.width;
    }

    public int getHeight() {
        return screen.height;
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

	public WavesManager getWavesManager() {
		return wavesManager;
	}

	public List<CollisionSurface> getCollisionSurfaces() {
		return collisionSurfaces;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public Camera getCamera() {
		return camera;
	}

	public MapManager getMapManager() {
		return mapm;
	}

	public GraphicUtils getGraphicUtils() {
		return gUtils;
	}

	public void setP(Player p) {
		this.p = p;
	}

	public Menus getMenus() {
		return menus;
	}

	public Quadtree getQuadtree() {
		return quadtree;
	}

	public boolean isPaused() {
		return paused;
	};

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public EventManager getEventManager() {
		return eventManager;
	}
	
}
