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
	
	public void partyGoingIntoGamePacket(Vector2D position) {
		try {
			JSONObject json = this.getBasePacket("host_to_clients");
			
			json.getJSONObject("data").put("ingame", true);
			
			client.sendData(json.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void playerPositionPacket(Vector2D position) {
		try {
			JSONObject json = this.getBasePacket("host_to_clients");
			
			json.getJSONObject("data").put("player_position", new JSONObject());

			json.getJSONObject("player_position").put("username", client.getUsername());
			json.getJSONObject("player_position").put("x", position.getX());
			json.getJSONObject("player_position").put("y", position.getY());
			
			client.sendData(json.toString().getBytes());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/*
	 * Make request packets and send here, redo server code to make this acceptable and just overall make server code better
	 */
}
