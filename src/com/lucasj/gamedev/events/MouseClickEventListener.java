package com.lucasj.gamedev.events;

import java.awt.event.MouseEvent;

public interface MouseClickEventListener {
	void onMouseClicked(MouseEvent e);
	void onMousePressed(MouseEvent e);
	void onMouseReleased(MouseEvent e);
}