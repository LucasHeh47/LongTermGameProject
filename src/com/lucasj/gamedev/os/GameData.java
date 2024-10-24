package com.lucasj.gamedev.os;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.lucasj.gamedev.game.entities.player.PlayerStats;

public class GameData {
    private static final String ALGORITHM = "AES";
    private static final int GCM_IV_LENGTH = 12; // Recommended length for GCM IV
    private static final int GCM_TAG_LENGTH = 128; // Authentication tag length
    private static final String KEY_FILE = "encryption.key";
    private static final String MAC_ALGORITHM = "HmacSHA256";
    private File gameDataFile;

    public GameData(String gameTitle, String fileName) {
        File gameDataDir = GameDataDirectory.getGameDataDirectory(gameTitle);
        this.gameDataFile = new File(gameDataDir, fileName);
    }

    private SecretKey getKey(File keyFile) throws Exception {
        if (!keyFile.exists()) {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                fos.write(secretKey.getEncoded());
            }
            return secretKey;
        } else {
            byte[] keyBytes = new byte[32];
            try (FileInputStream fis = new FileInputStream(keyFile)) {
                fis.read(keyBytes);
            }
            return new SecretKeySpec(keyBytes, ALGORITHM);
        }
    }

    private SecretKey getMacKey(File keyFile) throws Exception {
        // Using the same key for HMAC, but you can derive another key if necessary
        return getKey(keyFile);
    }

    // Save the PlayerStats object securely
    public void savePlayerStats(PlayerStats stats) {
        try {
            File keyFile = new File(gameDataFile.getParentFile(), KEY_FILE);
            SecretKey secretKey = getKey(keyFile);
            SecretKey macKey = getMacKey(keyFile);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            // Serialize the PlayerStats object
            byte[] encryptedData;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(stats);
                oos.flush();
                encryptedData = cipher.doFinal(baos.toByteArray());
            }

            // Calculate the HMAC for integrity
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(macKey);
            byte[] macBytes = mac.doFinal(encryptedData);

            // Store IV, HMAC, and encrypted data together
            try (FileOutputStream fos = new FileOutputStream(gameDataFile)) {
                fos.write(iv); // Write IV first
                fos.write(macBytes); // Write HMAC next
                fos.write(encryptedData); // Write encrypted data
            }

            System.out.println("Player stats saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to save player stats.");
        }
    }

    // Load the PlayerStats object securely
    public PlayerStats loadPlayerStats() {
        if (!gameDataFile.exists()) {
            System.out.println("No save file found. Initializing with default player stats.");
            return null;
        }

        try {
            File keyFile = new File(gameDataFile.getParentFile(), KEY_FILE);
            SecretKey secretKey = getKey(keyFile);
            SecretKey macKey = getMacKey(keyFile);

            try (FileInputStream fis = new FileInputStream(gameDataFile)) {
                byte[] iv = new byte[GCM_IV_LENGTH];
                fis.read(iv);

                byte[] macBytes = new byte[32]; // HMAC-SHA256 produces 32 bytes
                fis.read(macBytes);

                byte[] encryptedData = fis.readAllBytes();

                // Validate the HMAC before decrypting
                Mac mac = Mac.getInstance(MAC_ALGORITHM);
                mac.init(macKey);
                byte[] calculatedMac = mac.doFinal(encryptedData);
                if (!Arrays.equals(macBytes, calculatedMac)) {
                    throw new SecurityException("Data integrity check failed. The file may have been tampered with.");
                }

                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

                byte[] decryptedData = cipher.doFinal(encryptedData);

                try (ByteArrayInputStream bais = new ByteArrayInputStream(decryptedData);
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    return (PlayerStats) ois.readObject();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load player stats.");
            return null;
        }
    }
}