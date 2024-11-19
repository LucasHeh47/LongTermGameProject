package com.lucasj.gamedev.essentials.ui.broadcast;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.GameColors;
import com.lucasj.gamedev.essentials.ui.Panel;
import com.lucasj.gamedev.essentials.ui.GameColors.colors;
import com.lucasj.gamedev.game.weapons.Tier;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Broadcast {
	
	private Game game;
	private String title;
	private String subText;
	private Vector2D position;
	private float setTime;
	private float time;
	public boolean finished = false;
	
	
	public Broadcast(Game game, String title, String subText, Vector2D position, float time) {
		this.game = game;
		this.title = title;
		this.subText = subText;
		this.position = position;
		this.time = time;
		this.setTime = time;
	}
	
	public void update(double deltaTime) {
		time -= deltaTime;
		if( time <= 0 ) finished = true;
	}
	
	public void render(Graphics2D g2d) {
        // Render the background panel
        Panel panel = new Panel((int) position.getX(), (int) position.getY(), 500, 250, Color.DARK_GRAY, Color.BLACK, 20, 10);
        panel.render(g2d);
        
        // Render the timer bar
        g2d.setColor(Color.YELLOW);
        g2d.fill(new RoundRectangle2D.Float((int) position.getX(), (int) position.getY(), (int) (495 * (time / setTime)), 5, 10, 10));
        g2d.setColor(Color.WHITE);
        
        // Set font and color for the title
        g2d.setFont(game.font.deriveFont(1f));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int panelCenterX = (int) position.getX() + panel.getWidth() / 2;
        int titleY = (int) position.getY() + 50; // Adjust as needed for spacing
        
        // Draw the title with colors
        drawTextWithColors(g2d, title, panelCenterX, titleY, game.font.deriveFont(48f), true);
        
        // Set font and process subText for {NL} and word wrapping
        g2d.setFont(game.font.deriveFont(32f));
        FontMetrics subTextMetrics = g2d.getFontMetrics();
        int subTextY = titleY + 60; // Adjust spacing below the title
        int panelPadding = 20; // Padding on the sides of the panel
        int availableWidth = panel.getWidth() - 2 * panelPadding;
        
        // Split subText into lines based on {NL}
        String[] lines = subText.split("\\{NL\\}");
        for (String line : lines) {
            String[] words = line.split(" ");
            StringBuilder currentLine = new StringBuilder();
            
            for (String word : words) {
                String testLine = currentLine + word + " ";
                if (subTextMetrics.stringWidth(testLine.replaceAll("\\{\\w+\\}", "")) > availableWidth) {
                    // Draw the current line
                    drawTextWithColors(g2d, currentLine.toString(), panelCenterX, subTextY, game.font.deriveFont(32f), true);
                    
                    // Start a new line
                    currentLine = new StringBuilder(word + " ");
                    subTextY += subTextMetrics.getHeight();
                } else {
                    currentLine.append(word).append(" ");
                }
            }
            
            // Draw the last part of the current line
            if (currentLine.length() > 0) {
                drawTextWithColors(g2d, currentLine.toString(), panelCenterX, subTextY, game.font.deriveFont(32f), true);
                subTextY += subTextMetrics.getHeight();
            }
        }
    }
    
    private void drawTextWithColors(Graphics2D g2d, String text, int x, int y, Font font, boolean centered) {
        g2d.setFont(font);
        String[] textParts = text.split("(?=\\{\\w+\\})|(?<=\\})");
        Map<String, Color> colorMap = createColorMap();
        int currentX = x;
        
        if (centered) {
            // Measure total width without tags for centering
            int totalWidth = 0;
            for (String part : textParts) {
                if (!part.matches("\\{\\w+\\}")) {
                    totalWidth += g2d.getFontMetrics().stringWidth(part);
                }
            }
            currentX -= totalWidth / 2; // Adjust starting x for centering
        }
        
        for (String part : textParts) {
            if (part.matches("\\{\\w+\\}")) {
                String colorName = part.replaceAll("[{}]", "").toUpperCase();
                g2d.setColor(colorMap.getOrDefault(colorName, Color.WHITE));
            } else {
                g2d.drawString(part, currentX, y);
                currentX += g2d.getFontMetrics().stringWidth(part);
            }
        }
    }

	/**
	 * Creates a map of color tags to Color values.
	 *
	 * @return the map of color tags to Color values
	 */
	private Map<String, Color> createColorMap() {
	    Map<String, Color> colorMap = new HashMap<>();
	    colorMap.put("RED", Color.RED);
	    colorMap.put("BLUE", Color.BLUE);
	    colorMap.put("GREEN", Color.GREEN);
	    colorMap.put("YELLOW", Color.YELLOW);
	    colorMap.put("WHITE", Color.WHITE);
	    colorMap.put("BLACK", Color.BLACK);
	    colorMap.put("GOLD", colors.GOLD.getValue());
	    colorMap.put("LIGHT_GRAY", Color.LIGHT_GRAY);
	    colorMap.put("COMMON", GameColors.GUN_COLORS(Tier.Common));
	    colorMap.put("UNCOMMON", GameColors.GUN_COLORS(Tier.Uncommon));
	    colorMap.put("RARE", GameColors.GUN_COLORS(Tier.Rare));
	    colorMap.put("EPIC", GameColors.GUN_COLORS(Tier.Epic));
	    colorMap.put("LEGENDARY", GameColors.GUN_COLORS(Tier.Legendary));
	    colorMap.put("MYTHIC", GameColors.GUN_COLORS(Tier.Mythic));
	    colorMap.put("DIVINE", GameColors.GUN_COLORS(Tier.Divine));
	    colorMap.put("ETHEREAL", GameColors.GUN_COLORS(Tier.Ethereal));
	    // Add more colors as needed
	    return colorMap;
	}

	public void setSubText(String subText) {
		this.subText = subText;
	}

}
