package com.lucasj.gamedev.game.entities.collectibles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class Mana extends Collectible {
	private float timeAlive;
	private long timeStarted;
	
	public static final List<Color> colors = SpriteTools.createColorGradient(new Color(107, 214, 250), new Color(2, 121, 207), 16);
	private int colorIndex = 0;
	private boolean reversed;
	
	private Vector2D randomVelocity;

	public Mana(Game game, Vector2D position, Vector2D size) {
		super(game, position, size);
		Random rand = new Random();
		this.randomVelocity = Vector2D.fromAngleDegrees(rand.nextInt(0, 360)).multiply(rand.nextInt(200, 500));
		Debug.log(this, this.randomVelocity);
		this.colorIndex = rand.nextInt(0, 15);
		this.animationTick = 0.02f;
	}
	
	public void update(double deltaTime) {
		super.update(deltaTime);
		timeAlive += deltaTime;
		Vector2D direction = game.getPlayer().getPosition().subtract(this.position).normalize();
	    Vector2D velocity = direction.multiply(75 * deltaTime * (timeAlive*10));
	    
	    // Update position based on velocity
	    Vector2D newPosition = this.position.add(velocity);
	    if(timeAlive >= 0.3) this.position = newPosition;
	    
		if((System.currentTimeMillis() - this.lastAnimationTick)/1000.0 > this.animationTick) {
			int increment = reversed ? -1 : 1;
			colorIndex += increment;
			if(colorIndex >= 16) {
				colorIndex = 15;
				reversed = true;
			}
			if(colorIndex < 0) {
				colorIndex = 0;
				reversed = false;
			}
			lastAnimationTick = System.currentTimeMillis();
		}
		
		this.position = this.position.add(this.randomVelocity.multiply(deltaTime));
		this.randomVelocity = this.randomVelocity.multiply(0.99);
		
	}

	@Override
	public List<Render> render() {
		List<Render> renders = new ArrayList<>();
		renders.add(new Render(Layer.Collectible, g -> {
			g.setColor(colors.get(colorIndex));
			RoundRectangle2D roundedSquare = new RoundRectangle2D.Double((int) screenPosition.getX(), (int) screenPosition.getY(), 15, 15, 8, 8);
			((Graphics2D)g).fill(roundedSquare);
		}));
		return renders;
	}

	@Override
	void collect(Player p) {
		Random rand = new Random();
		p.addMana(rand.nextFloat(0, 1));
		game.getAudioPlayer().playSound("/Collectible/mana.wav", this.position);
		game.instantiatedCollectibles.remove(this);
	}

}
