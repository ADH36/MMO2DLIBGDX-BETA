package com.mmo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Simple particle for visual effects
 */
public class Particle {
    private Vector2 position;
    private Vector2 velocity;
    private Color color;
    private float size;
    private float lifetime;
    private float maxLifetime;
    private float alpha;
    
    public Particle(float x, float y, float vx, float vy, Color color, float size, float lifetime) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(vx, vy);
        this.color = new Color(color);
        this.size = size;
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.alpha = 1f;
    }
    
    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
        lifetime -= delta;
        alpha = lifetime / maxLifetime;
    }
    
    public boolean isAlive() {
        return lifetime > 0;
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public Color getColor() {
        return color;
    }
    
    public float getSize() {
        return size;
    }
    
    public float getAlpha() {
        return alpha;
    }
}
