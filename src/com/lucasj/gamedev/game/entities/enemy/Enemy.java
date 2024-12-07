package com.lucasj.gamedev.game.entities.enemy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.GameColors;
import com.lucasj.gamedev.essentials.ui.GameColors.colors;
import com.lucasj.gamedev.events.entities.EntityAggroEvent;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.ai.Breadcrumb;
import com.lucasj.gamedev.game.entities.collectibles.Coin;
import com.lucasj.gamedev.game.entities.particles.Particle;
import com.lucasj.gamedev.game.entities.particles.ParticleGenerator;
import com.lucasj.gamedev.game.entities.particles.ParticleShape;
import com.lucasj.gamedev.game.entities.placeables.data.LandmineEnemyDistanceData;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.entities.player.multiplayer.PlayerMP;
import com.lucasj.gamedev.game.gamemodes.waves.WavesManager;
import com.lucasj.gamedev.game.levels.LevelUpPerk;
import com.lucasj.gamedev.mathutils.Quadtree;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;
import com.lucasj.gamedev.physics.CollisionSurface;

// This file is gonna get messy

/**
 * Creating an Enemy:
 * 1) Create a class that extends Enemy
 * 2) Create a public static void initializeClass() and call registerEnemyType(class, EnemyWavesData(int: round where enemy starts spawning, int: spawning rate (higher int = higher chance of spawning))
 * 3) Initialize in referenceEnemies() in Game
 * 4) Add movement in update(double) & display in render(Graphics)
 */
public abstract class Enemy extends Entity implements MouseMotionEventListener {
	
	public static Map<String, Class<?extends Enemy>> enemyClassMap = new HashMap<>();
	
	public static Enemy getEnemyByTag(Game game, String tag) {
		for(Entity entity : game.instantiatedEntities) {
			if(entity instanceof Enemy) {
				if(((Enemy) entity).tag.equals(tag)) return (Enemy) entity;
			}
		}
		return null;
	}
	
	// WAVES DATA --------------------------------
	public static class EnemyWavesData {
		
		public int startRound;
		public int spawnRate;
		
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
        private int aggroRange;
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
        
        public Builder setAggroRange(int range) {
            this.aggroRange = range;
            return this;
        }

        public Enemy build() {
            // Validate required fields
            if (maxHealth == 0 || movementSpeed == 0 || size == 0) {
                throw new IllegalStateException("All fields must be set before building the enemy");
            }

            try {
                // Find the constructor for the enemy class with the required parameters
                Constructor<? extends Enemy> constructor = type.getConstructor(
                    Game.class, Vector2D.class, int.class, int.class, int.class, String.class
                );

                // Create a new instance of the enemy using the constructor
                return constructor.newInstance(game, position, maxHealth, movementSpeed, size, tag).setAggroRange(aggroRange);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Could not create an instance of " + type.getSimpleName());
            }
        }
    }
	
	protected static <T extends Enemy> void registerEnemyType(Class<T> enemyClass, EnemyWavesData data) {
	    registerWavesData(enemyClass, data);
	    addEnemyType(enemyClass);
	    Enemy.enemyClassMap.put(enemyClass.getSimpleName(), enemyClass);
	}
	
	abstract void setCashDrop();
	abstract void setDamageMultiplier();
	
	private static final double SEPARATION_RADIUS = 150; // Distance to keep between enemies
    private static final double ALIGNMENT_RADIUS = 800.0; // Distance within which to align with others
    private static final double COHESION_RADIUS = 400.0; // Distance within which to group together
    private final double MAX_FORCE = this.movementSpeed; // Max steering force applied to enemies
	
	private static final Map<Class<? extends Enemy>, EnemyWavesData> wavesDataMap = new HashMap<>();
	private static List<Class<? extends Enemy>> enemyTypes = new ArrayList<>();

	private float respawnTimer;
	
	private boolean isPathToPlayerClear = false;
	protected float damageMultiplier = 1.0f;
	private int aggroRange = 750;
	private PlayerMP aggrod = null;
	private int attackRange = 25;
	private float attackRate = 1.0f;
	private long lastAttack;
	protected int[] cashDrop = new int[2];
	private List<Ray> rays = new ArrayList<>();
	protected boolean isMoving = false;
	protected float angleToPlayer;
	private long lastTimeHurt;
	private boolean onFire = false;
	private int fireTick = 1;
	private float fireSpeed = 0.05f;
	private long setFire;
	private float fireLength;
	
	private long lastFireTick;
	private boolean isZapped = false;
	private long zapStart;
	
	private float healthUnderlayDelay = 0.5f;
	private float healthUnderlayLength;
	
	private Quadtree quadtree;
	
	private boolean blocked;
	private Vector2D intersectionPoint;
	
