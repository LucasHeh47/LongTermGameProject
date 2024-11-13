package com.lucasj.gamedev.essentials.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

import com.lucasj.gamedev.essentials.Game;

public class MusicClip extends AudioClip {
	
	public MusicClip(Clip clip, Game game, float sound) {
		super(clip, game, sound);
		FloatControl control = (FloatControl) clip.getControl(Type.MASTER_GAIN);
		control.setValue(getVolume() * sound);
		clip.start();
	}

	@Override
	protected float getVolume() {
		
		return 0;
	}

}
