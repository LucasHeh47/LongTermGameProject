package com.lucasj.gamedev.essentials.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.swing.SwingUtilities;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.entities.player.PlayerPlaceableManager;
import com.lucasj.gamedev.game.entities.player.PlayerWavesStats;
import com.lucasj.gamedev.game.entities.player.multiplayer.PlayerMP;
import com.lucasj.gamedev.game.weapons.AmmoMod;
import com.lucasj.gamedev.game.weapons.Tier;
import com.lucasj.gamedev.game.weapons.guns.AssaultRifle;
import com.lucasj.gamedev.game.weapons.guns.SMG;
import com.lucasj.gamedev.game.weapons.guns.Shotgun;
import com.lucasj.gamedev.game.weapons.guns.Sniper;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;
import com.lucasj.gamedev.utils.ConcurrentList;

public class Menus implements MouseClickEventListener, MouseMotionEventListener {
    
    private Game game;
    private ConcurrentList<GUI> GUIs;
    
    private Tooltip activeTooltip = null;
    
    private Vector2D mousePos;
    
    private LevelUpShopCategories lvlUpShop = LevelUpShopCategories.None;
    
    private Point previousMousePosition = new Point();
    
    private boolean hoverSoundPlayed = false;
    private String lastHoveredButton = null;
    
    public Menus(Game game) {
    	mousePos = new Vector2D(0, 0);
    	this.game = game;
    	this.GUIs = new ConcurrentList<>();
    	createGUIs();
    }
    
    public void update() {
    	GUIs.update();
    }
    
    private GUI playerUpgradeMenu;
    
    public void createGUIs() {
    	GUIs.clear();
    	
    	GUI mainMenu = new GUI(game, this, () -> {
    		return game.getGameState() == GameState.mainmenu && game.settingsOpen == false;
    	}, () -> {
    		List<Button> buttons = new ArrayList<>();
    		buttons.add(new Button(game, this, GameState.mainmenu, "Play Waves", (game.getWidth() - game.getWidth()/3) / 2 - 200, game.getHeight()/2 - 125, 400, 50,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                game.setGameState(GameState.wavesmenu);
    	                try {
    	        			game.getMapManager().map.generateMap(SpriteTools.assetDirectory + "Art/Maps/" + game.getMapManager().selectedMap + ".png");
    	        		} catch (IOException e) {
    	        			e.printStackTrace();
    	        		}
    	                game.getWavesManager().setHasGameStarted(false);
    	            }, null).setBorderRadius(20));
    		
    	    buttons.add(new Button(game, this, GameState.mainmenu, "Settings", (game.getWidth() - game.getWidth()/3) / 2 - 200, game.getHeight()/2 - 25, 400, 50,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> game.settingsOpen = true, null).setBorderRadius(20));

    	    buttons.add(new Button(game, this, GameState.mainmenu, "Exit", (game.getWidth() - game.getWidth()/3) / 2 - 200, game.getHeight()/2 + 75, 400, 50,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> System.exit(0), null).setBorderRadius(20));
    	    return buttons;
    	}, null, null).setPanel(new Panel(game.getWidth()/6, 50, (int) (game.getWidth() - game.getWidth()/3), 380, 
        		Color.GRAY.darker(), Color.black, 20, 20));
    	
    
    	GUI wavesMenu = new GUI(game, this, () -> {
    		return game.getGameState() == GameState.wavesmenu;
    	}, () -> {
    		List<Button> buttons = new ArrayList<>();
    		
    		buttons.add(new Button(game, this, GameState.wavesmenu, "Play", (game.getWidth() - 450), (game.getHeight() - 320), 400, 150,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> {
    	            	if(game.party != null && game.party.getHost().getUsername().equals(game.username)) game.getSocketClient().getPacketManager().partyGoingIntoGamePacket();
    	                game.setGameState(GameState.waves);
    	                try {
    	        			game.getMapManager().map.generateMap(SpriteTools.assetDirectory + "Art/Maps/" + game.getMapManager().selectedMap + ".png");
    	        		} catch (IOException e) {
    	        			e.printStackTrace();
    	        		}
    	                //game.instantiatePlayer();
    	                this.createGUIs();
    	            }, null).setBorderRadius(20));
    		
    		buttons.add(new Button(game, this, GameState.wavesmenu, 
    	    		"Level: " + Integer.toString(Player.getGlobalStats().getLevel()), 
    	    		100, game.getHeight() - 350, 200, 100, new Color(255, 252, 82), Color.black, () -> {
    	    			game.setGameState(GameState.levelmenu);
    	    		}, new Tooltip(game, "Level " + Integer.toString(Player.getGlobalStats().getLevel()), 
    	    				"{LIGHT_GRAY}XP Needed until Level {YELLOW}" + Integer.toString(Player.getGlobalStats().getLevel()+1) + "{LIGHT_GRAY}: {WHITE}" + NumberFormat.getInstance(Locale.US).format((Player.getGlobalStats().getXpToNextLevel() - Player.getGlobalStats().getCurrentXP()))
    	    				 + "{NL}{LIGHT_GRAY}Total XP: {WHITE}" + NumberFormat.getInstance(Locale.US).format(Player.getGlobalStats().getTotalXP()),
    	    				this.mousePos.getXint(),
    	    				this.mousePos.getYint(),
    	    				Color.DARK_GRAY,
    	    				Color.white)).setBorderRadius(40).setBorderColor(new Color(192, 190, 78).darker()));
    		
    		buttons.add(new Button(game, this, GameState.wavesmenu, "Create Party", (game.getWidth() - 450), 50, 400, 100,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                game.createParty();
    	            }, null).setBorderRadius(20).setDecidingFactor(() -> {
    	            	return game.party == null;
    	            }));
    		
    		buttons.add(new Button(game, this, GameState.wavesmenu, "Join Party", (game.getWidth() - 450), 200, 400, 100,
    	            Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                game.joinParty("LucasHeh2");
    	            }, null).setBorderRadius(20).setDecidingFactor(() -> {
    	            	return game.party == null;
    	            }));
    	        
