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
import java.util.Random;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;
import com.lucasj.gamedev.essentials.InputHandler;
import com.lucasj.gamedev.essentials.ui.GameColors.colors;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.events.input.KeyboardEventListener;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.events.player.PlayerDamageTakenEvent;
import com.lucasj.gamedev.events.player.PlayerMoveEvent;
import com.lucasj.gamedev.events.player.PlayerStaminaUseEvent;
import com.lucasj.gamedev.events.weapons.SwapWeaponEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.ai.BreadcrumbCache;
import com.lucasj.gamedev.game.entities.placeables.Placeable;
import com.lucasj.gamedev.game.entities.player.multiplayer.OnlinePlayer;
import com.lucasj.gamedev.game.entities.player.multiplayer.PlayerMP;
import com.lucasj.gamedev.game.entities.projectiles.Bullet;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.game.weapons.guns.AssaultRifle;
import com.lucasj.gamedev.game.weapons.guns.Shotgun;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;
import com.lucasj.gamedev.utils.ConcurrentList;
import com.lucasj.gamedev.world.map.MiniMap;

public class Player extends Entity implements PlayerMP, MouseClickEventListener, MouseMotionEventListener, KeyboardEventListener{

	private BufferedImage[][] walking;
	private int currentWalkingImage = 1; // 1 = down 2 = up = 3 = left 4 = right
	private float animationSpeed = 0.1f;
	private int animationTick = 1;
	private long lastAnimationUpdate;
	
	private boolean WASD[] = {false, false, false, false};
	private boolean isMoving = false;
	
	private Vector2D mousePosition = new Vector2D(0, 0);
	
	private float playerRotation = 0.0f;
	
	private InputHandler input;
	
	private boolean playerAttacking = false;
	private float bulletSpeed = 40.0f;
	private float attackSpeed = 0.2f;
	private long lastAttack = 0;
	
	private long lastTimeHurt;
	private float timeToRegen = 2.5f;
	
	private float staminaUseRate = 0.35f;
	private float stamina = 100;
	private int maxStamina = 100;
	private long timeStaminaRanOut;
	private float breathTime = 2.0f;
	private boolean breathing = false;
	
	private float sprintMultiplier = 1.5f;
	private boolean isSprinting;
	private boolean isReadyToSprint;
	
	private Gun primaryGun;
	
	private boolean pickingSecondary = false;
	private Gun secondaryGun;
	
	private PlayerUpgrades playerUpgrades;
	
	private boolean isPickingClass = true;
	
	private float xp;
	
	private ConcurrentList<Placeable> activePlaceables;
	private PlayerPlaceableManager placeableManager;
	private boolean placingMode;
	
	private static PlayerStats globalStats = new PlayerStats();
	private PlayerBreadcrumbManager crumbManager;
	private BreadcrumbCache crumbCache = new BreadcrumbCache();
	
	private PlayerRewarder playerRewarder;
	
	private int money = 500;
	private int gems = 0;
	
	private long lastWalkSound;
	private float walkSoundCooldown = 0.5f;
	
	private float healthUnderlayDelay = 1;
	private float healthUnderlayRate = 0.35f;
	private float healthUnderlayLength;
	
	private MiniMap minimap;
	
	
	public Player(Game game, InputHandler input) {
		super(game);
		minimap = new MiniMap(game);
		activePlaceables = new ConcurrentList<>();
		placeableManager = new PlayerPlaceableManager(game, this);
		this.input = input;
		this.size = (int) (64);
		this.healthUnderlayLength = this.health;
		this.movementSpeed = 350;
		this.playerRewarder = new PlayerRewarder(game);
		importance = 1;
		crumbManager = new PlayerBreadcrumbManager(game, this, 0.6);
		this.playerUpgrades = new PlayerUpgrades(game, this);
		input.addKeyboardListener(this);
		input.addMouseClickListener(this);
		input.addMouseMotionListener(this);
		
		this.primaryGun = new AssaultRifle(game, this);
		
		
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
		if(!game.getWavesManager().hasGameStarted()) return;
		this.activePlaceables.update();
		this.screenPosition = new Vector2D(game.getWidth()/2, game.getHeight()/2);
		isMoving = false;
		crumbCache.updateDistances(new ArrayList<>(crumbManager.activeBreadcrumbs));
		this.primaryGun.update();
		move(deltaTime);
		
		if((System.currentTimeMillis() - this.lastTimeHurt)/1000.0 >= this.healthUnderlayDelay) {
			this.healthUnderlayLength -= this.healthUnderlayRate;
			if(this.healthUnderlayLength <= this.health) this.healthUnderlayLength = this.health;
		}
		
		if(playerAttacking && (System.currentTimeMillis() - lastAttack)/1000.0 > primaryGun.getFireRate()) attack(deltaTime);
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

		this.activePlaceables.forEach(placeable -> {
			placeable.update(deltaTime);
		});
		if(minimap != null) minimap.update(deltaTime);
		
		

    	if(game.party != null) {
    		game.getSocketClient().getPacketManager().playerInfoPacket(health, maxHealth, position, this.currentWalkingImage);
    		Debug.log(this, "Packet Sent");
    	} else {
    		Debug.log(this, "Not in party");
    	}
		
		
	    if(game.party != null) {
	    	game.party.update(deltaTime);
	    }
	}

