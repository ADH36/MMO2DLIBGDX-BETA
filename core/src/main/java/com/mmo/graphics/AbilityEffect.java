package com.mmo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Visual effects for abilities and skills
 */
public class AbilityEffect {
    private float x, y;
    private float targetX, targetY;
    private String abilityName;
    private float progress;
    private float duration;
    private boolean active;
    private EffectType type;
    
    public enum EffectType {
        PROJECTILE,      // Flies from caster to target
        BEAM,            // Instant beam effect
        AREA,            // Area of effect circle
        BUFF             // Visual buff around character
    }
    
    public AbilityEffect() {
        this.active = false;
    }
    
    public void start(float x, float y, float targetX, float targetY, String abilityName, EffectType type) {
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.abilityName = abilityName;
        this.type = type;
        this.progress = 0;
        this.duration = 0.5f;
        this.active = true;
    }
    
    public void update(float delta) {
        if (active) {
            progress += delta;
            if (progress >= duration) {
                active = false;
            }
        }
    }
    
    public void render(ShapeRenderer shapeRenderer) {
        if (!active) return;
        
        float t = progress / duration;
        
        switch (type) {
            case PROJECTILE:
                renderProjectile(shapeRenderer, t);
                break;
            case BEAM:
                renderBeam(shapeRenderer, t);
                break;
            case AREA:
                renderArea(shapeRenderer, t);
                break;
            case BUFF:
                renderBuff(shapeRenderer, t);
                break;
        }
    }
    
    private void renderProjectile(ShapeRenderer shapeRenderer, float t) {
        float currentX = x + (targetX - x) * t;
        float currentY = y + (targetY - y) * t;
        
        Color color = getAbilityColor();
        shapeRenderer.setColor(color.r, color.g, color.b, 1 - t);
        
        // Main projectile
        shapeRenderer.circle(currentX, currentY, 8);
        
        // Trail
        shapeRenderer.setColor(color.r, color.g, color.b, 0.5f * (1 - t));
        shapeRenderer.circle(currentX - (targetX - x) * 0.1f * t, 
                           currentY - (targetY - y) * 0.1f * t, 5);
    }
    
    private void renderBeam(ShapeRenderer shapeRenderer, float t) {
        Color color = getAbilityColor();
        float alpha = t < 0.5f ? t * 2 : 2 - t * 2;
        shapeRenderer.setColor(color.r, color.g, color.b, alpha * 0.8f);
        shapeRenderer.rectLine(x, y, targetX, targetY, 4);
        
        shapeRenderer.setColor(1, 1, 1, alpha);
        shapeRenderer.rectLine(x, y, targetX, targetY, 2);
    }
    
    private void renderArea(ShapeRenderer shapeRenderer, float t) {
        Color color = getAbilityColor();
        float radius = 50 * t;
        float alpha = 1 - t;
        
        shapeRenderer.setColor(color.r, color.g, color.b, alpha * 0.5f);
        shapeRenderer.circle(targetX, targetY, radius);
        
        shapeRenderer.setColor(color.r, color.g, color.b, alpha);
        shapeRenderer.circle(targetX, targetY, radius * 0.8f);
    }
    
    private void renderBuff(ShapeRenderer shapeRenderer, float t) {
        Color color = getAbilityColor();
        float angle = t * MathUtils.PI2 * 3;
        float radius = 25;
        
        for (int i = 0; i < 6; i++) {
            float a = angle + i * MathUtils.PI2 / 6;
            float px = x + MathUtils.cos(a) * radius;
            float py = y + MathUtils.sin(a) * radius;
            shapeRenderer.setColor(color.r, color.g, color.b, 0.7f);
            shapeRenderer.circle(px, py, 3);
        }
    }
    
    private Color getAbilityColor() {
        if (abilityName == null) return Color.WHITE;
        
        String lower = abilityName.toLowerCase();
        if (lower.contains("fire") || lower.contains("meteor")) {
            return Color.ORANGE;
        } else if (lower.contains("ice") || lower.contains("frost")) {
            return Color.CYAN;
        } else if (lower.contains("heal") || lower.contains("holy")) {
            return Color.YELLOW;
        } else if (lower.contains("shadow") || lower.contains("poison")) {
            return Color.PURPLE;
        } else if (lower.contains("lightning") || lower.contains("shock")) {
            return Color.SKY;
        } else {
            return Color.WHITE;
        }
    }
    
    public boolean isActive() {
        return active;
    }
}
