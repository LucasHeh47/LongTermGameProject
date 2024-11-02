package com.lucasj.gamedev.game.entities.player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;
import com.lucasj.gamedev.essentials.InputHandler;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.events.input.KeyboardEventListener;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.events.player.PlayerAttackEvent;
import com.lucasj.gamedev.events.player.PlayerMoveEvent;
import com.lucasj.gamedev.events.player.PlayerMoveEventListener;
import com.lucasj.gamedev.events.player.PlayerStaminaUseEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.ai.BreadcrumbCache;
import com.lucasj.gamedev.game.entities.projectiles.Bullet;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Player extends Entity implements MouseClickEventListener, MouseMotionEventListener, KeyboardEventListener{

	// Player move event
	private List<PlayerMoveEventListener> movementListeners = new ArrayList<>();
	
    public void addPlayerMoveListener(PlayerMoveEventListener listener) {
    	movementListeners.add(listener);
    }

    public void removePlayerMoveListener(PlayerMoveEventListener listener) {
    	movementListeners.remove(listener);
    }
    
	private BufferedImage[][] walking;
	private int currentWalkingImage = 1; // 1 = down 2 = up = 3 = left 4 = right
	private float animationSpeed = 0.1f;
	private int animationTick = 1;
	private long lastAnimationUpdate;
	
	private boolean WASD[] = {false, false, false, false};
	private boolean isMoving = false;
	
	private Vector2D mousePosition = new Vector2D(0, 0);
	
	private float playerRotation = 0.0f;
	
	InputHandler input;
	
	private boolean playerAttacking = false;
	private float bulletSpeed = 40.0f;
	private float attackSpeed = 0.2f;
	private long lastAttack = 0;
	
	private long lastTimeHurt;
	private float timeToRegen = 2.5f;
	
	private float staminaUseRate = 0.25f;
	private float stamina = 100;
	private int maxStamina = 100;
	private long timeStaminaRanOut;
	private float breathTime = 2.0f;
	private boolean breathing = false;
	
	private float sprintMultiplier = 1.8f;
	private boolean isSprinting;
	private boolean isReadyToSprint;
	
	private Gun equippedGun;
	
	private PlayerUpgrades playerUpgrades;
	
	private float xp;
	
	private static PlayerStats globalStats = new PlayerStats();
	private PlayerBreadcrumbManager crumbManager;
	private BreadcrumbCache crumbCache = new BreadcrumbCache();
	
	private PlayerRewarder playerRewarder;
	
	private int money = 0;
	private int gems = 5;
	
	public Player(Game game, InputHandler input) {
		super(game);
		this.input = input;
		this.size = (int) (30*game.getCamera().getScale());
		this.movementSpeed = 500;
		this.playerRewarder = new PlayerRewarder(game);
		importance = 1;
		crumbManager = new PlayerBreadcrumbManager(game, this, 0.6);
		this.playerUpgrades = new PlayerUpgrades(game, this);
		input.addKeyboardListener(this);
		input.addMouseClickListener(this);
		input.addMouseMotionListener(this);
		
		walking = new BufferedImage[4][4];
		for(int i = 0; i < 4; i++) {
			for (int j = 0; j<4; j++) {
				walking[i][j] = SpriteTools.getSprite(SpriteTools.assetPackDirectory + "Actor/Characters/Boy/SeparateAnim/Walk.png", new Vector2D(i*16, j*16), new Vector2D(16, 16));
			}
		}
		lastAnimationUpdate = System.currentTimeMillis();
	}

	@Override
	public void update(double deltaTime) {
		this.screenPosition = new Vector2D(game.getWidth()/2, game.getHeight()/2);
		isMoving = false;
		crumbCache.updateDistances(new ArrayList<>(crumbManager.activeBreadcrumbs));
		move(deltaTime);
		if(playerAttacking && (System.currentTimeMillis() - lastAttack)/1000.0 > attackSpeed) attack(deltaTime);
		crumbManager.update(deltaTime);
		float checkAnimationSpeed = animationSpeed;
		if(isSprinting) checkAnimationSpeed = animationSpeed/sprintMultiplier;
		else checkAnimationSpeed = animationSpeed;
		if((System.currentTimeMillis() - lastAnimationUpdate)/1000.0 > checkAnimationSpeed) {
			animationTick++;
			if (animationTick > 4) {
			    animationTick = 1; // Reset to first frame if it exceeds available frames
			}
			lastAnimationUpdate = System.currentTimeMillis();
		}
		if(!isMoving) animationTick = 1;
		regenHealth();
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		if(game.testing) {
			g2d.setColor(new Color(255, 150, 150, 77));
			crumbManager.activeBreadcrumbs.forEach(crumb -> {
				g2d.fillRect((int) crumb.getScreenPosition().getX(), (int) crumb.getScreenPosition().getY(),
					25, 25);
			});
		}
			

	    // Create a rectangle representing the player's body
//	    Rectangle rect = new Rectangle((int) this.getPosition().getX() - size, 
//	                                   (int) this.getPosition().getY() - size, 
//	                                   size * 2, size * 2);
		
		// Load the image you want to render (e.g., walking sprite)
	    Image img = walking[currentWalkingImage-1][animationTick-1];
	    
	    int imageWidth = img.getWidth(null);
	    int imageHeight = img.getHeight(null);
	    
	    double x = this.getPosition().getX();
	    double y = this.getPosition().getY();

	    AffineTransform transform = new AffineTransform();
	    transform.translate(x - imageWidth / 2.0, y - imageHeight / 2.0);
	    //transform.rotate(playerRotation, imageWidth / 2.0, imageHeight / 2.0);

	    // Draw the transformed image
//	    g2d.drawImage(img, transform, null);
	    
	    g2d.drawImage(img, (int) this.screenPosition.getX(), (int) this.screenPosition.getY(), 64, 64, null);
	
	    renderHealthBar(g2d);
	    renderMoney(g2d);
	    renderStaminaBar(g2d);
	}
	
	private void renderHealthBar(Graphics2D g2d) {
		
		int margin = 100;
		int healthBarSize = 100;
		int healthBarLength = 4; //length x size = actual length
		
		g2d.setColor(Color.black);
		g2d.fillRect(margin, game.getHeight()-(margin*2), healthBarLength * healthBarSize, healthBarSize);

		g2d.setColor(Color.red);
		g2d.fillRect((int)(margin + (margin*0.1))
				, (int) (game.getHeight() - margin*2 + margin*0.1), 
				(int) (((healthBarSize * healthBarLength) - (margin*0.2)) * ((double) health/maxHealth)), 
				(int)(healthBarSize - (healthBarSize * 0.2)));
	}
	
private void renderStaminaBar(Graphics2D g2d) {
		
		int margin = 100;
		int staminaBarSize = 100;
		int staminaBarLength = 4; //length x size = actual length
		
		g2d.setColor(Color.black);
		g2d.fillRect(margin, game.getHeight()-(margin*2), staminaBarLength * staminaBarSize, staminaBarSize/3);

		g2d.setColor(Color.blue);
		g2d.fillRect((int)(margin + (margin*0.1))
				, (int) (game.getHeight() - margin*2 + margin*0.1), 
				(int) (((staminaBarSize * staminaBarLength) - (margin*0.2)) * ((double) stamina/maxStamina)), 
				(int)(staminaBarSize/3 - (staminaBarSize * 0.2)));
	}
	
	private void renderMoney(Graphics2D g2d) {

		g2d.setFont(game.font.deriveFont(Font.PLAIN, 64f)); // Derive the font size explicitly as a float

	    g2d.setColor(Color.black);
	    g2d.drawString("$" + Integer.toString(this.money), 100, game.getHeight() - 220);

	    g2d.setColor(Color.black);
	    g2d.drawString("Gems: " + Integer.toString(this.gems), 100, game.getHeight() - 270);
	}
	
	private void regenHealth() {
		if(this.getPlayerUpgrades().hasHealthRegen() && (System.currentTimeMillis() - this.lastTimeHurt)/1000.0 > this.timeToRegen) {
			System.out.println("Regenerating");
			this.health += this.getPlayerUpgrades().getHealthRegen();
	    	if(health > maxHealth) health = maxHealth;
		}
	}
	
	private void move(double deltaTime) {
		
		Vector2D camPosUpdate = new Vector2D(0, 0);

	    // Update camera position based on WASD input
	    if (WASD[0]) { // W - Move up
	        camPosUpdate.addY(-1);
	    }
	    if (WASD[1]) { // A - Move left
	        camPosUpdate.addX(-1);
	    }
	    if (WASD[2]) { // S - Move down
	        camPosUpdate.addY(1);
	    }
	    if (WASD[3]) { // D - Move right
	        camPosUpdate.addX(1);
	    }

	    camPosUpdate = camPosUpdate.normalize();
	    if (camPosUpdate.distanceTo(Vector2D.zero()) != 0) {
	        isMoving = true;
	    }

	    Vector2D movement = camPosUpdate.multiply(movementSpeed * deltaTime);
	    movement = movement.multiply(this.getPlayerUpgrades().getMovementSpeedMultiplier());
	    
	    if(isReadyToSprint && stamina > 0 && isMoving) {
	    	movement = movement.multiply(sprintMultiplier);
	    	stamina -= staminaUseRate;
	    	PlayerStaminaUseEvent e = new PlayerStaminaUseEvent(staminaUseRate);
	    	game.getEventManager().dispatchEvent(e);
	    	isSprinting = true;
	    } else isSprinting = false;
	    if(stamina <= 0 && !breathing) {
	    	breathing = true;
	    	timeStaminaRanOut = System.currentTimeMillis();
	    } else if(breathing) {
	    	if((System.currentTimeMillis() - timeStaminaRanOut)/1000.0 > this.breathTime) {
	    		breathing = false;
	    	}
	    }
	    if(!isSprinting && !breathing && stamina != maxStamina) {
	    	stamina += staminaUseRate/1.5;
	    	if(stamina > maxStamina) stamina = maxStamina;
	    }

	    PlayerMoveEvent e = new PlayerMoveEvent();
	    e.setPositionBefore(this.position.copy());
	    
	    this.position = this.position.add(movement);
	    e.setPositionAfter(this.position.copy());
	    e.setPositionChange(movement);
	    if(e.getPositionChange() != new Vector2D(0, 0)) game.getEventManager().dispatchEvent(e);

	    // Calculate camera boundaries and player position relative to the camera
	    Vector2D newCamPos = this.position.subtract(new Vector2D(game.getWidth() / 2, game.getHeight() / 2));
	    Vector2D cameraWorldPosition = game.getCamera().getWorldPosition();
	    Vector2D viewport = game.getCamera().getViewport();
	    Vector2D worldSize = game.getMapManager().map.getWorldSize();

	    // Determine if the camera is bounded in each direction
	    boolean cameraAtLeft = newCamPos.getX() <= 0;
	    boolean cameraAtTop = newCamPos.getY() <= 0;
	    boolean cameraAtRight = newCamPos.getX() + viewport.getX() >= worldSize.getX();
	    boolean cameraAtBottom = newCamPos.getY() + viewport.getY() >= worldSize.getY();

	    // Update the camera's position while respecting the boundaries
	    double newCamX = cameraWorldPosition.getX();
	    double newCamY = cameraWorldPosition.getY();

	    // If the camera is not bounded on the left or right, update its X position
	    if (!cameraAtLeft && !cameraAtRight) {
	        newCamX = newCamPos.getX();
	    }

	    // If the camera is not bounded on the top or bottom, update its Y position
	    if (!cameraAtTop && !cameraAtBottom) {
	        newCamY = newCamPos.getY();
	    }

	    // Set the new camera position
	    game.getCamera().setWorldPosition(new Vector2D(newCamX, newCamY));

	    // Update the player's screen position based on the camera's world position
	    this.screenPosition = this.position.subtract(game.getCamera().getWorldPosition());


		float dx = (float) (mousePosition.getX() - this.getScreenPosition().add(new Vector2D(this.getSize()).divide(2)).getX());
		float dy = (float) (mousePosition.getY() - this.getScreenPosition().add(new Vector2D(this.getSize()).divide(2)).getY());

		playerRotation = (float) Math.atan2(dy, dx);
	}
	
	private void attack(double deltaTime) {
		if(!isClickAnAttack()) return;
		float dx = (float) (mousePosition.getX() - this.getScreenPosition().getX());
		float dy = (float) (mousePosition.getY() - this.getScreenPosition().getY());
		
		// Implement the guns fire() method
		Vector2D vel = new Vector2D(dx, dy).normalize();
		Vector2D bulletVelocity = vel.multiply(bulletSpeed*25*deltaTime);
		int damage = (int) (10 * this.getPlayerUpgrades().getDamageMultiplier());
		Bullet b = new Bullet(game, this, this.position.add(new Vector2D(this.getSize()).divide(2)), bulletVelocity, 10, null, 2, damage);
		b.instantiate();
		PlayerAttackEvent e = new PlayerAttackEvent(b, damage);
		b.setPlayerAttackEvent(e);
		game.getEventManager().dispatchEvent(e);
		lastAttack = System.currentTimeMillis();
	}
	
	private boolean isClickAnAttack() {
		if (game.getWavesManager().getNPCManager().getPlayerUpgradeNPC().isOpen()) {
			return false;
		}
		return true;
	}
	
	public boolean takeDamage(float dmg) {
		this.lastTimeHurt = System.currentTimeMillis();
		return super.takeDamage(dmg);
	}

	@Override
	public void entityDeath() {
		game.setGameState(GameState.mainmenu);
		System.out.println("Total xp earned: " + xp);
		globalStats.addXP((int) xp);
		xp = 0;
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) playerAttacking = true;
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) playerAttacking = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
        	WASD[0] = true;
        	currentWalkingImage = 2;
        }
        if(e.getKeyCode() == KeyEvent.VK_A) {
        	WASD[1] = true;
        	currentWalkingImage = 3;
        }
        if(e.getKeyCode() == KeyEvent.VK_S) {
        	WASD[2] = true;
        	currentWalkingImage = 1;
        }
        if(e.getKeyCode() == KeyEvent.VK_D) {
        	WASD[3] = true;
        	currentWalkingImage = 4;
        }
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
        	isReadyToSprint = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        	game.setPaused(!game.isPaused());
        }
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
        	WASD[0] = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_A) {
        	WASD[1] = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_S) {
        	WASD[2] = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_D) {
        	WASD[3] = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
        	isReadyToSprint = false;
        }
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
		mousePosition = new Vector2D(e.getX(), e.getY());
		float dx = (float) (mousePosition.getX() - this.getPosition().getX());
		float dy = (float) (mousePosition.getY() - this.getPosition().getY());

		playerRotation = (float) Math.atan2(dy, dx);
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		mousePosition = new Vector2D(e.getX(), e.getY());
		float dx = (float) (mousePosition.getX() - this.getPosition().getX());
		float dy = (float) (mousePosition.getY() - this.getPosition().getY());

		playerRotation = (float) Math.atan2(dy, dx);
	}
	
	public void addXp(float num) {
		this.xp += num;
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		
	}

	public PlayerBreadcrumbManager getCrumbManager() {
		return crumbManager;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public BreadcrumbCache getCrumbCache() {
		return crumbCache;
	}

	public int getMoney() {
		return money;
	}
	
	public void addMoney(int money) {
		this.money += money;
		this.xp += money/100;
	}
	
	public static PlayerStats getGlobalStats() {
		return globalStats;
	}
	
	public void addGem(int num) {
		this.gems += num;
	}
	
	public boolean removeGem(int num) {
		if(this.gems >= num) {
			this.gems -= num;
			return true;
		}
		return false;
	}

	public PlayerUpgrades getPlayerUpgrades() {
		return playerUpgrades;
	}

	public Gun getEquippedGun() {
		return equippedGun;
	}

	public void setEquippedGun(Gun equippedGun) {
		this.equippedGun = equippedGun;
	}

	public PlayerRewarder getPlayerRewarder() {
		return playerRewarder;
	}
	
}
