package com.lucasj.gamedev.game.weapons.guns;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.game.weapons.GunType;
import com.lucasj.gamedev.mathutils.Vector2D;

public class SMG extends Gun {

	public SMG(Game game, Player p) {
		super(game, p, 
				13, // Base damage
				25, // Projectile speed
				0.25f, // Fire rate
				20, // Clip size
				1.5f, // range (seconds
				GunType.AR, // Type
				2.5f, // Reload speed
				true, // Automatic
				0.33f, // Bloom
				"GunFire/SMG/gunshot.wav",
				SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Guns/SMG.png", new Vector2D(0, 0), new Vector2D(128, 64)));
	}

}
