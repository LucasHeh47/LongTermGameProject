package com.lucasj.gamedev.game.levels;

import java.awt.Color;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.broadcast.Broadcast;
import com.lucasj.gamedev.events.level.LevelUpEvent;
import com.lucasj.gamedev.events.level.LevelUpEventListener;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.game.weapons.Tier;
import com.lucasj.gamedev.mathutils.Vector2D;

public class LevelUpManager implements LevelUpEventListener {

	public Game game;
	
	public LevelUpManager(Game game) {
		this.game = game;
		game.getEventManager().addListener(this, LevelUpEvent.class);
	}
	
	@Override
	public void onLevelUp(LevelUpEvent e) {
		game.addBroadcast(new Broadcast(game, 
				"Level " + (Player.getGlobalStats().getLevel()-1) + " > " + Player.getGlobalStats().getLevel(), 
				"", 
				new Vector2D(-300, game.getHeight()/4),
				new Vector2D(300, 75), 2)
				.setBackgroundColor(new Color(255, 252, 82))
				.setBorderColor(new Color(192, 190, 78))
				.setTextColor(Color.DARK_GRAY.darker()));
		
		
	}

	
	
}
