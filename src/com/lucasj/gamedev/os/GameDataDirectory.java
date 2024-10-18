package com.lucasj.gamedev.os;

import java.io.File;

public class GameDataDirectory {
	public static File getGameDataDirectory(String gameTitle) {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        File gameDataDirectory;

        if (os.contains("win")) {
            // Windows, store in AppData\Roaming
            String appData = System.getenv("APPDATA");
            gameDataDirectory = new File(appData, "." + gameTitle);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // Linux/Mac, store in home directory
            gameDataDirectory = new File(userHome, "." + gameTitle);
        } else {
            // Default fallback to user home
            gameDataDirectory = new File(userHome, "." + gameTitle);
        }

        // Create directory if it doesn't exist
        if (!gameDataDirectory.exists()) {
            if (gameDataDirectory.mkdirs()) {
                System.out.println("Game data directory created: " + gameDataDirectory.getAbsolutePath());
            } else {
                System.err.println("Failed to create game data directory!");
            }
        } else {
            System.out.println("Game data directory already exists: " + gameDataDirectory.getAbsolutePath());
        }

        return gameDataDirectory;
    }
}
