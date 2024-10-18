package com.lucasj.gamedev.world.particles;

import java.awt.Graphics2D;
import java.util.Random;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.mathutils.Vector2D;

public class ParticleEmitter {
    private ParticlePool particlePool;
    private Random random;
    @SuppressWarnings("unused")
	private Game game;
    
    public ParticleEmitter(Game game, int amt) {
    	this.game = game;
        this.particlePool = new ParticlePool(amt);
        this.random = new Random();
    }

    // Emit a burst of particles from a position
    public void emit(Vector2D position, int amount) {
        for (int i = 0; i < amount; i++) {
            // Create a random velocity for each particle
            double velocityX = (random.nextDouble() * 2 - 1) * 100;  // Random X velocity (-100 to 100)
            double velocityY = (random.nextDouble() * -2) * 100;     // Random Y velocity (-200 to 0, moving upwards)

            Vector2D velocity = new Vector2D(velocityX, velocityY);
            double lifetime = random.nextDouble() * 2 + 1;  // Random lifetime between 1 and 3 seconds

            // Activate a particle at the specified position with the random velocity and lifetime
            particlePool.activateParticle(position, velocity, lifetime);
        }
    }

    public void update() {
        particlePool.updateParticles();
    }

    public void render(Graphics2D g) {
        particlePool.renderParticles(g);
    }
}