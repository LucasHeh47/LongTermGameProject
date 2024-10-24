package com.lucasj.gamedev.Assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lucasj.gamedev.mathutils.Vector2D;

public class SpriteTools {

	public static final String assetPackDirectory = System.getProperty("user.dir") + "/src/com/lucasj/gamedev/Assets/Ninja Adventure - Asset Pack/";
	public static final String assetDirectory = System.getProperty("user.dir") + "/src/com/lucasj/gamedev/Assets/";
	
	public static final BufferedImage getSprite(String fileDirectory, Vector2D location, Vector2D size) {
        BufferedImage spritesheet = null;
        BufferedImage tile = null;

        try {
            // Load the spritesheet
            spritesheet = ImageIO.read(new File(fileDirectory));
            
            // Extract the tile from the spritesheet
            tile = spritesheet.getSubimage(
                (int) location.getX(),   // X coordinate of the top-left corner
                (int) location.getY(),   // Y coordinate of the top-left corner
                (int) size.getX(),       // Width of the tile
                (int) size.getY()        // Height of the tile
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tile;
    }
	
}
