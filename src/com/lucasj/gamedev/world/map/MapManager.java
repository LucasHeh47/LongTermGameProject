package com.lucasj.gamedev.world.map;

import java.awt.Graphics;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;

public class MapManager {

	private int tileSize = 16;
	
	private Game game;
	
	public Map map;
	
	private Tile[] grass;
	
	private Vector2D worldSize;

	private static final String assetPackDirectory = System.getProperty("user.dir") + "/src/com/lucasj/gamedev/Assets/Ninja Adventure - Asset Pack/";
	
	public MapManager(Game game) {
		this.game = game;
		worldSize = new Vector2D(50, 50);
		map = new Map(game, this, (int) (worldSize.getX()), (int) (worldSize.getY()));
		
		grass = new Tile[5];
		grass[0] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(64, 192), new Vector2D(16, 16));
		grass[1] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(48, 192), new Vector2D(16, 16));
		grass[2] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(32, 192), new Vector2D(16, 16));
		grass[3] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16, 192), new Vector2D(16, 16));
		grass[4] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(0, 192), new Vector2D(16, 16));
		
		
		map.generateMap();
	}
	
	public void render(Graphics g) {
		map.render(g);
	}

	public int getTileSize() {
		return tileSize;
	}
	
	public Tile[] getGrass() {
		return grass;
	}

	public Vector2D getWorldSize() {
		return new Vector2D(worldSize.getX()*tileSize, worldSize.getY()*tileSize);
	}

}
