package com.lucasj.gamedev.essentials;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.audio.AudioPlayer;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Menus;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.essentials.ui.broadcast.Broadcast;
import com.lucasj.gamedev.events.EventManager;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.collectibles.Collectible;
import com.lucasj.gamedev.game.entities.enemy.Beast;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.enemy.Skeleton;
import com.lucasj.gamedev.game.entities.enemy.Slime;
import com.lucasj.gamedev.game.entities.enemy.Zombie;
import com.lucasj.gamedev.game.entities.particles.Particle;
import com.lucasj.gamedev.game.entities.particles.ParticleGenerator;
import com.lucasj.gamedev.game.entities.placeables.Landmine;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.entities.projectiles.Projectile;
import com.lucasj.gamedev.game.gamemodes.waves.WavesManager;
import com.lucasj.gamedev.game.multiplayer.GameClient;
import com.lucasj.gamedev.game.multiplayer.Party;
import com.lucasj.gamedev.mathutils.Quadtree;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.os.GameData;
import com.lucasj.gamedev.physics.CollisionSurface;
import com.lucasj.gamedev.settings.SettingsManager;
import com.lucasj.gamedev.utils.ConcurrentList;
import com.lucasj.gamedev.utils.GraphicUtils;
import com.lucasj.gamedev.world.map.MapManager;

@SuppressWarnings("unused")
public class Game {

    private InputHandler input;
    public ConcurrentList<Entity> instantiatedEntities;
    public ConcurrentList<Collectible> instantiatedCollectibles;
    public ConcurrentList<ParticleGenerator> instantiatedParticles;
    public ConcurrentList<Particle> instantiatedSingleParticles;
    public List<Entity> instantiatedEntitiesOnScreen;
    private Quadtree<Entity> quadtree;
    private Quadtree<Entity> entityQuadtree;
    private EntityCollisionEvent collisionEvent;
    
    private ConcurrentList<CollisionSurface> collisionSurfaces;

    private GameState gameState;
    private SettingsManager settings;
    private Dimension screen;
    private MapManager mapm;
    private GraphicUtils gUtils;
    private WavesManager wavesManager;
    private Camera camera;
    private EventManager eventManager;
    
    private Queue<Broadcast> broadcastQueue;
    
    private AudioPlayer audioPlayer;
    
    public boolean testing = false;
    
    public boolean settingsOpen = false;
    
    private short renderScreenTick = 0;
    private short lengthToCheckScreen = 5;
    
    private boolean paused;
    
    private Menus menus;
    
    private Player p;
    
    public Font font;

    GameData gameData;
    
    private Window window;
    
    public String username = "LucasHeh1";
    public Party party;
    
    private GameClient socketClient;
    public String authToken;
    
    public Game(InputHandler input, GraphicUtils gUtils, SettingsManager settings, Dimension screen, Window window) {
    	gameData = new GameData("projectgame", "playerStats.dat");
        
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
    	Player.getGlobalStats().load(gameData);
    	
    	socketClient = new GameClient(this, "lucas-j.com");
    	socketClient.start();
    	socketClient.getPacketManager().requestLoginPacket();
    	
    	audioPlayer = new AudioPlayer(this);
    	
    	broadcastQueue = new LinkedList<>();
    	
    	this.window = window;
    	
    	eventManager = new EventManager();
    	
    	referenceEnemies();
    	
        this.settings = settings;
        this.screen = screen;
        paused = false;
        gameState = GameState.mainmenu;
        
        this.input = input;
        
        menus = new Menus(this);
        input.addMouseClickListener(menus);
        input.addMouseMotionListener(menus);
        
        camera = new Camera(this, new Vector2D(getWidth(), getHeight()), new Vector2D(0, 0));
        instantiatedEntities = new ConcurrentList<Entity>();
        instantiatedEntitiesOnScreen = new ArrayList<Entity>();
        instantiatedParticles = new ConcurrentList<ParticleGenerator>();
        instantiatedSingleParticles = new ConcurrentList<Particle>();
        entityQuadtree = new Quadtree<>(0, new Vector2D(0, 0), new Vector2D(screen.width, screen.height));
        quadtree = new Quadtree<>(0, new Vector2D(0, 0), new Vector2D(screen.width, screen.height));
        
        instantiatedCollectibles = new ConcurrentList<Collectible>();
        collisionSurfaces = new ConcurrentList<>();
        
        this.gUtils = gUtils;
        
        mapm = new MapManager(this);
        wavesManager = new WavesManager(this);
        
    	
    	this.instantiatePlayer();
        
    }

