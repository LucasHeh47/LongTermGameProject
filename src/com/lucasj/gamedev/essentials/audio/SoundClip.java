package com.lucasj.gamedev.essentials.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class SoundClip extends AudioClip {

    private final Vector2D position;

    public SoundClip(Clip clip, Game game, Vector2D position, float sound) {
        super(clip, game, sound);
        this.position = position;

        // Get the MASTER_GAIN control for adjusting volume
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        // Calculate the initial volume based on distance and settings
        float distanceVolume = getVolume(); // Calculated volume based on position and distance
        float masterVolumePercent = game.getSettings().getIntSetting("master_volume"); // From 1 to 100
        float soundVolumePercent = game.getSettings().getIntSetting("sound_volume");   // From 1 to 100
        

        // Map the master and sound volume percentages to a decibel range from -80 dB to 0 dB
        float masterVolumeDb = mapVolumeToDecibels(masterVolumePercent);
        float soundVolumeDb = mapVolumeToDecibels(soundVolumePercent);
        
       // Debug.log(this, masterVolumeDb);

        // Combine volumes (distanceVolume is already a dB value)
        float combinedVolume = distanceVolume + masterVolumeDb + soundVolumeDb;

        Debug.log(this, combinedVolume);
        
        // Clamp combined volume between -80 dB (minimum) and 0 dB (maximum)
        combinedVolume = Math.max(-80f, Math.min(combinedVolume, 0f));

        // Set the volume and start the clip
        control.setValue(combinedVolume);
        clip.start();
    }

    // Helper method to map a percentage (1-100) to a decibel range (-80 dB to 0 dB)
    private float mapVolumeToDecibels(float percent) {
        if (percent == 0) {
            return -80f; // Mute if the percentage is 0
        }
        return (percent / 100f) * 80f - 80f; // Maps 1-100 to -80 to 0
    }

    @Override
    protected float getVolume() {
        if (this.position == null) {
            return 0;
        }

        float maxVolume = 0.0f;    // Maximum volume (in decibels)
        float minVolume = -80.0f;  // Minimum volume (muted, in decibels)
        float halfViewport = (float) (game.getCamera().getViewport().getX() * 2);

        // Calculate distance from player to sound position
        float distance = (float) game.getPlayer().getPosition().distanceTo(position) - 200;
        distance = Math.max(0, distance); // Ensure distance is non-negative

        // If the sound is beyond the halfViewport, it should be at minimum volume
        if (distance >= halfViewport) {
            return minVolume;
        }

        // Calculate the volume attenuation based on distance (inverse-linear scaling)
        float volume = maxVolume - (distance / halfViewport) * (maxVolume - minVolume);

        // Clamp volume within the range [minVolume, maxVolume]
        return Math.max(minVolume, Math.min(volume, maxVolume));
    }
}