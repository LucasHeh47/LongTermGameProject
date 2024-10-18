package com.lucasj.gamedev.settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsManager {
    private Properties properties;
    private String configFilePath;

    public SettingsManager() {
        properties = new Properties();
        // Set the config file path based on the user directory
        configFilePath = System.getProperty("user.dir") + "/src/com/lucasj/gamedev/settings/config.properties";
        
        // Load properties from the file
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            // Optional: Create a default properties file if it doesn't exist
        }
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(configFilePath)) {
            properties.store(fos, "Game Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSetting(String key) {
        return properties.getProperty(key);
    }
    
    public void setStringSetting(String key, String value) {
        properties.setProperty(key, value);
    }

    public int getIntSetting(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;  // Return a default value or handle it as needed
        }
    }
    
    public void setIntSetting(String key, int value) {
        properties.setProperty(key, Integer.toString(value));
    }

    public boolean getBoolSetting(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }
}
