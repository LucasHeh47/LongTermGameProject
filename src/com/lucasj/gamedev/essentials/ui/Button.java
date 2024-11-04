package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;
import com.lucasj.gamedev.game.gamemodes.waves.WavesManager;

public class Button {
    private String text;
    private int x, y, width, height;
    private Color bgColor, textColor;
    private Runnable onClick;
    private GameState gameState;
    private Tooltip tooltip;
    private Menus menu;
    private Supplier<Boolean> decidingFactor;
    private Game game;
    
    public Button(Game game, Menus menu, GameState state, String text, int x, int y, int width, int height, Color bgColor, Color textColor, Runnable onClick, Tooltip tooltip) {
        this.text = text;
        this.game = game;
        this.gameState = state;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.onClick = onClick;
        this.menu = menu;
        this.tooltip = tooltip;
        this.decidingFactor = () -> {
        	return true;
        };
    }

	public void render(Graphics2D g2d, Font font) {
    	if(!decidingFactor.get()) return;
        g2d.setColor(bgColor);
        g2d.fillRect(x, y, width, height);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(x, y, width, height);
        
        g2d.setFont(font);
        g2d.setColor(textColor);
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + g2d.getFontMetrics().getAscent()) / 2 - 5;
        g2d.drawString(text, textX, textY);
    }

    public boolean isClicked(MouseEvent e) {
    	if(game.getGameState() == this.gameState) {
	        int mouseX = e.getX();
	        int mouseY = e.getY();
	        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    	} return false;
    }

    public void click() {
        if (onClick != null) {
            onClick.run();
        }
    }

	public GameState getGameState() {
		return gameState;
	}

	public Tooltip getTooltip() {
		return tooltip;
	}

	public void setTooltip(Tooltip tooltip) {
		this.tooltip = tooltip;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}