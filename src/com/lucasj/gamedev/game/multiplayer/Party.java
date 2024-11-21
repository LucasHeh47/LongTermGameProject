package com.lucasj.gamedev.game.multiplayer;

import java.awt.Color;
import java.util.List;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.player.multiplayer.OnlinePlayer;
import com.lucasj.gamedev.game.entities.player.multiplayer.PlayerMP;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Party {

	private List<PlayerMP> players;
	
	private static int maxSize = 4;
	private int partySize;
	private boolean ingame = false;
	private PlayerMP host;
	
	private Game game;
	
	public Party(Game game) {
		this.game = game;
	}

	public List<PlayerMP> getPlayers() {
		return players;
	}

	public void setPlayers(List<PlayerMP> players) {
		this.players = players;
	}
	
	public boolean handlePartyJoinRequest(String username) {
		if(partySize < maxSize) {
			boolean inParty = false;
			for(PlayerMP player : players) {
				if(player.getUsername().equals(username)) inParty = true;
			}
			if(inParty) return false;
			addPlayer(new OnlinePlayer(game, null, username, new Vector2D(0, 0)));
		}
		return false;
	}
	
	public Color getColor(PlayerMP player) {
        if (player.equals(host)) {
            return Color.BLUE; // Host is blue
        }

        int playerIndex = players.indexOf(player);
        switch (playerIndex) {
            case 0:
                return Color.GREEN; // First player is green
            case 1:
                return Color.YELLOW; // Second player is yellow
            case 2:
                return Color.ORANGE; // Third player is orange
            default:
                return Color.GRAY; // Default fallback color
        }
    }
	
	public void addPlayer(PlayerMP player) {
		players.add(player);
		((OnlinePlayer) player.getPlayer()).setColor(getColor(player));
	}

	public int getPartySize() {
		return partySize;
	}

	public void setPartySize(int partySize) {
		this.partySize = partySize;
	}

	public boolean isInGame() {
		return ingame;
	}

	public void setInGame(boolean ingame) {
		this.ingame = ingame;
	}

	public PlayerMP getHost() {
		return host;
	}

	public void setHost(PlayerMP host) {
		this.host = host;
	}
	
	
	
}
