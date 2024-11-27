package com.lucasj.gamedev.game.entities.projectiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.npc.NPC;
import com.lucasj.gamedev.game.entities.particles.ParticleGenerator;
import com.lucasj.gamedev.game.entities.particles.ParticleShape;
import com.lucasj.gamedev.game.entities.placeables.Turret;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.entities.projectiles.data.BulletMutantModData;
import com.lucasj.gamedev.game.weapons.AmmoMod;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class Bullet extends Projectile {

	private int initialPierce;
	private int pierce = 0;
	private List<Entity> entitiesPierced;
	private ParticleGenerator particles;
	
	private BulletMutantModData mutantData;
	
	private AmmoMod ammoMod;
	
	/**
	 * 
	 * @param game
	 * @param sender
	 * @param position
	 * @param velocity
	 * @param size
	 * @param tag
	 * @param timeToLive
	 * @param damage
	 */
	public Bullet(Game game, Entity sender, Vector2D position, Vector2D velocity, int size,
			String tag, float timeToLive, int damage) {
		super(game, sender, position, velocity, 0, size, tag, timeToLive, damage);
		this.entitiesPierced = new ArrayList<>();

	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.blue);
		g2d.fillOval((int)screenPosition.getX(), (int)screenPosition.getY(), 
				size, size);
		
	}
	
	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		if(particles != null) particles.setPosition(position);
	}

	@Override
	public void entityDeath() {
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		if(!this.isAlive) return;
		if(this.getSender() instanceof Player && e.getCollider() instanceof Player) return;
		if(this.getSender() instanceof Turret && e.getCollider() instanceof Player) return;
		if(e.getCollider() instanceof NPC) return;
		if(!e.getCollider().isAlive()) return;
		boolean died;
		if(initialPierce > 1) {
			died = e.getCollider().takeDamage(this.getDamage() * (pierce) / (float) initialPierce);
		} else {
			died = e.getCollider().takeDamage(this.getDamage());
		}
		if(e.getCollider() instanceof Enemy) {

			float dx = (float) (velocity.getX() - this.getScreenPosition().getX());
			float dy = (float) (velocity.getY() - this.getScreenPosition().getY());
	
			// Calculate the angle in radians and convert to degrees
			float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
	
			// Ensure the angle is in the range [0, 360)
			if (angle < 0) {
			    angle += 360;
			}
			if(ammoMod == AmmoMod.Flame) ((Enemy) e.getCollider()).setOnFire(5);
			if(ammoMod == AmmoMod.Electric) ((Enemy) e.getCollider()).zap();
			ParticleGenerator particles = new ParticleGenerator(game, 
					this.position.add(size/2), 
					0.2f,
					0.5f,
					angle,
					45,
					1,
					0.08f, 
					20).setColorAndImage(() -> {
						return SpriteTools.createColorGradient(new Color(245, 37, 37), new Color(100, 8, 8), 8);
					}, () -> {
						List<Image> images = new ArrayList<>();
						for(int i = 1; i < 10; i++) {
							images.add(SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Particles/particle-" + i + ".png", 
									new Vector2D(), new Vector2D(4, 4)));
						}
						return images;
					});
			
			if(died) {
				e.getCollider().setKiller(this.getSender());
			}
		}
		if (!this.entitiesPierced.contains(e.getCollider())) {
		    if (pierce <= 1) {
		        die();

				if(ammoMod == AmmoMod.Mutant) {
					Player p;
					if(this.getSender() instanceof Player) {
						p = (Player) this.getSender();
					} else {
						return;
					}
					if(!this.getMutantData().willSplit()) return;
					int damage = this.getDamage();
					Bullet b = new Bullet(game, this.getSender(), this.position, 
							this.velocity.rotate(45), 
							size, 
							null, 
				            (int) this.timeToLive, 
							damage);
					b.setPierce(this.initialPierce/2);
					b.entitiesPierced.add(e.getEntity());
					b.setAmmoMod(AmmoMod.Mutant);
					b.getMutantData().setGeneration(this.getMutantData().getGeneration()+1);
					b.instantiate();
					Bullet b2 = new Bullet(game, this, this.position, 
							this.velocity.rotate(-45), 
							size, 
							null, 
				            (int) this.timeToLive, 
							damage);
					b2.setPierce(this.initialPierce/2);
					b.entitiesPierced.add(e.getEntity());  
					b2.setAmmoMod(AmmoMod.Mutant);
					b2.getMutantData().setGeneration(this.getMutantData().getGeneration()+1);
					b2.instantiate();
				}
		    } else {
		        pierce -= 1;
		        Debug.log(this, pierce);
		        if (!died) {
		            this.entitiesPierced.add(e.getCollider());
		        }
		    }
		}

		if(this.playerAttackEvent != null) {
			this.playerAttackEvent.registerHit();
		}
	}

	@Override
	public void updateProjectile(double deltaTime) {
		this.position = position.add(velocity.multiply(deltaTime));
	}
	
	public void setPierce(int pierce) {
		this.pierce = pierce;
		this.initialPierce = pierce;
	}

	public AmmoMod getAmmoMod() {
		return ammoMod;
	}

	public void setAmmoMod(AmmoMod ammoMod) {
	    this.ammoMod = ammoMod;
		if(ammoMod == AmmoMod.Mutant) {
			this.mutantData = new BulletMutantModData(1);
		}

	    final Color color1;
	    final Color color2;
	    
	    switch (ammoMod) {
	        case Flame:
	            color1 = new Color(255, 211, 25);
	            color2 = new Color(255, 72, 25);
	            break;
	        case Electric:
	            color1 = new Color(225, 253, 255);
	            color2 = new Color(147, 246, 255);
	            break;
	        case Mutant:
	        	color1 = new Color(1, 174, 0);
	        	color2 = new Color(2, 138, 1);
	        	break;
	        case None:
	        	return;
	        default:
	            color1 = Color.black;
	            color2 = Color.black;
	            break;
	    }

	    particles = new ParticleGenerator(
	            game,
	            this.position.add(size / 2),
	            (float) timeToLive,
	            0.1f,
	            velocity.multiply(-2).toDeg(),
	            90,
	            4,
	            0.008f,
	            15
	    ).setColorAndShape(() -> SpriteTools.createColorGradient(color1, color2, 8), ParticleShape.Circle).setStaticParticles();
	}

	public BulletMutantModData getMutantData() {
		return this.mutantData;
	}

}
