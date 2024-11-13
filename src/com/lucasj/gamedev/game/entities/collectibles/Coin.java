package com.lucasj.gamedev.game.entities.collectibles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.collectibles.CoinCollectedEvent;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Coin extends Collectible {

	private BufferedImage[] coinImages;
	private int currentFrame = 0;
	private int cashAmount;
	
	public Coin(Game game, Vector2D position, int cashAmount) {
		super(game, position, new Vector2D(Math.max(cashAmount, 75)));
		this.cashAmount = cashAmount;
		coinImages = new BufferedImage[4];
		
		for(int i = 0; i < 4; i++) {
			coinImages[i] = SpriteTools.getSprite(SpriteTools.assetPackDirectory + "Items/Treasure/Coin2.png", new Vector2D(i*10, 0), new Vector2D(10, 10));
		}
		
	}
	
	public void update(double deltaTime) {
		super.update(deltaTime);
		
		if((System.currentTimeMillis() - this.lastAnimationTick)/1000.0 > this.animationTick) {
			currentFrame += 1;
			if(currentFrame >= coinImages.length) currentFrame = 0;
			lastAnimationTick = System.currentTimeMillis();
		}
		
	}
	
	public void render(Graphics g) {
		//super.render(g);
		
		g.drawImage(coinImages[currentFrame], (int) screenPosition.getX(), (int) screenPosition.getY(), 30, 30, null);
		
	}

	@Override
	void collect(Player p) {
		game.instantiatedCollectibles.remove(this);
		CoinCollectedEvent e = new CoinCollectedEvent(cashAmount, p);
		game.getAudioPlayer().playSound("/Collectible/coin.wav", this.position);
		game.getEventManager().dispatchEvent(e);
	}

}
