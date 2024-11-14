package com.lucasj.gamedev.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

public class GraphicUtils {

	public void drawVignette(Graphics2D g2d, int width, int height, int r, int g, int b, int a) {

	    // Darker gray for the vignette
	    Color vignetteColor = new Color(r, g, b, a);  // RGBA, last value is transparency

	    // Create a radial gradient for the vignette effect
	    RadialGradientPaint vignette = new RadialGradientPaint(
	            new Point2D.Double(width / 2, height / 2),   // Center of the screen
	            Math.max(width, height) / 2,                 // Radius of the gradient
	            new float[] { 0f, 0.6f, 0.85f, 1f },        // Gradient stops at various points
	            new Color[] { 
	                new Color(r, g, b, 0),                 // Center is fully transparent
	                new Color(r, g, b, 50),                // Slightly dark at 60% radius
	                new Color(r, g, b, 150),               // Darker at 85% radius
	                vignetteColor                          // Darkest gray at the edges
	            }
	        );

	    // Set the paint to the vignette gradient and draw a rectangle over the entire screen
	    g2d.setPaint(vignette);
	    g2d.fillRect(0, 0, width, height);
	}
	
	public void drawVignette(Graphics2D g2d, int x, int y, int width, int height, int r, int g, int b, int a) {

	    // Darker gray for the vignette
	    Color vignetteColor = new Color(r, g, b, a);  // RGBA, last value is transparency

	    // Create a radial gradient for the vignette effect
	    RadialGradientPaint vignette = new RadialGradientPaint(
	            new Point2D.Double(width / 2, height / 2),   // Center of the screen
	            Math.max(width, height) / 2,                 // Radius of the gradient
	            new float[] { 0f, 0.6f, 0.85f, 1f },        // Gradient stops at various points
	            new Color[] { 
	                new Color(r, g, b, 0),                 // Center is fully transparent
	                new Color(r, g, b, 50),                // Slightly dark at 60% radius
	                new Color(r, g, b, 150),               // Darker at 85% radius
	                vignetteColor                          // Darkest gray at the edges
	            }
	        );

	    // Set the paint to the vignette gradient and draw a rectangle over the entire screen
	    g2d.setPaint(vignette);
	    g2d.fillRect(x, y, width, height);
	}
	
}