    public void update(double deltaTime) {
    	input.getKeyboardListeners().update();
    	input.getMouseClickListeners().update();
    	input.getMouseMotionListeners().update();
    	
    	menus.update();
    	camera.update(deltaTime);
    	audioPlayer.update();
    	
    	instantiatedParticles.forEach(generator -> {
    		generator.update(deltaTime);
    	});
        
        if (gameState == GameState.waves) { // -------------------------------------------------------------------------------- Game State
//        	this.renderScreenTick++;
//        	if (this.renderScreenTick >= this.lengthToCheckScreen) {
//
//                renderScreenTick = 0;
//    	    }
        	
            wavesManager.update(deltaTime);
            
        }
        
		if(broadcastQueue.peek() != null) {
			Broadcast cast = broadcastQueue.peek();
			cast.update(deltaTime);
			if(cast.getPosition().getXint() != 0) {
				cast.setPosition(
						new Vector2D(
								cast.getPosition().getXint() + 2, 
								cast.getPosition().getYint()));
				if(cast.getPosition().getX() >= 0) {
					cast.setPosition(
							new Vector2D(
									0, 
									cast.getPosition().getYint()));
				}
			}
			if(cast.finished) {
				cast.setPosition(
						new Vector2D(
								cast.getPosition().getXint() - 4, 
								cast.getPosition().getYint()));
				if(cast.getPosition().getX() <= cast.getSize().getX()*-1.5) {
					broadcastQueue.remove();
				}
			}
		}
        
		for (Particle particle : this.instantiatedSingleParticles) {
			particle.update(deltaTime);
		}
        
    	this.updateLists();
    }
    
    public void createParty() {
    	if(party != null) return;
    	socketClient.getPacketManager().requestCreatePartyPacket();
    }
    
    public void joinParty(String username) {
    	if(party != null) return;
    	socketClient.getPacketManager().requestJoinPartyPacket(username);
    }
    
    public void updateLists() {
    	this.collisionSurfaces.update();
    	this.instantiatedEntities.update();
    	this.instantiatedCollectibles.update();
    	this.instantiatedParticles.update();
    	this.instantiatedSingleParticles.update();
    }
    
    public void instantiatePlayer() {
    	p = (Player) new Player(this, getInput()).setTag("Player").setMaxHealth(100).setHealth(100);
        p.setPosition(new Vector2D(this.getSettings().getIntSetting("width"), this.getSettings().getIntSetting("height")));
        this.setP(p);
        p.instantiate();
    }
    
    public void referenceEnemies() {
    	System.out.println("Initialzing enemy types...");
    	this.referenceEnemy(Zombie.class);
    	this.referenceEnemy(Skeleton.class);
    	this.referenceEnemy(Slime.class);
    	this.referenceEnemy(Beast.class);
    }
    
