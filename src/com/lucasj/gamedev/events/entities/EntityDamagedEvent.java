package com.lucasj.gamedev.events.entities;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.entities.Entity;

public class EntityDamagedEvent implements GameEvent{

	private float damage;
	private float realDamage;
	private Entity attacker;
	private Entity entity;
	
	public EntityDamagedEvent(Entity entity, Entity attacker, float damage) {
		this.entity = entity;
		this.attacker = attacker;
		this.realDamage = damage;
		if(entity.isAlive()) {
			this.damage = damage;
		} else {
			this.damage = entity.getLastHealth();
		}
	}
	
	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}

	public float getDamage() {
		return damage;
	}
	
	public float getRealDamage() {
		return realDamage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public Entity getAttacker() {
		return attacker;
	}

	public void setAttacker(Entity attacker) {
		this.attacker = attacker;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
