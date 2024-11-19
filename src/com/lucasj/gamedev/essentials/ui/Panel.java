package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

public class Panel {
    private int x, y, width, height;
    private Color bgColor, borderColor;
    private int borderRadius;
    private int padding;

    public Panel(int x, int y, int width, int height, Color bgColor, Color borderColor, int borderRadius, int padding) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bgColor = bgColor;
        this.borderColor = borderColor;
        this.borderRadius = borderRadius;
        this.padding = padding;
    }

    // Render method for the panel
    public void render(Graphics2D g2d) {
        // Draw the background with rounded corners if borderRadius > 0
        g2d.setColor(bgColor);
        if (borderRadius > 0) {
            g2d.fill(new RoundRectangle2D.Float(x, y, width, height, borderRadius, borderRadius));
        } else {
            g2d.fillRect(x, y, width, height);
        }

        // Draw the border
        if (borderColor != null) {
            g2d.setColor(borderColor);
            if (borderRadius > 0) {
                g2d.draw(new RoundRectangle2D.Float(x, y, width, height, borderRadius, borderRadius));
            } else {
                g2d.drawRect(x, y, width, height);
            }
        }
    }

    // Setters for customization
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }
    
    public int getWidth() {
    	return width;
    }

    // Getters for padding to use in positioning buttons within the panel
    public int getPadding() {
        return padding;
    }

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}
}