package com.lucasj.gamedev.game.entities.npc;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;

public class NPCManager {

	private Game game;
	private Player player;
	
	private NPC playerUpgradeNPC;
	private NPC craftingTable;
	private NPC gunsmith;
	private NPC mission;
	
	public NPCManager(Game game, Player player) {
		this.game = game;
		this.player = player;
		
	}
	
	public void instantiateNPCs() {
		playerUpgradeNPC = new NPC(game, new Vector2D(53*game.getMapManager().getTileSize(), 8 * game.getMapManager().getTileSize()), 50);
		playerUpgradeNPC.instantiate();
		craftingTable = new NPC(game, new Vector2D(91*game.getMapManager().getTileSize(), 46 * game.getMapManager().getTileSize()), 50);
		craftingTable.instantiate();
		gunsmith = new NPC(game, new Vector2D(14*game.getMapManager().getTileSize(), 50 * game.getMapManager().getTileSize()), 50);
		gunsmith.instantiate();
		mission = new NPC(game, new Vector2D(14*game.getMapManager().getTileSize(), 14 * game.getMapManager().getTileSize()), 50);
		mission.instantiate();
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
		this.gunsmith.close();
		this.mission.close();
	}
	
	public boolean isAnyOpen() {
		return this.playerUpgradeNPC.isOpen() || this.craftingTable.isOpen() || gunsmith.isOpen() || this.mission.isOpen();
	}

	public NPC getGunsmith() {
		return gunsmith;
	}
	
	public NPC getMission() {
		return mission;
	}
}