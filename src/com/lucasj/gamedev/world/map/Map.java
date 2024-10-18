package com.lucasj.gamedev.world.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Random;

public class Map {

	public static HashMap<String, Map> maps = new HashMap<String, Map>();
	
	private int width, height;
	private MapManager mapm;
	
	public Map(MapManager mapm, int width, int height) {
		this.height = height;
		this.width = width;
		this.mapm = mapm;
	}
	
	public void render(Graphics g) {
		for(int i = 0; i < width*mapm.getTileSize(); i += mapm.getTileSize()) {
			for (int j = 0; j < height*mapm.getTileSize(); j += mapm.getTileSize()) {
				Graphics2D g2d = (Graphics2D)g;
				
				Random rand = new Random();
				g2d.setColor(new Color(rand.nextInt(1), rand.nextInt(200, 210), rand.nextInt(1)));
				g2d.fillRect(i, j, mapm.getTileSize(), mapm.getTileSize());
			}
		}
	}

}
