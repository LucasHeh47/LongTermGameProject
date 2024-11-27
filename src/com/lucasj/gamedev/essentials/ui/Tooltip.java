package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.weapons.Tier;
import com.lucasj.gamedev.misc.Debug;

public class Tooltip {
    private String text;
    private String subText;
    private String finalSubText;
    private int x, y;
    private int padding = 10;
    private Color bgColor;
    private Color textColor;
    private Font font;
    private Game game;
    private List<Supplier<String>> requirements;

    
    /**
     * @param game
     * @param text
     * @param x
     * @param y
     * @param bgColor
     * @param textColor
     */
    public Tooltip(Game game, String text, int x, int y, Color bgColor, Color textColor) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.subText = "";
        this.game = game;
        this.font = game.font;
        this.finalSubText = buildFinalSubText();
    }
    
    /***
     * 
     * @param game
     * @param text
     * @param subText
     * @param x
     * @param y
     * @param bgColor
     * @param textColor
     */
    public Tooltip(Game game, String text, String subText, int x, int y, Color bgColor, Color textColor) {
        this.text = text;
        this.subText = subText;
        this.x = x;
        this.y = y;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.game = game;
        this.font = game.font;
        this.finalSubText = buildFinalSubText();
    }
    
    /***
     * 
     * @param game
     * @param text
     * @param subText
     * @param x
     * @param y
     * @param bgColor
     * @param textColor
     * @param requirements
     */
    public Tooltip(Game game, String text, String subText, int x, int y, Color bgColor, Color textColor, Supplier<List<Supplier<String>>> requirements) {
        this.text = text;
        this.subText = "{LIGHT_GRAY}" + subText;
        this.x = x;
        this.y = y;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.game = game;
        this.font = game.font;
        if(requirements != null) this.requirements = requirements.get();
        this.finalSubText = buildFinalSubText();
    }
    
    private String buildFinalSubText() {
        StringBuilder sb = new StringBuilder(subText);
        if (requirements != null) {
            requirements.forEach(req -> {
            	if(req.get() != "") sb.append("{NL}").append(req.get());
            });
        }
        return sb.toString();
    }

    public void render(Graphics2D g2d) {
        if (text == null || text.isEmpty()) return;

        g2d.setFont(font);
        int textWidth = g2d.getFontMetrics().stringWidth(text.replaceAll("\\{\\w+\\}", ""));
        int textHeight = g2d.getFontMetrics().getHeight();
        Font subFont = font.deriveFont(font.getSize() * 0.8f);
        int subTextHeight = 0;
        List<String> wrappedSubTextLines = new ArrayList<>();

        // Process finalSubText for {NL} and wrapping
        if (finalSubText != null && !finalSubText.isEmpty()) {
            g2d.setFont(subFont);
            String[] lines = finalSubText.split("\\{NL\\}");
            for (String line : lines) {
                String[] words = line.split(" ");
                StringBuilder currentLine = new StringBuilder();
                for (String word : words) {
                    String testLine = currentLine + word + " ";
                    if (g2d.getFontMetrics().stringWidth(testLine) > 800) {
                        wrappedSubTextLines.add(currentLine.toString().trim());
                        currentLine = new StringBuilder(word + " ");
                    } else {
                        currentLine.append(word).append(" ");
                    }
                }
                if (currentLine.length() > 0) {
                    wrappedSubTextLines.add(currentLine.toString().trim());
                }
                wrappedSubTextLines.add(""); // Add an empty line after each {NL}
            }
            subTextHeight = wrappedSubTextLines.size() * g2d.getFontMetrics().getHeight();
            g2d.setFont(font);
        }

        // Calculate the final box dimensions
        int boxWidth = subText != "" ? Math.max(textWidth, 400) + padding * 2 : Math.min(textWidth, 400) + padding * 2;
        int boxHeight = textHeight + (subText.isEmpty() ? 0 : subTextHeight) + padding * 2;

        // Draw the background box with padding
        g2d.setColor(bgColor);
        g2d.fillRoundRect(x, y - boxHeight, boxWidth, boxHeight, 10, 10);

        // Draw the border
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(x, y - boxHeight, boxWidth, boxHeight, 10, 10);

        // Parse and draw the main text with color tags
        g2d.setColor(textColor);
        int currentX = x + padding;
        int currentY = y - boxHeight + padding + textHeight;
        drawTextWithColors(g2d, text, currentX, currentY, font);

        // Draw the subText if available
        if (!wrappedSubTextLines.isEmpty()) {
            g2d.setFont(subFont);
            currentY += 25; // Add more space between main text and subText
            for (String line : wrappedSubTextLines) {
                if (line.isEmpty()) {
                    currentY += g2d.getFontMetrics().getHeight(); // Add space for empty line
                } else {
                    drawTextWithColors(g2d, line, x + padding, currentY, subFont);
                    currentY += g2d.getFontMetrics().getHeight();
                }
            }
            g2d.setFont(font); // Reset font
        }
    }

    private void drawTextWithColors(Graphics2D g2d, String text, int x, int y, Font font) {
        g2d.setFont(font);
        String[] textParts = text.split("(?=\\{\\w+\\})|(?<=\\})");
        int currentX = x;

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

    // Method to position the tooltip
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Method to set the text
    public void setText(String text) {
        this.text = text;
    }

    // Method to set the subText
    public void setSubText(String subText) {
        this.subText = "{LIGHT_GRAY}" + subText;
    }

    // Method to check if the tooltip should be shown based on mouse position
    public boolean shouldShow(int mouseX, int mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth && mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

	public List<Supplier<String>> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<Supplier<String>> requirements) {
		this.requirements = requirements;
	}
}
