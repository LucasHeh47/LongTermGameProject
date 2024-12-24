package com.lucasj.gamedev.essentials.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.misc.Debug;

public class Button extends UIComponent implements MouseClickEventListener {
    private String text;
    private int x, y, width, height;
    private Color bgColor, textColor;
    private Runnable onClick;
    private GameState gameState;
    private Tooltip tooltip;
    private Menus menu;
    private Supplier<Boolean> decidingFactor;
    private Game game;
    public boolean hovering = false;
    private int borderRadius; // New property for border radius
    private Color borderColor;
    
    public boolean adjustedPositionWithPanel = false;
    
    /***
     * 
     * @param game
     * @param menu
     * @param state
     * @param text
     * @param x
     * @param y
     * @param width
     * @param height
     * @param bgColor
     * @param textColor
     * @param onClick
     * @param tooltip
     */
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
        borderColor = Color.DARK_GRAY;
        this.decidingFactor = () -> {
        	return true;
        };
    }

	public Button setDecidingFactor(Supplier<Boolean> factor) {
    	this.decidingFactor = factor;
    	return this;
    }

	public void render(Graphics2D g2d) {
    	if(!decidingFactor.get()) return;
        g2d.setColor(bgColor);
     // Create a rounded rectangle shape if borderRadius > 0, otherwise a regular rectangle
        if (borderRadius > 0) {
            g2d.fill(new RoundRectangle2D.Float(x, y, width, height, borderRadius, borderRadius));
        } else {
            g2d.fillRect(x, y, width, height);
        }
        hovering = menu.getLastHoveredButton() == this.text;
        if(hovering) g2d.setStroke(new BasicStroke(6));
        // Draw border
        g2d.setColor(borderColor);
        if (borderRadius > 0) {
            g2d.draw(new RoundRectangle2D.Float(x, y, width, height, borderRadius, borderRadius));
        } else {
            g2d.drawRect(x, y, width, height);
        }
        g2d.setStroke(new BasicStroke(3));
        
        g2d.setFont(game.font);
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + g2d.getFontMetrics().getAscent()) / 2 - 5;
        
        g2d.setColor(Color.black);
        g2d.drawString(text, textX, textY+3);
        g2d.setColor(textColor);
        g2d.drawString(text, textX, textY);
        
    }

    public boolean isClicked(MouseEvent e) {
    	if((this.getGameState() == null || game.getGameState() == this.gameState) && this.decidingFactor.get()) {
	        int mouseX = e.getX();
	        int mouseY = e.getY();
	        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    	} return false;
    }

    public void click() {
        if (onClick != null) {
        	menu.setActiveTooltip(tooltip);
            onClick.run();
        }
    }
    
    public void updatePositionWithPanel(Panel panel) {
        int newX = panel.getX() + this.getX();
        int newY = panel.getY() + this.getY();
        this.setPosition(newX, newY);
        this.adjustedPositionWithPanel = true;
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
	
	public String getText() {
		return this.text;
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

	public int getBorderRadius() {
		return borderRadius;
	}

	public Button setBorderRadius(int borderRadius) {
		this.borderRadius = borderRadius;
		return this;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
//		if(gui == null) {
//			if(this.decidingFactor.get()) {
//				
//			}
//		} else if(gui.getDecider().get()) {
//			if(this.decidingFactor.get()) {
//				if(this.isClicked(e)) {
//					this.click();
//				}
//			}
//		}
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public Button setBorderColor(Color color) {
		this.borderColor = color;
		return this;
	}
}