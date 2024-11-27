package com.lucasj.gamedev.Assets;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	
	public static BufferedImage tintImage(BufferedImage source, Color tint) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage tintedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the original pixel color
                int pixel = source.getRGB(x, y);

                // Extract alpha channel
                int alpha = (pixel >> 24) & 0xFF;

                // Ignore fully transparent pixels
                if (alpha == 0) continue;

                // Apply the tint
                int red = (tint.getRed() * alpha) / 255;
                int green = (tint.getGreen() * alpha) / 255;
                int blue = (tint.getBlue() * alpha) / 255;

                // Recombine with the alpha channel
                int tintedPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;

                // Set the tinted pixel
                tintedImage.setRGB(x, y, tintedPixel);
            }
        }

        return tintedImage;
    }
	
	public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img; // If it's already a BufferedImage, return it directly
        }

        // Create a BufferedImage with the same dimensions and type
        BufferedImage bufferedImage = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB // Use ARGB for transparency support
        );

        // Draw the Image into the BufferedImage
        bufferedImage.getGraphics().drawImage(img, 0, 0, null);
        return bufferedImage;
    }
	
	/**
     * Generates a list of colors forming a gradient between two colors.
     * 
     * @param startColor The starting color of the gradient.
     * @param endColor   The ending color of the gradient.
     * @param steps      The number of steps (colors) in the gradient.
     * @return A list of colors forming the gradient.
     */
    public static List<Color> createColorGradient(Color startColor, Color endColor, int steps) {
        List<Color> gradient = new ArrayList<>();

        if (steps <= 1) {
            gradient.add(startColor);
            return gradient;
        }

        for (int i = 0; i < steps; i++) {
            float ratio = (float) i / (steps - 1); // Calculate the blend ratio
            int red = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
            int green = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
            int blue = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);
            int alpha = (int) (startColor.getAlpha() * (1 - ratio) + endColor.getAlpha() * ratio);

            gradient.add(new Color(red, green, blue, alpha));
        }

        return gradient;
    }
	
}
