package com.lucasj.gamedev.world.map;

import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.world.tiles.Biome;
import com.lucasj.gamedev.world.tiles.Tile;

public class MapManager {

	private int tileSize = 64;
	
	private Game game;
	
	public Map map;
	
	private Vector2D worldSize;
    
    public String selectedMap = "Map1-extended";

	private static final String assetPackDirectory = System.getProperty("user.dir") + "/src/com/lucasj/gamedev/Assets/Ninja Adventure - Asset Pack/";
	
	private List<Vector2D> enemySpawnpoints;
	
	private List<Biome> biomes;
	
	public MapManager(Game game) {
		this.game = game;
		enemySpawnpoints = new ArrayList<Vector2D>();
		worldSize = new Vector2D(200, 200);
		map = new Map(game, this, (int) (worldSize.getX()), (int) (worldSize.getY()));
		biomes = new ArrayList<Biome>();
		Biome grassland = new Biome(game, 25);
		
		grassland.addGrassTiles(new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176 + 16*4, 192), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176 + 16*3, 192), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176 + 16*2, 192), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176 + 16, 192), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(176, 192), new Vector2D(16, 16)));
		
		grassland.addTreeTiles(new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetNature.png", new Vector2D(32, 0), new Vector2D(32)));
		
		
		Biome savanah = new Biome(game, 50);
		savanah.addGrassTiles(new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16*4, 192), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16*3, 192), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16*2, 192), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16, 192), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(0, 192), new Vector2D(16, 16)));

		savanah.addTreeTiles(new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetNature.png", new Vector2D(0, 0), new Vector2D(32)));
		
		Biome desert = new Biome(game, 100);
		desert.addGrassTiles(new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16*4, 80), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16*3, 80), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16*2, 80), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(16, 80), new Vector2D(16, 16)),
		new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetFloor.png", new Vector2D(0, 80), new Vector2D(16, 16)));
		
		desert.addTreeTiles(new Tile(assetPackDirectory + "Backgrounds/Tilesets/TilesetNature.png", new Vector2D(32*2, 0), new Vector2D(32)));
		
		biomes.add(grassland);
		biomes.add(savanah);
		biomes.add(desert);
		
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
	
	public Tile[] getGrass(int x, int y) {
		return map.getBiome(x, y).getGrass().toArray(new Tile[0]);
	}
	
	public Tile[] getTrees(int x, int y) {
		return map.getBiome(x, y).getTrees().toArray(new Tile[0]);
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
	
	public List<Biome> getBiomes() {
		return this.biomes;
	}

}
