package com.lucasj.gamedev.game.entities.placeables;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.enemy.Enemy;
import com.lucasj.gamedev.game.entities.placeables.data.LandmineEnemyDistanceData;
import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;

public class Landmine extends Placeable {
	
	private int radius = 500;
	private Image img;

	/**
	 * 
	 * @param game
	 * @param player
	 * @param position
	 * @param tag
	 */
	public Landmine(Game game, Player player, Vector2D position,
			String tag) {
		super(game, player, position, new Vector2D(), 1, 20, tag);
		this.size = 60;
		this.img = SpriteTools.getSprite(SpriteTools.assetDirectory + "Art/Placeables/landmine.png", new Vector2D(0, 0), new Vector2D(32, 32));
	}
	
	public void render(Graphics g) {
	    Graphics2D g2d = (Graphics2D) g;

	    g2d.drawImage(img, (int) this.getScreenPosition().getX(), (int) this.getScreenPosition().getY(), this.getSize(), this.getSize(), null);
	    
	}

	public void onEntityCollision(EntityCollisionEvent e) {
		if(e.getCollider() instanceof Enemy) {
			this.die();
			player.getActivePlaceables().remove(this);
		}
		
	}

	@Override
	public void entityDeath() {
	    Debug.log(this, "Collision");
	    
	    List<LandmineEnemyDistanceData> enemiesHit = Enemy.getEntitiesInLandmineRadius(game, position.add(this.size / 2), radius);
	    game.getAudioPlayer().playSound("Landmine/landmine.wav", this.position);
	    enemiesHit.forEach(data -> {
	        if (data.getEntity() instanceof Enemy) {
	            Enemy enemy = (Enemy) data.getEntity();
	            int distance = data.getDistance();

	            // Scale damage based on distance from the landmine center
	            double distanceFactor = (radius - distance) / (double) radius;
	            int baseDamage = (int) (10 * player.getPlayerUpgrades().getDamageMultiplier() * 5);
	            int damage = (int) (baseDamage * distanceFactor);

	            Debug.log(this, "Distance: " + distance + " Damage: " + damage);

	            // Apply damage and check if the enemy dies
	            boolean died = enemy.takeDamage(damage);
	            if (died) {
	                enemy.setKiller(this.player);
	            }
	        }
	    });
	}

	public int getRadius() {
		return radius;
	}

	@Override
	public Image getImage() {
		return img;
	}
	
		

}
