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
