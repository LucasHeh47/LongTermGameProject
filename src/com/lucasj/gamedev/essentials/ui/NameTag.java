package com.lucasj.gamedev.essentials.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;

public class NameTag {

	private Game game;
	private String username;
	private Vector2D position;
	
	public NameTag(Game game, String username, Vector2D entityPosition) {
		this.game = game;
		this.username = username;
		this.position = entityPosition;
	}
	
	public Render render() {
		return new Render(Layer.UI, g -> {
			g.setFont(game.font.deriveFont(16f));
			int titleWidth = g.getFontMetrics().stringWidth(username);
			int margin = 15;
			int padding = 5;
			int x = position.getXint() - (titleWidth/2);
			int y = position.getYint() - margin;
			Graphics2D g2d = (Graphics2D) g;
			Color bgColor = Color.DARK_GRAY;
			
    		g.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 150));
            g2d.fill(new RoundRectangle2D.Float(x-padding, y-margin, titleWidth+(padding*2), 23, 5, 5));
            g.setColor(Color.LIGHT_GRAY);
            g2d.draw(new RoundRectangle2D.Float(x-padding-2, y-margin-2, titleWidth+(padding*2)+4, 23+4, 5, 5));
			g.setColor(GameColors.colors.WHITE.getValue());
			g.drawString(username, x, y);
		});
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}
	
}
