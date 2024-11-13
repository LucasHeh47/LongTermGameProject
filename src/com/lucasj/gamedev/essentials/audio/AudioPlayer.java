package com.lucasj.gamedev.essentials.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class AudioPlayer {

	public static final String SFX_DIRECTORY = SpriteTools.assetDirectory + "SFX/";
	
	private List<AudioClip> audioClips;
	private Game game;
	
	public AudioPlayer(Game game) {
		this.game = game;
		this.audioClips = new ArrayList<>();
	}
	
	public void update() {
		audioClips.forEach(audioClip -> audioClip.update());
			
		List.copyOf(audioClips).forEach(audioClip -> {
			if(audioClip.hasFinishedPlaying()) {
				audioClip.cleanUp();
				audioClips.remove(audioClip);
			}
		});
	}
	
	public void playMusic(String fileName) {
		final Clip clip = getClip(AudioPlayer.SFX_DIRECTORY + fileName);
		audioClips.add(new MusicClip(clip, game, 1));
	}

	public void playSound(String fileName, Vector2D position) {
	    String fullPath = AudioPlayer.SFX_DIRECTORY + fileName;
	    final Clip clip = getClip(fullPath);
	    if (clip != null) {
	        audioClips.add(new SoundClip(clip, game, position, 1));
	    } else {
	        Debug.log(this, "Failed to load sound: " + fullPath);
	    }
	}
	
	public void playMusic(String fileName, float mult) {
		final Clip clip = getClip(AudioPlayer.SFX_DIRECTORY + fileName);
		audioClips.add(new MusicClip(clip, game, mult));
	}

	public void playSound(String fileName, Vector2D position, float mult) {
	    String fullPath = AudioPlayer.SFX_DIRECTORY + fileName;
	    final Clip clip = getClip(fullPath);
	    if (clip != null) {
	        audioClips.add(new SoundClip(clip, game, position, mult));
	    } else {
	        Debug.log(this, "Failed to load sound: " + fullPath);
	    }
	}

	private Clip getClip(String filePath) {
	    final File soundFile = new File(filePath);
	    try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile)) {
	        final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.setMicrosecondPosition(0);
	        return clip;
	    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
	        System.out.println(e);
	    }
	    return null;
	}
	
}