	public Enemy(Game game) {
		super(game);
		importance = 2;
		if(aggroRange == 0) aggroRange = 750;
		lastAttack = System.currentTimeMillis();
		this.setCashDrop();
		this.setDamageMultiplier();
		this.setHealthMultiplier();
		this.setMovementSpeedMultiplier();
	}
	
	public Enemy(Game game, Vector2D position, Vector2D velocity, int maxHealth, int movementSpeed, int size,
			String tag) {
		super(game, position, velocity, maxHealth, movementSpeed, size, tag);
		importance = 2;
		if(aggroRange == 0) aggroRange = 750;
		lastAttack = System.currentTimeMillis();
		this.setCashDrop();
		this.setDamageMultiplier();
		this.setHealthMultiplier();
		this.setMovementSpeedMultiplier();
	}
	
	private class Ray {
	    Shape rayShape;
	    boolean isBlocked;

	    public Ray(Shape rayShape, boolean isBlocked) {
	        this.rayShape = rayShape;
	        this.isBlocked = isBlocked;
	    }
	}

	
	/**
	 * Do not override as it will remove healthbar
	 */
	public void render(Graphics g) {
		if(!isAlive) return;
		super.render(g);
		// Health bar
		g.setColor(Color.black);
		int barWidth = (int) (size + (size * 0.075));
		int barHeight = (int) (size * 0.05);
		int barX = (int) (screenPosition.getX()+(size/2) - (barWidth / 2));
		int barY = (int) (screenPosition.getY() - (size * 0.2));
		g.fillRect(barX, barY, barWidth, barHeight);

		// Health portion of the bar
		g.setColor(colors.LIGHT_RED.getValue());
		int healthWidth = (int) (barWidth * ((double) this.healthUnderlayLength / maxHealth));
		g.fillRect(barX, barY, healthWidth, barHeight);

		// Health portion of the bar
		g.setColor(Color.red);
		healthWidth = (int) (barWidth * ((double) health / maxHealth));
		g.fillRect(barX, barY, healthWidth, barHeight);
		
		if(this.onFire) {
			Image fire = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Fire/EntityOnFire/" + fireTick + ".png", 
					new Vector2D(), new Vector2D(16, 16));
			g.drawImage(fire, this.getScreenPosition().getXint(), this.getScreenPosition().getYint(), 
					this.size, this.size, null);
			if((System.currentTimeMillis() - lastFireTick)/1000.0 >= fireSpeed) {
				fireTick++;
				if(fireTick == 10) {
					fireTick = 1;
					float damage = (float) ((10 * game.getPlayer().getPlayerUpgrades().getDamageMultiplier()) * (game.getWavesManager().getWave() * WavesManager.HEALTH_GROWTH_RATE));
					if(Player.getGlobalStats().hasPerkUnlocked(LevelUpPerk.ExtraFlameWeaponTier)) {
						damage *= game.getPlayer().getPrimaryGun().getTier().FlamePerkMultiplier();
					}
					this.takeDamage(damage);
				}
				this.lastFireTick = System.currentTimeMillis();
			}
		}
		
