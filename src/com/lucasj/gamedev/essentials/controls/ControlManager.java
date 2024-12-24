package com.lucasj.gamedev.essentials.controls;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.input.ControllerEvent;
import com.lucasj.gamedev.events.input.KeyboardEventListener;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.mathutils.BiMap;
import com.lucasj.gamedev.misc.Debug;

public class ControlManager implements KeyboardEventListener, MouseClickEventListener {

	// Uses custom map class. Map does not allow duplicate keys or values.
	public BiMap<Controls, Integer> controlMap;
	
	private Controls isBinding = null;
	
	private Game game;
	
	public ControlManager(Game game) {
		this.game = game;
		controlMap = new BiMap<Controls, Integer>();
		this.setDefaults();
	}
	
	public void setDefaults() {
		controlMap.put(Controls.UP, KeyEvent.VK_W);
		controlMap.put(Controls.DOWN, KeyEvent.VK_S);
		controlMap.put(Controls.LEFT, KeyEvent.VK_A);
		controlMap.put(Controls.RIGHT, KeyEvent.VK_D);
		controlMap.put(Controls.SPRINT, KeyEvent.VK_SHIFT);
		controlMap.put(Controls.SPECIAL, KeyEvent.VK_F);
		controlMap.put(Controls.RELOAD, KeyEvent.VK_R);
		controlMap.put(Controls.PRIMARY, MouseEvent.BUTTON1);
		controlMap.put(Controls.SECONDARY, MouseEvent.BUTTON3);
		controlMap.put(Controls.SWAP, KeyEvent.VK_Q);
		controlMap.put(Controls.PLACE, KeyEvent.VK_1);
		controlMap.put(Controls.MAP, KeyEvent.VK_TAB);
		controlMap.put(Controls.PAUSE, KeyEvent.VK_ESCAPE);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(controlMap.getKey(e.getKeyCode()) != null) {
			game.getEventManager().dispatchEvent(new ControllerEvent(controlMap.getKey(e.getKeyCode()), true));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(controlMap.getKey(e.getKeyCode()) != null) {
			game.getEventManager().dispatchEvent(new ControllerEvent(controlMap.getKey(e.getKeyCode()), false));
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMousePressed(MouseEvent e) {
		if(controlMap.getKey(e.getButton()) != null) {
			game.getEventManager().dispatchEvent(new ControllerEvent(controlMap.getKey(e.getButton()), true));
		}
		
	}

	@Override
	public void onMouseReleased(MouseEvent e) {
		if(controlMap.getKey(e.getButton()) != null) {
			game.getEventManager().dispatchEvent(new ControllerEvent(controlMap.getKey(e.getButton()), false));
		}
		
	}
}
