package com.lucasj.gamedev.game.entities.npc;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;

public class NPCManager {

	private Game game;
	private Player player;
	
	private NPC playerUpgradeNPC;
	private NPC craftingTable;
	
	public NPCManager(Game game, Player player) {
		this.game = game;
		this.player = player;
		
	}
	
	public void instantiateNPCs() {
		playerUpgradeNPC = new NPC(game, new Vector2D(1000, 1000), 50);
		playerUpgradeNPC.instantiate();
		craftingTable = new NPC(game, new Vector2D(1000, 300), 50);
		craftingTable.instantiate();
	}

	public NPC getPlayerUpgradeNPC() {
		return playerUpgradeNPC;
	}

	public NPC getCraftingTable() {
		return craftingTable;
	}
	
	public void closeAll() {
		this.playerUpgradeNPC.close();
		this.craftingTable.close();
	}
}
