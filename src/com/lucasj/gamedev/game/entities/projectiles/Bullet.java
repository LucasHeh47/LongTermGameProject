package com.lucasj.gamedev.game.entities.projectiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.npc.NPC;
import com.lucasj.gamedev.game.entities.placeables.Turret;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class Bullet extends Projectile {

	private int pierce = 0;
	private List<Entity> entitiesPierced;
	
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
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.blue);
		g2d.fillOval((int)screenPosition.getX(), (int)screenPosition.getY(), 
				size, size);
		
	}

	@Override
	public void entityDeath() {
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		if(this.getSender() instanceof Player && e.getCollider() instanceof Player) return;
		if(this.getSender() instanceof Turret && e.getCollider() instanceof Player) return;
		if(e.getCollider() instanceof NPC) return;
		if(!e.getCollider().isAlive()) return;
		boolean died = e.getCollider().takeDamage(this.getDamage());
		System.out.println("died -----------------------------");
		if(e.getCollider() instanceof Enemy && died) {
			game.getWavesManager().killedEnemy();
			e.getCollider().setKiller(this.getSender());
		}
		if(pierce <= 1) {
			die();
		} else {
			if (!this.entitiesPierced.contains(e.getCollider())) {
				pierce -= 1;
				Debug.log(this, pierce);
				if(!died) {
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
		this.position = position.add(velocity);
	}
	
	public void setPierce(int pierce) {
		this.pierce = pierce;
	}

}
