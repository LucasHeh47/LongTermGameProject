package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;
import com.lucasj.gamedev.world.map.MapManager;

public class Menus implements MouseClickEventListener, MouseMotionEventListener {
    
    private Game game;
    private List<GUI> GUIs;
    
    private Tooltip activeTooltip = null;
    
    private Vector2D mousePos;
    
    public Menus(Game game) {
    	mousePos = new Vector2D(0, 0);
    	this.game = game;
    	this.GUIs = new ArrayList<>();
    	createGUIs();
    }
    
    public void createGUIs() {
    	GUIs.clear();
    	
    	GUI mainMenu = new GUI(game, this, () -> {
    		return game.getGameState() == GameState.mainmenu;
    	}, () -> {
    		List<Button> buttons = new ArrayList<>();
    		buttons.add(new Button(game, this, GameState.mainmenu, "Play Waves", (game.getWidth() - 225) / 2, 200, 250, 50,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                game.setGameState(GameState.wavesmenu);
    	                game.getMapManager().map.generateMap();
    	            }, null));

    	    buttons.add(new Button(game, this, GameState.mainmenu, "Exit", (game.getWidth() - 225) / 2, 300, 250, 50,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> System.exit(0), null));
    	    return buttons;
    	});
    	
    
    	GUI wavesMenu = new GUI(game, this, () -> {
    		return game.getGameState() == GameState.wavesmenu;
    	}, () -> {
    		List<Button> buttons = new ArrayList<>();
    		
    		buttons.add(new Button(game, this, GameState.wavesmenu, "Play", (game.getWidth() - 200) / 2, 200, 200, 50,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                game.setGameState(GameState.waves);
    	                game.getMapManager().map.generateMap();
    	                game.getWavesManager().startWaves();
    	                game.instantiatePlayer();
    	            }, null));
    	        
    	    buttons.add(new Button(game, this, GameState.wavesmenu, "Back", (game.getWidth() - 200) / 2, 255, 200, 50,
    	                Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                    game.setGameState(GameState.mainmenu);
    	                    game.getMapManager().map.generateMap();
    	                }, null));
    	    return buttons;
    	});
        
        GUI pausedGame = new GUI(game, this, () -> {
        	return game.getGameState() == GameState.waves && game.isPaused();
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	
        	buttons.add(new Button(game, this, GameState.waves, "Back to Menu", (game.getWidth() - 225) / 2, 300, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        game.getPlayer().die();
                        game.setPaused(false);
                    }, null));
            
            buttons.add(new Button(game, this, GameState.waves, "Resume Game", (game.getWidth() - 225) / 2, 200, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        game.setPaused(false);
                    }, null));
            return buttons;
        });
        
            
        
        // NPCS ------------
        
        GUI playerUpgradeMenu = new GUI(game, this, () ->{
        	return game.getGameState() == GameState.waves && 
        			game.getWavesManager() != null && 
        			game.getWavesManager().getNPCManager() != null && 
        			game.getWavesManager().getNPCManager().getPlayerUpgradeNPC() != null &&
        			game.getWavesManager().getNPCManager().getPlayerUpgradeNPC().isOpen();
        			
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	
        	Button regen = (new Button(game, this, GameState.waves, "Unlock Health Regen", ((int) ((game.getWidth() - 225) / 1.2)), 600, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	Debug.log("DEBUG", "Unlocking Health Regen");
                        boolean success = game.getPlayer().getPlayerUpgrades().unlockHealthRegen();
                        this.activeTooltip = null;
                    }, 
                    new Tooltip(game, "5 Gems", "Unlocking this will give you health regen",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= 5;
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	return list;
                    })));
        	
        	if(!game.getPlayer().getPlayerUpgrades().hasHealthRegen()) buttons.add(regen);
        	
        	buttons.add(new Button(game, this, GameState.waves, "Upgrade Health", ((int) ((game.getWidth() - 225) / 1.2)), 300, 250, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    boolean success = game.getPlayer().getPlayerUpgrades().upgrade("health");
                }, 
                new Tooltip(game, "1 Gem", "Upgrading will increase your health regeneration.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                	List<Supplier<String>> list = new ArrayList<>();
                	list.add(() -> {
                		boolean hasRegen = game.getPlayer().getPlayerUpgrades().hasHealthRegen();
                		return hasRegen ? "" : "{RED}(!) Health Regen Not Unlocked";
                	});
                	list.add(() -> {
                		boolean hasGems = game.getPlayer().getGems() >= 1;
                		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                	});
                	return list;
                })));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Upgrade Max Health", ((int) ((game.getWidth() - 225) / 1.2)), 375, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        boolean success = game.getPlayer().getPlayerUpgrades().upgrade("maxHealth");
                    }, 
                    new Tooltip(game, "1 Gem", "Purchasing will extend your max health.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= 1;
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Upgrade Damage", ((int) ((game.getWidth() - 225) / 1.2)), 450, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        boolean success = game.getPlayer().getPlayerUpgrades().upgrade("damage");
                    }, 
                    new Tooltip(game, "1 Gem", "Purchasing will upgrade your damage multiplier.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= 1;
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Upgrade Movement", ((int) ((game.getWidth() - 225) / 1.2)), 525, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        boolean success = game.getPlayer().getPlayerUpgrades().upgrade("movement");
                        System.out.println("Got movement");
                    }, 
                    new Tooltip(game, "1 Gem", "Purchasing will upgrade your movement speed.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= 1;
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "X", ((int) ((game.getWidth() - 225) / 1.2)), 200, 50, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    game.getWavesManager().getNPCManager().getPlayerUpgradeNPC().close();
                }, null));
        	
        	return buttons;
        });
    }
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
        Font font = game.font;
        g2d.setFont(font);

        MapManager mapm = game.getMapManager();
        mapm.render(g);

        // Render buttons
        for (GUI gui : GUIs) {
            gui.render(g2d);
        }
        
     // Render tooltip if active
        if (activeTooltip != null) {
            activeTooltip.render(g2d);
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
	    // Iterate through all GUIs
	    for (GUI gui : GUIs) {
	        // Check if the GUI is currently active
	        if (gui.getDecider().get()) {
	            // Iterate through the buttons of the active GUI
	            for (Button button : gui.getButtons().get()) {
	                // Check if the button was clicked
	                if (button.isClicked(e)) {
	                    button.click(); // Trigger the button's action
	                    return; // Exit after clicking to avoid multiple activations
	                }
	            }
	        }
	    }
	}
	
	public void addGUI(GUI gui) {
		this.GUIs.add(gui);
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
	    int mouseX = e.getX();
	    int mouseY = e.getY();

	    // Iterate through all GUIs
	    for (GUI gui : GUIs) {
	        // Check if the GUI is currently active
	        if (gui.getDecider().get()) {
	            // Iterate through the buttons of the active GUI
	            for (Button button : gui.getButtons().get()) {
	                // Check if the button is active and if the mouse is hovering over it
	                if (button.getTooltip() != null && button.getTooltip().shouldShow(mouseX, mouseY, button.getX(), button.getY(), button.getWidth(), button.getHeight())) {
	                    // Position the tooltip near the mouse cursor
	                    button.getTooltip().setPosition(mouseX + 15, mouseY + 15);
	                    // Store the button to render its tooltip later
	                    activeTooltip = button.getTooltip();
	                    return; // Exit after finding one tooltip to display
	                }
	            }
	        }
	    }
	    activeTooltip = null; // Reset if no button is hovered over
	}

	@Override
	public void onMouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
}
