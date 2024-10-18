package com.lucasj.gamedev.events;

import java.awt.event.KeyEvent;

public interface KeyboardEventListener {
	void keyPressed(KeyEvent e);
	void keyReleased(KeyEvent e);
	void keyTyped(KeyEvent e);
}
