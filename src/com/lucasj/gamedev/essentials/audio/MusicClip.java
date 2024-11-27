package com.lucasj.gamedev.essentials.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;

import com.lucasj.gamedev.essentials.Game;

public class MusicClip extends AudioClip {
	
	public MusicClip(Clip clip, Game game, float sound, String fileName) {
		super(clip, game, sound, fileName);
		FloatControl control = (FloatControl) clip.getControl(Type.MASTER_GAIN);
		control.setValue(getVolume() * sound * (game.getSettings().getIntSetting("music_volume")/100) * (game.getSettings().getIntSetting("master_volume")/100));
		clip.start();
	}

	@Override
	protected float getVolume() {
		
		return 0;
	}

}
