package com.lucasj.gamedev.game.multiplayer.packets;

import org.json.JSONException;
import org.json.JSONObject;

import com.lucasj.gamedev.game.multiplayer.GameClient;
import com.lucasj.gamedev.mathutils.Vector2D;

public class PacketManager {
	
	private GameClient client;
	
	public PacketManager(GameClient client) {
		this.client = client;
	}
	
	/***
	 * 
	 * Returns
	 * {
	 * 		username:
	 * 		auth_token:
	 * 		data {
	 * 	
	 * 		}	
	 * }
	 * @throws JSONException 
	 */
	public JSONObject getBasePacket(String packetType) throws JSONException {
        JSONObject basePacket = new JSONObject();
        basePacket.put("username", this.client.getUsername());
        basePacket.put("auth_token", this.client.getAuthToken());
        basePacket.put("packet_type", packetType);
        basePacket.put("data", new JSONObject()); // Empty data object

        return basePacket;
    }
	
	public void requestLoginPacket() {
        JSONObject basePacket = new JSONObject();
        try {
			basePacket.put("username", this.client.getUsername());
	        basePacket.put("auth_token", "none");
	        basePacket.put("packet_type", "login_request");

			client.sendData(basePacket.toString().getBytes());
	        
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void requestLogoffPacket() {
        JSONObject basePacket = new JSONObject();
        try {
			basePacket.put("username", this.client.getUsername());
	        basePacket.put("auth_token", this.client.getAuthToken());
	        basePacket.put("packet_type", "logging_off");

			client.sendData(basePacket.toString().getBytes());
	        
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void requestCreatePartyPacket() {
		try {
			JSONObject json = getBasePacket("create_party");
			
			json.getJSONObject("data").put("request", "create_party");
			
			client.sendData(json.toString().getBytes());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void requestJoinPartyPacket(String username) {
		try {
			JSONObject json = getBasePacket("join_party");
			
			json.getJSONObject("data").put("join_party", username);
			
			client.sendData(json.toString().getBytes());
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void partyGoingIntoGamePacket() {
		try {
			JSONObject json = this.getBasePacket("host_to_clients");
			
			json.getJSONObject("data").put("ingame", true);
			
			client.sendData(json.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void playerPickedClassPacket() {
		try {
			JSONObject json = this.getBasePacket("host_to_clients");
			JSONObject players = new JSONObject();
			
			players.put(client.getGame().party.getHost().getUsername(), !client.getGame().party.getHost().isPickingClass());
			this.client.getGame().party.getPlayers().forEach(player -> {
				try {
					players.put(player.getUsername(), !player.isPickingClass());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
			
			json.getJSONObject("data").put("players", players);
			
			client.sendData(json.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void playerPickedClass() {
	    try {
	        JSONObject json = this.getBasePacket("picked_class");
	        client.sendData(json.toString().getBytes());
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
	}

	
	public void playerInfoPacket(float health, float maxHealth, Vector2D position, int walkingImage) {
		try {
			JSONObject json = this.getBasePacket("host_to_clients");
			
			// Create player_position object
	        JSONObject playerPosition = new JSONObject();
	        playerPosition.put("username", client.getUsername());
	        playerPosition.put("walking_image", walkingImage);
	        playerPosition.put("x", position.getX());
	        playerPosition.put("y", position.getY());
	        
	        JSONObject playerHealth = new JSONObject();
	        playerHealth.put("username", client.getUsername());
	        playerHealth.put("current", health);
	        playerHealth.put("max", maxHealth);

	        // Add player_position to data
	        json.getJSONObject("data").put("player_position", playerPosition);
	        json.getJSONObject("data").put("player_health", playerHealth);
			
			client.sendData(json.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
