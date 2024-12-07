package com.lucasj.gamedev.world.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.lucasj.gamedev.essentials.Camera;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.physics.CollisionSurface;

public class Map {

	public static HashMap<String, Map> maps = new HashMap<String, Map>();
	
	private int width, height;
	private Game game;
	private MapManager mapm;
	private Tile[][][] mapTiles;
	
	public Map(Game game, MapManager mapm, int width, int height) {
		this.game = game;
		this.height = height;
		this.width = width;
		this.mapm = mapm;
		mapTiles = new Tile[2][width][height];
	}
	
	public void generateMap(String mapDirectory) throws IOException {
	    Random rand = new Random();
	    game.getCollisionSurfaces().clear();
	    int terrainData[][] = generateGrayscaleArray(mapDirectory);
	    
	    // Biomes
//	    float[][] noise = PerlinNoise.generatePerlinNoise(width, height, 6, 0.4f);

	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            Tile tile = null;

	            // Check the value from the terrainData array
	            int terrainType = terrainData[y][x]; // Note: Use y as the row index and x as the column index

	            mapTiles[0][x][y] = mapm.getGrass()[rand.nextInt(mapm.getGrass().length)];
	            if (terrainType == 100) {
	                tile = mapm.getTrees();
	            }

	            mapTiles[1][x][y] = tile; // Store the selected tile in the 2D array
	        }
	    }
	}

	private int mapNoiseToIndex(float noiseValue, float bias, int arrayLength) {
	    // Apply bias using exponential adjustment
	    float adjustedValue = (float) Math.pow(noiseValue, 1 / (1 - bias));
	    return Math.min((int) (adjustedValue * arrayLength), arrayLength - 1);
	}
	
	public List<Render> render() {
		List<Render> renders = new ArrayList<>();
		renders.add(new Render(Layer.Background, g -> {
			Graphics2D g2d = (Graphics2D) g;
		    int tileSize = mapm.getTileSize();

		    Camera camera = game.getCamera();
		    Vector2D cameraWorldPos = camera.getWorldPosition();
		    Vector2D viewport = camera.getViewport();

		    int startX = Math.max((int) (cameraWorldPos.getX() / tileSize), 0);
		    int endX = Math.min((int) ((cameraWorldPos.getX() + viewport.getX()) / tileSize) + 1, width);
		    int startY = Math.max((int) (cameraWorldPos.getY() / tileSize), 0);
		    int endY = Math.min((int) ((cameraWorldPos.getY() + viewport.getY()) / tileSize) + 1, height);
		    for (int x = startX; x < endX; x++) {
		        for (int y = startY; y < endY; y++) {
		            Tile tile = mapTiles[0][x][y];
		            if (tile != null) {
			            int screenX = (x * tileSize) - (int) cameraWorldPos.getX();
			            int screenY = (y * tileSize) - (int) cameraWorldPos.getY();
			            int size = tileSize;
			            g2d.drawImage(tile.getTile(), screenX, screenY, size, size, null);
		            }
		        }
		    }
		}));
		// Trees above the player's position (Background)
	    renders.add(new Render(Layer.Background, g -> {
	        Graphics2D g2d = (Graphics2D) g;
	        int tileSize = mapm.getTileSize();

	        Camera camera = game.getCamera();
	        Vector2D cameraWorldPos = camera.getWorldPosition();
	        Vector2D viewport = camera.getViewport();

	        int startX = Math.max((int) (cameraWorldPos.getX() / tileSize), 0);
	        int endX = Math.min((int) ((cameraWorldPos.getX() + viewport.getX()) / tileSize) + 1, width);
	        int startY = Math.max((int) (cameraWorldPos.getY() / tileSize)-1, 0);
	        int endY = Math.min((int) ((game.getPlayer().getPosition().getY() + (game.getPlayer().getSize()/2)) / tileSize), height); // Trees above player

	        for (int y = startY; y < endY; y++) {
	            for (int x = startX; x < endX; x++) {
	                Tile tile = mapTiles[1][x][y];
	                if (tile != null) {
	                    int screenX = (x * tileSize) - (int) cameraWorldPos.getX() - (tileSize / 2);
	                    int screenY = (y * tileSize) - (int) cameraWorldPos.getY();
	                    int size = (int) (tileSize * 1.5f);
	                    g2d.drawImage(tile.getTile(), screenX + (size/6), screenY, size, size, null);
	                }
	            }
	        }
	    }));

	    // Trees below the player's position (Foreground)
	    renders.add(new Render(Layer.Foreground, g -> {
	        Graphics2D g2d = (Graphics2D) g;
	        int tileSize = mapm.getTileSize();

	        Camera camera = game.getCamera();
	        Vector2D cameraWorldPos = camera.getWorldPosition();
	        Vector2D viewport = camera.getViewport();

	        int startX = Math.max((int) (cameraWorldPos.getX() / tileSize), 0);
	        int endX = Math.min((int) ((cameraWorldPos.getX() + viewport.getX()) / tileSize) + 1, width);
	        int startY = Math.max((int) ((game.getPlayer().getPosition().getY() + (game.getPlayer().getSize()/2)) / tileSize), 0); // Trees below player
	        int endY = Math.min((int) ((cameraWorldPos.getY() + viewport.getY()) / tileSize) + 1, height);

	        for (int y = startY; y < endY; y++) {
	            for (int x = startX; x < endX; x++) {
	                Tile tile = mapTiles[1][x][y];
	                if (tile != null) {
	                    int screenX = (x * tileSize) - (int) cameraWorldPos.getX() - (tileSize / 2);
	                    int screenY = (y * tileSize) - (int) cameraWorldPos.getY();
	                    int size = (int) (tileSize * 1.5f);
	                    g2d.drawImage(tile.getTile(), screenX + (size/6), screenY, size, size, null);
	                }
	            }
	        }
	    }));
		return renders;
	}
	
	public int[][] generateGrayscaleArray(String filePath) throws IOException {
        // Read the image
        BufferedImage image = ImageIO.read(new File(filePath));

        // Get the dimensions
        int width = image.getWidth();
        int height = image.getHeight();

        // Create the 2D array
        int[][] grayscaleArray = new int[height][width];

        // Process each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB value of the pixel
                int rgb = image.getRGB(x, y);

                // Extract color components
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                
                if (red == 0 && green == 0 && blue == 0) {
                    addCollisionSurface(x, y);
                }
                if (red == 66 && green == 245 && blue == 72) {
                	mapm.addSpawnpoint(new Vector2D(x * mapm.getTileSize() + mapm.getTileSize()/2, y * mapm.getTileSize() + mapm.getTileSize()/2));
                	red = 255;
                	green = 255;
                	blue = 255;
                }
                

                // Calculate the grayscale value
                int grayscale = (red + green + blue) / 3;

                // Scale grayscale (255 to 0 -> 0 to 100)
                int scaledValue = (int) ((1 - (grayscale / 255.0)) * 100);

                // Store in the array
                grayscaleArray[y][x] = scaledValue;
            }
        }

        return grayscaleArray;
    }
	
	private void addCollisionSurface(int x, int y) {
	    int tileSize = mapm.getTileSize();

	    // Calculate the position of the current tile in world coordinates
	    Vector2D position = new Vector2D(x * tileSize, y * tileSize);

	    // Check for existing surfaces to merge with
	    for (CollisionSurface surface : game.getCollisionSurfaces()) {
	        if (canMerge(surface, position, tileSize)) {
	            mergeSurface(surface, position, tileSize);
	            return;
	        }
	    }

	    // If no merge is possible, create a new collision surface
	    new CollisionSurface(game, position, tileSize, tileSize, Color.RED);
	}

	/**
	 * Checks if a collision surface can merge with a new tile's position.
	 */
	private boolean canMerge(CollisionSurface surface, Vector2D position, int tileSize) {
	    // Horizontal merging
	    if (surface.getPosition().getY() == position.getY() &&
	        surface.getPosition().getX() + surface.getWidth() == position.getX()) {
	        return true;
	    }

	    // Vertical merging
	    if (surface.getPosition().getX() == position.getX() &&
	        surface.getPosition().getY() + surface.getHeight() == position.getY()) {
	        return true;
	    }

	    return false;
	}

	/**
	 * Merges a collision surface with a new tile's position.
	 */
	private void mergeSurface(CollisionSurface surface, Vector2D position, int tileSize) {
	    if (surface.getPosition().getY() == position.getY()) {
	        // Horizontal merge: Extend width
	        surface.setWidth(surface.getWidth() + tileSize);
	    } else if (surface.getPosition().getX() == position.getX()) {
	        // Vertical merge: Extend height
	        surface.setHeight(surface.getHeight() + tileSize);
	    }
	}
	
	public void setSize(Vector2D size) {
		this.width = size.getXint();
		this.height = size.getYint();
	}

}
