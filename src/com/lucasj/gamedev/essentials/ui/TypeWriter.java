package com.lucasj.gamedev.essentials.ui;

import com.lucasj.gamedev.essentials.Game;

public class TypeWriter {
	
	private String stringToType;
	private String finalString;
	
	private long lastType;
	private float typingSpeed;
	
	private int index;
	
	private boolean done;
	private Game game;
	
	public TypeWriter(Game game, String stringToType, float speed) {
		this.game = game;
		finalString = "";
		this.stringToType = stringToType;
		this.typingSpeed = speed;
		index = 0;
	}
	
	public void update(double deltaTime) {
		if(index >= stringToType.length()) done = true;
        if ((System.currentTimeMillis() - lastType)/1000.0 >= typingSpeed && !done) {
            char currentChar = stringToType.charAt(index);

            if (currentChar == '{') {
                // Handle placeholder
                int closeIndex = stringToType.indexOf('}', index);
                if (closeIndex != -1) {
                    // Add the full placeholder, including braces, to finalString
                    finalString = finalString.concat(stringToType.substring(index, closeIndex + 1));
                    index = closeIndex + 1; // Move past the closing brace
                } else {
                    // If no closing brace, treat as a normal character
                    finalString = finalString.concat(Character.toString(currentChar));
                    index++;
                }
            } else {
                // Add normal characters to the final string
                finalString = finalString.concat(Character.toString(currentChar));
                index++;
            }
        	game.getAudioPlayer().playSound("UI/type.wav", null);

            lastType = System.currentTimeMillis(); // Reset typing timer
        }
    }

    public String getCurrentString() {
        return finalString.toString(); // Return the current state of the string
    }
    
    public void setStringToType(String str) {
    	if(done) {
    		finalString = str;
    		return;
    	}
        if (index > str.length()) {
            // If index exceeds the length of the new string, reset it to the end of the new string
            index = str.length();
        }
        // Set finalString to the substring of the new string up to the current index
        finalString = str.substring(0, index);
        // Update stringToType to the new string
        stringToType = str;
    }

	public boolean isDone() {
		return done;
	}
	
	public void reset() {
		finalString = "";
		index = 0;
		done = false;
	}

}
