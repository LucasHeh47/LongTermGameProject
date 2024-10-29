package com.lucasj.gamedev.events.player;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.entities.projectiles.Projectile;

public class PlayerAttackEvent implements GameEvent {

	private Projectile projectileSummoned;
	private int damage;
	private Boolean didHit = null;
	
	public PlayerAttackEvent(Projectile projectileSummoned, int damageDealt) {
		this.projectileSummoned = projectileSummoned;
		this.damage = damageDealt;
	}
	
    public Boolean getDidHit() {
        return didHit;
    }

    public void registerHit() {
        this.didHit = true;
    }

    public void registerMiss() {
        this.didHit = false;
    }

    public boolean isPending() {
        return didHit == null;
    }

	public Projectile getProjectileSummoned() {
		return projectileSummoned;
	}

	public int getDamage() {
		return damage;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}
	
}
