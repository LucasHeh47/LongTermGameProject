package com.lucasj.gamedev.game.weapons.guns;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.game.weapons.GunType;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Sniper extends Gun {

	public Sniper(Game game, Player p) {
		super(game, p, 
				80, // Base damage
				100, // Projectile speed
				3f, // Fire rate
				1, // Clip size
				3, // range (seconds
				GunType.AR, // Type
				3, // Reload speed
				false, // Automatic
				0.1f, // Bloom
				"GunFire/Sniper/gunshot.wav",
				SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Guns/Sniper.png", new Vector2D(0, 0), new Vector2D(128, 64)));
		this.pierce = 2;
	}

}
