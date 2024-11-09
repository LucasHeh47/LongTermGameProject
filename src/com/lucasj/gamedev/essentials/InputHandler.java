package com.lucasj.gamedev.essentials;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.lucasj.gamedev.events.input.KeyboardEventListener;
import com.lucasj.gamedev.events.input.MouseClickEventListener;
import com.lucasj.gamedev.events.input.MouseMotionEventListener;
import com.lucasj.gamedev.utils.ConcurrentList;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

	private ConcurrentList<MouseClickEventListener> mouseClickListeners = new ConcurrentList<>();
	private ConcurrentList<MouseMotionEventListener> mouseMotionListeners = new ConcurrentList<>();
	private ConcurrentList<KeyboardEventListener> keyboardListeners = new ConcurrentList<>();
	
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

	public ConcurrentList<MouseClickEventListener> getMouseClickListeners() {
		return mouseClickListeners;
	}

	public ConcurrentList<MouseMotionEventListener> getMouseMotionListeners() {
		return mouseMotionListeners;
	}

	public ConcurrentList<KeyboardEventListener> getKeyboardListeners() {
		return keyboardListeners;
	}
}
