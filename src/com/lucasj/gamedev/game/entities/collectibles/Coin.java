package com.lucasj.gamedev.game.entities.collectibles;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.events.collectibles.CoinCollectedEvent;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Coin extends Collectible {

	private BufferedImage[] coinImages;
	private int currentFrame = 0;
	private int cashAmount;
	private float timeAlive;
	private long timeStarted;
	
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
		timeAlive += deltaTime;
		Vector2D direction = game.getPlayer().getPosition().subtract(this.position).normalize();
	    Vector2D velocity = direction.multiply(100 * deltaTime * (timeAlive*10));
	    
	    // Update position based on velocity
	    Vector2D newPosition = this.position.add(velocity);
	    this.position = newPosition;
	    
		if((System.currentTimeMillis() - this.lastAnimationTick)/1000.0 > this.animationTick) {
			currentFrame += 1;
			if(currentFrame >= coinImages.length) currentFrame = 0;
			lastAnimationTick = System.currentTimeMillis();
		}
		
	}
	
	@Override
	public List<Render> render() {
		List<Render> renders = new ArrayList<>();
		renders.add(new Render(Layer.Collectible, g -> {
			g.drawImage(coinImages[currentFrame], (int) screenPosition.getX(), (int) screenPosition.getY(), 30, 30, null);
		}));
		return renders;
	}

	@Override
	void collect(Player p) {
		game.instantiatedCollectibles.remove(this);
		CoinCollectedEvent e = new CoinCollectedEvent(cashAmount, p);
		game.getAudioPlayer().playSound("/Collectible/coin.wav", this.position);
		game.getEventManager().dispatchEvent(e);
	}

}
