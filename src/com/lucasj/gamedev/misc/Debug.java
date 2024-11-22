package com.lucasj.gamedev.misc;

import com.lucasj.gamedev.game.multiplayer.GameClient;

public class Debug {

	public static void log(String type, String message) {
		System.out.println("[" + type + "] " + message);
	}
	
	public static void log(Object obj, Object message) {
		if(obj.getClass() != GameClient.class) return;
		System.out.println("[" + obj.getClass().getSimpleName().toUpperCase() + "] " + message);
	}
	
}
