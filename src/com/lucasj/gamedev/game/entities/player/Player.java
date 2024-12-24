package com.lucasj.gamedev.game.entities.player;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.InputHandler;
import com.lucasj.gamedev.essentials.controls.Controller;
import com.lucasj.gamedev.essentials.controls.Controls;
import com.lucasj.gamedev.essentials.ui.GameColors;
import com.lucasj.gamedev.essentials.ui.GameColors.colors;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.essentials.ui.Tooltip;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.events.input.ControllerEvent;
import com.lucasj.gamedev.events.input.KeyboardEventListener;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.events.level.LevelUpEvent;
import com.lucasj.gamedev.events.level.LevelUpEventListener;
import com.lucasj.gamedev.events.player.PlayerDamageTakenEvent;
import com.lucasj.gamedev.events.player.PlayerMoveEvent;
import com.lucasj.gamedev.events.player.PlayerStaminaUseEvent;
import com.lucasj.gamedev.events.weapons.SwapWeaponEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.ai.BreadcrumbCache;
import com.lucasj.gamedev.game.entities.collectibles.Mana;
import com.lucasj.gamedev.game.entities.particles.Particle;
import com.lucasj.gamedev.game.entities.particles.ParticleShape;
import com.lucasj.gamedev.game.entities.placeables.Placeable;
import com.lucasj.gamedev.game.entities.player.multiplayer.PlayerMP;
import com.lucasj.gamedev.game.entities.projectiles.Bullet;
import com.lucasj.gamedev.game.levels.LevelUpManager;
import com.lucasj.gamedev.game.skills.Dash;
import com.lucasj.gamedev.game.skills.Skill;
import com.lucasj.gamedev.game.skills.Teleport;
import com.lucasj.gamedev.game.weapons.AmmoMod;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.game.weapons.guns.AssaultRifle;
import com.lucasj.gamedev.game.weapons.guns.Shotgun;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;
import com.lucasj.gamedev.physics.CollisionSurface;
import com.lucasj.gamedev.utils.ConcurrentList;
import com.lucasj.gamedev.world.map.MiniMap;

public class Player extends Entity implements PlayerMP, MouseClickEventListener, MouseMotionEventListener, KeyboardEventListener, LevelUpEventListener, Controller {
	
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
	
	private int maxMana = 60;
	private float mana = 0;
	private float manaRegenRate = 0.25f;
	private int manaBarColorIndex = 0;
	private boolean manaBarColorReversed;
	private long lastManaBarUpdate;
	
	private float staminaUseRate = 40;
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
	
	private ConcurrentList<Placeable> activePlaceables;
	private PlayerPlaceableManager placeableManager;
	private boolean placingMode;
	
	private static PlayerStats globalStats = new PlayerStats();
	private PlayerBreadcrumbManager crumbManager;
	private BreadcrumbCache crumbCache = new BreadcrumbCache();
	
	private PlayerRewarder playerRewarder;
	
	private LevelUpManager lvlUpManager;
	
	private Skill equippedSkill;
	
	private int money = 500;
	private int gems = 0;
	
	private int xp;
	
	private long lastWalkSound;
	private float walkSoundCooldown = 0.5f;
	
	private float healthUnderlayDelay = 1;
	private float healthUnderlayRate = 0.35f;
	private float healthUnderlayLength;
	
	private Color color;
	
	private MiniMap minimap;
	
	private PlayerWavesStats wavesStats;
	
