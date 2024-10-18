package com.lucasj.gamedev.world.particles;

import java.awt.Color;
import java.awt.Graphics2D;

import com.lucasj.gamedev.mathutils.Vector2D;

public class Particle {
    private Vector2D position;
    private Vector2D velocity;
    private double lifetime;  // How long the particle lasts
    private double maxLifetime;
    private boolean active;

    public Particle() {
        this.position = new Vector2D(0, 0);
        this.velocity = new Vector2D(0, 0);
        this.active = false;
    }

    // Activate the particle with position, velocity, and lifetime
    public void activate(Vector2D position, Vector2D velocity, double lifetime) {
        this.position = position.copy();
        this.velocity = velocity.copy();
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.active = true;
    }

    // Update the particle's position and lifetime
    public void update() {
        if (!active) return;

        // Update position using velocity
        position.add(velocity.copy());

        // Deactivate the particle if its lifetime is over
        if (lifetime <= 0) {
            active = false;
        }
    }

    // Render the particle
    public void render(Graphics2D g) {
        if (!active) return;

        // Example of rendering a simple fading particle based on remaining lifetime
        float alpha = (float) (lifetime / maxLifetime);  // Fade out as the particle nears the end of its lifetime
        g.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));
        g.fillRect((int)position.getX(), (int)position.getY(), 5, 5);  // Example square particle
    }

    public boolean isActive() {
        return active;
    }
}