    public void referenceEnemy(Class<? extends Enemy> enemy) {
    	try {
            Method method = enemy.getMethod("initializeClass");
            method.invoke(null); // Invoke static method
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Render> render() {
    	List<Render> renders = new ArrayList<Render>();
    	renders.addAll(mapm.render());
    	
    	
    	if (gameState == GameState.waves) { // -------------------------------------------------------------------------------- Game State
        	this.instantiatedCollectibles.forEach(collectible -> {
            	renders.addAll(collectible.render());
            });

        	this.instantiatedEntitiesOnScreen.forEach(entity -> {
        		if(!(entity instanceof Player)) {
        			renders.addAll(entity.render());
        		}
        		
            });
        	if(p != null) renders.addAll(p.render());
        	
        	instantiatedParticles.forEach(generator -> {
        		renders.addAll(generator.render());
        	});
        	
        	renders.addAll(this.getWavesManager().render());
        }
    	
		for (Particle particle : this.instantiatedSingleParticles) {
    		renders.add(particle.render());
		}
    	
    	renders.add(menus.render());
    	
    	renders.add(new Render(Layer.UI, g -> { if(broadcastQueue != null && broadcastQueue.peek() != null) broadcastQueue.peek().render(); }));
        //this.getGraphicUtils().drawVignette(g2d, this.getWidth(), this.getHeight(), 0, 0, 0, 160);
    	return renders;
    }
    
    private void renderSurface(Graphics g, CollisionSurface surface) {
        g.setColor(Color.GREEN);  // Use green for the ground
        g.fillRect((int) surface.getPosition().getX(), 
                   (int) surface.getPosition().getY(),
                   surface.getWidth(),
                   surface.getHeight());
    }
    
    public void checkEntityCollisions() {
        // Loop through all entities to check for collisions
        for (int i = 0; i < instantiatedEntities.size(); i++) {
            Entity entityA = instantiatedEntities.get(i);
            for (int j = i + 1; j < instantiatedEntities.size(); j++) {
                Entity entityB = instantiatedEntities.get(j);

                if (entityA.isCollidingWith(entityB)) {
                    handleEntityCollision(entityA, entityB);
                }
            }
            
            // Check for collisions with placeables
            if(this.getPlayer() != null) if(this.getPlayer().getActivePlaceables() != null ) {
	            this.getPlayer().getActivePlaceables().forEach(placeable -> {
	                if (placeable instanceof Landmine && entityA.isCollidingWith(placeable)) {
	                    handlePlaceableCollision(entityA, (Landmine) placeable);
	                }
	            });
            }
        }
    }

    // Separate method to handle entity-to-entity collision
    private void handleEntityCollision(Entity entityA, Entity entityB) {
        Entity collider;
        Entity entity;

        if (entityA.importance > entityB.importance) {
            collider = entityB;
            entity = entityA;
        } else if (entityB.importance > entityA.importance) {
            collider = entityA;
            entity = entityB;
        } else {
            // Both entities have the same importance; no action taken
            return;
        }

        // Create collision event at midpoint of the two entities
        Vector2D collisionPoint = new Vector2D(
            (entityA.getPosition().getX() + entityB.getPosition().getX()) / 2,
            (entityA.getPosition().getY() + entityB.getPosition().getY()) / 2
        );

        EntityCollisionEvent e = new EntityCollisionEvent(entity, collider, collisionPoint);

        // Check if `entityB` is a projectile from the same sender class as `entityA`
        if (entityB instanceof Projectile) {
            Projectile projectile = (Projectile) entityB;
            if (projectile.getSender().getClass().equals(entityA.getClass())) {
                return;
            }
        }

        // Dispatch collision event
        this.getEventManager().dispatchEvent(e);
    }

    // Separate method to handle entity-to-placeable collision
    private void handlePlaceableCollision(Entity entity, Landmine landmine) {
        // Create collision event at midpoint of the entity and landmine
        Vector2D collisionPoint = new Vector2D(
            (entity.getPosition().getX() + landmine.getPosition().getX()) / 2,
            (entity.getPosition().getY() + landmine.getPosition().getY()) / 2
        );

        EntityCollisionEvent e = new EntityCollisionEvent(landmine, entity, collisionPoint);

        // Dispatch collision event
        this.getEventManager().dispatchEvent(e);
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

	public ConcurrentList<CollisionSurface> getCollisionSurfaces() {
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

	public Quadtree<Entity> getQuadtree() {
		return this.quadtree;
	}
	
	public Quadtree<Entity> getEntityQuadtree() {
		return this.entityQuadtree;
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

	public Window getWindow() {
		return window;
	}

	public GameClient getSocketClient() {
		return socketClient;
	}

	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}
	
	public void addBroadcast(Broadcast cast) {
		this.broadcastQueue.add(cast);
	}
	
}
