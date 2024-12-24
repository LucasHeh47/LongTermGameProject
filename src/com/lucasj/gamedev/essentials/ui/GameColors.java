package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;

import com.lucasj.gamedev.game.weapons.Tier;
import com.lucasj.gamedev.misc.Debug;

public class GameColors {
	
	public static enum colors {
		
		RED(Color.RED),
		LIGHT_RED(new Color(255, 140, 140)),
		BLUE(Color.BLUE),
		LIGHT_BLUE(new Color(43, 202, 255)),
		GREEN(Color.GREEN),
		YELLOW(Color.YELLOW),
		GOLD(new Color(235, 182, 49)),
		WHITE(new Color(240, 240, 240)),
		BLACK(Color.BLACK),
		PINK(Color.PINK),
		PURPLE(Color.MAGENTA),
		ORANGE(Color.ORANGE),
		CYAN(Color.CYAN),
		
	    COMMON(new Color(166, 166, 166)),
	    UNCOMMON(new Color(12, 221, 0)),
	    RARE(new Color(39, 86, 255)),
	    EPIC(new Color(128, 0, 255)),
	    LEGENDARY(new Color(255, 67, 0)),
	    MYTHIC(new Color(255, 230, 101)),
	    DIVINE(new Color(200, 73, 73)),
	    ETHEREAL(new Color(48, 210, 255));
		
		private Color value;

		colors(Color color) {
			this.value = color;
		}
		
		public Color getValue() {
			return value;
		}
		
		public static Color getColor(String name) {
	        try {
	            return colors.valueOf(name.toUpperCase()).getValue();
	        } catch (IllegalArgumentException | NullPointerException e) {
	            try {
	                return (Color) Color.class.getField(name.toUpperCase()).get(null);
	            } catch (NoSuchFieldException | IllegalAccessException ex) {
	                return Color.BLACK;
	            }
	        }
	    }
		
	}
	
	public static Color GUN_COLORS(Tier tier) {
		try {
	        return colors.valueOf(tier.name().toUpperCase()).getValue();
	    } catch (IllegalArgumentException e) {
	        return new Color(0, 0, 0); 
	    }
	}
	
	public static Color brighter(Color color, int intensity) {
		Color result = color;
		for(int i = 0; i < intensity; i++) {
			result.brighter();
			Debug.log(GameColors.class, result);
		}
		return result;
	}
	
	public static Color darker(Color color, int intensity) {
		Color result = color;
		for(int i = 0; i < intensity; i++) {
			result.darker();
		}
		return result;
	}

}
