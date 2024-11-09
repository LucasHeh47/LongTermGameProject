package com.lucasj.gamedev.game.entities.player;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.placeables.Landmine;
import com.lucasj.gamedev.game.entities.placeables.Placeable;
import com.lucasj.gamedev.game.entities.placeables.Turret;

public class PlayerPlaceableManager {
	
	public static final int turretCost = 0;
	public static final int landmineCost = 0;

	private Placeable equippedPlaceable;
	
	private Game game;
	private Player player;
	
	public PlayerPlaceableManager(Game game, Player player) {
		this.game = game;
		this.player = player;
		
	}

	public Placeable getEquippedPlaceable() {
		return equippedPlaceable;
	}

	public void setEquippedPlaceable(Placeable equippedPlaceable) {
		this.equippedPlaceable = equippedPlaceable;
	}
	
	public boolean purchase(String placeable) {
		switch (placeable) {
		case "turret":
			if(player.removeMoney(turretCost)) {
				equippedPlaceable = new Turret(game, player, game.getCamera().screenToWorldPosition(player.getMousePosition()), 30, 30, null);
				return true;
			}
			return false;
		case "landmine":
			if(player.removeMoney(landmineCost)) {
				equippedPlaceable = new Landmine(game, player, game.getCamera().screenToWorldPosition(player.getMousePosition()), null);
				return true;
			}
			return false;
		default:
			return false;
		}
	}
	
}
