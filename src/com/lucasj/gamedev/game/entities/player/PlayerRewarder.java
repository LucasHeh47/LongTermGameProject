package com.lucasj.gamedev.game.entities.player;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.collectibles.CoinCollectedEvent;
import com.lucasj.gamedev.events.collectibles.CoinCollectedEventListener;
import com.lucasj.gamedev.events.entities.EntityDeathEvent;
import com.lucasj.gamedev.events.entities.EntityDeathEventListener;
import com.lucasj.gamedev.events.waves.WaveEndEvent;
import com.lucasj.gamedev.events.waves.WaveEndEventListener;
import com.lucasj.gamedev.misc.Debug;

public class PlayerRewarder implements EntityDeathEventListener, CoinCollectedEventListener, WaveEndEventListener {

	private Game game;
	
	public PlayerRewarder(Game game) {
		this.game = game;
		game.getEventManager().addListener(this, EntityDeathEvent.class);
		game.getEventManager().addListener(this, CoinCollectedEvent.class);
		game.getEventManager().addListener(this, WaveEndEvent.class);
		System.out.println("This is a constructor");
	}
	
	
	@Override
	public void onEntityDeath(EntityDeathEvent e) {
		
	}
	
	@Override
	public void onCoinCollect(CoinCollectedEvent e) {
		rewardCoinCollect(e.getPlayer(), e);
		Debug.log("TEST", "Coin Collected!");
	}
	
	@Override
	public void onWaveEnd(WaveEndEvent e) {
		if(e.getWaveEnded() % 5 == 0) game.getPlayer().addGem(2);
		else game.getPlayer().addGem(1);
	}
	
	public void rewardCoinCollect(Player p, CoinCollectedEvent e) {
		p.addMoney(e.getAmountCollected());
	}
	
}
