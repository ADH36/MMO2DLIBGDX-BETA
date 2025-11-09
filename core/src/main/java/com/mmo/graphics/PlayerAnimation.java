package com.mmo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Enhanced sprite-based animation for players with more realistic appearance
 */
public class PlayerAnimation {
    private float animationTime;
    private float bobOffset;
    private AnimationState state;
    private float direction; // 0-2PI for facing direction
    private float walkCycle; // For leg animation
    
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
        this.walkCycle = 0;
    }
    
    public void update(float delta, boolean isMoving, float velocityX, float velocityY) {
        animationTime += delta;
        
        // Update direction based on velocity
        if (isMoving && (velocityX != 0 || velocityY != 0)) {
            direction = MathUtils.atan2(velocityY, velocityX);
            state = AnimationState.WALKING;
            bobOffset = MathUtils.sin(animationTime * 10) * 3;
            walkCycle += delta * 12; // Walking animation speed
        } else {
            state = AnimationState.IDLE;
            bobOffset = MathUtils.sin(animationTime * 2) * 1;
            walkCycle = 0;
        }
    }
    
    public void playAttackAnimation() {
        state = AnimationState.ATTACKING;
        animationTime = 0;
    }
    
    /**
     * Render an enhanced animated player sprite with more realistic details
     */
    public void render(ShapeRenderer shapeRenderer, float x, float y, Color color, float radius) {
        float renderY = y + bobOffset;
        
        // Draw shadow
        shapeRenderer.setColor(0, 0, 0, 0.3f);
        shapeRenderer.circle(x + 2, y - radius - 2, radius * 0.8f);
        
        // Draw legs with walking animation
        if (state == AnimationState.WALKING) {
            float legOffset1 = MathUtils.sin(walkCycle) * 5;
            float legOffset2 = MathUtils.sin(walkCycle + MathUtils.PI) * 5;
            
            // Left leg
            shapeRenderer.setColor(color.r * 0.6f, color.g * 0.6f, color.b * 0.6f, 1f);
            shapeRenderer.circle(x - 4, renderY - radius + legOffset1, radius * 0.35f);
            
            // Right leg
            shapeRenderer.circle(x + 4, renderY - radius + legOffset2, radius * 0.35f);
        } else {
            // Standing legs
            shapeRenderer.setColor(color.r * 0.6f, color.g * 0.6f, color.b * 0.6f, 1f);
            shapeRenderer.circle(x - 3, renderY - radius, radius * 0.35f);
            shapeRenderer.circle(x + 3, renderY - radius, radius * 0.35f);
        }
        
        // Draw body with gradient effect
        shapeRenderer.setColor(color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, 1f);
        shapeRenderer.circle(x, renderY, radius);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x - 2, renderY + 2, radius * 0.9f);
        
        // Draw highlight for 3D effect
        shapeRenderer.setColor(1, 1, 1, 0.3f);
        shapeRenderer.circle(x - 4, renderY + 4, radius * 0.4f);
        
        // Draw head
        float headRadius = radius * 0.6f;
        shapeRenderer.setColor(color.r * 0.85f, color.g * 0.7f, color.b * 0.6f, 1f); // Skin tone
        shapeRenderer.circle(x, renderY + radius * 0.8f, headRadius);
        
        // Head highlight
        shapeRenderer.setColor(1, 0.9f, 0.8f, 0.4f);
        shapeRenderer.circle(x - 2, renderY + radius * 0.8f + 2, headRadius * 0.5f);
        
        // Draw direction indicator (face/front)
        float dirX = x + MathUtils.cos(direction) * (headRadius * 0.8f);
        float dirY = renderY + radius * 0.8f + MathUtils.sin(direction) * (headRadius * 0.8f);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.9f);
        shapeRenderer.circle(dirX, dirY, headRadius * 0.25f);
        
        // Draw simple eyes
        if (state != AnimationState.ATTACKING) {
            float eyeX1 = x - 2;
            float eyeX2 = x + 2;
            float eyeY = renderY + radius * 0.8f + 2;
            shapeRenderer.setColor(1, 1, 1, 1f);
            shapeRenderer.circle(eyeX1, eyeY, 1.5f);
            shapeRenderer.circle(eyeX2, eyeY, 1.5f);
            shapeRenderer.setColor(0, 0, 0, 1f);
            shapeRenderer.circle(eyeX1, eyeY, 0.8f);
            shapeRenderer.circle(eyeX2, eyeY, 0.8f);
        }
        
        // Animation-specific effects
        if (state == AnimationState.ATTACKING) {
            // Show attack arc with weapon trail
            if (animationTime < 0.3f) {
                float arcRadius = radius * 1.8f;
                float weaponX = x + MathUtils.cos(direction) * arcRadius;
                float weaponY = renderY + MathUtils.sin(direction) * arcRadius;
                
                // Weapon trail
                shapeRenderer.setColor(1, 1, 0.5f, 0.6f - animationTime * 2);
                shapeRenderer.circle(weaponX, weaponY, radius * 0.6f);
                
                // Weapon blade
                shapeRenderer.setColor(0.8f, 0.8f, 0.9f, 1f);
                shapeRenderer.rectLine(x, renderY, weaponX, weaponY, 3);
            }
        } else if (state == AnimationState.WALKING) {
            // Show motion lines
            float lineLength = 12;
            float lineX = x - MathUtils.cos(direction) * (radius + 5);
            float lineY = renderY - MathUtils.sin(direction) * (radius + 5);
            shapeRenderer.setColor(color.r, color.g, color.b, 0.25f);
            shapeRenderer.rectLine(lineX, lineY, 
                lineX - MathUtils.cos(direction) * lineLength, 
                lineY - MathUtils.sin(direction) * lineLength, 2);
        }
        
        // Draw armor/equipment glow for equipped players
        if (animationTime % 3 < 0.1f) {
            shapeRenderer.setColor(color.r, color.g, color.b, 0.2f);
            shapeRenderer.circle(x, renderY, radius * 1.15f);
        }
    }
    
    public AnimationState getState() {
        return state;
    }
    
    public float getDirection() {
        return direction;
    }
}
