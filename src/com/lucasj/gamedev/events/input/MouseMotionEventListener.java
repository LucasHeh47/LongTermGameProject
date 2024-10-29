package com.lucasj.gamedev.events.input;

import java.awt.event.MouseEvent;

public interface MouseMotionEventListener {
	void onMouseDragged(MouseEvent e);
	void onMouseMoved(MouseEvent e);
	void onMouseEntered(MouseEvent e);
	void onMouseExited(MouseEvent e);
}
