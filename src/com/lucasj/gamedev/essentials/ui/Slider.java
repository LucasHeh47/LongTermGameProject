package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;

public class Slider extends UIComponent implements MouseClickEventListener, MouseMotionEventListener {
    private int x, y, width, height;
    private int minValue, maxValue;
    private int currentValue;
    private boolean isDragging;
    private boolean userDefined; // True if the slider can be controlled by the user
    
    private Game game;
    
    public Slider(Game game, int x, int y, int width, int height, int minValue, int maxValue, int initialValue, boolean userDefined) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = initialValue;
        this.isDragging = false;
        this.userDefined = userDefined;
        
        game.getInput().addMouseMotionListener(this);
        game.getInput().addMouseClickListener(this);
        
    }

    // Render the slider
    public void render(Graphics2D g) {
        // Draw the slider track
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);

        // Calculate the position of the slider handle
        int handleX = x + (int) ((currentValue - minValue) / (double) (maxValue - minValue) * (width - 10));

        // Draw the slider handle
        g.setColor(Color.DARK_GRAY);
        g.fillRect(handleX, y, 10, height);
    }

    // Method to get the current value of the slider
    public int getValue() {
        return currentValue;
    }

    // Method to set the current value programmatically (e.g., for health bars)
    public void setValue(int value) {
        if (!userDefined) {
            currentValue = Math.max(minValue, Math.min(maxValue, value));
        }
    }

    // Handle mouse click events
    @Override
    public void onMousePressed(MouseEvent e) {
        if (userDefined && new Rectangle(x, y, width, height).contains(e.getPoint())) {
            isDragging = true;
            updateValue(e.getX());
        }
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        if (userDefined) {
            isDragging = false;
        }
    }

    @Override
    public void onMouseDragged(MouseEvent e) {
        if (isDragging && userDefined) {
            updateValue(e.getX());
        }
    }

    // Update the current value based on the mouse position
    private void updateValue(int mouseX) {
        int relativeX = Math.max(x, Math.min(x + width, mouseX)) - x;
        currentValue = minValue + (int) ((relativeX / (double) width) * (maxValue - minValue));
    }

	@Override
	public void onMouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}