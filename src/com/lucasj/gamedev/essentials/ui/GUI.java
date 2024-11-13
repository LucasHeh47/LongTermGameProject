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
	private Supplier<List<Slider>> sliders;
	private Menus menus;
	private Panel panel;
	
	public GUI(Game game, Menus menus, Supplier<Boolean> decider, Supplier<List<Button>> buttons, Supplier<List<Slider>> sliders) {
		this.game = game;
		this.decider = decider;
		this.buttons = buttons;
		this.sliders = sliders;
		this.menus = menus;
		menus.addGUI(this);
	}
	
	public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
		if(decider.get()) {
			if (panel != null) {
                panel.render(g2d);   
            }
			if (sliders != null) for (Slider slider : sliders.get()) {
				slider.render(g2d);
			}
			if(buttons != null) for (Button button : buttons.get()) {
				if(!button.adjustedPositionWithPanel && panel != null) button.updatePositionWithPanel(panel);
				button.render((Graphics2D)g);
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
		for (Button button : buttons.get()) {
			button.updatePositionWithPanel(panel);
		}
		return this;
	}

	public Supplier<List<Slider>> getSliders() {
		return sliders;
	}
}
