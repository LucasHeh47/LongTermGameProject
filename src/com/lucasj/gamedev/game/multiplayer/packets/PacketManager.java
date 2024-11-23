package com.lucasj.gamedev.game.multiplayer.packets;

import org.json.JSONException;
import org.json.JSONObject;

import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.multiplayer.GameClient;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

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
	
	public void playerUpdateEnemyPacket(Enemy enemy) {
		try {
			JSONObject json = this.getBasePacket("clients_to_host");
			
			JSONObject enemyObj = new JSONObject();
			enemyObj.put("tag", enemy.getTag());
			enemyObj.put("health", enemy.getHealth());
			enemyObj.put("maxHealth", enemy.getMaxHealth());
			enemyObj.put("speed", enemy.getMovementSpeed());
			if(enemy.getAggrod() != null) enemyObj.put("Aggro", enemy.getAggrod().getUsername());
			JSONObject position = new JSONObject();
			position.put("x", enemy.getPosition().getX());
			position.put("y", enemy.getPosition().getY());
			enemyObj.put("position", position);
			enemyObj.put("type", enemy.getClass().getSimpleName());
			
			json.getJSONObject("data").put("enemy_update", enemyObj);
			
			client.sendData(json.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void playerKilledEnemyPacket(Enemy enemy) {
		try {
			JSONObject json = this.getBasePacket("clients_to_host");
			
			json.getJSONObject("data").put("killed_enemy", enemy.getTag());
			
			client.sendData(json.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void syncEnemiesPacket() {
		try {
			JSONObject json = this.getBasePacket("host_to_clients");
			
			JSONObject enemies = new JSONObject();
			for(Entity entity : client.getGame().instantiatedEntities) {
				if(entity instanceof Enemy) {
					Enemy enemy = (Enemy) entity;
					JSONObject enemyObj = new JSONObject();
					enemyObj.put("health", enemy.getHealth());
					enemyObj.put("maxHealth", enemy.getMaxHealth());
					enemyObj.put("speed", enemy.getMovementSpeed());
					if(enemy.getAggrod() != null) enemyObj.put("Aggro", enemy.getAggrod().getUsername());
					JSONObject position = new JSONObject();
					position.put("x", enemy.getPosition().getX());
					position.put("y", enemy.getPosition().getY());
					enemyObj.put("position", position);
					enemyObj.put("type", enemy.getClass().getSimpleName());
					
					enemies.put(enemy.getTag(), enemyObj);
				}
			}
			
			json.put("enemies", enemies);
			Debug.log(this, "Syncing Enemies");
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
	        playerPosition.put("walking_image", walkingImage);
	        playerPosition.put("x", position.getX());
	        playerPosition.put("y", position.getY());
	        
	        JSONObject playerHealth = new JSONObject();
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