	private Tooltip activeTooltip;
	
	
	public Player(Game game, InputHandler input) {
		super(game);
		minimap = new MiniMap(game);
		lvlUpManager = new LevelUpManager(game);
		activePlaceables = new ConcurrentList<>();
		placeableManager = new PlayerPlaceableManager(game, this);
		this.input = input;
		this.size = (int) (64);
		this.healthUnderlayLength = this.health;
		this.movementSpeed = 270;
		this.playerRewarder = new PlayerRewarder(game);
		importance = 1;
		crumbManager = new PlayerBreadcrumbManager(game, this, 0.6);
		this.playerUpgrades = new PlayerUpgrades(game, this);
		input.addKeyboardListener(this);
		input.addMouseClickListener(this);
		input.addMouseMotionListener(this);
		
		game.getEventManager().addListener(this, ControllerEvent.class);
		
		this.primaryGun = new AssaultRifle(game, this);
		this.equippedSkill = new Teleport(game);
		
		this.wavesStats = new PlayerWavesStats();
		
		
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
		if(globalStats.addXP((int) xp)) {
	        LevelUpEvent e = new LevelUpEvent(globalStats.getLevel());
	        game.getEventManager().dispatchEvent(e);
		}
		xp = 0;
		this.activePlaceables.update();
		this.screenPosition = new Vector2D(game.getWidth()/2, game.getHeight()/2);
		isMoving = false;
		crumbCache.updateDistances(new ArrayList<>(crumbManager.activeBreadcrumbs));
		this.primaryGun.update();
		move(deltaTime);
		
		regenMana(deltaTime);
		
		if((System.currentTimeMillis() - this.lastTimeHurt)/1000.0 >= this.healthUnderlayDelay) {
			this.healthUnderlayLength -= this.healthUnderlayRate;
			if(this.healthUnderlayLength <= this.health) this.healthUnderlayLength = this.health;
		}
		
		if(playerAttacking && (System.currentTimeMillis() - lastAttack)/1000.0 > primaryGun.getFireRate()) attack(deltaTime);
		crumbManager.update(deltaTime);
		
		float checkAnimationSpeed = animationSpeed;
		// Sprint Multiplier
		if(isSprinting) checkAnimationSpeed = animationSpeed/sprintMultiplier;
		// Dashing Multiplier
		if(this.isDashing()) checkAnimationSpeed = checkAnimationSpeed/((Dash)this.equippedSkill).getSprintMultiplier();
		// Apply Multiplier
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
		
		if(this.activeTooltip != null) {
			if(this.activeTooltip.getTitle().equals("Player Information")) {
			
				activeTooltip = new Tooltip(
						game,
						"Player Information",
						
						String.format("""
						{RED}Health{LIGHT_GRAY}: 
						{WHITE} %s{LIGHT_GRAY} / {WHITE}%s{LIGHT_GRAY} ({WHITE}%s%%{LIGHT_GRAY}) {NL}
						{BLUE}Stamina{LIGHT_GRAY}: 
						{WHITE} %s{LIGHT_GRAY} / {WHITE}%s{LIGHT_GRAY} ({WHITE}%s%%{LIGHT_GRAY}) {NL}
						{LIGHT_BLUE}Mana{LIGHT_GRAY}: 
						{WHITE} %s{LIGHT_GRAY} / {WHITE}%s{LIGHT_GRAY} ({WHITE}%s%%{LIGHT_GRAY})
						""", 
						(int) (this.health),
						(int) (this.maxHealth),
						(int) ((this.health / this.maxHealth) * 100),
						(int) (this.stamina),
						(int) (this.maxStamina),
						(int) ((this.stamina / this.maxStamina) * 100), 
						(int) this.mana,
						(int) this.maxMana,
						(int) ((this.mana / this.maxMana) * 100)),
						
						this.mousePosition.getXint(),
						this.mousePosition.getYint(),
						new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 150),
						Color.white
						);
			} else if(this.activeTooltip.getTitle().equals("Gun Information")) {
					
					activeTooltip = new Tooltip(
							game,
							"Gun Information",
							
							String.format("""
							{LIGHT_RED}Damage{LIGHT_GRAY}: 
							{WHITE} %s{LIGHT_GRAY}{NL}
							{LIGHT_RED}Fire Rate{LIGHT_GRAY}: 
							{WHITE} %s{NL}
							{LIGHT_RED}Projectile Speed{LIGHT_GRAY}: 
							{WHITE} %s{NL}{NL}
							{%s}%s
							""", 
							(this.primaryGun.getDamage()),
							this.primaryGun.getFireRate(),
							this.primaryGun.getProjectileSpeed(),
							this.primaryGun.getTier().toString().toUpperCase(),
							this.primaryGun.getTier().toString()),
							
							this.mousePosition.getXint(),
							this.mousePosition.getYint(),
							new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 150),
							Color.white
							);
				}
		}
		
		if(minimap != null) minimap.update(deltaTime);

    	if(game.party != null) {
    		game.getSocketClient().getPacketManager().playerInfoPacket(health, maxHealth, position, this.currentWalkingImage);
    	}
		
		
	    if(game.party != null) {
	    	game.party.update(deltaTime);
	    }
	}

	@Override
	public List<Render> render() {
		if(!game.getWavesManager().hasGameStarted()) return new ArrayList<>();
		List<Render> renders = new ArrayList<>();
		renders.addAll(super.render());
		renders.add(new Render(Layer.Player, g -> {
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
		    
		}));
		
		renders.add(new Render(Layer.UI, g-> {
			Graphics2D g2d = (Graphics2D)g;
		    renderHealthBar(g2d);
		    renderMoney(g2d);
		    renderStaminaAndMana(g2d);
		    renderPlaceable(g2d);
		    renderEquippedGun(g2d);
		    renderAmmo(g2d);
		}));

	    if(minimap != null) renders.add(minimap.render());
	    
	    if(game.party != null) {
	    	renders.addAll(game.party.render());
	    }
		return renders;
	}
	
	private void renderAmmo(Graphics2D g2d) {
		int strWidth = g2d.getFontMetrics().stringWidth(this.primaryGun.getCurrentClip() + "/" + this.getPrimaryGun().getClipSize());
		g2d.setColor(Color.white);
		g2d.drawString(this.primaryGun.getCurrentClip() + "/" + this.getPrimaryGun().getClipSize(), game.getWidth()/2-(strWidth/2), game.getHeight()-47);

		g2d.setColor(Color.black);
		g2d.drawString(this.primaryGun.getCurrentClip() + "/" + this.getPrimaryGun().getClipSize(), game.getWidth()/2-(strWidth/2), game.getHeight()-50);
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
		
		if(this.primaryGun.getAmmoMod() != AmmoMod.None) {
			Image image = null;
			switch(this.primaryGun.getAmmoMod()) {
			case Flame:
				image = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/AmmoMods/flame.png", new Vector2D(), new Vector2D(32, 32));
				break;
			case Electric:
				image = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/AmmoMods/electric.png", new Vector2D(), new Vector2D(32, 32));
				break;
			case Mutant:
				image = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/AmmoMods/mutant.png", new Vector2D(), new Vector2D(32, 32));
				break;
			case None:
				Debug.log(this, "how the fuck did you get here");
				break;
				
			}
			g2d.drawImage(image, 128*2-36, game.getHeight()-136, 32, 32, null);
			
		}
		
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
			
			if(this.secondaryGun.getAmmoMod() != AmmoMod.None) {
				Image image = null;
				switch(this.secondaryGun.getAmmoMod()) {
				case Flame:
					image = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/AmmoMods/flame.png", new Vector2D(), new Vector2D(32, 32));
					break;
				case Electric:
					image = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/AmmoMods/electric.png", new Vector2D(), new Vector2D(32, 32));
					break;
				case Mutant:
					image = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/AmmoMods/mutant.png", new Vector2D(), new Vector2D(32, 32));
					break;
				case None:
					Debug.log(this, "how the fuck did you get here");
					break;
					
				}
				g2d.drawImage(image, 260 + 42, game.getHeight()-118, 16, 16, null);
				
			}
			
		}
	}
	
	private void renderHealthBar(Graphics2D g2d) {
		
		int margin = 100;
		int healthBarSize = 100;
		int healthBarLength = 4; //length x size = actual length
		
		int x = game.getWidth()/2 - (healthBarSize*healthBarLength)/2;
		
		g2d.setColor(Color.black);
		g2d.fillRect(x, game.getHeight()-(margin*2) - 15, healthBarLength * healthBarSize, healthBarSize + 15);

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
	
	private void renderStaminaAndMana(Graphics2D g2d) {
		
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
		
		if((System.currentTimeMillis() - this.lastManaBarUpdate)/1000.0 > 0.25f) {
			int increment = this.manaBarColorReversed ? -1 : 1;
			manaBarColorIndex += increment;
			if(manaBarColorIndex >= 16) {
				manaBarColorIndex = 15;
				manaBarColorReversed = true;
			}
			if(manaBarColorIndex < 0) {
				manaBarColorIndex = 0;
				manaBarColorReversed = false;
			}
			lastManaBarUpdate = System.currentTimeMillis();
		}
		
		g2d.setColor(Mana.colors.get(this.manaBarColorIndex));
		g2d.fillRect((int)(x + (staminaBarSize*0.1))
				, (int) (game.getHeight() - margin*2 + margin*0.1) - (int)(staminaBarSize/3 - (staminaBarSize * 0.2)), 
				(int) (((staminaBarSize * staminaBarLength) - (margin*0.2)) * ((double) mana/maxMana)), 
				(int)(staminaBarSize/3 - (staminaBarSize * 0.2)));
	}
	
	private void renderMoney(Graphics2D g2d) {

		g2d.setFont(game.font.deriveFont(Font.PLAIN, 64f)); // Derive the font size explicitly as a float

	    g2d.setColor(Color.black);
	    g2d.drawString("$" + NumberFormat.getInstance(Locale.US).format(this.money), game.getWidth()/2 - 200, game.getHeight() - 220);
	    g2d.setColor(Color.green);
	    g2d.drawString("$" + NumberFormat.getInstance(Locale.US).format(this.money), game.getWidth()/2 - 197, game.getHeight() - 220);

	    g2d.drawImage(SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Currency/Gem.png", new Vector2D(0, 0), new Vector2D(32, 32)), 
	    		game.getWidth()/2 + 150, game.getHeight() - 275, 64, 64, null);
	    g2d.setColor(Color.black);
	    g2d.drawString(Integer.toString(this.gems), game.getWidth()/2 + 150 - g2d.getFontMetrics().stringWidth(Integer.toString(this.gems)), game.getHeight() - 220);
	    g2d.setColor(GameColors.colors.PURPLE.getValue());
	    g2d.drawString(Integer.toString(this.gems), game.getWidth()/2 + 147 - g2d.getFontMetrics().stringWidth(Integer.toString(this.gems)), game.getHeight() - 220);
	}
	
	private void regenHealth() {
		if(this.getPlayerUpgrades().hasHealthRegen() && (System.currentTimeMillis() - this.lastTimeHurt)/1000.0 > this.timeToRegen) {
			this.health += this.getPlayerUpgrades().getHealthRegen();
	    	if(health > maxHealth) health = maxHealth;
		}
	}
	
	private void regenMana(double deltaTime) {
		if(this.mana < this.maxMana) {
			this.mana += this.manaRegenRate * deltaTime;
			if(this.mana > this.maxMana) this.mana = this.maxMana;
		}
	}
	
	private void move(double deltaTime) {
		
		Vector2D posUpdate = new Vector2D(0, 0);

	    // Update camera position based on WASD input
	    if (WASD[0]) { // W - Move up
	    	posUpdate.addY(-1);
	    }
	    if (WASD[1]) { // A - Move left
	    	posUpdate.addX(-1);
	    }
	    if (WASD[2]) { // S - Move down
	    	posUpdate.addY(1);
	    }
	    if (WASD[3]) { // D - Move right
	    	posUpdate.addX(1);
	    }
	    
	    posUpdate = posUpdate.normalize();
	    if (posUpdate.distanceTo(Vector2D.zero()) != 0) {
	        isMoving = true;
	    }
	    float speed = movementSpeed;
	    boolean dashing = false;
	    
	    if(this.equippedSkill instanceof Dash) {
	    	if(((Dash)equippedSkill).isActive()) {
	    		speed = movementSpeed * ((Dash)this.equippedSkill).getSprintMultiplier();
	    		dashing = true;
	    	}
	    }

	    Vector2D movement = posUpdate.multiply(speed * deltaTime);
	    movement = movement.multiply(this.getPlayerUpgrades().getMovementSpeedMultiplier());
	    
	    if(isReadyToSprint && stamina > 0 && isMoving) {
	    	movement = movement.multiply(sprintMultiplier);
	    	stamina -= staminaUseRate * deltaTime;
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
	    	stamina += (staminaUseRate/1.5) * deltaTime;
	    	if(stamina > maxStamina) stamina = maxStamina;
	    }

	    PlayerMoveEvent e = new PlayerMoveEvent();
	    e.setPositionBefore(this.position.copy());
	    
	    for (CollisionSurface surface : game.getCollisionSurfaces().toList()) {
	        // Calculate the entity's new position after applying movement
	        Vector2D newPosition = this.position.add(movement);

	        // Get properties of the collision surface
	        Vector2D surfacePosition = surface.getPosition();
	        int surfaceWidth = surface.getWidth();
	        int surfaceHeight = surface.getHeight();

	        // Check for collisions on the X axis
	        if (newPosition.getX() < surfacePosition.getX() + surfaceWidth &&
	            newPosition.getX() + this.size > surfacePosition.getX() &&
	            this.position.getY() + this.size > surfacePosition.getY() &&
	            this.position.getY() < surfacePosition.getY() + surfaceHeight) {
	            
	            // If colliding on the left
	            if (movement.getX() > 0 && this.position.getX() + this.size <= surfacePosition.getX()) {
	                movement = new Vector2D(0, movement.getY()); // Remove X movement
	            }

	            // If colliding on the right
	            if (movement.getX() < 0 && this.position.getX() >= surfacePosition.getX() + surfaceWidth) {
	                movement = new Vector2D(0, movement.getY()); // Remove X movement
	            }
	        }

	        // Check for collisions on the Y axis
	        if (newPosition.getY() < surfacePosition.getY() + surfaceHeight &&
	            newPosition.getY() + this.size > surfacePosition.getY() &&
	            this.position.getX() + this.size > surfacePosition.getX() &&
	            this.position.getX() < surfacePosition.getX() + surfaceWidth) {
	            
	            // If colliding on the top
	            if (movement.getY() > 0 && this.position.getY() + this.size <= surfacePosition.getY()) {
	                movement = new Vector2D(movement.getX(), 0); // Remove Y movement
	            }

	            // If colliding on the bottom
	            if (movement.getY() < 0 && this.position.getY() >= surfacePosition.getY() + surfaceHeight) {
	                movement = new Vector2D(movement.getX(), 0); // Remove Y movement
	            }
	        }
	    }


	    if(dashing) {
	    	((Dash)this.equippedSkill).setDistanceTravelled((int) (((Dash)this.equippedSkill).getDistanceTravelled() + movement.magnitude()));
	    	if(((Dash)this.equippedSkill).getDistanceTravelled() > ((Dash)this.equippedSkill).getDashDistance()) ((Dash)this.equippedSkill).setActive(false);
	    }
	    
	    
	    this.position = this.position.add(movement);
	    this.screenPosition = game.getCamera().worldToScreenPosition(this.position);
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
		
		this.getWavesStats().shoot();
		
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
		        Vector2D bulletVelocity = bloomedVelocity.multiply(this.primaryGun.getProjectileSpeed() * 25);

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
		        
		        Random rand = new Random();
		        int chance = rand.nextInt(100);
		        if(chance <= primaryGun.getTier().getAmmoModMultiplier()) {
		        	b.setAmmoMod(primaryGun.getAmmoMod());
		        }
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
			
			Vector2D bulletVelocity = bloomedVelocity.multiply(this.primaryGun.getProjectileSpeed()*25);
			
			int damage = (int) ((10 + this.primaryGun.getDamage()) * this.getPlayerUpgrades().getDamageMultiplier());
			Bullet b = new Bullet(game, this, this.position, 
					bulletVelocity, 
					10, 
					null, 
		            this.getPrimaryGun().getRange(), 
					damage);
			b.setPierce(this.getPrimaryGun().getPierce());

	        Random rand = new Random();
	        int chance = rand.nextInt(100);
	        if(chance <= primaryGun.getTier().getAmmoModMultiplier()) {
	        	b.setAmmoMod(primaryGun.getAmmoMod());
	        }
	        
			this.primaryGun.useRound();
			if(!this.primaryGun.isAutomatic()) this.playerAttacking = false;
			b.instantiate();
		}
		game.getAudioPlayer().playSound(primaryGun.getGunFireSound(), this.getPosition());
		
//		float dx = (float) (mousePosition.getX() - this.getScreenPosition().getX());
//		float dy = (float) (mousePosition.getY() - this.getScreenPosition().getY());
//
//		// Calculate the angle in radians and convert to degrees
//		float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
//
//		// Ensure the angle is in the range [0, 360)
//		if (angle < 0) {
//		    angle += 360;
//		}
//
//		
//		ParticleGenerator particles = new ParticleGenerator(game, 
//				this.position.add(size/2), 
//				0.1f,
//				0.5f,
//				angle,
//				90f,
//				5,
//				0.001f).setColorAndShape(() -> {
//					return SpriteTools.createColorGradient(new Color(173, 173, 173), new Color(40, 40, 40), 8);
//				}, ParticleShape.Circle);
		
		
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
	
	private void useSkill() {
		if(this.mana < this.equippedSkill.getManaCost()) return;
		if(this.equippedSkill instanceof Dash) {
			Dash skill = (Dash) equippedSkill;
			if(skill.isActive()) return;
			skill.setActive(true);
		} else if (this.equippedSkill instanceof Teleport) {
			Teleport skill = (Teleport) equippedSkill;
			
			this.position = this.game.getCamera().screenToWorldPosition(mousePosition);
			
		}
		this.mana -= equippedSkill.getManaCost();
	}
	
	public boolean takeDamage(float dmg) {
		this.wavesStats.damageTaken += dmg;
		//return false;
		this.lastTimeHurt = System.currentTimeMillis();
		PlayerDamageTakenEvent e = new PlayerDamageTakenEvent(this, dmg);
		game.getEventManager().dispatchEvent(e);
		return super.takeDamage(dmg);
	}

	@Override
	public void entityDeath() {
		game.getWavesManager().endGame();
		this.money = 500;
		this.gems = 0;
		this.position = new Vector2D(game.getWidth(), game.getHeight());
		this.health = maxHealth;
		this.stamina = maxStamina;
		this.playerUpgrades.reset();
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		
	}

	@Override
	public void onMousePressed(MouseEvent e) {
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void onControlKeyPressed(Controls control) {
		switch(control) {
		case UP:
	        WASD[0] = true;
	        break;
	        	
		case DOWN:
			WASD[2] = true;
			break;
			
		case LEFT:
			WASD[1] = true;
			break;
			
		case RIGHT:
			WASD[3] = true;
			break;
		
		case SPRINT:
        	isReadyToSprint = true;
			break;
        	
		case PRIMARY:
			playerAttacking = true;
			break;
			
		case SECONDARY:
			useSkill();
			break;
			
		case SWAP:
        	this.swapWeapons();
			break;
			
		case RELOAD:
			if(!this.primaryGun.isReloading() && this.primaryGun.getCurrentClip() < this.primaryGun.getClipSize()) this.primaryGun.reload();
			break;
			
		case SPECIAL:
			break;
			
		case PLACE:
			if(this.placeableManager.getEquippedPlaceable() != null) {
				this.placeableManager.getEquippedPlaceable().instantiate(game.getCamera().screenToWorldPosition(mousePosition));
				this.placeableManager.setEquippedPlaceable(null);
			}
			break;
		
		case MAP:
        	this.minimap.setMinimized(!this.minimap.isMinimized());
			break;
        	
		case PAUSE:
        	if(game.getWavesManager().getNPCManager().isAnyOpen()) {
        		game.getWavesManager().getNPCManager().closeAll();
        	} else game.setPaused(!game.isPaused());
			break;
        	
		}
	}

	@Override
	public void onControlKeyReleased(Controls control) {
		switch(control) {
		case UP:
	        WASD[0] = false;
	        break;
	        	
		case DOWN:
			WASD[2] = false;
			break;
			
		case LEFT:
			WASD[1] = false;
			break;
			
		case RIGHT:
			WASD[3] = false;
			break;
        	
		case PRIMARY:
			playerAttacking = false;
			break;
		
		case SPRINT:
	    	isReadyToSprint = false;
			break;
			
		default:
			break;
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
		this.handleTooltips(e);
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
		if(!game.getWavesManager().hasGameStarted()) return;
		mousePosition = new Vector2D(e.getX(), e.getY());
		
		this.handleTooltips(e);
		
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
	
	public void handleTooltips(MouseEvent e ) {
		mousePosition = new Vector2D(e.getX(), e.getY());
		
		int margin = 100;
		int healthBarSize = 100;
		int healthBarLength = 4; //length x size = actual length
		int x = game.getWidth()/2 - (healthBarSize*healthBarLength)/2;
		int y = game.getHeight()-(margin*2) - 15;
		int length = healthBarLength * healthBarSize;
		int height = healthBarSize + 15;
		double mouseX = mousePosition.getX();
		double mouseY = mousePosition.getY();
		
		boolean isInsideInfoBox = (mouseX >= x && mouseX <= x + length &&
                mouseY >= y && mouseY <= y + height);
		
		int primaryGunX = 128;
		int primaryGunY = game.getHeight()-228;
		int primaryGunLength = 128;
		int primaryGunHeight= 128;
		
		boolean isInsideGunBox = (mouseX >= primaryGunX && mouseX <= primaryGunX + primaryGunLength &&
                mouseY >= primaryGunY && mouseY <= primaryGunY + primaryGunHeight);

		if (isInsideInfoBox) {
			activeTooltip = new Tooltip(
					game,
					"Player Information",
					
					String.format("""
					{RED}Health{LIGHT_GRAY}: 
					{WHITE} %s{LIGHT_GRAY} / {WHITE}%s{LIGHT_GRAY} ({WHITE}%s%%{LIGHT_GRAY}) {NL}
					{BLUE}Stamina{LIGHT_GRAY}: 
					{WHITE} %s{LIGHT_GRAY} / {WHITE}%s{LIGHT_GRAY} ({WHITE}%s%%{LIGHT_GRAY}) {NL}
					{LIGHT_BLUE}Mana{LIGHT_GRAY}: 
					{WHITE} %s{LIGHT_GRAY} / {WHITE}%s{LIGHT_GRAY} ({WHITE}%s%%{LIGHT_GRAY})
					""", 
					(int) (this.health),
					(int) (this.maxHealth),
					(int) ((this.health / this.maxHealth) * 100),
					(int) (this.stamina),
					(int) (this.maxStamina),
					(int) ((this.stamina / this.maxStamina) * 100), 
					(int) this.mana,
					(int) this.maxMana,
					(int) ((this.mana / this.maxMana) * 100)),
					
					this.mousePosition.getXint(),
					this.mousePosition.getYint(),
					new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 150),
					Color.white
					);
		} else if (isInsideGunBox) {
			activeTooltip = new Tooltip(
					game,
					"Gun Information",
					
					String.format("""
					{LIGHT_RED}Damage{LIGHT_GRAY}: 
					{WHITE} %s{LIGHT_GRAY}{NL}
					{LIGHT_RED}Fire Rate{LIGHT_GRAY}: 
					{WHITE} %s{NL}
					{LIGHT_RED}Projectile Speed{LIGHT_GRAY}: 
					{WHITE} %s{NL}{NL}
					{%s}%s
					""", 
					(this.primaryGun.getDamage()),
					this.primaryGun.getFireRate(),
					this.primaryGun.getProjectileSpeed(),
					this.primaryGun.getTier().toString().toUpperCase(),
					this.primaryGun.getTier().toString()),
					
					this.mousePosition.getXint(),
					this.mousePosition.getYint(),
					new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 150),
					Color.white
					);
			
		} else {
			this.activeTooltip = null;
		}
	}
	
	public void addXp(float num) {
		this.xp += num*100;
		this.wavesStats.totalExp += num;
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
		this.addXp(money/100);
		this.wavesStats.moneyEarned += money;
		
		Random rand = new Random();
		
		Particle particle = new Particle(game, null, new Vector2D(rand.nextInt(-100, 100), rand.nextInt(-100, 0)).normalize().multiply(2),
				0.6f, Color.green, ParticleShape.Text, 16).setText("+$" + money);
		
		Particle.spawnSingleParticle(particle, new Vector2D(game.getWidth()/2 - 200, game.getHeight() - 220));
		
	}
	
	public void addMana(float mana) {
		this.mana += mana;
		if(this.mana > this.maxMana) this.mana = this.maxMana;
	}
	
	public static PlayerStats getGlobalStats() {
		return globalStats;
	}
	
	public void addGem(int num) {
		this.gems += num;
		this.wavesStats.gemsEarned += num;
		Random rand = new Random();
		Particle particle = new Particle(game, null, new Vector2D(rand.nextInt(-100, 100), rand.nextInt(-100, 0)).normalize().multiply(2),
				0.6f, GameColors.colors.PURPLE.getValue(), ParticleShape.Text, 16).setText("+" + num);
		
		Particle.spawnSingleParticle(particle, new Vector2D(game.getWidth()/2 + 175, game.getHeight() - 220));
	}
	
	public boolean removeGem(int num) {
		if(this.gems >= num) {
			this.gems -= num;
			this.wavesStats.gemsSpent+= num;
			return true;
		}
		return false;
	}
	
	public boolean removeMoney(int num) {
		if(this.money >= num) {
			this.money -= num;
			this.wavesStats.moneySpent += num;
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
		this.maxHealth = num;
		return this;
	}

	public boolean isPickingClass() {
		return isPickingClass;
	}

	public void setPickingClass(boolean isPickingClass) {
		this.isPickingClass = isPickingClass;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public void resetWavesStats() {
		this.wavesStats = new PlayerWavesStats();
	}
	
	public PlayerWavesStats getWavesStats() {
		return wavesStats;
	}

	@Override
	public void onLevelUp(LevelUpEvent e) {
		this.wavesStats.finalLevel = e.getNewLevel();
	}

	public Skill getEquippedSkill() {
		return equippedSkill;
	}

	public void setEquippedSkill(Skill equippedSkill) {
		this.equippedSkill = equippedSkill;
	}
	
	public Tooltip getActiveTooltip() {
		return this.activeTooltip;
	}
	
	public boolean isDashing() {
		if(this.getEquippedSkill() != null && this.equippedSkill instanceof Dash) {
			if(((Dash)this.equippedSkill).isActive()) return true;
		}
		return false;
	}

	public boolean isSprinting() {
		return isSprinting;
	}
	
}
