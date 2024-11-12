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
import com.lucasj.gamedev.game.multiplayer.packets.PacketManager;
import com.lucasj.gamedev.misc.Debug;

public class GameClient extends Thread {

	private InetAddress serverAddress;
	private DatagramSocket socket;
	private PacketManager packetManager;
	private Game game;
	
	public GameClient(Game game, String ipAddress) {
		this.game = game;
		packetManager = new PacketManager(this);
		try {
			this.socket = new DatagramSocket();
			this.serverAddress = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.readPacket(packet);
		}
	}
	
	public void readPacket(DatagramPacket packet) {
	    String message = new String(packet.getData()).trim();
	    
	    try {
	        JSONObject response = new JSONObject(message);

	        Debug.log(this, response.toString());
	    } catch (JSONException e) {
	        Debug.log(this, "Invalid JSON response: " + message);
	    }
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, 1331);
		try {
			socket.send(packet);
			Debug.log(this, "Packet sent!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
