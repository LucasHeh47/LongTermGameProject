package com.lucasj.gamedev.game.multiplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import com.lucasj.gamedev.essentials.Game;
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
        Debug.log(this, "Client stopped.");
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
                case "response_packet":
                    handleResponseAsHost(response);
                    break;
                default:
                    Debug.log(this, "Unknown packet type: " + packetType);
                    break;
            }
            
            if(response.has("party_created")) {
            	game.party = new Party(game);
            	game.party.setHost(game.getPlayer());
            }

            // Update auth_token if provided
            if (response.has("auth_token")) {
                String authToken = response.getString("auth_token");
                game.authToken = authToken; // Save auth token
                Debug.log(this, "Auth token updated: " + authToken);
            }

        } catch (JSONException e) {
            Debug.log(this, "Invalid JSON response: " + message);
        }
    }

    private void processForwardedPacket(JSONObject packet) throws JSONException {
        // Handle forwarded packets from the server
        JSONObject data = packet.optJSONObject("data");
        if (data != null) {
            Debug.log(this, "Processing forwarded packet data: " + data.toString());
            // Example: Update player positions
            if (data.has("player_position")) {
                JSONObject position = data.getJSONObject("player_position");
                double x = position.optDouble("x", 0);
                double y = position.optDouble("y", 0);
                Debug.log(this, "Updated player position: (" + x + ", " + y + ")");
                
                game.party.getPlayers().forEach(player -> {
                	try {
						if(player.getUsername().equals(position.get("username"))) {
							((OnlinePlayer) player).setPosition(new Vector2D(x, y));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
                });
                
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
}
