package com.lucasj.gamedev.world.tiles;

import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.essentials.Game;

public class Biome {
	
	private Game game;
	
	private List<Tile> grass;
	private List<Tile> trees;
	
	private int height;
	
	public Biome(Game game, int height) {
		this.game = game;
		this.height = height;
		grass = new ArrayList<>();
		trees = new ArrayList<>();
	}
	
	public Biome addGrassTiles(Tile...tiles) {
		for(Tile tile : tiles) {
			grass.add(tile);
		}
		return this;
	}
	
	public Biome addTreeTiles(Tile...tiles) {
		for(Tile tile : tiles) {
			trees.add(tile);
		}
		return this;
	}
	
	public int getHeight() {
		return height;
	}

	public List<Tile> getGrass() {
		return grass;
	}

	public List<Tile> getTrees() {
		return trees;
	}
	
}
