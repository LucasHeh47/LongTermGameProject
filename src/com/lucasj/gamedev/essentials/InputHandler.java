package com.lucasj.gamedev.essentials;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.events.KeyboardEventListener;
import com.lucasj.gamedev.events.MouseClickEventListener;
import com.lucasj.gamedev.events.MouseMotionEventListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

	private List<MouseClickEventListener> mouseClickListeners = new ArrayList<>();
	private List<MouseMotionEventListener> mouseMotionListeners = new ArrayList<>();
	private List<KeyboardEventListener> keyboardListeners = new ArrayList<>();
	
    public void addMouseClickListener(MouseClickEventListener listener) {
        mouseClickListeners.add(listener);
    }

    public void removeMouseClickListener(MouseClickEventListener listener) {
        mouseClickListeners.remove(listener);
    }
    
    public void addMouseMotionListener(MouseMotionEventListener listener) {
        mouseMotionListeners.add(listener);
    }

    public void removeMouseMotionListener(MouseMotionEventListener listener) {
        mouseMotionListeners.remove(listener);
    }
    
    public void addKeyboardListener(KeyboardEventListener listener) {
    	keyboardListeners.add(listener);
    }

    public void removeKeyboardListener(KeyboardEventListener listener) {
        keyboardListeners.remove(listener);
    }
	
    @Override
    public void keyPressed(KeyEvent e) {
    	for (KeyboardEventListener listener : keyboardListeners) {
            listener.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	for (KeyboardEventListener listener : keyboardListeners) {
            listener.keyReleased(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        for (KeyboardEventListener listener : keyboardListeners) {
            listener.keyTyped(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    	for (MouseClickEventListener listener : mouseClickListeners) {
            listener.onMouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    	for (MouseClickEventListener listener : mouseClickListeners) {
            listener.onMousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    	for (MouseClickEventListener listener : mouseClickListeners) {
            listener.onMouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    	for (MouseMotionEventListener listener : mouseMotionListeners) {
            listener.onMouseEntered(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    	for (MouseMotionEventListener listener : mouseMotionListeners) {
            listener.onMouseExited(e);
        }
    }

	@Override
	public void mouseDragged(MouseEvent e) {
    	for (MouseMotionEventListener listener : mouseMotionListeners) {
            listener.onMouseDragged(e);
        }
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
    	for (MouseMotionEventListener listener : mouseMotionListeners) {
            listener.onMouseMoved(e);
        }
		
	}
}
