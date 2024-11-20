package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;

import com.lucasj.gamedev.game.weapons.Tier;

public class GameColors {
	
	public static enum colors {
		
		RED(Color.RED),
		LIGHT_RED(new Color(255, 140, 140)),
		BLUE(Color.BLUE),
		GREEN(Color.GREEN),
		YELLOW(Color.YELLOW),
		GOLD(new Color(235, 182, 49)),
		WHITE(Color.WHITE),
		BLACK(Color.BLACK),
		PINK(Color.PINK),
		PURPLE(Color.MAGENTA),
		ORANGE(Color.ORANGE),
		CYAN(Color.CYAN),
		
	    Common(new Color(166, 166, 166)),
	    Uncommon(new Color(12, 221, 0)),
	    Rare(new Color(39, 86, 255)),
	    Epic(new Color(128, 0, 255)),
	    Legendary(new Color(255, 67, 0)),
	    Mythic(new Color(255, 230, 101)),
	    Divine(new Color(200, 73, 73)),
	    Ethereal(new Color(48, 210, 255));
		
		private Color value;

		colors(Color color) {
			this.value = color;
		}
		
		public Color getValue() {
			return value;
		}
		
	}
	
	public static Color GUN_COLORS(Tier tier) {
		try {
	        return colors.valueOf(tier.name()).getValue();
	    } catch (IllegalArgumentException e) {
	        return new Color(0, 0, 0); 
	    }
	}

}
