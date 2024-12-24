package com.lucasj.gamedev.essentials;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.image.BufferStrategy;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;

import com.lucasj.gamedev.essentials.ui.Layer;
import com.lucasj.gamedev.essentials.ui.Render;
import com.lucasj.gamedev.misc.Debug;
import com.lucasj.gamedev.settings.SettingsManager;
import com.lucasj.gamedev.utils.GraphicUtils;

public class Window extends Canvas implements Runnable {
    private static final long serialVersionUID = -171090556192089278L;

    public boolean isRunning = false;
    
	SettingsManager settings;
	
	private Thread gameThread;
    private boolean running = false;
    private Dimension size;
    
    private JFrame frame;
    private InputHandler inputHandler;
    private WindowHandler windowHandler;
    
    private int fpslimit = 0;  // Max FPS limit
    
    GraphicUtils gUtils;
    
    Game game;

    public Window(String title, int width, int height, SettingsManager settings) {
    	Debug.initializeLogging();
        this.settings = settings;
        fpslimit = settings.getIntSetting("fpslimit");
    	frame = new JFrame(title);
        Dimension size = new Dimension(width, height);
        this.size = size;
        setPreferredSize(size);
        
        gUtils = new GraphicUtils();
        // Add canvas to the JFrame
        frame.add(this);
        inputHandler = new InputHandler();
        
        frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
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
            timePerFrame = 1000000000 / game.getSettings().getIntSetting("fpslimit");
        	
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
        		frame.setTitle(Integer.toString(frames));
        		frames = 0;
        	}
        	
        }
    }

    private void update(double deltaTime) {
//    	Runtime runtime = Runtime.getRuntime();
//    	long totalMemory = runtime.totalMemory();
//
//        // Get the maximum heap size in bytes
//        long maxMemory = runtime.maxMemory();
//
//        // Get the free memory in bytes
//        long freeMemory = runtime.freeMemory();
//
//        System.out.println("Total Memory: " + totalMemory / 1024 / 1024 + " MB");
//        System.out.println("Max Memory: " + maxMemory / 1024 / 1024 + " MB");
//        System.out.println("Free Memory: " + freeMemory / 1024 / 1024 + " MB");
//    
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
        g2d.setStroke(new BasicStroke(3));

        // 1. Draw the gray background
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        List<Render> renders = game.render();
        
        renders.sort(Comparator.comparingInt(Render::getLayerValue).reversed());
        
        
        for(Render render : renders) {
        	render.render(g);
        }
        
        g.dispose();
        bs.show();

    }
}
