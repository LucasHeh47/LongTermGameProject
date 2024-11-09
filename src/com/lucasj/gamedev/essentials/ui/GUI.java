package com.lucasj.gamedev.essentials.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;

public class GUI {
	
	private Game game;
	private Supplier<Boolean> decider;
	private Supplier<List<Button>> buttons;
	private Menus menus;
	private Panel panel;
	
	public GUI(Game game, Menus menus, Supplier<Boolean> decider, Supplier<List<Button>> buttons) {
		this.game = game;
		this.decider = decider;
		this.buttons = buttons;
		this.menus = menus;
		menus.addGUI(this);
	}
	
	public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
		if(decider.get()) {
			if (panel != null) {
                panel.render(g2d);
            }
			for (Button button : buttons.get()) {
				button.render((Graphics2D)g, game.font);
			}
		}
	}

	public Supplier<Boolean> getDecider() {
		return decider;
	}

	public Supplier<List<Button>> getButtons() {
		// TODO Auto-generated method stub
		return buttons;
	}

	public Panel getPanel() {
		return panel;
	}

	public GUI setPanel(Panel panel) {
		this.panel = panel;
		buttons.get().forEach(btn -> {
			btn.setPosition(panel.getX() + btn.getX(), panel.getY() + btn.getY());
		});
		return this;
	}

}