	@Override
	public void render(Graphics g) {
		if(!game.getWavesManager().hasGameStarted()) return;
		super.render(g);
		Graphics2D g2d = (Graphics2D) g;

		this.activePlaceables.forEach(placeable -> {
			placeable.render(g);
		});
		
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
	    
	    g2d.drawImage(img, (int) this.screenPosition.getX(), (int) this.screenPosition.getY(), size, size, null);
        
	    renderHealthBar(g2d);
	    renderMoney(g2d);
	    renderStaminaBar(g2d);
	    renderPlaceable(g2d);
	    renderEquippedGun(g2d);
	    renderAmmo(g2d);
	    
	    if(minimap != null) minimap.render(g2d);
	    
	    
	    if(game.party != null) {
	    	game.party.render(g2d);
	    }
	    
	}
	
	private void renderAmmo(Graphics2D g2d) {
		g2d.setColor(Color.black);
		g2d.drawString(this.primaryGun.getCurrentClip() + "/" + this.getPrimaryGun().getClipSize(), game.getWidth()/2-100, game.getHeight()-50);
	}
	
	private void renderPlaceable(Graphics2D g2d) {
		Image frame = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/UI/placeable_frame.png", new Vector2D(0, 0), new Vector2D(32, 32));
		Image none = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/UI/no_placeable.png", new Vector2D(0, 0), new Vector2D(32, 32));
		g2d.drawImage(frame, game.getWidth()-228, game.getHeight()-228, 128, 128, null);
		
		if(this.getPlaceableManager().getEquippedPlaceable() == null) g2d.drawImage(none, game.getWidth()-228, game.getHeight()-228, 128, 128, null);
		else g2d.drawImage(this.getPlaceableManager().getEquippedPlaceable().getImage(), game.getWidth()-228, game.getHeight()-228, 128, 128, null);
	}
	
	private void renderEquippedGun(Graphics2D g2d) {
		Image frame = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/UI/frame.png", new Vector2D(0, 0), new Vector2D(32, 32));
		Color color = new Color(0, 0, 0);
		
		switch (this.primaryGun.getTier()) {
		case Common:
			color = new Color(166, 166, 166);
			break;
		case Uncommon:
			color = new Color(17, 221, 0);
			break;
		case Rare:
			color = new Color(39, 86, 255);
			break;
		case Epic:
			color = new Color(128, 0, 255);
			break;
		case Legendary:
			color = new Color(255, 67, 0);
			break;
		case Mythic:
			color = new Color(255, 230, 101);
			break;
		case Divine:
			color = new Color(200, 73, 73);
			break;
		case Ethereal:
			color = new Color(48, 210, 255);
			break;
		}
		
		Color vignette = color.darker();
		
		int r = vignette.getRed();
		int g = vignette.getGreen();
		int b = vignette.getBlue();

		g2d.setColor(color);
		g2d.fillRect(128, game.getHeight()-228, 128, 128);
        game.getGraphicUtils().drawVignette(g2d, 128, game.getHeight()-228, 128, 128, r, g, b, 120);
		g2d.drawImage(frame, 128, game.getHeight()-228, 128, 128, null);
		g2d.drawImage(this.primaryGun.getUIImage(), 128, game.getHeight()-228+32, 128, 64, null);
		
		if(this.secondaryGun != null) {
			frame = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/UI/frame.png", new Vector2D(0, 0), new Vector2D(32, 32));
			color = new Color(0, 0, 0);
			
			switch (this.secondaryGun.getTier()) {
			case Common:
				color = new Color(166, 166, 166);
				break;
			case Uncommon:
				color = new Color(17, 221, 0);
				break;
			case Rare:
				color = new Color(39, 86, 255);
				break;
			case Epic:
				color = new Color(128, 0, 255);
				break;
			case Legendary:
				color = new Color(255, 67, 0);
				break;
			case Mythic:
				color = new Color(255, 230, 101);
				break;
			case Divine:
				color = new Color(200, 73, 73);
				break;
			case Ethereal:
				color = new Color(48, 210, 255);
				break;
			}
			
			vignette = color.darker();
			
			r = vignette.getRed();
			g = vignette.getGreen();
			b = vignette.getBlue();

			g2d.setColor(color);
			g2d.fillRect(260, game.getHeight()-164, 64, 64);
	        game.getGraphicUtils().drawVignette(g2d, 260, game.getHeight()-164, 64, 64, r, g, b, 120);
			g2d.drawImage(frame, 260, game.getHeight()-164, 64, 64, null);
			g2d.drawImage(this.secondaryGun.getUIImage(), 260, game.getHeight()-164+16, 64, 32, null);
		}
	}
	
