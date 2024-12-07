package com.lucasj.gamedev.world.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.mathutils.Vector2D;

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
	
	public Render render() {
		
		Render render = new Render(Layer.UI, g -> {
			Graphics2D g2d = (Graphics2D) g;
			Image frame = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/UI/frame.png", new Vector2D(0, 0), new Vector2D(32, 32));
			
			g2d.setColor(Color.DARK_GRAY.darker());
			Image img = SpriteTools.getSprite((SpriteTools.assetDirectory + "Art/Maps/" + game.getMapManager().selectedMap + ".png"), new Vector2D(0, 0), new Vector2D(100));
			img = SpriteTools.tintGrayscaleImage(SpriteTools.toBufferedImage(img), new Color(50, 50, 50, 10));
			img = SpriteTools.setOpacity(SpriteTools.toBufferedImage(img), 0.75f);
			g2d.drawImage(img, game.getWidth()-300, 50, 250, 250, null);
			
			g2d.setColor(Color.red);
			entityPositions.forEach(entity -> {
				g2d.fillOval((int) (game.getWidth()-300 + entity.getX()), (int) (50 + entity.getY()), 1, 1);
			});
			
			g2d.setColor(Color.green);
			Vector2D pos = new Vector2D(clamp(game.getPlayer().getPosition().getXint(), game.getMapManager().getWorldSize().getXint(), 250), clamp(game.getPlayer().getPosition().getYint(), game.getMapManager().getWorldSize().getYint(), 250));
			g2d.fillOval(game.getWidth()-300 + pos.getXint(), (50 + pos.getYint()), 3, 3);
			
			//g2d.drawImage(frame, game.getWidth()-300, 50, 250, 250, null);
		});
		return render;
	}
	
	private void loadPositions() {
		entityPositions.clear();
		game.instantiatedEntities.forEach(entity -> {
			if(entity instanceof Enemy) {
				
				Vector2D pos = new Vector2D(clamp(entity.getPosition().getXint(), game.getMapManager().getWorldSize().getXint(), 250), clamp(entity.getPosition().getYint(), game.getMapManager().getWorldSize().getYint(), 250));
				//Debug.log(this, game.getMapManager().getWorldSize());
				//Debug.log(this, pos.toString());
				entityPositions.add(pos);
			}
		});
	}
	
	private int clamp(int x, int originalMax, int newMax) {
        int factor = originalMax / newMax;
        return x / factor;
    }

}
