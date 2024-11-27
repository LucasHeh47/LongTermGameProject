package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;

public class Label extends UIComponent {
	private String text;
    private int x, y, size, wordWrap;
    private Color textColor;
    private GameState gameState;
    private Menus menu;
    private Supplier<Boolean> decidingFactor;
    private boolean centered;
    private Game game;
    
    public boolean adjustedPositionWithPanel = false;
    
    /***
     * 
     * @param game
     * @param menu
     * @param state
     * @param text
     * @param wordWrap
     * @param centered
     * @param x
     * @param y
     * @param size
     * @param textColor
     */
    public Label(Game game, Menus menu, GameState state, String text, int wordWrap, boolean centered, int x, int y, int size, Color textColor) {
        this.text = text;
        this.game = game;
        this.gameState = state;
        this.x = x;
        this.y = y;
        this.size = size;
        this.centered = centered;
        this.wordWrap = wordWrap;
        this.textColor = textColor;
        this.menu = menu;
        this.decidingFactor = () -> {
        	return true;
        };
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!decidingFactor.get()) return;

        // Set the font and color
        g.setFont(game.font.deriveFont((float) size));
        g.setColor(textColor);

        // Split the text into words
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineCount = 0;
        int currentY = y;

        // Measure font metrics
        FontMetrics metrics = g.getFontMetrics();
        int panelWidth = game.getWidth(); // Assume panel width is game's width
        int lineHeight = metrics.getHeight();

        for (int i = 0; i < words.length; i++) {
            // Add a word to the current line
            if (line.length() > 0) line.append(" ");
            line.append(words[i]);

            // Check if wordWrap limit is reached or it's the last word
            if (line.length() >= wordWrap || i == words.length - 1) {
                String lineText = line.toString();

                // Calculate x for centering if needed
                int textWidth = metrics.stringWidth(lineText);
                int drawX = centered ? x - textWidth / 2 : x;

                // Draw the current line
                g.drawString(lineText, drawX, currentY);

                // Prepare for the next line
                line = new StringBuilder();
                currentY += lineHeight;
                lineCount++;
            }
        }
    }

}
