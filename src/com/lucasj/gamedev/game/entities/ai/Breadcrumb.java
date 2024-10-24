package com.lucasj.gamedev.game.entities.ai;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.player.PlayerBreadcrumbManager;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Breadcrumb {
	
	private Game game;
	private Vector2D position;
	private Entity dropper;
	private Vector2D screenPosition;
	
	private PlayerBreadcrumbManager crumbManager;

	public Breadcrumb(Game game, Vector2D position, Entity dropper, PlayerBreadcrumbManager crumbManager) {
		this.position = position;
		this.dropper = dropper;
		this.game = game;
		this.crumbManager = crumbManager;
		this.screenPosition = game.getCamera().worldToScreenPosition(position);
	}
	
	public void update() {
		Vector2D cameraPos = game.getCamera().getWorldPosition();
		screenPosition = this.position.subtract(cameraPos);
	}

	public Vector2D getPosition() {
		return position;
	}
	
	public Vector2D getScreenPosition() {
		return screenPosition;
	}

	public Entity getDropper() {
		return dropper;
	}

}
