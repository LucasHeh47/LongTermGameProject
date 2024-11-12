package com.lucasj.gamedev.essentials;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import com.lucasj.gamedev.game.multiplayer.GameClient;
import com.lucasj.gamedev.settings.SettingsManager;
import com.lucasj.gamedev.utils.GraphicUtils;

public class Window extends Canvas implements Runnable {
    private static final long serialVersionUID = -171090556192089278L;

    public boolean isRunning = false;
    
	SettingsManager settings;
	
	private Thread gameThread;
    private boolean running = false;
    
    private JFrame frame;
    private InputHandler inputHandler;
    private WindowHandler windowHandler;
    
    private int fpslimit = 0;  // Max FPS limit
    
    GraphicUtils gUtils;
    
    Game game;

    public Window(String title, int width, int height, SettingsManager settings) {
        this.settings = settings;
        fpslimit = settings.getIntSetting("fpslimit");
    	frame = new JFrame(title);
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        
        gUtils = new GraphicUtils();
        // Add canvas to the JFrame
        frame.add(this);
        inputHandler = new InputHandler();
        
        frame.pack();
        frame.setMinimumSize(new Dimension(1920, 1080));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // Center the window
        frame.setVisible(true);
        
        game = new Game(inputHandler, gUtils, settings, size, this);
        windowHandler = new WindowHandler(game, frame);
        
        this.addComponentListener(windowHandler);
        frame.addWindowListener(windowHandler);
        this.addKeyListener(inputHandler);
        this.addMouseListener(inputHandler);
        this.addMouseMotionListener(inputHandler);
        
    }
    
    public synchronized void start() {
        if (running) return;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        isRunning = true;
        double timePerFrame = 1000000000 / fpslimit;
        long lastFrame = System.nanoTime();
        long now = System.nanoTime();
        
        int frames = 0;
        long lastCheck = System.currentTimeMillis();
        
        while (true ) {
        	
        	now = System.nanoTime();
    		double deltaTime = (now - lastFrame) / 1_000_000_000.0;
        	if (now - lastFrame >= timePerFrame) {
        		
        		update(deltaTime);
        		render();
        		
        		lastFrame = now;
        		frames++;
        		
        	}
        	
        	if (System.currentTimeMillis() - lastCheck >= 1000) {
        		lastCheck = System.currentTimeMillis();
        		System.out.println("FPS: " + frames);
        		frames = 0;
        	}
        	
        }
    }





    private void update(double deltaTime) {
    	game.update(deltaTime);
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);  // Triple buffering
            return;
        }

        Graphics g = bs.getDrawGraphics();
        
        Graphics2D g2d = (Graphics2D) g;

        // 1. Draw the gray background
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        game.render(g);
        g.dispose();
        bs.show();

    }
}
