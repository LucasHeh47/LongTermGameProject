package com.lucasj.gamedev.game.entities.player.multiplayer;

import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;

public interface PlayerMP {
	public String getUsername();
	public Object getPlayer();
	public Entity setPosition(Vector2D pos);
	public void setWalkingImage(int num);
	public Entity setHealth(float num);
	public Entity setMaxHealth(float num);
}
