package com.lucasj.gamedev.game.gamemodes.waves.missions;

import java.util.ArrayList;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.essentials.ui.broadcast.Broadcast;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public abstract class Mission {

	public static Mission activeMission;
	
	protected Game game;
	protected String name;
	protected String description;
	protected String descriptionToDisplay;
	protected String rewards;
	protected float length;
	
	protected Broadcast broadcast;
	
	protected int waveStart = 0;
	
	public Mission (Game game, String name, String description, String rewards, float length) {
		this.game = game;
		this.name = name;
		this.description = description;
		this.descriptionToDisplay = description;
		this.rewards = rewards;
		this.length = length;
		
		Mission.activeMission = this;
		
		this.broadcast = new Broadcast(game, name, descriptionToDisplay, new Vector2D(100, 100), new Vector2D(500, 250), length);
	}
	
	public void update(double deltaTime) {
		
		descriptionToDisplay = descriptionToDisplay.concat("{NL}{NL}{GREEN}" + rewards);
		
		if(broadcast != null) {
			broadcast.setSubText(descriptionToDisplay);
			broadcast.update(deltaTime);
		} else {
			Debug.log(this, "Updated before instance");
		}

		if(isAccomplished()) {
			game.getPlayer().addXp(100);
			reward();
			Mission.activeMission = null;
		}
	}
	
	public Render render() {
		if(broadcast != null) {
			return broadcast.render();
		} else {
			return null;
		}
	}
	
	public abstract boolean isAccomplished();
	public abstract void reward();

	public String getName() {
		return name;
	}
	
	public Broadcast getBroadcast() {
		return this.broadcast;
	}

	public int getWaveStart() {
		return waveStart;
	}

	public void setWaveStart(int waveStart) {
		this.waveStart = waveStart;
	}
}
