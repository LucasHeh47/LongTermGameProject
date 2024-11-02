package com.lucasj.gamedev.events.entities;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.entities.Entity;

public class EntityDeathEvent implements GameEvent {

	private Entity killer;
	private Entity entity;
	
	
	public EntityDeathEvent(Entity entity, Entity killer) {
		this.setEntity(entity);
		this.setKiller(killer);
	}
	
	
	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}


	public Entity getEntity() {
		return entity;
	}


	public void setEntity(Entity entity) {
		this.entity = entity;
	}


	public Entity getKiller() {
		return killer;
	}


	public void setKiller(Entity killer) {
		this.killer = killer;
	}
	
}
