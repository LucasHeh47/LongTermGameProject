package com.lucasj.gamedev.events.entities;

import com.lucasj.gamedev.events.GameEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.player.Player;

public class EntityAggroEvent implements GameEvent {
	
	private Entity entity;
	private Player player;
	
	public EntityAggroEvent(Entity entity, Player player) {
		this.entity = entity;
		this.player = player;
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

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}