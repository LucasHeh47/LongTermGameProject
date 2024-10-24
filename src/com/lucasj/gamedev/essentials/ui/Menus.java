package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;
import com.lucasj.gamedev.events.MouseClickEventListener;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.world.map.MapManager;

public class Menus implements MouseClickEventListener {
    
    private Game game;
    private List<Button> buttons;
    
    public Menus(Game game) {
    	this.game = game;
    	this.buttons = new ArrayList<>();
    	createButtons();
    }
    
    public void createButtons() {
    	buttons.clear();
    	Button playWaves = new Button(game, this, GameState.mainmenu, "Play Waves", (game.getWidth() - 225) / 2, 200, 250, 50,
            Color.LIGHT_GRAY, Color.BLACK, () -> {
                game.setGameState(GameState.wavesmenu);
                game.getMapManager().map.generateMap();
            });

        Button exit = new Button(game, this, GameState.mainmenu, "Exit", (game.getWidth() - 225) / 2, 300, 250, 50,
            Color.LIGHT_GRAY, Color.BLACK, () -> System.exit(0));
    

        Button play = new Button(game, this, GameState.wavesmenu, "Play", (game.getWidth() - 200) / 2, 200, 200, 50,
            Color.LIGHT_GRAY, Color.BLACK, () -> {
                game.setGameState(GameState.waves);
                game.getMapManager().map.generateMap();
                game.getWavesManager().startWaves();
                game.instantiatePlayer();
            });
        
        Button backToMainMenu = new Button(game, this, GameState.waves, "Back to Menu", (game.getWidth() - 225) / 2, 300, 250, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    game.getPlayer().die();
                    game.setPaused(false);
                }, () -> {
                	return game.isPaused();
                });
        
        Button resumeGame = new Button(game, this, GameState.waves, "Resume Game", (game.getWidth() - 225) / 2, 200, 250, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    game.setPaused(false);
                }, () -> {
                	return game.isPaused();
                });
            
    }
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
        Font font = game.font;
        g2d.setFont(font);

        MapManager mapm = game.getMapManager();
        mapm.render(g);

        // Render buttons
        for (Button button : buttons) {
            if(button.getGameState() == game.getGameState()) button.render(g2d, font.deriveFont(48.0f));
        }
        
        if(game.getGameState() == GameState.mainmenu) {
        	String title = "Game";
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (game.getWidth() - titleWidth) / 2, 100);
        }
        if(game.getGameState() == GameState.wavesmenu) {
        	int margin = 100;
    		int XpBarSize = 100;
    		int XpBarLength = 4; //length x size = actual length
    		
    		g2d.setFont(game.font.deriveFont(Font.PLAIN, 64f)); // Derive the font size explicitly as a float

    	    // Measure the width of the money string to center it if needed
    	    int titleWidth = g2d.getFontMetrics().stringWidth("$" + Integer.toString(Player.getGlobalStats().getLevel()));

    	    g2d.setColor(Color.black);
    	    g2d.drawString("Level: " + Integer.toString(Player.getGlobalStats().getLevel()), 100, game.getHeight() - 220);
    		
    		g2d.setColor(Color.black);
    		g2d.fillRect(margin, game.getHeight()-(margin*2), XpBarLength * XpBarSize, XpBarSize);
    		
    		g2d.setColor(Color.green);
    		g2d.fillRect((int)(margin + (margin*0.1))
    				, (int) (game.getHeight() - margin*2 + margin*0.1), 
    				(int) (((XpBarSize * XpBarLength) - (margin*0.2)) * ((double) Player.getGlobalStats().getCurrentXP()/Player.getGlobalStats().getXpToNextLevel())), 
    				(int)(XpBarSize - (XpBarSize * 0.2)));
        }
    }
	
	@Override
	public void onMouseClicked(MouseEvent e) {
		for (Button button : buttons) {
            if (button.isClicked(e)) {
                button.click();
                break;
            }
        }
		
	}
	
	public void addButton(Button button) {
		this.buttons.add(button);
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
}
