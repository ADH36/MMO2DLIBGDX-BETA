package com.mmo.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mmo.network.Network;

import java.util.Map;

/**
 * Renders a minimap showing the full world and player positions
 */
public class Minimap {
    
    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 100;
    private static final int TILE_SIZE = 50;
    
    private boolean expanded = false; // Toggle between small and large minimap
    private int minimapSize = 150; // Small minimap size
    private int expandedSize = 400; // Expanded minimap size
    
    /**
     * Render the minimap
     */
    public void render(ShapeRenderer shapeRenderer, Vector2 playerPosition, 
                      Map<Long, Network.PlayerUpdate> otherPlayers,
                      int screenWidth, int screenHeight) {
        
        int size = expanded ? expandedSize : minimapSize;
        int mapX = screenWidth - size - 10;
        int mapY = screenHeight - size - 10;
        
        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Shadow
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(mapX + 3, mapY - 3, size, size);
        
        // Map background with gradient
        for (int i = 0; i < 10; i++) {
            float progress = i / 10.0f;
            shapeRenderer.setColor(0.1f + progress * 0.05f, 0.15f + progress * 0.05f, 0.1f + progress * 0.05f, 0.9f);
            shapeRenderer.rect(mapX, mapY + i * (size / 10), size, size / 10);
        }
        
        // Draw world grid (optional for expanded view)
        if (expanded) {
            shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.3f);
            float gridSize = size / 10.0f; // 10x10 grid
            for (int i = 1; i < 10; i++) {
                // Vertical lines
                shapeRenderer.rect(mapX + i * gridSize, mapY, 1, size);
                // Horizontal lines
                shapeRenderer.rect(mapX, mapY + i * gridSize, size, 1);
            }
        }
        
        // Draw water areas (edges of map)
        shapeRenderer.setColor(0.2f, 0.4f, 0.8f, 0.6f);
        float scale = size / (float)(WORLD_WIDTH * TILE_SIZE);
        
        // Top and bottom water
        shapeRenderer.rect(mapX, mapY + size - 5 * TILE_SIZE * scale, size, 5 * TILE_SIZE * scale);
        shapeRenderer.rect(mapX, mapY, size, 5 * TILE_SIZE * scale);
        
        // Left and right water
        shapeRenderer.rect(mapX, mapY, 5 * TILE_SIZE * scale, size);
        shapeRenderer.rect(mapX + size - 5 * TILE_SIZE * scale, mapY, 5 * TILE_SIZE * scale, size);
        
        // Draw paths
        shapeRenderer.setColor(0.6f, 0.5f, 0.3f, 0.5f);
        for (int x = 0; x < WORLD_WIDTH; x++) {
            if (x % 20 == 0) {
                float pathX = mapX + (x * TILE_SIZE) * scale;
                shapeRenderer.rect(pathX, mapY, 2, size);
            }
        }
        for (int y = 0; y < WORLD_HEIGHT; y++) {
            if (y % 20 == 0) {
                float pathY = mapY + (y * TILE_SIZE) * scale;
                shapeRenderer.rect(mapX, pathY, size, 2);
            }
        }
        
        // Draw other players
        shapeRenderer.setColor(Color.RED);
        for (Network.PlayerUpdate player : otherPlayers.values()) {
            float px = mapX + (player.x * scale);
            float py = mapY + (player.y * scale);
            shapeRenderer.circle(px, py, expanded ? 4 : 2);
        }
        
        // Draw current player (larger and different color)
        shapeRenderer.setColor(Color.CYAN);
        float playerX = mapX + (playerPosition.x * scale);
        float playerY = mapY + (playerPosition.y * scale);
        shapeRenderer.circle(playerX, playerY, expanded ? 6 : 3);
        
        // Draw player direction indicator
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(playerX, playerY + (expanded ? 3 : 2), expanded ? 2 : 1);
        
        shapeRenderer.end();
        
        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(mapX, mapY, size, size);
        
        // Inner border
        shapeRenderer.setColor(new Color(0.8f, 0.6f, 0.2f, 0.6f));
        shapeRenderer.rect(mapX + 2, mapY + 2, size - 4, size - 4);
        
        shapeRenderer.end();
    }
    
    /**
     * Toggle between small and expanded minimap
     */
    public void toggleExpanded() {
        expanded = !expanded;
    }
    
    /**
     * Check if minimap is expanded
     */
    public boolean isExpanded() {
        return expanded;
    }
    
    /**
     * Set minimap expanded state
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    /**
     * Check if position is within minimap bounds (for click detection)
     */
    public boolean isPositionInMinimap(int x, int y, int screenWidth, int screenHeight) {
        int size = expanded ? expandedSize : minimapSize;
        int mapX = screenWidth - size - 10;
        int mapY = screenHeight - size - 10;
        
        return x >= mapX && x <= mapX + size && y >= mapY && y <= mapY + size;
    }
}
