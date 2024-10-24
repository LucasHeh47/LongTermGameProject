package com.lucasj.gamedev.world.map;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lucasj.gamedev.mathutils.Vector2D;

public class Tile {
    private BufferedImage tile;

    public Tile(String fileDirectory, Vector2D location, Vector2D size) {
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

        this.tile = tile;
    }

    public Image getTile() {
        return this.tile;
    }

}
