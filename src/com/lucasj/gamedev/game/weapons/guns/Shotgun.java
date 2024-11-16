package com.lucasj.gamedev.game.weapons.guns;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.game.weapons.GunType;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Shotgun extends Gun {

	public Shotgun(Game game, Player p) {
		super(game, p, 
				25 * 9, // Base damage per pellet * pellet
				70, // Projectile speed
				1.2f, // Fire rate
				8, // Clip size
				0.75f, // range (seconds)
				GunType.Shotgun, // Type
				3, // Reload speed
				true, // Automatic
				0.05f, // Bloom
				"GunFire/AR/gunshot.wav",
				SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Guns/Shotgun.png", new Vector2D(0, 0), new Vector2D(128, 64)));
	}
	
}
