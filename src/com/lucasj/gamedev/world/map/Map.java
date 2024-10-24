package com.lucasj.gamedev.world.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Random;

import com.lucasj.gamedev.essentials.Camera;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Map {

	public static HashMap<String, Map> maps = new HashMap<String, Map>();
	
	private int width, height;
	private Game game;
	private MapManager mapm;
	private Tile[][] mapTiles;
	
	public Map(Game game, MapManager mapm, int width, int height) {
		this.game = game;
		this.height = height;
		this.width = width;
		this.mapm = mapm;
		mapTiles = new Tile[width][height];
	}
	
	public void generateMap() {
		Random rand = new Random();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Randomly select a tile for each position
                Tile tile = mapm.getGrass()[rand.nextInt(mapm.getGrass().length)];
                mapTiles[x][y] = tile; // Store the selected tile in the 2D array
            }
        }
	}
	
	public Vector2D getWorldSize() {
		return new Vector2D(this.width * ((mapm.getTileSize() * 4) * game.getCamera().getScale()), this.height * (int) ((mapm.getTileSize() * 4) * game.getCamera().getScale()));
	}
	
	public void render(Graphics g) {
	    Graphics2D g2d = (Graphics2D) g;
	    int tileSize = mapm.getTileSize();
	    int scaledTileSize = (int) ((tileSize * 4) * game.getCamera().getScale()); // Example scaling factor

	    // Get the camera's world position and viewport
	    Camera camera = game.getCamera();
	    Vector2D cameraWorldPos = camera.getWorldPosition();
	    Vector2D viewport = camera.getViewport();

	    // Calculate the range of tiles visible in the camera's viewport
	    int startX = Math.max((int) (cameraWorldPos.getX() / scaledTileSize), 0);
	    int endX = Math.min((int) ((cameraWorldPos.getX() + viewport.getX()) / scaledTileSize) + 1, width);
	    int startY = Math.max((int) (cameraWorldPos.getY() / scaledTileSize), 0);
	    int endY = Math.min((int) ((cameraWorldPos.getY() + viewport.getY()) / scaledTileSize) + 1, height);

	    // Iterate only over the visible range of tiles
	    for (int x = startX; x < endX; x++) {
	        for (int y = startY; y < endY; y++) {
	            Tile tile = mapTiles[x][y]; // Retrieve the tile from the 2D array

	            // Calculate the screen position based on the camera's world position
	            int screenX = (x * scaledTileSize) - (int) cameraWorldPos.getX();
	            int screenY = (y * scaledTileSize) - (int) cameraWorldPos.getY();

	            // Draw the tile image at the calculated screen position
	            g2d.drawImage(tile.getTile(), screenX, screenY, scaledTileSize, scaledTileSize, null);
	        }
	    }
	}

}
