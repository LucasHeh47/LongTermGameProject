package com.lucasj.gamedev.game.multiplayer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.game.entities.player.Player;
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
		players = new ArrayList<>();
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
	
	public void update(double deltaTime) {
		if(host != game.getPlayer()) ((OnlinePlayer) host).update(deltaTime);
		players.forEach(player -> {
			if(player != game.getPlayer()) {
				((OnlinePlayer) player).update(deltaTime);
			}
		});
	}
	
	public List<Render> render() {
		List<Render> renders = new ArrayList<>();
		if(host != game.getPlayer()) renders.addAll(((OnlinePlayer) host).render());
		players.forEach(player -> {
			if(player != game.getPlayer()) {
				renders.addAll(((OnlinePlayer) player).render());
			}
		});
		return renders;
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
	
	public void updatePlayerPosition(String username, Vector2D position, int walkingImage) {
		if(host.getUsername().equals(username)) {
			host.setPosition(position);
			host.setWalkingImage(walkingImage);
		} else {
			for(PlayerMP p : this.getPlayers()) {
				p.setPosition(position);
				p.setWalkingImage(walkingImage);
			}
		}
	}
	
	public void updatePlayerHealth(String username, float health, float maxHealth) {
		if(host.getUsername().equals(username)) {
			host.setHealth(health);
			host.setMaxHealth(maxHealth);
		} else {
			for(PlayerMP p : this.getPlayers()) {
				p.setHealth(health);
				p.setMaxHealth(maxHealth);
			}
		}
	}
	
	public void removePlayer(String username) {
		players.remove(this.getPlayerByUsername(username));
		partySize -= 1;
	}
	
	public void addPlayer(PlayerMP player) {
		if(player instanceof Player) player.setColor(Color.blue);
		players.add(player);
		partySize++;
		((OnlinePlayer) player.getPlayer()).setColor(getColor(player));
	}
	
	public boolean haveAllPickedAClass() {
		boolean allPicked = true;
		for (PlayerMP player : players) {
			if(player.isPickingClass()) allPicked = false;
		}
		return allPicked;
	}

	public int getPartySize() {
		return partySize;
	}

	public void setPartySize(int partySize) {
		this.partySize = partySize;
	}
	
	public PlayerMP getPlayerByUsername(String username) {
		if(host.equals(username)) {
			return host;
		}
		for (PlayerMP p : players) {
			if(p.getUsername().equals(username)) return p;
		}
		return null;
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
