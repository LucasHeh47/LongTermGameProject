package com.lucasj.gamedev.essentials.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;

public class GUI {
	
	private Game game;
	private Supplier<Boolean> decider;
	private Supplier<List<Button>> buttons;
	private List<Label> labels;
	private List<Slider> sliders;
	private Menus menus;
	private Panel panel;
	
	public GUI(Game game, Menus menus, Supplier<Boolean> decider, Supplier<List<Button>> buttons, Supplier<List<Slider>> sliders, Supplier<List<Label>> labels) {
		this.game = game;
		this.decider = decider;
		this.buttons = buttons;
		if(labels != null) this.labels = labels.get();
		if(sliders != null) {
			this.sliders = sliders.get();
			this.sliders.forEach(slider -> {
				slider.setDecidingFactor(decider);
			});
		}
		this.menus = menus;
		menus.addGUI(this);
	}
	
	public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
		if(decider.get()) {
			if (panel != null) {
                panel.render(g2d);   
            }
			if (sliders != null) for (Slider slider : sliders) {
				slider.render(g2d);
			}
			if (labels != null) for (Label label : labels) {
				label.render(g2d);
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
		if (sliders != null) {
			for (Slider slider : sliders) {
				slider.updatePositionWithPanel(panel);
			}
		}
		return this;
	}

	public List<Slider> getSliders() {
		return sliders;
	}
}