    	    buttons.add(new Button(game, this, GameState.wavesmenu, "Back", (game.getWidth() - 450), (game.getHeight() - 150), 400, 100,
    	                Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                    game.setGameState(GameState.mainmenu);
        	                try {
        	        			game.getMapManager().map.generateMap(SpriteTools.assetDirectory + "Art/Maps/Map1.png");
        	        		} catch (IOException e) {
        	        			e.printStackTrace();
        	        		}
    	                }, null).setBorderRadius(20));
    	    return buttons;
    	}, null, null);
    
    	GUI levelMenu = new GUI(game, this, () -> {
    		return game.getGameState() == GameState.levelmenu;
    	}, () -> {
    		List<Button> buttons = new ArrayList<>();
    	        
    	    buttons.add(new Button(game, this, GameState.levelmenu, "Back", (game.getWidth() - 450), (game.getHeight() - 150), 400, 100,
    	                Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                    game.setGameState(GameState.wavesmenu);
    	                }, null).setBorderRadius(20));
    	    return buttons;
    	}, null, () -> {
    		List<Label> labels = new ArrayList<>();
    		labels.add(new Label(game, this, GameState.levelmenu, "Tokens: " + Player.getGlobalStats().getLevelTokens(),
    				50, false, game.getWidth()-300, 50, 64, Color.black));
    		return labels;
    	});

    	GUI levelMenuShopTabs = new GUI(game, this, () -> {
    		return game.getGameState() == GameState.levelmenu;
    	}, () -> {
    		List<Button> buttons = new ArrayList<>();
    	        
    	    buttons.add(new Button(game, this, GameState.levelmenu, "Ammo Mods", 20, 20, 360, 75,
    	                Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                    this.lvlUpShop = LevelUpShopCategories.AmmoMods;
    	                }, null).setBorderRadius(20));
    	    buttons.add(new Button(game, this, GameState.levelmenu, "Classes", 20, 100, 360, 75,
	                Color.LIGHT_GRAY, Color.BLACK, () -> {
	                    this.lvlUpShop = LevelUpShopCategories.Classes;
	                }, null).setBorderRadius(20));
    	    buttons.add(new Button(game, this, GameState.levelmenu, "Perks", 20, 180, 360, 75,
	                Color.LIGHT_GRAY, Color.BLACK, () -> {
	                    this.lvlUpShop = LevelUpShopCategories.Perks;
	                }, null).setBorderRadius(20));
    	    return buttons;
    	}, null, null).setPanel(new Panel(50, 75, 400, game.getHeight()-350, Color.DARK_GRAY, Color.black, 20, 20));

    	GUI levelMenuShop = new GUI(game, this, () -> {
    		return game.getGameState() == GameState.levelmenu;
    	}, () -> {
    		List<Button> buttons = new ArrayList<>();
    	        
    	    buttons.add(new Button(game, this, GameState.levelmenu, "Flame", 20, 20, 360, 75,
    	                Color.LIGHT_GRAY, Color.BLACK, () -> {
    	                    this.lvlUpShop = LevelUpShopCategories.AmmoMods;
    	                }, null).setBorderRadius(20).setDecidingFactor(() -> {
    	                	return this.lvlUpShop == LevelUpShopCategories.AmmoMods;
    	                }));
	        
		    buttons.add(new Button(game, this, GameState.levelmenu, "Electric", 400, 20, 360, 75,
		                Color.LIGHT_GRAY, Color.BLACK, () -> {
		                    this.lvlUpShop = LevelUpShopCategories.AmmoMods;
		                }, null).setBorderRadius(20).setDecidingFactor(() -> {
		                	return this.lvlUpShop == LevelUpShopCategories.AmmoMods;
		                }));
	        
		    buttons.add(new Button(game, this, GameState.levelmenu, "Mutant", 780, 20, 360, 75,
		                Color.LIGHT_GRAY, Color.BLACK, () -> {
		                    this.lvlUpShop = LevelUpShopCategories.AmmoMods;
		                }, null).setBorderRadius(20).setDecidingFactor(() -> {
		                	return this.lvlUpShop == LevelUpShopCategories.AmmoMods;
		                }));
    	    return buttons;
    	}, null, null).setPanel(new Panel(500, 75, (int) (game.getWidth()/1.5), game.getHeight()-350, Color.DARK_GRAY, Color.black, 20, 20));
        
        GUI pausedGame = new GUI(game, this, () -> {
        	return game.getGameState() == GameState.waves && game.isPaused() && !game.settingsOpen;
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	
        	buttons.add(new Button(game, this, GameState.waves, "Back to Menu", 125, 400, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        game.getPlayer().die();
    	                this.createGUIs();
                        game.setPaused(false);
                    }, null));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Settings", 125, 300, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        game.settingsOpen = true;
                    }, null));
            
            buttons.add(new Button(game, this, GameState.waves, "Resume Game", 125, 200, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        game.setPaused(false);
                    }, null));
            return buttons;
        }, null, () -> {
        	List<Label> labels = new ArrayList<>();
        	
        	labels.add(new Label(game, this, GameState.waves, "Paused", 20, true, (game.getWidth())/2, 200, 48, Color.white).setShadow(Color.black, new Vector2D(5)));
        	
        	return labels;
        }).setPanel(new Panel((game.getWidth()-500)/2, 100, 500, 1000, new Color(20, 20, 20, 150), Color.black, 20, 20));
        
        
        GUI settingsMenu = new GUI(game, this, () -> {
        	return (game.getGameState() == GameState.waves || game.getGameState() == GameState.mainmenu) && game.settingsOpen;
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	buttons.add(new Button(game, this, null, "Back", 125, 900, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	game.settingsOpen = false;
                    }, null));
        	return buttons;
        }, () -> {
        	List<Slider> sliders = new ArrayList<>();
        	
        	sliders.add(new Slider(game, 150, 100, 200, 25, 0, 100, "master_volume", true)
        			.setTextColor(GameColors.colors.WHITE.getValue())
        			.setTextShadowColor(Color.DARK_GRAY.darker()));
        	sliders.add(new Slider(game, 150, 225, 200, 25, 0, 100, "sound_volume", true)
        			.setTextColor(GameColors.colors.WHITE.getValue())
        			.setTextShadowColor(Color.DARK_GRAY.darker()));
        	sliders.add(new Slider(game, 150, 350, 200, 25, 0, 100, "music_volume", true)
        			.setTextColor(GameColors.colors.WHITE.getValue())
        			.setTextShadowColor(Color.DARK_GRAY.darker()));
        	sliders.add(new Slider(game, 150, 450, 200, 25, 0, 165, "fpslimit", true)
        			.setTextColor(GameColors.colors.WHITE.getValue())
        			.setTextShadowColor(Color.DARK_GRAY.darker()));
        	
        	return sliders;
        }, null).setPanel(new Panel((game.getWidth()-500)/2, 100, 500, 1000, new Color(20, 20, 20, 150), Color.black, 20, 20));
        
        GUI gameOverMenu = new GUI(game, this, () -> {
        	return (game.getGameState() == GameState.wavesGameOver);
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	buttons.add(new Button(game, this, null, "Back", game.getWidth() / 2 - 125, game.getHeight()-200, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	game.setGameState(GameState.wavesmenu);
                    }, null));
        	return buttons;
        }, null, null);
        
        if(game.getGameState() != GameState.waves) return;
        
        GUI selectAClassMenu = new GUI(game, this, () -> {
       
        	if(game.party != null) return game.getGameState() == GameState.waves && (game.getPlayer().isPickingClass() || game.getPlayer().isPickingSecondary());
        	return game.getGameState() == GameState.waves && (!game.getWavesManager().hasGameStarted() || game.getPlayer().isPickingSecondary());
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	
        	buttons.add(new Button(game, this, GameState.waves, "Commando", 250, 750, 500, 150,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	if(game.getWavesManager().hasGameStarted()) {
                    		if(!(game.getPlayer().getPrimaryGun() instanceof AssaultRifle)) {
                    			game.getPlayer().setSecondaryGun(new AssaultRifle(game, game.getPlayer()));
                    			game.getPlayer().setPickingSecondary(false);
                    		}
                    	} else {
                    		if(game.party == null) {
		                        game.getWavesManager().startWaves();
		                        game.getPlayer().setPrimaryGun(new AssaultRifle(game, game.getPlayer()));
		                        game.getPlayer().setPickingClass(false);
                    		} else {
                				game.getSocketClient().getPacketManager().playerPickedClass();
		                        game.getPlayer().setPrimaryGun(new AssaultRifle(game, game.getPlayer()));
		                        game.getPlayer().setPickingClass(false);
                    		}
                    	}
                    }, 
                    new Tooltip(game, "Assault Rifle", "{GREEN}- Good Control{NL}- Good Damage {NL}{YELLOW}- Medium Range{NL}{YELLOW}- Medium Fire Rate",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	if(!game.getWavesManager().hasGameStarted()) return null;
                    	List<Supplier<String>> list = new ArrayList<>();
                    	
                    	list.add(() -> {
                    		boolean hasGun = (game.getPlayer().getPrimaryGun() instanceof AssaultRifle);
                    		return hasGun ? "{RED}(!) Already Equipped" : "";
                    	});
                    	
                    	return list;
                    })).setBorderRadius(10));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Vanguard", 1250, 500, 500, 150,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	if(game.getWavesManager().hasGameStarted()) {
                    		if(!(game.getPlayer().getPrimaryGun() instanceof SMG)) {
                    			game.getPlayer().setSecondaryGun(new SMG(game, game.getPlayer()));
                    			game.getPlayer().setPickingSecondary(false);
                    		}
                    	} else {
                    		if(game.party == null) {
		                        game.getWavesManager().startWaves();
		                        game.getPlayer().setPrimaryGun(new SMG(game, game.getPlayer()));
		                        game.getPlayer().setPickingClass(false);
                    		} else {
		                        game.getPlayer().setPrimaryGun(new SMG(game, game.getPlayer()));
                				game.getSocketClient().getPacketManager().playerPickedClass();
		                        game.getPlayer().setPickingClass(false);
                    		}
                    	}
                    }, 
                    new Tooltip(game, "SMG", "{GREEN}- Fast Fire Rate{NL}{YELLOW}- Medium Control{NL}{RED}- Close Range{NL}- Low Damage",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	if(!game.getWavesManager().hasGameStarted()) return null;
                    	List<Supplier<String>> list = new ArrayList<>();
                    	
                    	list.add(() -> {
                    		boolean hasGun = (game.getPlayer().getPrimaryGun() instanceof SMG);
                    		return hasGun ? "{RED}(!) Already Equipped" : "";
                    	});
                    	
                    	return list;
                    })).setBorderRadius(10));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Breaker", (int) 1250, 750, 500, 150,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	if(game.getWavesManager().hasGameStarted()) {
                    		if(!(game.getPlayer().getPrimaryGun() instanceof Shotgun)) {
                    			game.getPlayer().setSecondaryGun(new Shotgun(game, game.getPlayer()));
                    			game.getPlayer().setPickingSecondary(false);
                    		}
                    	} else {
                    		if(game.party == null) {
		                        game.getWavesManager().startWaves();
		                        game.getPlayer().setPrimaryGun(new Shotgun(game, game.getPlayer()));
		                        game.getPlayer().setPickingClass(false);
                    		} else {
                				game.getSocketClient().getPacketManager().playerPickedClass();
		                        game.getPlayer().setPrimaryGun(new Shotgun(game, game.getPlayer()));
		                        game.getPlayer().setPickingClass(false);
                    		}
                    	}
                    }, 
                    new Tooltip(game, "Shotgun", "{GREEN}- Shoots Pellets{NL}- Medium-High Damage{NL}- Higher Chance for Ammo Mods{NL}{YELLOW}- Area Damage{NL}{RED}- Close Range{NL}{RED}- Medium-Slow Fire Rate{NL}{RED}- Semi-Automatic",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	if(!game.getWavesManager().hasGameStarted()) return null;
                    	List<Supplier<String>> list = new ArrayList<>();
                    	
                    	list.add(() -> {
                    		boolean hasGun = (game.getPlayer().getPrimaryGun() instanceof Shotgun);
                    		return hasGun ? "{RED}(!) Already Equipped" : "";
                    	});
                    	
                    	return list;
                    })).setBorderRadius(10));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Rifleman", (int) 250, 500, 500, 150,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	if(game.getWavesManager().hasGameStarted()) {
                    		if(!(game.getPlayer().getPrimaryGun() instanceof Sniper)) {
                    			game.getPlayer().setSecondaryGun(new Sniper(game, game.getPlayer()));
                    			game.getPlayer().setPickingSecondary(false);
                    		}
                    	} else {
                    		if(game.party == null) {
		                        game.getWavesManager().startWaves();
		                        game.getPlayer().setPrimaryGun(new Sniper(game, game.getPlayer()));
		                        game.getPlayer().setPickingClass(false);
                    		} else {
                				game.getSocketClient().getPacketManager().playerPickedClass();
		                        game.getPlayer().setPrimaryGun(new Sniper(game, game.getPlayer()));
		                        game.getPlayer().setPickingClass(false);
                    		}
                    	}
                    }, 
                    new Tooltip(game, "Sniper", "{GREEN}- High Damage{NL}{GREEN}- Long Range{NL}{GREEN}- Pierces through enemies{NL}{RED}- Slow Fire Rate{NL}- Semi-Automatic",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	if(!game.getWavesManager().hasGameStarted()) return null;
                    	List<Supplier<String>> list = new ArrayList<>();
                    	
                    	list.add(() -> {
                    		boolean hasGun = (game.getPlayer().getPrimaryGun() instanceof Sniper);
                    		return hasGun ? "{RED}(!) Already Equipped" : "";
                    	});
                    	
                    	return list;
                    })).setBorderRadius(10));
        	
        	return buttons;
        }, null, () -> {
        	List<Label> labels = new ArrayList<>();
        	labels.add(new Label(game, this, GameState.waves, "Select Your Class", 10, true, this.game.getWidth()/2, this.game.getHeight()/6, 156, GameColors.colors.WHITE.getValue()).setShadow(Color.DARK_GRAY, new Vector2D(5)));
        	return labels;
        }).setPanel(new Panel(game.getWidth()/8, 50, (int) (game.getWidth() - game.getWidth()/4), (int) (game.getHeight()/1.4), 
        		Color.black.brighter(), Color.black, 20, 20).setOpacity(140));
            
        // NPCS ------------
        
        playerUpgradeMenu = new GUI(game, this, () ->{
        	return game.getGameState() == GameState.waves && 
        			game.getWavesManager() != null && 
        			game.getWavesManager().getNPCManager() != null && 
        			game.getWavesManager().getNPCManager().getPlayerUpgradeNPC() != null &&
        			game.getWavesManager().getNPCManager().getPlayerUpgradeNPC().isOpen();
        			
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	
        	Button regen = (new Button(game, this, GameState.waves, "Unlock Health Regen", 50, 400, 250, 50,
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
        	
        	buttons.add(new Button(game, this, GameState.waves, "Upgrade Health Regen", 50, 100, 250, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    boolean success = game.getPlayer().getPlayerUpgrades().upgrade("health");
                }, 
                new Tooltip(game, game.getPlayer().getPlayerUpgrades().getCost("health") + " Gem(s)", "Upgrading will increase your health regeneration.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                	List<Supplier<String>> list = new ArrayList<>();
                	list.add(() -> {
                		boolean hasRegen = game.getPlayer().getPlayerUpgrades().hasHealthRegen();
                		return hasRegen ? "" : "{YELLOW}(!) Health Regen Not Unlocked";
                	});
                	list.add(() -> {
                		boolean hasGems = game.getPlayer().getGems() >= game.getPlayer().getPlayerUpgrades().getCost("health");
                		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                	});
                	return list;
                })));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Upgrade Max Health", 50, 175, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        boolean success = game.getPlayer().getPlayerUpgrades().upgrade("maxHealth");
                    }, 
                    new Tooltip(game, game.getPlayer().getPlayerUpgrades().getCost("maxHealth") + " Gem(s)", "Purchasing will extend your max health.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= game.getPlayer().getPlayerUpgrades().getCost("maxHealth");
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Upgrade Damage", 50, 250, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        boolean success = game.getPlayer().getPlayerUpgrades().upgrade("damage");
                    }, 
                    new Tooltip(game, game.getPlayer().getPlayerUpgrades().getCost("damage") + " Gem(s)", "Purchasing will upgrade your damage multiplier.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= game.getPlayer().getPlayerUpgrades().getCost("damage");
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Upgrade Movement", 50, 325, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        boolean success = game.getPlayer().getPlayerUpgrades().upgrade("movement");
                        System.out.println("Got movement");
                    }, 
                    new Tooltip(game, game.getPlayer().getPlayerUpgrades().getCost("movement") + " Gem(s)", "Purchasing will upgrade your movement speed.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= game.getPlayer().getPlayerUpgrades().getCost("movement");
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "X", 0, 0, 50, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    game.getWavesManager().getNPCManager().getPlayerUpgradeNPC().close();
                }, null));
        	
        	return buttons;
        }, null, null).setPanel(new Panel(
        	    (int) ((game.getWidth() - 275) / 1.2),  // x position, based on button alignment
        	    150,                                    // y position, starting above the first button
        	    350,                                    // width to fit buttons with padding
        	    800,                                    // height to cover all upgrade buttons
        	    new Color(50, 50, 50, 180),             // bgColor, semi-transparent dark color
        	    Color.BLACK,                            // borderColor, black for definition
        	    20,                                     // borderRadius, for rounded corners
        	    15                                      // padding for inner spacing
        	));
        
        GUI craftingTableMenu = new GUI(game, this, () ->{
        	return game.getGameState() == GameState.waves && 
        			game.getWavesManager() != null && 
        			game.getWavesManager().getNPCManager() != null && 
        			game.getWavesManager().getNPCManager().getCraftingTable() != null &&
        			game.getWavesManager().getNPCManager().getCraftingTable().isOpen();
        			
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	
        	buttons.add(new Button(game, this, GameState.waves, "Turret",  50, 100, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        game.getPlayer().getPlaceableManager().purchase("turret");
                    }, 
                    new Tooltip(game, "$" + PlayerPlaceableManager.turretCost, "Placeable turret (30s){NL}Click (1) to place.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasEnough= game.getPlayer().getMoney() >= PlayerPlaceableManager.turretCost;
                    		return hasEnough ? "" : "{RED}(!) Not Enough Money!";
                    	});
                    	list.add(() -> {
                    		boolean invEmpty = game.getPlayer().getPlaceableManager().getEquippedPlaceable() == null;
                    		return invEmpty ? "" : "{RED}(!) Holding a placeable already!";
                    	});
                    	return list;
                    })));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Landmine", 50, 175, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                        game.getPlayer().getPlaceableManager().purchase("landmine");
                    }, 
                    new Tooltip(game, "$" +  + PlayerPlaceableManager.landmineCost, "Placeable Landmine{NL}Click (1) to place.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasEnough = game.getPlayer().getMoney() >= PlayerPlaceableManager.landmineCost;
                    		return hasEnough ? "" : "{RED}(!) Not Enough Money!";
                    	});
                    	list.add(() -> {
                    		boolean invEmpty = game.getPlayer().getPlaceableManager().getEquippedPlaceable() == null;
                    		return invEmpty ? "" : "{RED}(!) Holding a placeable already!";
                    	});
                    	return list;
                    })));
        	
        	buttons.add(new Button(game, this, GameState.waves, "X", 0, 0, 50, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    game.getWavesManager().getNPCManager().getCraftingTable().close();
                }, null));
        	
        	return buttons;
        }, null, null).setPanel(new Panel(
        	    (int) ((game.getWidth() - 275) / 1.2),  // x position, based on button alignment
        	    150,                                    // y position, starting above the first button
        	    350,                                    // width to fit buttons with padding
        	    800,                                    // height to cover all upgrade buttons
        	    new Color(50, 50, 50, 180),             // bgColor, semi-transparent dark color
        	    Color.BLACK,                            // borderColor, black for definition
        	    20,                                     // borderRadius, for rounded corners
        	    15                                      // padding for inner spacing
        	));
        
        GUI missionMenu = new GUI(game, this, () ->{
        	return game.getGameState() == GameState.waves && 
        			game.getWavesManager() != null && 
        			game.getWavesManager().getNPCManager() != null && 
        			game.getWavesManager().getNPCManager().getMission() != null &&
        			game.getWavesManager().getNPCManager().getMission().isOpen();
        			
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	
        	buttons.add(new Button(game, this, GameState.waves, "Start Mission",  50, 100, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	if(game.getPlayer().getMoney() >= 1000 && game.getWavesManager().getMissionManager().canStartMission()) {
                    		game.getWavesManager().getMissionManager().startMission();
                    		game.getPlayer().removeMoney(1000);
                    	}
                    }, 
                    new Tooltip(game, "$1,000", "Start a mission once per wave.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasEnough= game.getPlayer().getMoney() >= 1000;
                    		return hasEnough ? "" : "{RED}(!) Not Enough Money!";
                    	});
                    	list.add(() -> {
                    		boolean canStart = game.getWavesManager().getMissionManager().canStartMission();
                    		return canStart ? "" : "{RED}(!) You have done a mission this wave!";
                    	});
                    	return list;
                    })));
        	
        	buttons.add(new Button(game, this, GameState.waves, "X", 0, 0, 50, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    game.getWavesManager().getNPCManager().getMission().close();
                }, null));
        	
        	return buttons;
        }, null, null).setPanel(new Panel(
        	    (int) ((game.getWidth() - 275) / 1.2),  // x position, based on button alignment
        	    150,                                    // y position, starting above the first button
        	    350,                                    // width to fit buttons with padding
        	    800,                                    // height to cover all upgrade buttons
        	    new Color(50, 50, 50, 180),             // bgColor, semi-transparent dark color
        	    Color.BLACK,                            // borderColor, black for definition
        	    20,                                     // borderRadius, for rounded corners
        	    15                                      // padding for inner spacing
        	));
        
        GUI gunsmithMenu = new GUI(game, this, () ->{
        	return game.getGameState() == GameState.waves && 
        			game.getWavesManager() != null && 
        			game.getWavesManager().getNPCManager() != null && 
        			game.getWavesManager().getNPCManager().getGunsmith() != null &&
        			game.getWavesManager().getNPCManager().getGunsmith().isOpen();
        			
        }, () -> {
        	List<Button> buttons = new ArrayList<>();
        	
        	String gunUpgradeCost = "$" + game.getPlayer().getPlayerUpgrades().getWeaponUpgradeCost();
        	if(gunUpgradeCost.equals("$-1")) gunUpgradeCost = "Weapon Maxed Out";
        	
        	String gunUpgradeDesc = 
        			"{" + game.getPlayer().getPrimaryGun().getTier().toString().toUpperCase() + "}" + game.getPlayer().getPrimaryGun().getTier().toString() + 
        			"{WHITE} >> " + 
        			"{" + game.getPlayer().getPrimaryGun().getNextTier().toString().toUpperCase() + "}" + game.getPlayer().getPrimaryGun().getNextTier().toString();
        	
        	if(game.getPlayer().getPrimaryGun().getTier() == Tier.lastTier()) gunUpgradeDesc = " ";
        	
        	buttons.add(new Button(game, this, GameState.waves, "Secondary Class", 50, 175, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	game.getWavesManager().getNPCManager().closeAll();
                        game.getPlayer().getPlayerUpgrades().unlockSecondClass();
                    }, 
                    new Tooltip(game, "Select a Secondary Weapon", " ",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasEnough = game.getPlayer().getMoney() >= 10000;
                    		return hasEnough ? "{GREEN}- $10,000" : "{RED}- $10,000";
                    	});
                    	list.add(() -> {
                    		boolean hasEnough = game.getPlayer().getGems() >= 10;
                    		return hasEnough ? "{GREEN}- 10 Gems" : "{RED}- 10 Gems";
                    	});
                    	return list;
                    })));

        	buttons.add(new Button(game, this, GameState.waves, "Gun Tier", 50, 250, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	if(game.getPlayer().getPrimaryGun().getTier() != Tier.lastTier()) game.getPlayer().getPlayerUpgrades().upgradeWeapon();
                    }, 
                    new Tooltip(game, gunUpgradeCost, gunUpgradeDesc,(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasMoney = game.getPlayer().getMoney() >= game.getPlayer().getPlayerUpgrades().getWeaponUpgradeCost();
                    		return hasMoney ? "" : "{RED}(!) Not Enough Money!";
                    	});
                    	list.add(() -> {
                    		boolean weaponMaxedOut = game.getPlayer().getPrimaryGun().getTier() == Tier.lastTier();
                    		if(weaponMaxedOut && Tier.lastTier() == Tier.Ethereal) return "";
                    		return weaponMaxedOut ?  "{RED}(!) Unlock more tiers by levelling up!" : "{WHITE}Current Tier: {" + game.getPlayer().getPrimaryGun().getTier().toString().toUpperCase() + "} " + game.getPlayer().getPrimaryGun().getTier().toString();
                    	});
                    	return list;
                    })
                    ));
        	

        	buttons.add(new Button(game, this, GameState.waves, "Ammo Mod - Flame", 50, 400, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	game.getPlayer().getPlayerUpgrades().purchaseAmmoMod(AmmoMod.Flame);
                    }, 
                    new Tooltip(game, "5 Gems", "{LIGHT_GRAY}Sets enemies on fire and deals {RED}" + game.getPlayer().getPlayerUpgrades().getDamageMultiplier()*10 + " {LIGHT_GRAY}damage",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= 5;
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	list.add(() -> {
                    		boolean hasMod = game.getPlayer().getPrimaryGun().getAmmoMod() != AmmoMod.Flame;
                    		return hasMod ? "" : "{RED}(!) Weapon already has Flame!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Ammo Mod - Electricity", 50, 475, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	game.getPlayer().getPlayerUpgrades().purchaseAmmoMod(AmmoMod.Electric);
                    }, 
                    new Tooltip(game, "5 Gems", "{LIGHT_GRAY}Stuns enemies for {RED}" + 5 + " {LIGHT_GRAY}seconds",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= 5;
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	list.add(() -> {
                    		boolean hasMod = game.getPlayer().getPrimaryGun().getAmmoMod() != AmmoMod.Electric;
                    		return hasMod ? "" : "{RED}(!) Weapon already has Electricity!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "Ammo Mod - Mutant", 50, 550, 250, 50,
                    Color.LIGHT_GRAY, Color.BLACK, () -> {
                    	game.getPlayer().getPlayerUpgrades().purchaseAmmoMod(AmmoMod.Mutant);
                    }, 
                    new Tooltip(game, "5 Gems", "{LIGHT_GRAY}Splits into 2 bullets on impact. Chance to further split.",(int) this.mousePos.getX(), (int) this.mousePos.getY(), Color.DARK_GRAY, Color.WHITE, () -> {
                    	List<Supplier<String>> list = new ArrayList<>();
                    	list.add(() -> {
                    		boolean hasGems = game.getPlayer().getGems() >= 5;
                    		return hasGems ? "" : "{RED}(!) Not Enough Gems!";
                    	});
                    	list.add(() -> {
                    		boolean hasMod = game.getPlayer().getPrimaryGun().getAmmoMod() != AmmoMod.Mutant;
                    		return hasMod ? "" : "{RED}(!) Weapon already has Electricity!";
                    	});
                    	return list;
                    })
                    ));
        	
        	buttons.add(new Button(game, this, GameState.waves, "X", 0, 0, 50, 50,
                Color.LIGHT_GRAY, Color.BLACK, () -> {
                    game.getWavesManager().getNPCManager().getGunsmith().close();
                }, null));
        	
        	return buttons;
        }, null, null).setPanel(new Panel(
        	    (int) ((game.getWidth() - 275) / 1.2),  // x position, based on button alignment
        	    150,                                    // y position, starting above the first button
        	    350,                                    // width to fit buttons with padding
        	    800,                                    // height to cover all upgrade buttons
        	    new Color(50, 50, 50, 180),             // bgColor, semi-transparent dark color
        	    Color.BLACK,                            // borderColor, black for definition
        	    20,                                     // borderRadius, for rounded corners
        	    15                                      // padding for inner spacing
        	));
        
    }
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
        Font font = game.font;
        g2d.setFont(font);
        
        //if(playerUpgradeMenu != null) Debug.log(this, this.playerUpgradeMenu.getDecider());

        // Render buttons
        for (GUI gui : GUIs) {
            gui.render(g2d);
        }
        
     // Render tooltip if active
        if (activeTooltip != null) {
            activeTooltip.render(g2d);
        }
        
        if(game.getGameState() == GameState.mainmenu && !game.settingsOpen) {
        	String title = "Shooter Dude";
            g2d.setFont(game.font.deriveFont(256f));
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g.setColor(Color.DARK_GRAY.darker());
            g2d.drawString(title, (game.getWidth() - titleWidth)/2, 300);
            g2d.setColor(new Color(240, 240, 240));
            g2d.drawString(title, (game.getWidth() - titleWidth)/2 + 7, 300 + 7);
        }
        if(game.getGameState() == GameState.wavesmenu) {
        	
        	
        	if(game.party != null && game.party.getHost() != null) {
        		g2d.setFont(game.font.deriveFont(128));
        		
        		g2d.drawString("Party", 50, 50);

        		g2d.setFont(game.font.deriveFont(72));
        		g2d.drawString(game.party.getHost().getUsername(), 50, 75);
        		
        		int count = 25;
        		for(PlayerMP player : game.party.getPlayers()) {
        			g2d.drawString(player.getUsername(), 50, 75+count);
        			count += 25;
        		}
        		
        	}
        	
        	
        	int margin = 100;
    		int XpBarSize = 100;
    		int XpBarLength = 4; //length x size = actual length
    		
    		g2d.setFont(game.font.deriveFont(Font.PLAIN, 64f));
    	    
    		g2d.setColor(Color.black);
    		g2d.fillRect(margin, game.getHeight()-(margin*2), XpBarLength * XpBarSize, XpBarSize);
    		
    		g2d.setColor(Color.green);
    		g2d.fillRect((int)(margin + (margin*0.1))
    				, (int) (game.getHeight() - margin*2 + margin*0.1), 
    				(int) (((XpBarSize * XpBarLength) - (margin*0.2)) * ((double) Player.getGlobalStats().getCurrentXP()/Player.getGlobalStats().getXpToNextLevel())), 
    				(int)(XpBarSize - (XpBarSize * 0.2)));
        }
        if(game.getGameState() == GameState.levelmenu) {
        	
        	int margin = 100;
    		int XpBarSize = 100;
    		int XpBarLength = 4; //length x size = actual length
    		
    		g2d.setFont(game.font.deriveFont(Font.PLAIN, 64f)); // Derive the font size explicitly as a float
    	    
    		g2d.setColor(Color.black);
    		g2d.fillRect(margin, game.getHeight()-(margin*2), XpBarLength * XpBarSize, XpBarSize);
    		
    		g2d.setColor(Color.green);
    		g2d.fillRect((int)(margin + (margin*0.1))
    				, (int) (game.getHeight() - margin*2 + margin*0.1), 
    				(int) (((XpBarSize * XpBarLength) - (margin*0.2)) * ((double) Player.getGlobalStats().getCurrentXP()/Player.getGlobalStats().getXpToNextLevel())), 
    				(int)(XpBarSize - (XpBarSize * 0.2)));
        	
        }
        if(game.getGameState() == GameState.wavesGameOver) {
        	
        	PlayerWavesStats stats = game.getPlayer().getWavesStats();

            // Draw "Game Over" title
            g2d.setFont(game.font.deriveFont(128f));
            int titleWidth = g2d.getFontMetrics().stringWidth("Game Over");
            g2d.drawString("Game Over", (game.getWidth() - titleWidth) / 2, 150);

            // Draw Wave information
            g2d.setFont(game.font.deriveFont(64f));
            String waveText = "Wave " + stats.wave;
            int waveWidth = g2d.getFontMetrics().stringWidth(waveText);
            int center = (game.getWidth() - waveWidth) / 2;
            g2d.setColor(Color.black);
            g2d.drawString(waveText, center, 350);

            // Draw all stats dynamically
            g2d.setFont(game.font.deriveFont(48f));
            int yPosition = 450; // Starting y position for stats
            int lineHeight = 60; // Space between lines

            Field[] fields = PlayerWavesStats.class.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true); // Allow access to private fields if any
                    Object value = field.get(stats); // Get the value of the field

                    String fieldName = field.getName();
                    fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

	                StringBuilder formattedName = new StringBuilder();
	                for (int i = 0; i < fieldName.length(); i++) {
	                    char c = fieldName.charAt(i);
	                    if (Character.isUpperCase(c)) {
	                        formattedName.append(" ");
	                    }
	                    formattedName.append(c);
	                }
	                String finalName = formattedName.substring(0, 1).toUpperCase() + formattedName.substring(1).toLowerCase();
                    String fieldValue = value.toString();
                    String statText = formattedName + ": " + fieldValue;

                    g2d.setColor(Color.black);
                    int statWidth = g2d.getFontMetrics().stringWidth(statText);
                    g2d.drawString(statText, (game.getWidth() - statWidth) / 2, yPosition);

                    yPosition += lineHeight; // Move to the next line
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
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
	            	if(gui.getPanel() != null && !button.adjustedPositionWithPanel) button.updatePositionWithPanel(gui.getPanel());
	                // Check if the button was clicked
	                if (button.isClicked(e)) {
	                	game.getAudioPlayer().playSound("UI/button_click.wav", null);
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
		
		if(game.getGameState().is(GameState.mainmenu, GameState.wavesmenu)) {
			if (SwingUtilities.isRightMouseButton(e)) { // Check for right mouse button
	            Point currentMousePosition = e.getPoint();
	            
	            // Calculate the difference between the current and previous mouse positions
	            int dx = currentMousePosition.x - previousMousePosition.x;
	            int dy = currentMousePosition.y - previousMousePosition.y;

	            // Adjust the camera's world position based on the mouse drag
	            Vector2D currentWorldPosition = game.getCamera().getWorldPosition();
	            
	            Vector2D viewport = game.getCamera().getViewport();
	            Vector2D newCamPos = (currentWorldPosition.subtract(new Vector2D(dx, dy)));
	    	    Vector2D worldSize = game.getMapManager().getWorldSize();
	            
	            boolean cameraAtLeft = newCamPos.getX() <= 0;
	    	    boolean cameraAtTop = newCamPos.getY() <= 0;
	    	    boolean cameraAtRight = newCamPos.getX() + viewport.getX() >= worldSize.getX();
	    	    boolean cameraAtBottom = newCamPos.getY() + viewport.getY() >= worldSize.getY();

	    	    // Update the camera's position while respecting the boundaries
	    	    double newCamX = currentWorldPosition.getX();
	    	    double newCamY = currentWorldPosition.getY();

	    	    // If the camera is not bounded on the left or right, update its X position
	    	    if (!cameraAtLeft && !cameraAtRight) {
	    	        newCamX = newCamPos.getX();
	    	    }

	    	    // If the camera is not bounded on the top or bottom, update its Y position
	    	    if (!cameraAtTop && !cameraAtBottom) {
	    	        newCamY = newCamPos.getY();
	    	    }
	    	    game.getCamera().setWorldPosition(new Vector2D(newCamX, newCamY));
	            
	            // Update the previous mouse position
	            previousMousePosition = currentMousePosition;
	        }
		}
		
	}

	@Override
	public void onMouseMoved(MouseEvent e) {
        previousMousePosition = e.getPoint();
	    int mouseX = e.getX();
	    int mouseY = e.getY();
	    boolean currentlyOverButton = false;
	    Button hoveredButton = null;

	    // Iterate through all GUIs
	    for (GUI gui : GUIs) {
	        // Check if the GUI is currently active
	    	boolean hasPanel = gui.getPanel() != null;
	        if (gui.getDecider().get()) {
	        	List<Button> buttons = gui.getButtons().get();
	            // Iterate through the buttons of the active GUI
	            for (Button button : buttons) {
	            	if(hasPanel && !button.adjustedPositionWithPanel) button.updatePositionWithPanel(gui.getPanel());
	                // Check if the mouse is hovering over the button
	                if (mouseX >= button.getX() && mouseX <= button.getX() + button.getWidth()
	                        && mouseY >= button.getY() && mouseY <= button.getY() + button.getHeight()) {
	                    
	                    currentlyOverButton = true;
	                    hoveredButton = button;
	                    hoveredButton.hovering = true;

	                    // Play the hover sound only if entering a new button or a different button
	                    if (!hoverSoundPlayed || !lastHoveredButton.equals(button.getText())) {
	                        game.getAudioPlayer().playSound("UI/button_hover.wav", null);
	                        hoverSoundPlayed = true;
	                        lastHoveredButton = button.getText();
	                    }

	                    // Check if the button has a tooltip and position it if needed
	                    if (button.getTooltip() != null) {
	                        button.getTooltip().setPosition(mouseX + 15, mouseY + 15);
	                        activeTooltip = button.getTooltip();
	                    }

	                    return; // Exit after finding one button to hover over
	                }
	            }
	        }
	    }

	    // Reset hover sound flag and last hovered button if no button is hovered over
	    if (!currentlyOverButton) {
	        hoverSoundPlayed = false;
	        lastHoveredButton = null;
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

	public void setActiveTooltip(Tooltip activeTooltip) {
		this.activeTooltip = activeTooltip;
	}

	public String getLastHoveredButton() {
		return lastHoveredButton;
	}

	
	
}