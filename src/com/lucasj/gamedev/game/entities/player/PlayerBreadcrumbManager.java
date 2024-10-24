package com.lucasj.gamedev.game.entities.player;

import java.util.LinkedList;
import java.util.Queue;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.ai.Breadcrumb;

// This class drops "breadcrumb" (invisible points) every 0.25 seconds, enemies will follow these breadcrumbs if there are no obstacles blocking the ray casted to the crumb
public class PlayerBreadcrumbManager {
	
	public Queue<Breadcrumb> activeBreadcrumbs = new LinkedList<>();
	private Game game;
	private Player player;
	
	private double spawnTime;
	private long lastSpawn;
	
	public PlayerBreadcrumbManager(Game game, Player player, double spawnTime) {
		this.game = game;
		this.player = player;
		this.spawnTime = spawnTime;
	}
	
	public void update(double deltaTime) {
		if((System.currentTimeMillis() - lastSpawn)/1000.0 >= spawnTime && player.isMoving()) {
			if(activeBreadcrumbs.size() >= 6) {
				removeLast();
			}
			dropCrumb();
			lastSpawn = System.currentTimeMillis();
		}
		activeBreadcrumbs.forEach(crumb -> {
			crumb.update();
		});
	}
	
	//private but public for testing
	public void dropCrumb() {
		Breadcrumb crumb = new Breadcrumb(game, player.getPosition(), player, this);
		addCrumb(crumb);
	}
	
	public void addCrumb(Breadcrumb crumb) {
		activeBreadcrumbs.add(crumb);
		System.out.println("New crumb");
	}
	
	public void removeLast() {
		activeBreadcrumbs.remove();
		System.out.println("Removed oldest crumb");
	}
	
	public void removeCrumb(Breadcrumb crumb) {
		activeBreadcrumbs.remove(crumb);
	}

}
