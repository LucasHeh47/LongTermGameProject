package com.lucasj.gamedev.game.multiplayer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.GameState;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.player.multiplayer.OnlinePlayer;
import com.lucasj.gamedev.game.multiplayer.packets.PacketManager;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class GameClient extends Thread {

    private InetAddress serverAddress;
    private DatagramSocket socket;
    private PacketManager packetManager;
    private Game game;
    private boolean running = true; // Controls the thread's lifecycle

    public GameClient(Game game, String ipAddress) {
        this.game = game;
        packetManager = new PacketManager(this);
        try {
            this.socket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException e) {
            Debug.log(this, "Failed to create DatagramSocket: " + e.getMessage());
            e.printStackTrace();
        } catch (UnknownHostException e) {
            Debug.log(this, "Unknown host: " + ipAddress + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void run() {
        Debug.log(this, "Client started.");
        while (running) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
                this.readPacket(packet);
            } catch (IOException e) {
                Debug.log(this, "Socket receive error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public void readPacket(DatagramPacket packet) {
        String message = new String(packet.getData()).trim();

        try {
            JSONObject response = new JSONObject(message);
            Debug.log(this, "Received: " + response.toString());

            // Check for packet type and process accordingly
            String packetType = response.optString("packet_type", "");
            switch (packetType) {
                case "host_to_clients":
                    processForwardedPacket(response);
                    break;
                case "clients_to_host":
                	processListeningPacket(response);
                case "response_packet":
                    handleResponseAsHost(response);
                    break;
                default:
                    break;
            }
            
            if (response.has("picking_class")) {
                Debug.log(this, "All players are ready. Starting the game...");
                game.getWavesManager().startWaves();
            }
            
            if (response.has("party")) {
                Object partyObject = response.get("party");

                if (partyObject instanceof JSONObject) {
                    JSONObject status = (JSONObject) partyObject;

                    if (status.has("party_created")) {
                        Debug.log(this, "Party created");
                        game.party = new Party(game);
                        game.party.setHost(game.getPlayer());
                    }
                    
                    if (status.has("player_joined")) {
                        OnlinePlayer player = new OnlinePlayer(game, null, status.getString("player_joined"), new Vector2D(0, 0));
                        Debug.log(this, player.getUsername() + " has joined the party!");
                        game.party.addPlayer(player);
                    }

                    if (status.has("join_success")) {
                        game.party = new Party(game);
                        Debug.log(this, game.party == null);
                        String hostUsername = status.getString("join_success");
                        Debug.log(this, "Host: " + hostUsername);
                        OnlinePlayer host = new OnlinePlayer(game, null, hostUsername, new Vector2D(0, 0));
                        game.party.setHost(host);
                    }
                } else if (partyObject instanceof String) {
                    String partyStatus = (String) partyObject;

                    if (partyStatus.equals("party_created")) {
                        Debug.log(this, "Party created (string)");
                        game.party = new Party(game);
                        game.party.setHost(game.getPlayer());
                    } else {
                        Debug.log(this, "Unknown party string status: " + partyStatus);
                    }
                } else {
                    Debug.log(this, "Unexpected type for 'party': " + partyObject.getClass().getName());
                }
            }

            // Update auth_token if provided
            if (response.has("new_auth_token")) {
                String authToken = response.getString("new_auth_token");
                game.authToken = authToken; // Save auth token
                Debug.log(this, "Auth token updated: " + authToken);
            }

        } catch (JSONException e) {
            Debug.log(this, "Invalid JSON response: " + message);
            e.printStackTrace();
        }
    }
    
    private void processListeningPacket(JSONObject packet) throws JSONException {
        JSONObject data = packet.optJSONObject("data");
        if (data != null) {
        	
        	if(data.has("enemy_update")) {
        		JSONObject enemyObj = data.getJSONObject("enemy_update");
        		
                // Extract enemy data from JSON
                String tag = enemyObj.getString("tag");
                String type = enemyObj.getString("type");
                float health = (float) enemyObj.getDouble("health");
                float maxHealth = (float) enemyObj.getDouble("maxHealth");
                float speed = (float) enemyObj.getDouble("speed");
                String aggroUsername = enemyObj.optString("Aggro", null); // Null if not present
                JSONObject position = enemyObj.getJSONObject("position");
                double x = position.getDouble("x");
                double y = position.getDouble("y");
                
                try {
                    // Create the enemy using the mapping and update its properties
                    Enemy enemy = Enemy.enemyClassMap.get(type).getDeclaredConstructor(Game.class).newInstance(game);

                    enemy.setTag(tag);
                    enemy.setPosition(new Vector2D(x, y));
                    enemy.setHealth(health);
                    enemy.setMaxHealth(maxHealth);
                    enemy.setMovementSpeed((int) speed);

                    if (aggroUsername != null) {
                        enemy.setAggrod(game.party.getPlayerByUsername(aggroUsername));
                    }

                    // Replace the existing enemy with the same tag (if any)
                    game.instantiatedEntities.remove(Enemy.getEnemyByTag(game, tag));
                    game.instantiatedEntities.add(enemy);

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    Debug.log(this, "Failed to create or update enemy: " + e.getMessage());
                    e.printStackTrace();
                }
        	}
        	
        	if(data.has("killed_enemy")) {
        		game.instantiatedEntities.remove(Enemy.getEnemyByTag(game, data.getString("killed_enemy")));
        	}
        	
        }
    }

    private void processForwardedPacket(JSONObject packet) throws JSONException {
        // Handle forwarded packets from the server
        JSONObject data = packet.optJSONObject("data");
        if (data != null) {
            Debug.log(this, "Processing forwarded packet data: " + data.toString());
            // Example: Update player positions
            
            if(data.has("ingame")) {
                game.setGameState(GameState.waves);
                game.getMapManager().map.generateMap();
                //game.instantiatePlayer();
                game.getMenus().createGUIs();
            }
            
            if(data.has("enemies")) {
            	JSONObject enemies = data.getJSONObject("enemies");
                Iterator<String> keys = enemies.keys(); // Use the iterator for keys
                
                List<Enemy> newEnemies = new ArrayList<>();
                
                while (keys.hasNext()) {
                    String tag = keys.next();
                    JSONObject enemyObj = enemies.getJSONObject(tag);
                    
                    // Extract enemy data from JSON
                    String type = enemyObj.getString("type");
                    float health = (float) enemyObj.getDouble("health");
                    float maxHealth = (float) enemyObj.getDouble("maxHealth");
                    float speed = (float) enemyObj.getDouble("speed");
                    String aggroUsername = enemyObj.optString("Aggro", null); // Null if not present
                    JSONObject position = enemyObj.getJSONObject("position");
                    double x = position.getDouble("x");
                    double y = position.getDouble("y");

                    // Debug log for each enemy
                    Debug.log(this, "Processing enemy: Tag=" + tag + ", Type=" + type + ", Position=(" + x + ", " + y + ")");
                    
                    Enemy enemy;
					try {
						enemy = Enemy.enemyClassMap.get(tag).getDeclaredConstructor().newInstance(game);
	                    enemy.setAggrod(game.party.getPlayerByUsername(aggroUsername));
	                    enemy.setPosition(new Vector2D(x, y));
	                    enemy.setHealth(health);
	                    enemy.setMaxHealth(maxHealth);
	                    enemy.setMovementSpeed((int) speed);
	                    
	                    newEnemies.add(enemy);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
                }
            }
            
            if (data.has("player_position")) {
                JSONObject position = data.getJSONObject("player_position");
                Debug.log(this, "Position: " + position);
                String username = packet.getString("username");
                double x = position.getDouble("x");
                double y = position.getDouble("y");
                int walkingImage = position.getInt("walking_image");

                // Update the player's position in the party
                game.party.updatePlayerPosition(username, new Vector2D(x, y), walkingImage);

                // Log if the update is from the host
                if (username.equals(game.username)) {
                    Debug.log(this, "Updated host position: (" + x + ", " + y + ")");
                }
            }

            
            if (data.has("player_health")) {
                JSONObject healthObject = data.getJSONObject("player_health");
                Debug.log(this, "Health: " + healthObject);
                float health = (float) healthObject.optDouble("health", -100);
                float maxHealth = (float) healthObject.optDouble("max", -100);
                
                game.party.updatePlayerHealth(packet.getString("username"), 
                								health,
                								maxHealth);
                
            }
        }
    }

    private void handleResponseAsHost(JSONObject packet) throws JSONException {
        // Handle responses when this client is the host
        JSONObject data = packet.optJSONObject("data");
        if (data != null) {
            Debug.log(this, "Processing host response: " + data.toString());
            if (data.has("status")) {
                String status = data.getString("status");
                if ("party_created".equals(status)) {
                    Debug.log(this, "Party created successfully!");
                } else if ("join_success".equals(status)) {
                    Debug.log(this, "Player joined the party.");
                }
            }
        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, 1331);
        try {
            socket.send(packet);
            Debug.log(this, "Packet sent to server.");
        } catch (IOException e) {
            Debug.log(this, "Error sending packet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stopClient() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        Debug.log(this, "Socket closed and client stopped.");
    }

    public String getUsername() {
        return game.username;
    }

    public String getAuthToken() {
        return game.authToken;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }
    
    public Game getGame() {
    	return game;
    }
}
