package com.mmo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages and renders particle effects
 */
public class ParticleSystem {
    private List<Particle> particles;
    
    public ParticleSystem() {
        particles = new ArrayList<>();
    }
    
    public void update(float delta) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update(delta);
            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }
    
    public void render(ShapeRenderer shapeRenderer) {
        for (Particle particle : particles) {
            Color c = particle.getColor();
            shapeRenderer.setColor(c.r, c.g, c.b, particle.getAlpha());
            shapeRenderer.circle(particle.getPosition().x, particle.getPosition().y, particle.getSize());
        }
    }
    
    /**
     * Create a burst of particles at a position
     */
    public void createBurst(float x, float y, Color color, int count, float speed) {
        for (int i = 0; i < count; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float velocity = MathUtils.random(speed * 0.5f, speed);
            float vx = MathUtils.cos(angle) * velocity;
            float vy = MathUtils.sin(angle) * velocity;
            float size = MathUtils.random(2f, 5f);
            float lifetime = MathUtils.random(0.3f, 0.8f);
            particles.add(new Particle(x, y, vx, vy, color, size, lifetime));
        }
    }
    
    /**
     * Create a directed spray of particles
     */
    public void createSpray(float x, float y, float directionAngle, Color color, int count, float speed) {
        for (int i = 0; i < count; i++) {
            float angle = directionAngle + MathUtils.random(-0.5f, 0.5f);
            float velocity = MathUtils.random(speed * 0.7f, speed);
            float vx = MathUtils.cos(angle) * velocity;
            float vy = MathUtils.sin(angle) * velocity;
            float size = MathUtils.random(2f, 4f);
            float lifetime = MathUtils.random(0.4f, 1.0f);
            particles.add(new Particle(x, y, vx, vy, color, size, lifetime));
        }
    }
    
    /**
     * Create hit effect particles
     */
    public void createHitEffect(float x, float y, boolean isCritical) {
        Color color = isCritical ? Color.ORANGE : Color.RED;
        int count = isCritical ? 20 : 12;
        createBurst(x, y, color, count, 150f);
    }
    
    /**
     * Create healing effect particles
     */
    public void createHealEffect(float x, float y) {
        for (int i = 0; i < 15; i++) {
            float angle = MathUtils.random(0f, MathUtils.PI2);
            float radius = MathUtils.random(10f, 30f);
            float px = x + MathUtils.cos(angle) * radius;
            float py = y + MathUtils.sin(angle) * radius;
            float vx = 0;
            float vy = MathUtils.random(30f, 60f);
            float size = MathUtils.random(3f, 6f);
            float lifetime = MathUtils.random(0.5f, 1.2f);
            particles.add(new Particle(px, py, vx, vy, Color.GREEN, size, lifetime));
        }
    }
    
    /**
     * Create movement trail particles
     */
    public void createMovementTrail(float x, float y, Color color) {
        float vx = MathUtils.random(-10f, 10f);
        float vy = MathUtils.random(-10f, 10f);
        float size = MathUtils.random(2f, 4f);
        float lifetime = 0.3f;
        particles.add(new Particle(x, y, vx, vy, color, size, lifetime));
    }
    
    public void clear() {
        particles.clear();
    }
}
