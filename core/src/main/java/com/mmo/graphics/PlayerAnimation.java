package com.mmo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Simple sprite-based animation for players
 */
public class PlayerAnimation {
    private float animationTime;
    private float bobOffset;
    private AnimationState state;
    private float direction; // 0-2PI for facing direction
    
    public enum AnimationState {
        IDLE,
        WALKING,
        ATTACKING
    }
    
    public PlayerAnimation() {
        this.animationTime = 0;
        this.bobOffset = 0;
        this.state = AnimationState.IDLE;
        this.direction = 0;
    }
    
    public void update(float delta, boolean isMoving, float velocityX, float velocityY) {
        animationTime += delta;
        
        // Update direction based on velocity
        if (isMoving && (velocityX != 0 || velocityY != 0)) {
            direction = MathUtils.atan2(velocityY, velocityX);
            state = AnimationState.WALKING;
            bobOffset = MathUtils.sin(animationTime * 10) * 3;
        } else {
            state = AnimationState.IDLE;
            bobOffset = MathUtils.sin(animationTime * 2) * 1;
        }
    }
    
    public void playAttackAnimation() {
        state = AnimationState.ATTACKING;
        animationTime = 0;
    }
    
    /**
     * Render an animated player sprite
     */
    public void render(ShapeRenderer shapeRenderer, float x, float y, Color color, float radius) {
        float renderY = y + bobOffset;
        
        // Draw body
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x, renderY, radius);
        
        // Draw direction indicator
        float dirX = x + MathUtils.cos(direction) * (radius * 0.7f);
        float dirY = renderY + MathUtils.sin(direction) * (radius * 0.7f);
        shapeRenderer.setColor(1, 1, 1, 0.8f);
        shapeRenderer.circle(dirX, dirY, radius * 0.3f);
        
        // Animation-specific effects
        if (state == AnimationState.ATTACKING) {
            // Show attack arc
            if (animationTime < 0.3f) {
                float arcRadius = radius * 1.5f;
                shapeRenderer.setColor(1, 1, 0, 0.5f);
                float arcX = x + MathUtils.cos(direction) * arcRadius;
                float arcY = renderY + MathUtils.sin(direction) * arcRadius;
                shapeRenderer.circle(arcX, arcY, radius * 0.5f);
            }
        } else if (state == AnimationState.WALKING) {
            // Show motion lines
            float lineLength = 10;
            float lineX = x - MathUtils.cos(direction) * (radius + 5);
            float lineY = renderY - MathUtils.sin(direction) * (radius + 5);
            shapeRenderer.setColor(color.r, color.g, color.b, 0.3f);
            shapeRenderer.rectLine(lineX, lineY, 
                lineX - MathUtils.cos(direction) * lineLength, 
                lineY - MathUtils.sin(direction) * lineLength, 2);
        }
    }
    
    public AnimationState getState() {
        return state;
    }
    
    public float getDirection() {
        return direction;
    }
}
