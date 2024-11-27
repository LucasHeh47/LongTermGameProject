package com.lucasj.gamedev.essentials.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

import com.lucasj.gamedev.essentials.Game;

public abstract class AudioClip {

	protected float soundMultiplier = 1.0f;

	private final Clip clip;
	private String fileName;
	protected Game game;
	
	public AudioClip(Clip clip, Game game, float soundMultiplier, String fileName) {
		this.clip = clip;
		this.game = game;
		this.fileName = fileName;
	}
	
	public void update() {
	}
	
	protected abstract float getVolume();
	
	public boolean hasFinishedPlaying() {
		return !clip.isRunning();
	}
	
	public void cleanUp() {
		clip.close();
	}
	
	public Clip getClip() {
		return clip;
	}
	
	public String getFileName() {
		return fileName;
	}
}

