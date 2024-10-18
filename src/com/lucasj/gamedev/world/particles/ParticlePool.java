package com.lucasj.gamedev.world.particles;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.lucasj.gamedev.mathutils.Vector2D;

public class ParticlePool {
    private List<Particle> particles;

    public ParticlePool(int maxParticles) {
        particles = new ArrayList<>(maxParticles);
        for (int i = 0; i < maxParticles; i++) {
            particles.add(new Particle());  // Pre-allocate particles
        }
    }

    // Activate a particle by resetting its properties
    public void activateParticle(Vector2D position, Vector2D velocity, double lifetime) {
        for (Particle p : particles) {
            if (!p.isActive()) {  // Find an inactive particle to reuse
                p.activate(position, velocity, lifetime);
                return;
            }
        }
    }

    // Update all active particles
    public void updateParticles() {
        for (Particle p : particles) {
            if (p.isActive()) {
                p.update();
            }
        }
    }

    // Render all active particles
    public void renderParticles(Graphics2D g) {
        for (Particle p : particles) {
            if (p.isActive()) {
                p.render(g);
            }
        }
    }
}