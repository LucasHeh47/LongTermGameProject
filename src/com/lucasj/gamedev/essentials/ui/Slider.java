package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.mathutils.Vector2D;

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
    private Supplier<Boolean> decidingFactor;
    private boolean adjustedPositionWithPanel = false;
    
    private Color textColor;
    private Color textShadowColor;
    private Vector2D textShadowDistance;

    /***
     * 
     * @param game
     * @param x
     * @param y
     * @param width
     * @param height
     * @param minValue
     * @param maxValue
     * @param setting
     * @param userDefined
     */
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
    
    /***
     * 
     * @param game
     * @param x
     * @param y
     * @param width
     * @param height
     * @param minValue
     * @param maxValue
     * @param initialValue
     * @param userDefined
     */
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

            int textX = x + width / 2 - g.getFontMetrics().stringWidth(displayText) / 2;
            int textY = y - 10;
            
            if(this.textShadowColor != null) {
            	g.setColor(this.textShadowColor);
            	int shadowTextX = 0;
            	int shadowTextY = 0;
                if(this.textShadowDistance == null) {
                	shadowTextX= textX + 3;
                	shadowTextY = textY + 3;
                } else {
	                textX +=textShadowDistance.getXint();
	                textY += textShadowDistance.getYint();
                }
                g.drawString(displayText, shadowTextX, shadowTextY);
            	
            }
            // Set the font color and draw the string
            if(this.textColor != null) {
            	g.setColor(textColor);
            } else {
            	g.setColor(Color.BLACK);
            }
            
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
        if (userDefined && (sliderBounds.contains(e.getPoint()) || handleBounds.contains(e.getPoint())) && this.decidingFactor.get()) {
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
        game.getAudioPlayer().playSound("UI/button_hover.wav", null);
        game.getSettings().setIntSetting(setting, currentValue);
    }
    
    public void updatePositionWithPanel(Panel panel) {
        int newX = panel.getX() + x;
        int newY = panel.getY() + y;
        this.setPosition(newX, newY);
        this.adjustedPositionWithPanel = true;
    }
    
    public void setPosition(int x, int y) {
    	this.x = x;
    	this.y = y;
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

	public void setDecidingFactor(Supplier<Boolean> decidingFactor) {
		this.decidingFactor = decidingFactor;
	}
	
	public Slider setTextColor(Color col) {
		this.textColor = col;
		return this;
	}

	public Slider setTextShadowColor(Color col) {
		this.textShadowColor = col;
		return this;
	}
	
	public Slider setTextShadowDistance(Vector2D dist) {
		this.textShadowDistance = dist;
		return this;
	}

}