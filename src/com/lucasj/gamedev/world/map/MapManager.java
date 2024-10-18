package com.lucasj.gamedev.world.map;

import java.awt.Graphics;

public class MapManager {

	private int tileSize = 64;
	
	Map map;
	
	public MapManager() {
		map = new Map(this, 100, 50);
	}
	
	public void render(Graphics g) {
		map.render(g);
	}

	public int getTileSize() {
		return tileSize;
	}

}
