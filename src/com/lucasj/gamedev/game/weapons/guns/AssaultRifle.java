package com.lucasj.gamedev.game.weapons.guns;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.weapons.Gun;
import com.lucasj.gamedev.game.weapons.GunType;
import com.lucasj.gamedev.mathutils.Vector2D;

public class AssaultRifle extends Gun {

	public AssaultRifle(Game game, Player p) {
		super(game, p, 
				25, // Base damage
				40, // Projectile speed
				0.5f, // Fire rate
				30, // Clip size
				3, // range (seconds
				GunType.AR, // Type
				3, // Reload speed
				true, // Automatic
				0.23f, // Bloom
				"GunFire/AR/gunshot.wav",
				SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Guns/AR_AK_47.png", new Vector2D(0, 0), new Vector2D(128, 64)));
	}

}
