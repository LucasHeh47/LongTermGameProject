package com.lucasj.gamedev;

import com.lucasj.gamedev.essentials.Window;
import com.lucasj.gamedev.settings.SettingsManager;

public class Main {

	public static void main(String[] args) {
		SettingsManager settings = new SettingsManager();
		Window window = new Window("Game", settings.getIntSetting("width"), settings.getIntSetting("height"), settings);
		window.start();
	}

}

/**
 * 
 *  To Do:
 * - Create Enemies - WIP
 * - Work on Map
 * - Create a settings page with sliders and buttons
 * - Create Enemy Waves
 * - Create a currency
 * - Save and load data
 * - Move config to data directory
 * - Load and save config depending on if its in directory or not
 * - Look into multiplayer (peer to peer) | maybe wait for steam api (?)
 * 
 * 
 * Known Issues:
 * Game.java - Looping through each entity 3 times each frame - 1) update 2) collision event 3) rendering. Maybe in 1 loop update, check if that entity is colliding, move onto next then render after. (2 iterations is fine)
 */
