package com.lucasj.gamedev.essentials.ui.broadcast;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.GameColors;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Panel;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.essentials.ui.TypeWriter;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class Broadcast {
	
	private Game game;
	private String title;
	private String subText;
	private String setSubText;
	private Vector2D position;
	private Vector2D size;
	private float setTime;
	private float time;
	public boolean finished = false;
	
	private Color borderColor;
	private Color backgroundColor;
	private Color textColor;
	private Color timerColor;
	
	public boolean hasTypeWriter;
	private TypeWriter typeWriter;
	
	/***
	 * 
	 * @param game
	 * @param title
	 * @param subText
	 * @param position
	 * @param time
	 */
	public Broadcast(Game game, String title, String subText, Vector2D position, Vector2D size, float time) {
		this.game = game;
		this.title = title;
		this.subText = subText;
		this.setSubText = subText;
		this.position = position;
		this.size = size;
		this.time = time;
		this.setTime = time;
		this.hasTypeWriter = true;
		
		borderColor = Color.black;
		backgroundColor = Color.DARK_GRAY;
		this.textColor = Color.white;
		this.timerColor = Color.yellow;
		
		if(this.hasTypeWriter) {
			typeWriter = new TypeWriter(game, subText, 0.05f);
			subText = "";
		}
		
	}
	
	public void update(double deltaTime) {
		time -= deltaTime;
		typeWriter.update(deltaTime);
		if( time <= 0 ) finished = true;
	}
	
	public Render render() {
		Render render = new Render(Layer.UI, g -> {
			Graphics2D g2d = (Graphics2D) g;
			if(typeWriter != null) {
				typeWriter.setStringToType(setSubText);
				subText = typeWriter.getCurrentString();
			}
	        // Render the background panel
	        Panel panel = new Panel((int) position.getX(), (int) position.getY(), size.getXint(), size.getYint(), backgroundColor, borderColor, 20, 10);
	        panel.render(g2d);
	        
	        g2d.setColor(timerColor);
	        g2d.fill(new RoundRectangle2D.Float((int) position.getX(), (int) position.getY(), (int) (size.getXint() * (time / setTime)), 5, 10, 10));
	        g2d.setColor(this.textColor);
	        
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
		});
		return render;
    }
    
    private void drawTextWithColors(Graphics2D g2d, String text, int x, int y, Font font, boolean centered) {
        g2d.setFont(font);
        String[] textParts = text.split("(?=\\{\\w+\\})|(?<=\\})");
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
                g2d.setColor(GameColors.colors.getColor(colorName));
            } else {
                g2d.drawString(part, currentX, y);
                currentX += g2d.getFontMetrics().stringWidth(part);
            }
        }
    }

	public void setSubText(String subText) {
		this.setSubText = subText;
	}
	
	public void setPosition(Vector2D pos) {
		this.position = pos;
	}
	
	public Vector2D getPosition() {
		return this.position;
	}

	public Broadcast setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	public Broadcast setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Broadcast setTextColor(Color textColor) {
		this.textColor = textColor;
		return this;
	}

	public Broadcast setTimerColor(Color timerColor) {
		this.timerColor = timerColor;
		return this;
	}
	
	public Vector2D getSize() {
		return this.size;
	}

}
