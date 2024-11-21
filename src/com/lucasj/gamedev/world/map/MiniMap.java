package com.lucasj.gamedev.world.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class MiniMap {
	
	private Game game;
	
	private float updateTick = 1;
	private long lastUpdate;
	
	private List<Vector2D> entityPositions;
	
	public MiniMap(Game game) {
		this.game = game;
		entityPositions = new ArrayList<>();
	}
	
	public void update(double deltaTime) {
		if((System.currentTimeMillis() - lastUpdate)/1000.0 >= updateTick) loadPositions();
	}
	
	public void render(Graphics2D g2d) {
		Image frame = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/UI/frame.png", new Vector2D(0, 0), new Vector2D(32, 32));
		
		g2d.setColor(Color.DARK_GRAY.darker());
		g2d.fillRect(game.getWidth()-300, 50, 250, 250);
		
		g2d.setColor(Color.red);
		entityPositions.forEach(entity -> {
			g2d.fillOval((int) (game.getWidth()-300 + entity.getX()), (int) (50 + entity.getY()), 1, 1);
		});
		
		//g2d.drawImage(frame, game.getWidth()-300, 50, 250, 250, null);
		
	}
	
	private void loadPositions() {
		entityPositions.clear();
		game.instantiatedEntities.forEach(entity -> {
			if(entity instanceof Enemy) {
				Vector2D normalizedPosition = entity.getPosition().divide(4).divide(game.getMapManager().getWorldSize());
				Vector2D pos = normalizedPosition.multiply(250);
	
				//Debug.log(this, normalizedPosition.toString());
				entityPositions.add(pos);
			}
		});
	}

}
