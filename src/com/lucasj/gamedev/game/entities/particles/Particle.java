package com.lucasj.gamedev.game.entities.particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import com.lucasj.gamedev.Assets.SpriteTools;
import com.lucasj.gamedev.essentials.Game;
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
	private String text;
	private boolean single = false;
	private Game game;

	public Particle(Game game, ParticleGenerator generator, Vector2D velocity, float time, Color color, ParticleShape shape, int size) {
		this.game = game;
		this.generator = generator;
		this.distance = new Vector2D(0, 0);
		this.velocity = velocity;
		this.time = time;
		this.color = color;
		this.shape = shape;
		this.size = size;
		startTime = System.currentTimeMillis();
	}
	
	public Particle(Game game, ParticleGenerator generator, Vector2D velocity, float time, Color color, Image image, int size) {
		this.game = game;
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
			if(!single) generator.removeParticle(this);
			else game.instantiatedSingleParticles.remove(this);
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
			} else if (shape == ParticleShape.Square) {
				g2d.fillRect(getPosition().getXint(), getPosition().getYint(), size, size);
			} else if (shape == ParticleShape.Text) {
				if(text != null) {
					g2d.setFont(game.font.deriveFont(size));
					g2d.drawString(text, this.getPosition().getXint(), this.getPosition().getYint());
				}
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
		if(!single) staticPivot = generator.getScreenPosition().copy();
		return this;
	}
	
	private Particle setSingle(boolean single) {
		this.single = single;
		game.instantiatedSingleParticles.add(this);
		return this;
	}
	
	public Particle setText(String text) {
		this.text = text;
		return this;
	}
	
	public static void spawnSingleParticle(Particle particle, Vector2D position) {
		particle.setSingle(true);
		particle.setDisplayStatic();
		particle.staticPivot = position;
	}

	public ParticleGenerator getGenerator() {
		return generator;
	}

	public long getStartTime() {
		return startTime;
	}

	public Vector2D getDistance() {
		return distance;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public float getTime() {
		return time;
	}

	public Color getColor() {
		return color;
	}

	public Image getImage() {
		return image;
	}

	public int getSize() {
		return size;
	}

	public ParticleShape getShape() {
		return shape;
	}

	public boolean isDisplayStatic() {
		return displayStatic;
	}

	public Vector2D getStaticPivot() {
		return staticPivot;
	}

	public String getText() {
		return text;
	}

	public boolean isSingle() {
		return single;
	}
	
}
