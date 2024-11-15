package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.misc.Debug;

public class Slider extends UIComponent implements MouseClickEventListener, MouseMotionEventListener {
    private int x, y, width, height;
    private int minValue, maxValue;
    private int currentValue;
    private boolean isDragging;
    private boolean userDefined; // True if the slider can be controlled by the user
    private String setting;
    private Game game;
    private Tooltip tooltip;
    private int mouseX, mouseY;
    

    public Slider(Game game, int x, int y, int width, int height, int minValue, int maxValue, String setting, boolean userDefined) {
    	this.game = game;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setting = setting;
        this.currentValue = game.getSettings().getIntSetting(setting);
        this.isDragging = false;
        this.userDefined = userDefined;
        game.getInput().addMouseMotionListener(this);
        game.getInput().addMouseClickListener(this);
        tooltip = new Tooltip(game, Integer.toString(currentValue), mouseX, mouseY, Color.DARK_GRAY, Color.white);
        
    }
    
    public Slider(Game game, int x, int y, int width, int height, int minValue, int maxValue, int initialValue, boolean userDefined) {
    	this.game = game;
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
    	if (setting != null) {
            // Replace underscores with spaces and capitalize each word
            String displayText = capitalizeWords(setting.replace("_", " "));
            
            // Set the font color and draw the string
            g.setColor(Color.BLACK);
            int textX = x + width / 2 - g.getFontMetrics().stringWidth(displayText) / 2; // Center the text
            int textY = y - 10; // Position above the slider
            g.drawString(displayText, textX, textY);
        }
    	
        // Draw the slider track
    	g.setColor(isDragging ? Color.GRAY : Color.LIGHT_GRAY);
        g.fillRect(x, y, width, height);
        // Draw the track border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // Calculate the position of the slider handle
        int handleX = x + (int) ((currentValue - minValue) / (double) (maxValue - minValue) * (width - 10));

        // Draw the slider handle
        g.setColor(isDragging ? Color.WHITE : Color.DARK_GRAY);
        if(isDragging) {
        	tooltip.setPosition(mouseX, mouseY);
        	tooltip.setText(Integer.toString(currentValue));
        	tooltip.render(g);
        }
        g.fillRect(handleX, y, 10, height);
        g.setColor(Color.BLACK);
        g.drawRect(handleX, y, 10, height);
    }

    private String capitalizeWords(String input) {
        String[] words = input.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                           .append(word.substring(1).toLowerCase())
                           .append(" ");
            }
        }

        return capitalized.toString().trim();
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
        Rectangle sliderBounds = new Rectangle(x, y, width, height);
        int handleX = x + (int) ((currentValue - minValue) / (double) (maxValue - minValue) * (width - 10));
        Rectangle handleBounds = new Rectangle(handleX, y, 10, height);
        if (userDefined && (sliderBounds.contains(e.getPoint()) || handleBounds.contains(e.getPoint()))) {
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
    		mouseX = e.getX();
    		mouseY = e.getY();
        }
    }

    // Update the current value based on the mouse position
    private void updateValue(int mouseX) {
        int relativeX = Math.max(0, Math.min(mouseX - x, width)); // Clamp relative position
        currentValue = minValue + (int) ((relativeX / (double) width) * (maxValue - minValue));
        Debug.log(this, "new value: " + currentValue);
        game.getSettings().setIntSetting(setting, currentValue);
    }

	@Override
	public void onMouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseX = e.getX();
		mouseY = e.getY();
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