	private void renderHealthBar(Graphics2D g2d) {
		
		int margin = 100;
		int healthBarSize = 100;
		int healthBarLength = 4; //length x size = actual length
		
		int x = game.getWidth()/2 - (healthBarSize*healthBarLength)/2;
		
		g2d.setColor(Color.black);
		g2d.fillRect(x, game.getHeight()-(margin*2), healthBarLength * healthBarSize, healthBarSize);

		g2d.setColor(colors.LIGHT_RED.getValue());
		g2d.fillRect((int)(x + (healthBarSize*0.1))
				, (int) (game.getHeight() - margin*2 + margin*0.1), 
				(int) (((healthBarSize * healthBarLength) - (margin*0.2)) * ((double) this.healthUnderlayLength/maxHealth)), 
				(int)(healthBarSize - (healthBarSize * 0.2)));

		g2d.setColor(Color.red);
		g2d.fillRect((int)(x + (healthBarSize*0.1))
				, (int) (game.getHeight() - margin*2 + margin*0.1), 
				(int) (((healthBarSize * healthBarLength) - (margin*0.2)) * ((double) health/maxHealth)), 
				(int)(healthBarSize - (healthBarSize * 0.2)));
	}
	
	private void renderStaminaBar(Graphics2D g2d) {
		
		int margin = 100;
		int staminaBarSize = 100;
		int staminaBarLength = 4; //length x size = actual length
		int x = game.getWidth()/2 - (staminaBarSize*staminaBarLength)/2;
		
		g2d.setColor(Color.black);
		g2d.fillRect(x, game.getHeight()-(margin*2), staminaBarLength * staminaBarSize, staminaBarSize/3);

		g2d.setColor(Color.blue);
		g2d.fillRect((int)(x + (staminaBarSize*0.1))
				, (int) (game.getHeight() - margin*2 + margin*0.1), 
				(int) (((staminaBarSize * staminaBarLength) - (margin*0.2)) * ((double) stamina/maxStamina)), 
				(int)(staminaBarSize/3 - (staminaBarSize * 0.2)));
	}
	
	private void renderMoney(Graphics2D g2d) {

		g2d.setFont(game.font.deriveFont(Font.PLAIN, 64f)); // Derive the font size explicitly as a float

	    g2d.setColor(Color.black);
	    g2d.drawString("$" + Integer.toString(this.money), game.getWidth()/2 - 200, game.getHeight() - 220);

	    g2d.setColor(Color.black);
	    g2d.drawImage(SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Currency/Gem.png", new Vector2D(0, 0), new Vector2D(32, 32)), 
	    		game.getWidth()/2 + 100, game.getHeight() - 275, 64, 64, null);
	    g2d.drawString(Integer.toString(this.gems), game.getWidth()/2 + 175, game.getHeight() - 220);
	}
	
