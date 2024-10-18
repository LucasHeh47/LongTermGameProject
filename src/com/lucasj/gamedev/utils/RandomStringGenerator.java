package com.lucasj.gamedev.utils;

import java.util.Random;

public class RandomStringGenerator {

	public static String generateRandomString(int length, String characters) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
	
}