	    Graphics2D g2d = (Graphics2D) g;
	 // Render the ray if it exists
	    if(game.testing) {
		    rays.parallelStream().forEach(ray -> { 
		        if (ray.isBlocked) {
		            g2d.setColor(new Color(255, 0, 0, 77)); // Red with 30% opacity
		        } else {
		            g2d.setColor(new Color(0, 255, 0, 77)); // Green with 30% opacity
		        }
		        g2d.draw(ray.rayShape);
		    });
	
		    // Render the intersection point if it exists
		    if (this.intersectionPoint != null) {
		        g2d.setColor(Color.RED);
		        g2d.fillOval((int) this.intersectionPoint.getX() - 3, (int) this.intersectionPoint.getY() - 3, 6, 6);
		    }
		    rays.clear();
		    g2d.setColor(new Color(128, 0, 128, 5)); // Purple with 12% opacity (out of 255)
		    int diameter = (int) (aggroRange * 2);
		    g2d.fillOval((int) (screenPosition.getX() - aggroRange), (int) (screenPosition.getY() - aggroRange), diameter, diameter);
		    
		    g2d.setColor(new Color(128, 0, 128, 10)); // Purple with 12% opacity (out of 255)
		    diameter = (int) (attackRange * 2);
		    g2d.fillOval((int) (screenPosition.getX() - attackRange), (int) (screenPosition.getY() - attackRange), diameter, diameter);

	    }
	}
	
	public boolean takeDamage(float damage) {
		this.lastTimeHurt = System.currentTimeMillis();
		
		game.getPlayer().getWavesStats().damageDealt += damage;
		
		Random rand = new Random();
		Color col = SpriteTools.createColorGradient(GameColors.colors.LIGHT_RED.getValue(), GameColors.colors.RED.getValue().darker(), 5).get(rand.nextInt(0, 4));

		Particle particle = new Particle(game, null, new Vector2D(rand.nextInt(-100, 100), rand.nextInt(-100, 0)).normalize(),
				0.5f, col, ParticleShape.Text, 12).setText(Integer.toString((int)damage));
		
		Particle.spawnSingleParticle(particle, this.screenPosition.add(new Vector2D(size/2)));
		
		return super.takeDamage(damage);
	}
	
	
	/**
	 * Do not Override
	 */
	public void update(double deltaTime) {
		if(!isAlive) return;
		
		if(this.aggrod == null || !isMoving) {
			this.respawnTimer += deltaTime;
		}
		if(this.respawnTimer >= 7.5) {
			this.respawn();
			this.respawnTimer = 0;
		}
		
		super.update(deltaTime);
		Vector2D oldPos = this.position.copy();
		if(game.instantiatedEntitiesOnScreen.contains(this)) {
			if(!this.isZapped) {
		        applyFlockingBehavior(deltaTime);
			    findNearestBreadcrumbToPlayer(deltaTime);
			}
		}
		this.isMoving = (this.position != oldPos);
		
		if(this.isZapped) {
			
			ParticleGenerator particles = new ParticleGenerator(game, 
					new Vector2D(this.position.add(size/2).getX(), this.position.add(size/4).getY()),
					0.1f,
					0.01f,
					0,
					365f,
					20f,
					0.08f, 
					5).setColorAndShape(() -> {
						return SpriteTools.createColorGradient(new Color(230, 245, 255), new Color(158, 213, 255), 8);
					}, ParticleShape.Square);
			
			if((System.currentTimeMillis() - this.zapStart)/1000.0 >= 5) {
				this.isZapped = false;
			}
		}
		
		if((System.currentTimeMillis() - this.lastTimeHurt)/1000.0 >= this.healthUnderlayDelay) {
			this.healthUnderlayLength -= this.maxHealth / 100;
			if(this.healthUnderlayLength <= this.health) this.healthUnderlayLength = this.health;
		}
		
		if(this.onFire && (System.currentTimeMillis() - this.setFire)/1000.0 >= this.fireLength) {
			this.onFire = false;
			this.fireLength = -1;
		}
		
		this.attackPlayer();
	}
	
	public abstract void setHealthMultiplier();
	public abstract void setMovementSpeedMultiplier();
	
	public void entityDeath() {
		Random rand = new Random();
		Coin coin = new Coin(game, this.position, rand.nextInt(cashDrop[0], cashDrop[1]));
		game.getPlayer().addXp(this.maxHealth/10);
		game.getPlayer().getWavesStats().enemiesKilled++;
	}
	
	public void attackPlayer() {
		if(inReachOfPlayer() && (System.currentTimeMillis() - lastAttack)/1000.0 > attackRate) {
			Player p = game.getPlayer();
			p.takeDamage((int) (20 * this.damageMultiplier));
			System.out.println("Hit player: " + p.getHealth() + "/" + p.getMaxHealth());
			lastAttack = System.currentTimeMillis();
		}
	}
	
	private boolean inReachOfPlayer() {
		return isPathToPlayerClear && this.getPosition().distanceTo(game.getPlayer().getPosition()) <= this.attackRange;
	}

	// AI FOR ENEMIES --------------------------------------------------------------------------------------------------------
	
	private void applyFlockingBehavior(double deltaTime) {
	    List<Entity> nearbyEntities = new ArrayList<>();
	    game.getQuadtree().retrieve(nearbyEntities, this);

	    Vector2D separationForce = calculateSeparationForce(deltaTime, nearbyEntities);
	    Vector2D alignmentForce = calculateAlignmentForce(deltaTime, nearbyEntities);
	    Vector2D cohesionForce = calculateCohesionForce(deltaTime, nearbyEntities);

	    double separationWeight = 100000.0;
	    double alignmentWeight = 100.0;
	    double cohesionWeight = 50;

	    Vector2D flockingForce = separationForce.multiply(separationWeight)
	                            .add(alignmentForce.multiply(alignmentWeight))
	                            .add(cohesionForce.multiply(cohesionWeight));

	    Vector2D avoidanceForce = applyObstacleAvoidance();
	    flockingForce = flockingForce.add(avoidanceForce).limit(MAX_FORCE);

	    // Check if the enemy is on a collision surface
	    Vector2D escapeDirection = checkIfOnCollisionSurface(deltaTime);
	    if (escapeDirection != null) {
	        flockingForce = flockingForce.add(escapeDirection.multiply(500)); // Weight for escaping the collision surface
	    }

	    this.velocity = this.velocity.add(flockingForce.multiply(deltaTime)).limit(movementSpeed);
	    this.position = this.position.add(this.velocity.multiply(deltaTime));
	}

	/**
	 * Checks if the entity is on a collision surface and calculates the direction to escape it.
	 */
	private Vector2D checkIfOnCollisionSurface(double deltaTime) {
	    Vector2D newPosition = this.position.add(this.velocity.multiply(deltaTime));
	    double entityWidth = this.size;
	    double entityHeight = this.size;

	    for (CollisionSurface surface : game.getCollisionSurfaces().toList()) {
	        Vector2D surfacePos = surface.getPosition();
	        int surfaceWidth = surface.getWidth();
	        int surfaceHeight = surface.getHeight();

	        // Check if the entity is on the collision surface
	        if (newPosition.getX() < surfacePos.getX() + surfaceWidth &&
	            newPosition.getX() + entityWidth > surfacePos.getX() &&
	            newPosition.getY() < surfacePos.getY() + surfaceHeight &&
	            newPosition.getY() + entityHeight > surfacePos.getY()) {
	            
	            // Calculate the nearest escape direction
	            Vector2D escapePoint = calculateEscapePoint(newPosition, surfacePos, surfaceWidth, surfaceHeight, entityWidth, entityHeight);
	            return escapePoint.subtract(this.position).normalize();
	        }
	    }

	    return null; // Not on a collision surface
	}

	/**
	 * Calculates the nearest position away from the collision surface.
	 */
	private Vector2D calculateEscapePoint(Vector2D entityPos, Vector2D surfacePos, int surfaceWidth, int surfaceHeight, double entityWidth, double entityHeight) {
	    double escapeX = entityPos.getX();
	    double escapeY = entityPos.getY();

	    // Find the nearest X escape point
	    if (entityPos.getX() + entityWidth / 2 < surfacePos.getX()) {
	        escapeX = surfacePos.getX() - entityWidth; // Move left
	    } else if (entityPos.getX() + entityWidth / 2 > surfacePos.getX() + surfaceWidth) {
	        escapeX = surfacePos.getX() + surfaceWidth; // Move right
	    }

	    // Find the nearest Y escape point
	    if (entityPos.getY() + entityHeight / 2 < surfacePos.getY()) {
	        escapeY = surfacePos.getY() - entityHeight; // Move up
	    } else if (entityPos.getY() + entityHeight / 2 > surfacePos.getY() + surfaceHeight) {
	        escapeY = surfacePos.getY() + surfaceHeight; // Move down
	    }

	    return new Vector2D(escapeX, escapeY);
	}



	private Vector2D calculateSeparationForce(double deltaTime, List<Entity> nearbyEntities) {
	    Vector2D force = new Vector2D(0, 0);
	    int count = 0;

	    for (Entity entity : nearbyEntities) {
	        if (entity instanceof Enemy && entity != this) {
	            double distance = this.position.distanceTo(entity.getPosition());
	            
	            // Calculate the sum of the radii (half the size of each enemy)
	            double combinedRadius = (this.size / 2.0) + (((Enemy) entity).size / 2.0);
	            double effectiveSeparationRadius = combinedRadius + SEPARATION_RADIUS;

	            if (distance < effectiveSeparationRadius) {
	                // Calculate the force vector to push the enemies apart
	                Vector2D diff = this.position.subtract(entity.getPosition()).normalize();
	                double strength = (effectiveSeparationRadius + 2000) / effectiveSeparationRadius;

	                // Scale the force based on the strength (closer enemies result in stronger push)
	                diff = diff.multiply(strength * 500.0); // Adjust multiplier if needed

	                force = force.add(diff);
	                count++;
	            }
	        }
	    }

	    if (count > 0) {
	        force = force.divide(count).normalize().multiply(MAX_FORCE * deltaTime).limit(MAX_FORCE);
	    }

	    return force;
	}

	private Vector2D calculateAlignmentForce(double deltaTime, List<Entity> nearbyEntities) {
	    Vector2D averageVelocity = new Vector2D(0, 0);
	    int count = 0;

	    for (Entity entity : nearbyEntities) {
	        if (entity instanceof Enemy && entity != this) {
	            double distance = this.position.distanceTo(entity.getPosition());
	            
	            // Calculate the combined radius for alignment
	            double combinedRadius = (this.size / 2.0) + (((Enemy) entity).size / 2.0);
	            double effectiveAlignmentRadius = combinedRadius + ALIGNMENT_RADIUS;

	            if (distance < effectiveAlignmentRadius) {
	                averageVelocity = averageVelocity.add(entity.getVelocity());
	                count++;
	            }
	        }
	    }

	    if (count > 0) {
	        averageVelocity = averageVelocity.divide(count).normalize();
	        return averageVelocity.subtract(this.velocity).limit(MAX_FORCE * deltaTime);
	    }

	    return new Vector2D(0, 0);
	}

	private Vector2D calculateCohesionForce(double deltaTime, List<Entity> nearbyEntities) {
	    Vector2D centerOfMass = new Vector2D(0, 0);
	    int count = 0;

	    for (Entity entity : nearbyEntities) {
	        if (entity instanceof Enemy && entity != this) {
	            double distance = this.position.distanceTo(entity.getPosition());
	            
	            // Calculate the combined radius for cohesion
	            double combinedRadius = (this.size / 2.0) + (((Enemy) entity).size / 2.0);
	            double effectiveCohesionRadius = combinedRadius + COHESION_RADIUS;

	            if (distance < effectiveCohesionRadius) {
	                centerOfMass = centerOfMass.add(entity.getPosition());
	                count++;
	            }
	        }
	    }

	    if (count > 0) {
	        centerOfMass = centerOfMass.divide(count);
	        return centerOfMass.subtract(this.position).normalize().limit(MAX_FORCE * deltaTime);
	    }

	    return new Vector2D(0, 0);
	}

	private static class BreadcrumbPath {
	    Breadcrumb breadcrumb;
	    double distance;

	    public BreadcrumbPath(Breadcrumb breadcrumb, double distance) {
	        this.breadcrumb = breadcrumb;
	        this.distance = distance;
	    }
	}
	
	
	public void findNearestBreadcrumbToPlayer(double deltaTime) {
	    Vector2D playerPosition = game.getPlayer().getPosition();
	    double distanceToPlayer = playerPosition.distanceTo(this.position);
	    isPathToPlayerClear = isLineOfSightClear(this.position, playerPosition);

	    if (distanceToPlayer <= aggroRange && isPathToPlayerClear) {
	        this.aggrod = game.getPlayer();
	        
	        if(distanceToPlayer <= aggroRange/2) {
		    	moveToPosition(playerPosition, deltaTime);
		        return;
	        }
	    }

	    Queue<Breadcrumb> breadcrumbsQueue = game.getPlayer().getCrumbManager().activeBreadcrumbs;
	    List<Breadcrumb> breadcrumbs = new ArrayList<>(breadcrumbsQueue);
	    Breadcrumb chosenBreadcrumb = null;
	    double bestScore = Double.MAX_VALUE;

	    for (int i = 0; i < breadcrumbs.size(); i++) {
	        Breadcrumb breadcrumb = breadcrumbs.get(i);
	        Vector2D breadcrumbPosition = breadcrumb.getPosition();
	        double distanceToPlayerFromBreadcrumb = breadcrumb.getPosition().distanceTo(game.getPlayer().getPosition());
	        double distanceToEnemy = breadcrumbPosition.distanceTo(this.position);

	        if (distanceToEnemy <= aggroRange && isLineOfSightClear(this.position, breadcrumbPosition)) {
	            double score = calculateBreadcrumbScore(breadcrumb, distanceToPlayerFromBreadcrumb, distanceToEnemy, i, breadcrumbs.size());

	            if (score < bestScore) {
	                chosenBreadcrumb = breadcrumb;
	                bestScore = score;
	            }
	        }
	    }

	    if (chosenBreadcrumb != null) {
	        moveToBreadcrumb(chosenBreadcrumb, deltaTime);
	    } else {
	        // If no valid breadcrumb is found, perform a wandering behavior or idle
	        performWanderOrIdleBehavior(deltaTime);
	    }
	}

	private void performWanderOrIdleBehavior(double deltaTime) {
	    // Add logic for wandering or waiting if no breadcrumb is available
	    // For example, enemies could wander randomly or look for alternate routes
	    
	    // Reset aggrod when the enemy is idle
	    this.aggrod = null;
	}
	
	private void moveToPosition(Vector2D targetPosition, double deltaTime) {
	    Vector2D direction = targetPosition.subtract(this.position).normalize();
	    this.velocity = direction.multiply(getMovementSpeed() * deltaTime);

	    if (targetPosition.equals(game.getPlayer().getPosition()) && this.aggrod != game.getPlayer()) {
	        this.aggrod = game.getPlayer();
	        
	        EntityAggroEvent e = new EntityAggroEvent(this, this.aggrod);
	        game.getEventManager().dispatchEvent(e);
	        
	    }
	    
	    // Update position based on velocity
	    Vector2D newPosition = this.position.add(velocity.divide(50));

	    // Check for collisions or obstacles and update position
	    if (isLineOfSightClear(this.position, newPosition)) {
	        this.position = newPosition;
	    }
	}
	
	private double calculatePathDistanceToPlayer(Breadcrumb start, List<Breadcrumb> breadcrumbs) {
	    Queue<BreadcrumbPath> queue = new LinkedList<>();
	    Set<Breadcrumb> visited = new HashSet<>();

	    queue.add(new BreadcrumbPath(start, 0));
	    visited.add(start);

	    while (!queue.isEmpty()) {
	        BreadcrumbPath current = queue.poll();
	        if (current == null) {
	            continue; // Skip to the next iteration if current is null
	        }

	        Breadcrumb breadcrumb = current.breadcrumb;
	        double distanceSoFar = current.distance;

	        // If we reach a breadcrumb close to the player, return the distance
	        if (isLineOfSightClear(breadcrumb.getPosition(), game.getPlayer().getPosition())) {
	            return distanceSoFar + breadcrumb.getPosition().distanceTo(game.getPlayer().getPosition());
	        }

	        AtomicInteger counter = new AtomicInteger(0);

	        for (Breadcrumb neighbor : breadcrumbs) {
	            if (counter.get() >= 3) break; // Limit to 10 breadcrumbs

	            if (neighbor != breadcrumb && !visited.contains(neighbor) && isLineOfSightClear(breadcrumb.getPosition(), neighbor.getPosition())) {
	                double newDistance = distanceSoFar + breadcrumb.getPosition().distanceTo(neighbor.getPosition());
	                queue.add(new BreadcrumbPath(neighbor, newDistance));
	                visited.add(neighbor);

	                counter.incrementAndGet();
	            }
	        }
	    }

	    // Return a large value if no path to the player was found
	    return Double.MAX_VALUE;
	}
	
	// Calculate the score of a breadcrumb based on its attributes
	private double calculateBreadcrumbScore(Breadcrumb breadcrumb, double distanceToPlayer, double distanceToEnemy, int index, int totalBreadcrumbs) {
	    // We use the index to determine age: the older the crumb (closer to 0), the higher the penalty
	    double playerDistanceWeight = 4.0;
	    double enemyDistanceWeight = 0.5; // Lower priority to enemy distance to encourage moving toward the player
	    double ageWeight = 5.5; // Weight for older breadcrumbs

	    // Normalize the age as a value between 0 and 1 (0 is the oldest, 1 is the newest)
	    double ageFactor = (double) index / totalBreadcrumbs;

	    // Example calculation: player distance and age reduce score; enemy distance adds to it
	    double score = (distanceToPlayer * playerDistanceWeight) - (ageFactor * ageWeight) + (distanceToEnemy * enemyDistanceWeight);

	    return score;
	}
	
	// Calculate the intersection point between two line segments
    private Vector2D getIntersectionPoint(Vector2D p1, Vector2D p2, Vector2D q1, Vector2D q2) {
        double a1 = p2.getY() - p1.getY();
        double b1 = p1.getX() - p2.getX();
        double c1 = a1 * p1.getX() + b1 * p1.getY();

        double a2 = q2.getY() - q1.getY();
        double b2 = q1.getX() - q2.getX();
        double c2 = a2 * q1.getX() + b2 * q1.getY();

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            // Lines are parallel
            return null;
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;

            // Check if the intersection point is within both line segments
            if (isWithinSegment(x, y, p1, p2) && isWithinSegment(x, y, q1, q2)) {
                return new Vector2D(x, y);
            } else {
                return null;
            }
        }
    }
	
	private boolean isWithinSegment(double x, double y, Vector2D p1, Vector2D p2) {
        return (x >= Math.min(p1.getX(), p2.getX()) && x <= Math.max(p1.getX(), p2.getX())) &&
               (y >= Math.min(p1.getY(), p2.getY()) && y <= Math.max(p1.getY(), p2.getY()));
    }
	

	private boolean isLineOfSightClear(Vector2D start, Vector2D end) {
		Vector2D intersectionPoint = null; // Reset the intersection point

	    // Create a Line2D object for the ray
		Vector2D startToScreen = game.getCamera().worldToScreenPosition(start);
		Vector2D endToScreen = game.getCamera().worldToScreenPosition(end);
	    Line2D.Double rayLine = new Line2D.Double(startToScreen.getX(), startToScreen.getY(), endToScreen.getX(), endToScreen.getY());

	    // Assume the line of sight is clear initially
	    boolean isBlocked = false;

	    // Loop through all collision surfaces in the game
	    for (CollisionSurface surface : game.getCollisionSurfaces().toList()) {
	        double surfaceLeft = surface.getPosition().getX();
	        double surfaceRight = surfaceLeft + surface.getWidth();
	        double surfaceTop = surface.getPosition().getY();
	        double surfaceBottom = surfaceTop + surface.getHeight();

	        // Check for intersections with all four edges of the rectangle
	        Vector2D intersection;

	        intersection = getIntersectionPoint(start, end, new Vector2D(surfaceLeft, surfaceTop), new Vector2D(surfaceRight, surfaceTop));
	        if (intersection != null) {
	            isBlocked = true;
	            intersectionPoint = intersection;
	            break;
	        }

	        intersection = getIntersectionPoint(start, end, new Vector2D(surfaceLeft, surfaceBottom), new Vector2D(surfaceRight, surfaceBottom));
	        if (intersection != null) {
	            isBlocked = true;
	            intersectionPoint = intersection;
	            break;
	        }

	        intersection = getIntersectionPoint(start, end, new Vector2D(surfaceLeft, surfaceTop), new Vector2D(surfaceLeft, surfaceBottom));
	        if (intersection != null) {
	            isBlocked = true;
	            intersectionPoint = intersection;
	            break;
	        }

	        intersection = getIntersectionPoint(start, end, new Vector2D(surfaceRight, surfaceTop), new Vector2D(surfaceRight, surfaceBottom));
	        if (intersection != null) {
	            isBlocked = true;
	            intersectionPoint = intersection;
	            break;
	        }
	    }

	    // Add this ray to the list of rays for rendering
	    if(game.testing) this.rays.add(new Ray(rayLine, isBlocked));

	    // Return whether the line of sight is clear
	    return !isBlocked;
    }

	// Helper method to check if a line intersects a rectangle
	private boolean lineIntersectsRect(Vector2D start, Vector2D end, double left, double top, double right, double bottom) {
		boolean topEdge = lineIntersectsLine(start, end, new Vector2D(left, top), new Vector2D(right, top));
	    boolean bottomEdge = lineIntersectsLine(start, end, new Vector2D(left, bottom), new Vector2D(right, bottom));
	    boolean leftEdge = lineIntersectsLine(start, end, new Vector2D(left, top), new Vector2D(left, bottom));
	    boolean rightEdge = lineIntersectsLine(start, end, new Vector2D(right, top), new Vector2D(right, bottom));

	    return topEdge || bottomEdge || leftEdge || rightEdge;
	}

	// Helper method to check if two line segments intersect
	private boolean lineIntersectsLine(Vector2D p1, Vector2D p2, Vector2D q1, Vector2D q2) {
	    double d1 = direction(q1, q2, p1);
	    double d2 = direction(q1, q2, p2);
	    double d3 = direction(p1, p2, q1);
	    double d4 = direction(p1, p2, q2);

	    if (d1 != d2 && d3 != d4) {
	        return true;
	    }

	    boolean onSegment1 = (d1 == 0 && onSegment(q1, q2, p1));
	    boolean onSegment2 = (d2 == 0 && onSegment(q1, q2, p2));
	    boolean onSegment3 = (d3 == 0 && onSegment(p1, p2, q1));
	    boolean onSegment4 = (d4 == 0 && onSegment(p1, p2, q2));

	    return onSegment1 || onSegment2 || onSegment3 || onSegment4;
	}

	// Helper method to determine the direction of the turn
	private double direction(Vector2D p, Vector2D q, Vector2D r) {
	    return (r.getX() - p.getX()) * (q.getY() - p.getY()) - (r.getY() - p.getY()) * (q.getX() - p.getX());
	}

	// Helper method to check if a point is on a line segment
	private boolean onSegment(Vector2D p, Vector2D q, Vector2D r) {
	    return Math.min(p.getX(), q.getX()) <= r.getX() && r.getX() <= Math.max(p.getX(), q.getX()) &&
	           Math.min(p.getY(), q.getY()) <= r.getY() && r.getY() <= Math.max(p.getY(), q.getY());
	}

	private void moveToBreadcrumb(Breadcrumb breadcrumb, double deltaTime) {
		if (breadcrumb.getPosition().distanceTo(this.position) > aggroRange) return;

	    // Calculate the direction to the breadcrumb
	    Vector2D direction = breadcrumb.getPosition().subtract(position).normalize();

	    // Apply obstacle avoidance force
	    Vector2D avoidanceForce = applyObstacleAvoidance();
	    Vector2D totalForce = direction.add(avoidanceForce.multiply(deltaTime)).normalize();

	    // Adjust velocity based on movement speed and direction
	    this.velocity = totalForce.multiply(getMovementSpeed() * deltaTime);

	    // Update position based on velocity
	    Vector2D newPosition = this.position.add(velocity.divide(50));

	    // Ensure that the new position is valid (no collisions or obstacles blocking)
	    if (isLineOfSightClear(this.position, newPosition)) {
	        this.position = newPosition;
	    }
    }
	
	private Vector2D applyObstacleAvoidance() {
	    double avoidanceRadius = this.size; // Increased radius for better reaction to obstacles
	    Vector2D avoidanceForce = new Vector2D(0, 0);

	    for (CollisionSurface surface : game.getCollisionSurfaces().toList()) {
	        double surfaceLeft = surface.getPosition().getX();
	        double surfaceRight = surfaceLeft + surface.getWidth();
	        double surfaceTop = surface.getPosition().getY();
	        double surfaceBottom = surfaceTop + surface.getHeight();

	        double closestX = Math.max(surfaceLeft, Math.min(this.position.getX(), surfaceRight));
	        double closestY = Math.max(surfaceTop, Math.min(this.position.getY(), surfaceBottom));
	        Vector2D closestPoint = new Vector2D(closestX, closestY);

	        double distance = this.position.distanceTo(closestPoint);

	        if (distance < avoidanceRadius) {
	            Vector2D forceDirection = this.position.subtract(closestPoint).normalize();
	            double strength = (avoidanceRadius - distance) / avoidanceRadius;
	            avoidanceForce = avoidanceForce.add(forceDirection.multiply(strength));
	        }
	    }

	    return avoidanceForce.limit(MAX_FORCE);
	}

    
    // END OF AI ------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public static List<Entity> getEntitiesInRadius(Game game, Vector2D center, int radius) {
	    // Define the top-left and bottom-right bounds of the search area
	    Vector2D areaTopLeft = new Vector2D(center.getX() - radius, center.getY() - radius);
	    Vector2D areaBottomRight = new Vector2D(center.getX() + radius, center.getY() + radius);

	    // Retrieve all entities within the bounding box using the quadtree
	    List<Entity> entitiesInRadius = new ArrayList<>();
	    game.getQuadtree().retrieve(entitiesInRadius, areaTopLeft, areaBottomRight);

	    // Filter entities that are strictly within the circular radius
	    entitiesInRadius.removeIf(entity -> entity.getPosition().distanceTo(center) > radius);

	    return entitiesInRadius;
	}
	
	public static List<LandmineEnemyDistanceData> getEntitiesInLandmineRadius(Game game, Vector2D center, int radius) {
	    // Define the top-left and bottom-right bounds of the search area
	    Vector2D areaTopLeft = new Vector2D(center.getX() - radius, center.getY() - radius);
	    Vector2D areaBottomRight = new Vector2D(center.getX() + radius, center.getY() + radius);

	    // Temporary list to retrieve entities from the quadtree
	    List<Entity> retrievedEntities = new ArrayList<>();
	    game.getQuadtree().retrieve(retrievedEntities, areaTopLeft, areaBottomRight);

	    // List to store entities and their distances within the circular radius
	    List<LandmineEnemyDistanceData> entitiesInRadius = new ArrayList<>();

	    // Filter entities strictly within the circular radius and populate LandmineEnemyDistanceData
	    for (Entity entity : retrievedEntities) {
	        double distance = entity.getPosition().distanceTo(center);
	        if (distance <= radius) {
	            entitiesInRadius.add(new LandmineEnemyDistanceData(entity, (int) distance));
	        }
	    }

	    return entitiesInRadius;
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
			System.out.println("Adding enemy type: " + type.getName());
			enemyTypes.add(type);
		}
	}
	
	public Enemy setAggroRange(int range) {
        this.aggroRange = range;
        return this;
    }

	public PlayerMP getAggrod() {
		return aggrod;
	}

	public void setAggrod(PlayerMP playerMP) {
		this.aggrod = playerMP;
	}
	
	public void setOnFire(float seconds) {
		this.onFire = true;
		this.setFire = System.currentTimeMillis();
		this.fireLength = seconds;
	}
	
	public void zap() {
		this.isZapped = true;
		this.zapStart = System.currentTimeMillis();
	}
	
	public void respawn() {
		Random rand = new Random();

		List<Vector2D> spawnpoints = game.getMapManager().getSpawnpoints();
    	
    	Vector2D playerPosition = game.getPlayer().getPosition();
    	int minDistance = 750;
    	int maxDistance = 2000;

    	// Filter spawnpoints based on the distance criteria
    	List<Vector2D> validSpawnpoints = new ArrayList<>();
    	for (Vector2D spawnpoint : spawnpoints) {
    	    double distance = spawnpoint.distanceTo(playerPosition);
    	    if (distance >= minDistance && distance <= maxDistance) {
    	        validSpawnpoints.add(spawnpoint);
    	    }
    	}
    	if (!validSpawnpoints.isEmpty()) {
    	    // Select a random spawnpoint from the valid list
    	    Vector2D selectedSpawnpoint = validSpawnpoints.get(rand.nextInt(validSpawnpoints.size()));

    		this.position = selectedSpawnpoint;
    	}
	}

}