	private void regenHealth() {
		if(this.getPlayerUpgrades().hasHealthRegen() && (System.currentTimeMillis() - this.lastTimeHurt)/1000.0 > this.timeToRegen) {
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
	    if(!e.getPositionChange().isZero()) {
	    	game.getEventManager().dispatchEvent(e);
	    	float walkSound = this.isSprinting ? this.walkSoundCooldown/sprintMultiplier : this.walkSoundCooldown;
	    	if((System.currentTimeMillis() - this.lastWalkSound)/1000.0 >= walkSound) {
	    		Random rand = new Random();
	    		game.getAudioPlayer().playSound("Walk/grass" + rand.nextInt(2, 6) + ".wav", this.position, .08f);
	    		this.lastWalkSound = System.currentTimeMillis();
	    	}
		}

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
	
	public void swapWeapons() {
		if(this.secondaryGun == null) return;
		Gun gun = this.primaryGun;
		this.primaryGun = this.secondaryGun;
		this.secondaryGun = gun;
		
		SwapWeaponEvent e = new SwapWeaponEvent(this.primaryGun, this.secondaryGun);
		game.getEventManager().dispatchEvent(e);
		
	}
	
	private void attack(double deltaTime) {
		if(!isClickAnAttack() || this.primaryGun.isReloading()) return;
		
		// Implement the guns fire() method
		game.getCamera().shake(1, 50);
		if(this.primaryGun instanceof Shotgun) {
			float dx = (float) (mousePosition.getX() - this.getScreenPosition().getX());
		    float dy = (float) (mousePosition.getY() - this.getScreenPosition().getY());

		    Vector2D baseVelocity = new Vector2D(dx, dy).normalize();

		    for (int i = -2; i <= 2; i++) {
		        // Calculate the angle offset for each bullet
		        double angleOffset = Math.toRadians(5 * i); // 5 degrees for each step
		        double cos = Math.cos(angleOffset);
		        double sin = Math.sin(angleOffset);

		        // Apply rotation to the base velocity
		        Vector2D rotatedVelocity = new Vector2D(
		            baseVelocity.getX() * cos - baseVelocity.getY() * sin,
		            baseVelocity.getX() * sin + baseVelocity.getY() * cos
		        ).normalize();

		        // Apply bloom to the rotated velocity
		        double bloomX = (Math.random() - 0.5) * this.primaryGun.getBloom(); // Random deviation for bloom
		        double bloomY = (Math.random() - 0.5) * this.primaryGun.getBloom();
		        Vector2D bloomedVelocity = new Vector2D(
		            rotatedVelocity.getX() + bloomX,
		            rotatedVelocity.getY() + bloomY
		        ).normalize();

		        // Calculate the final bullet velocity
		        Vector2D bulletVelocity = bloomedVelocity.multiply(this.primaryGun.getProjectileSpeed() * 25 * deltaTime);

		        // Create and instantiate the bullet
		        int damage = (int) ((10 + this.primaryGun.getDamage()) * this.getPlayerUpgrades().getDamageMultiplier());
		        Bullet b = new Bullet(
		            game, 
		            this, 
		            this.position, 
		            bulletVelocity, 
		            10, 
		            null, 
		            this.getPrimaryGun().getRange(), 
		            damage
		        );
		        b.setPierce(this.getPrimaryGun().getPierce());
		        b.instantiate();
		    }
	        this.primaryGun.useRound();

		    if (!this.primaryGun.isAutomatic()) {
		        this.playerAttacking = false;
		    }
			
		} else {
			float dx = (float) (mousePosition.getX() - this.getScreenPosition().getX());
			float dy = (float) (mousePosition.getY() - this.getScreenPosition().getY());
			Vector2D vel = new Vector2D(dx, dy).normalize();
			
		    // Apply bloom by introducing random deviations to the velocity
		    double bloomX = (Math.random() - 0.5) * this.getPrimaryGun().getBloom(); // Random deviation between -0.375 and 0.375
		    double bloomY = (Math.random() - 0.5) * this.getPrimaryGun().getBloom();
	
		    // Add the bloom to the normalized direction
		    Vector2D bloomedVelocity = new Vector2D(vel.getX() + bloomX, vel.getY() + bloomY).normalize();
			
			Vector2D bulletVelocity = bloomedVelocity.multiply(this.primaryGun.getProjectileSpeed()*25*deltaTime);
			
			int damage = (int) ((10 + this.primaryGun.getDamage()) * this.getPlayerUpgrades().getDamageMultiplier());
			Bullet b = new Bullet(game, this, this.position, 
					bulletVelocity, 
					10, 
					null, 
		            this.getPrimaryGun().getRange(), 
					damage);
			b.setPierce(this.getPrimaryGun().getPierce());
			this.primaryGun.useRound();
			if(!this.primaryGun.isAutomatic()) this.playerAttacking = false;
			b.instantiate();
		}
		game.getAudioPlayer().playSound(primaryGun.getGunFireSound(), this.getPosition());
//		PlayerAttackEvent e = new PlayerAttackEvent(b, damage);
//		b.setPlayerAttackEvent(e);
		//game.getEventManager().dispatchEvent(e);
		lastAttack = System.currentTimeMillis();
	}
	
	private boolean isClickAnAttack() {
		if (game.getWavesManager().getNPCManager() == null || game.getWavesManager().getNPCManager().isAnyOpen()) {
			return false;
		}
		return true;
	}
	
	public boolean takeDamage(float dmg) {
		return false;
//		this.lastTimeHurt = System.currentTimeMillis();
//		PlayerDamageTakenEvent e = new PlayerDamageTakenEvent(this, dmg);
//		game.getEventManager().dispatchEvent(e);
//		return super.takeDamage(dmg);
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
        }
        if(e.getKeyCode() == KeyEvent.VK_A) {
        	WASD[1] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_S) {
        	WASD[2] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_D) {
        	WASD[3] = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
        	isReadyToSprint = true;
        }
        
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        	if(game.getWavesManager().getNPCManager().isAnyOpen()) {
        		game.getWavesManager().getNPCManager().closeAll();
        	} else game.setPaused(!game.isPaused());
        }
        if(e.getKeyCode() == KeyEvent.VK_Q) {
        	this.swapWeapons();
        }
        if(e.getKeyCode() == KeyEvent.VK_E) {
        	game.getWavesManager().getMissionManager().startMission();
        }
		if(e.getKeyCode() == KeyEvent.VK_1) {
			if(this.placeableManager.getEquippedPlaceable() != null) {
				this.placeableManager.getEquippedPlaceable().instantiate(mousePosition);
				this.placeableManager.setEquippedPlaceable(null);
			}
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
		this.onMouseMoved(e);
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		mousePosition = new Vector2D(e.getX(), e.getY());
		float dx = (float) (mousePosition.getX() - this.getScreenPosition().getX());
		float dy = (float) (mousePosition.getY() - this.getScreenPosition().getY());
		playerRotation = (float) Math.atan2(dy, dx);
		if(this.isClickAnAttack()) {
			Vector2D direction = new Vector2D(dx, dy).normalize();
			if (Math.abs(direction.getX()) > Math.abs(direction.getY())) {
		        // Horizontal movement
		        currentWalkingImage = (direction.getX() > 0) ? 4 : 3; // Right: 4, Left: 3
		    } else {
		        // Vertical movement
		        currentWalkingImage = (direction.getY() > 0) ? 1 : 2; // Down: 1, Up: 2
		    }
		}
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
		this.money += money*3;
		this.xp += money/100;
	}
	
	public static PlayerStats getGlobalStats() {
		return globalStats;
	}
	
	public void addGem(int num) {
		this.gems += num*3;
	}
	
	public boolean removeGem(int num) {
		if(this.gems >= num) {
			this.gems -= num;
			return true;
		}
		return false;
	}
	
	public boolean removeMoney(int num) {
		if(this.money >= num) {
			this.money -= num;
			return true;
		}
		return false;
	}

	public PlayerUpgrades getPlayerUpgrades() {
		return playerUpgrades;
	}

	public Gun getPrimaryGun() {
		return primaryGun;
	}

	public void setPrimaryGun(Gun equippedGun) {
		this.primaryGun = equippedGun;
	}

	public PlayerRewarder getPlayerRewarder() {
		return playerRewarder;
	}

	public int getGems() {
		return gems;
	}

	public ConcurrentList<Placeable> getActivePlaceables() {
		return activePlaceables;
	}

	public PlayerPlaceableManager getPlaceableManager() {
		return placeableManager;
	}

	public Vector2D getMousePosition() {
		return mousePosition;
	}

	public boolean getPlacingMode() {
		return placingMode;
	}

	public void setPlacingMode(boolean placingMode) {
		this.placingMode = placingMode;
	}

	public Gun getSecondaryGun() {
		return secondaryGun;
	}

	public void setSecondaryGun(Gun secondaryGun) {
		this.secondaryGun = secondaryGun;
	}

	public boolean isPickingSecondary() {
		return pickingSecondary;
	}

	public void setPickingSecondary(boolean pickingSecondary) {
		this.pickingSecondary = pickingSecondary;
	}

	@Override
	public String getUsername() {
		return game.username;
	}

	@Override
	public Player getPlayer() {
		return this;
	}

	public void setWalkingImage(int num) {
		this.currentWalkingImage = num;
	}

	@Override
	public Entity setMaxHealth(float num) {
		return this;
	}

	public boolean isPickingClass() {
		return isPickingClass;
	}

	public void setPickingClass(boolean isPickingClass) {
		this.isPickingClass = isPickingClass;
	}
	
}
