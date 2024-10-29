package com.lucasj.gamedev.game.entities.npc;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;

public class NPCManager {

	private Game game;
	private Player player;
	
	private NPC playerUpgradeNPC;
	
	public NPCManager(Game game, Player player) {
		this.game = game;
		this.player = player;
		
	}
	
	public void instantiateNPCs() {
		playerUpgradeNPC = new NPC(game, new Vector2D(1000, 1000), 50);
		playerUpgradeNPC.instantiate();
	}

	public NPC getPlayerUpgradeNPC() {
		return playerUpgradeNPC;
	}
	
}
