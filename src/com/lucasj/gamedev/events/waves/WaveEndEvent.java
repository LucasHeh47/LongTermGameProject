package com.lucasj.gamedev.events.waves;

import com.lucasj.gamedev.events.GameEvent;

public class WaveEndEvent implements GameEvent {

	private int waveEnded;
	
	public WaveEndEvent(int waveEnded) {
		this.setWaveEnded(waveEnded);
	}
	
	
	@Override
	public void setCancelled(boolean isCancelled) {
		// TODO Auto-generated method stub
		
	}


	public int getWaveEnded() {
		return waveEnded;
	}


	public void setWaveEnded(int waveEnded) {
		this.waveEnded = waveEnded;
	}

}
