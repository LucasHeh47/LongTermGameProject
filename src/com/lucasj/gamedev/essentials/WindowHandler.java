package com.lucasj.gamedev.essentials;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.json.JSONException;

import com.lucasj.gamedev.game.entities.player.Player;
import com.lucasj.gamedev.mathutils.Vector2D;

public class WindowHandler implements ComponentListener, WindowListener{

	private Game game;
	private JFrame frame;
	
	public WindowHandler(Game game, JFrame frame) {
		this.game = game;
		this.frame = frame;
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		game.setScreen(frame.getSize());
		game.getCamera().setViewport(new Vector2D(frame.getSize().getWidth(), frame.getSize().getHeight()));
		game.getCamera().recalculateScale();
		game.getMenus().createGUIs();
		game.getQuadtree().setBounds(new Vector2D(0, 0), new Vector2D(frame.getWidth(), frame.getHeight()));
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		game.getSettings().save();
		Player.getGlobalStats().save(game.gameData);
		game.getSocketClient().getPacketManager().requestLogoffPacket();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
