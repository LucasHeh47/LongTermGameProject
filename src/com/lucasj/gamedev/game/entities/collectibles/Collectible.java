package com.lucasj.gamedev.game.entities.collectibles;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;

public abstract class Collectible{

	protected Game game;
	protected Vector2D position;
	protected Vector2D screenPosition;
	protected Vector2D size;
	
	protected float animationTick = 0.08f;
	protected long lastAnimationTick;
	
	public Collectible(Game game, Vector2D position, Vector2D size) {
		this.game = game;
		this.position = position;
		this.size = size;
		this.screenPosition = game.getCamera().worldToScreenPosition(position);
		lastAnimationTick = System.currentTimeMillis();
		game.instantiatedCollectibles.add(this);
	}
	
	public void update(double deltaTime) {
		screenPosition = game.getCamera().worldToScreenPosition(position);
		playerCollisionCheck();
	}

	public List<Render> render() {
		List<Render> renders = new ArrayList<>();
		return renders;
	}
	
	abstract void collect(Player p);
	
	public void playerCollisionCheck() {
		if(game.getPlayer().isCollidingWith(position, size)) {
			collect(game.getPlayer());
		}
	}

}
