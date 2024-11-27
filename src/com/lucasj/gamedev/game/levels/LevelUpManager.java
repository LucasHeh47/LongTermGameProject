package com.lucasj.gamedev.game.levels;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.level.LevelUpEvent;
import com.lucasj.gamedev.events.level.LevelUpEventListener;
import com.lucasj.gamedev.game.weapons.Tier;

public class LevelUpManager implements LevelUpEventListener {

	public Game game;
	
	public LevelUpManager(Game game) {
		this.game = game;
		game.getEventManager().addListener(this, LevelUpEvent.class);
	}
	
	@Override
	public void onLevelUp(LevelUpEvent e) {
		if(e.getNewLevel() == 25) {
			Tier.setLastTier(Tier.Mythic);
		} else if (e.getNewLevel() == 40) {
			Tier.setLastTier(Tier.Divine);
		} else if (e.getNewLevel() == 65) {
			Tier.setLastTier(Tier.Ethereal);
		}
	}

	
	
}
