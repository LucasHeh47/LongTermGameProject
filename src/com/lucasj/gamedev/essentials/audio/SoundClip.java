package com.lucasj.gamedev.essentials.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;

public class SoundClip extends AudioClip {

	private final Vector2D position;
	
	public SoundClip(Clip clip, Game game, Vector2D position, float sound) {
		super(clip, game, sound);
		this.position = position;
		FloatControl control = (FloatControl) clip.getControl(Type.MASTER_GAIN);
		control.setValue(getVolume() / sound);
		clip.start();
	}

	@Override
	protected float getVolume() {
	    if (this.position == null) {
	    	return 0; // Muted if position is not set
	    }

	    float maxVolume = -0.1f; // Full volume
	    float minVolume = -20f; // Minimum volume (muted)
	    float halfViewport = (float) (game.getCamera().getViewport().getX()*2);
	    float distance = (float) game.getPlayer().getPosition().distanceTo(position)-200;
	    
	    // If distance is greater than half the viewport, set volume to minimum
	    if (distance > halfViewport) {
	        return minVolume;
	    } else {
	        // Calculate volume based on distance, proportionally reducing volume as distance increases
	        float volume = maxVolume - (distance / halfViewport) * (maxVolume - minVolume);
	        return Math.max(minVolume, Math.min(volume, maxVolume)); // Clamp between minVolume and maxVolume
	    }
	}

}
