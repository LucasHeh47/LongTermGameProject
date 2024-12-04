package com.lucasj.gamedev.game.entities.particles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.lucasj.gamedev.essentials.Game;
import com.lucasj.gamedev.events.entities.EntityCollisionEvent;
import com.lucasj.gamedev.game.entities.Entity;
import com.lucasj.gamedev.mathutils.Vector2D;
import com.lucasj.gamedev.misc.Debug;
import com.lucasj.gamedev.utils.ConcurrentList;

public class ParticleGenerator extends Entity {
	
	private ConcurrentList<Particle> particles;
	
	private float timeAlive;
	private long spawnTime;
	private float particleTime;
	private float angle;
	private float angleWidth;
	private float speed;
	private long lastSpawn;
	private float density;
	private Random rand = new Random();
	
	private List<Color> color;
	private ParticleShape shape;
	
	private List<Image> image;
	
	private boolean staticParticles = false;
	
	/***
	 * 
	 * @param game
	 * @param position
	 * @param timeAlive
	 * @param particleTime
	 * @param angle
	 * @param angleWidth
	 * @param speed
	 * @param density
	 */
	public ParticleGenerator(Game game, Vector2D position, float timeAlive, float particleTime, float angle, float angleWidth, float speed, float density, int size) {
		super(game);
		this.position = position;
		this.timeAlive = timeAlive;
		this.particleTime = particleTime;
		this.angle = angle;
		this.angleWidth = angleWidth;
		this.speed = speed;
		this.density = density;
		this.size = size;
		spawnTime = System.currentTimeMillis();
		particles = new ConcurrentList<>();
		color = new ArrayList<>();
		image = new ArrayList<>();
		game.instantiatedParticles.add(this);
	}
	
	public ParticleGenerator setColorAndShape(Color color, ParticleShape shape) {
		this.color.add(color);
		this.shape = shape;
		return this;
	}
	
	public ParticleGenerator setColorAndShape(Supplier<List<Color>> color, ParticleShape shape) {
		this.color.addAll(color.get());
		this.shape = shape;
		return this;
	}
	
	public ParticleGenerator setColorAndImage(Color color, Image image) {
		this.color.add(color);
		this.image.add(image);
		return this;
	}

	public ParticleGenerator setColorAndImage(Supplier<List<Color>> color, Image image) {
		this.color.addAll(color.get());
		this.image.add(image);
		return this;
	}
	
	public ParticleGenerator setColorAndImage(Supplier<List<Color>> color, Supplier<List<Image>> image) {
		this.color.addAll(color.get());
		this.image.addAll(image.get());
		return this;
	}
	
	public ParticleGenerator setColorAndImage(Color color, Supplier<List<Image>> image) {
		this.color.add(color);
		this.image.addAll(image.get());
		return this;
	}
	
	@Override 
	public void update(double deltaTime) {
		
		this.screenPosition = game.getCamera().worldToScreenPosition(position);
		
		if((System.currentTimeMillis() - this.spawnTime)/1000.0 >= timeAlive) {
			game.instantiatedParticles.remove(this);
		}
		
		if((System.currentTimeMillis() - lastSpawn)/1000.0 >= density) {
			
			summonParticle();
			
			lastSpawn = System.currentTimeMillis();
		}
		
		particles.forEach(particle -> {
			particle.update(deltaTime);
		});
		
		particles.update();
	}
	
	@Override
	public void render(Graphics g) {
		particles.forEach(particle -> {
			particle.render((Graphics2D) g);
		});
	}
	
	public void stop() {
		game.instantiatedParticles.remove(this);
	}
	
	private void summonParticle() {
		float randomAngle = angle + (float) (Math.random() * angleWidth) - angleWidth / 2;

	    // Convert the random angle to radians
        double angleInRadians = Math.toRadians(randomAngle);

        // Generate velocity using the random angle
        double vx = Math.cos(angleInRadians); // x-component of the velocity
        double vy = Math.sin(angleInRadians); // y-component of the velocity
        Vector2D velocity = new Vector2D(vx, vy).normalize().multiply(speed);
		Color col = color.get(rand.nextInt(0, color.size()-1));
		if(image.size() == 0) {
			particles.add(new Particle(game, this, velocity, particleTime, col, shape, size).setDisplayStatic());
		} else {
			
			Image img = image.get(rand.nextInt(0, image.size()-1));
			
			particles.add(new Particle(game, this, velocity, particleTime, col, img, size).setDisplayStatic());
		}
	}
	
	public void removeParticle(Particle particle) {
		particles.remove(particle);
	}

	@Override
	public void onEntityCollision(EntityCollisionEvent e) {
		return;
	}

	@Override
	public void entityDeath() {
		return;
	}
	
	public ParticleGenerator setStaticParticles() {
		staticParticles = true;
		return this;
	}
	
	public Game getGame() {
		return game;
	}

	public ConcurrentList<Particle> getParticles() {
		return particles;
	}

	public float getTimeAlive() {
		return timeAlive;
	}

	public long getSpawnTime() {
		return spawnTime;
	}

	public float getParticleTime() {
		return particleTime;
	}

	public float getAngle() {
		return angle;
	}

	public float getAngleWidth() {
		return angleWidth;
	}

	public float getSpeed() {
		return speed;
	}

	public long getLastSpawn() {
		return lastSpawn;
	}

	public float getDensity() {
		return density;
	}

	public List<Color> getColor() {
		return color;
	}

	public ParticleShape getShape() {
		return shape;
	}

	public List<Image> getImage() {
		return image;
	}

	public boolean isStaticParticles() {
		return staticParticles;
	}

}
