package com.lucasj.gamedev.game.entities.particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.mathutils.Vector2D;

public class Particle {
	
	private ParticleGenerator generator;

	private long startTime;
	private Vector2D distance;
	private Vector2D velocity;
	private float time;
	private Color color;
	private Image image;
	private int size;
	private ParticleShape shape;
	private boolean displayStatic = false;
	private Vector2D staticPivot;

	public Particle(ParticleGenerator generator, Vector2D velocity, float time, Color color, ParticleShape shape, int size) {
		this.generator = generator;
		this.distance = new Vector2D(0, 0);
		this.velocity = velocity;
		this.time = time;
		this.color = color;
		this.shape = shape;
		this.size = size;
		startTime = System.currentTimeMillis();
	}
	
	public Particle(ParticleGenerator generator, Vector2D velocity, float time, Color color, Image image, int size) {
		this.generator = generator;
		this.distance = new Vector2D(0, 0);
		this.velocity = velocity;
		this.time = time;
		this.color = color;
		this.image = image;
		this.size = size;
		startTime = System.currentTimeMillis();
	}
	
	public void update(double deltaTime) {
		distance = distance.add(velocity);
		
		if((System.currentTimeMillis() - startTime)/1000.0 >= time) {
			generator.removeParticle(this);
		}
	}
	
	public void render(Graphics2D g2d) {
		if(this.color != null) g2d.setColor(color);
		
		if(shape == null) {
			
			if(this.color != null ) {
				g2d.drawImage(SpriteTools.tintImage(SpriteTools.toBufferedImage(image), color), getPosition().getXint(), getPosition().getYint(), size, size, null);
			} else {
				g2d.drawImage(image, getPosition().getXint(), getPosition().getYint(), size, size, null);
			}
			
		}
		
		if(shape != null) {
			if(shape == ParticleShape.Circle) {
				g2d.fillOval(getPosition().getXint(), getPosition().getYint(), size, size);
			} else {
				g2d.fillRect(getPosition().getXint(), getPosition().getYint(), size, size);
			}
		}
		
	}
	
	private Vector2D getPosition() {
		if(this.displayStatic) {
			return staticPivot.add(distance);
		}
		return generator.getScreenPosition().add(distance);
	}

	public Particle setDisplayStatic() {
		this.displayStatic = true;
		staticPivot = generator.getScreenPosition().copy();
		return this;
	}
	
}
