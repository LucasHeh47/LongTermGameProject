package com.lucasj.gamedev.world.map;

import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;

public class MapManager {

	private int tileSize = 64;
	
	private Game game;
	
	public Map map;
	
	private Tile[] grass;
	private Tile trees;
	
	private Vector2D worldSize;
    
    public String selectedMap = "Map1-extended";

	private static final String assetPackDirectory = System.getProperty("user.dir") + "/src/com/lucasj/gamedev/Assets/Ninja Adventure - Asset Pack/";
	
	private List<Vector2D> enemySpawnpoints;
	
	public MapManager(Game game) {
		this.game = game;
		enemySpawnpoints = new ArrayList<Vector2D>();
		worldSize = new Vector2D(100, 100);
		map = new Map(game, this, (int) (worldSize.getX()), (int) (worldSize.getY()));
		grass = new Tile[5];
		grass[1] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176 + 16*4, 192), new Vector2D(16, 16));
		grass[2] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176 + 16*3, 192), new Vector2D(16, 16));
		grass[3] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176 + 16*2, 192), new Vector2D(16, 16));
		grass[4] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176 + 16, 192), new Vector2D(16, 16));
		grass[0] = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176, 192), new Vector2D(16, 16));
		
		trees = new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetNature.png", new Vector2D(32, 0), new Vector2D(32));
		
		
		try {
			map.generateMap(SpriteTools.assetDirectory + "Art/Maps/" + selectedMap + ".png");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public Tile getTrees() {
		return trees;
	}

	public Vector2D getWorldSize() {
		return new Vector2D(worldSize.getX()*tileSize, worldSize.getY()*tileSize);
	}
	
	public void setWorldSize(Vector2D size) {
		this.worldSize = size;
		this.map.setSize(size);
	}
	
	public List<Vector2D> getSpawnpoints() {
		return this.enemySpawnpoints;
	}
	
	public void addSpawnpoint(Vector2D pos) {
		this.enemySpawnpoints.add(pos);
	}

}
