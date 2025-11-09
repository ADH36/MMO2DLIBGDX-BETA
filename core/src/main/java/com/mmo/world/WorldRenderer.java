package com.mmo.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Renders the game world with enhanced graphics
 */
public class WorldRenderer {
    private static final int TILE_SIZE = 50;
    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 100;
    
    // Terrain colors
    private static final Color GRASS_DARK = new Color(0.2f, 0.6f, 0.2f, 1f);
    private static final Color GRASS_LIGHT = new Color(0.3f, 0.7f, 0.3f, 1f);
    private static final Color WATER_DARK = new Color(0.1f, 0.3f, 0.8f, 1f);
    private static final Color WATER_LIGHT = new Color(0.2f, 0.5f, 1f, 1f);
    private static final Color PATH_COLOR = new Color(0.6f, 0.5f, 0.3f, 1f);
    
    // Simple noise for terrain variation
    private int getTerrainType(int x, int y) {
        int hash = (x * 374761393 + y * 668265263) & 0x7FFFFFFF;
        hash = (hash ^ 61) ^ (hash >> 16);
        hash = hash + (hash << 3);
        hash = hash ^ (hash >> 4);
        return hash;
    }
    
    public void render(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        // Calculate visible tiles
        int startX = (int) (camera.position.x - camera.viewportWidth / 2) / TILE_SIZE - 1;
        int endX = (int) (camera.position.x + camera.viewportWidth / 2) / TILE_SIZE + 1;
        int startY = (int) (camera.position.y - camera.viewportHeight / 2) / TILE_SIZE - 1;
        int endY = (int) (camera.position.y + camera.viewportHeight / 2) / TILE_SIZE + 1;
        
        // Clamp to world bounds
        startX = Math.max(0, startX);
        endX = Math.min(WORLD_WIDTH, endX);
        startY = Math.max(0, startY);
        endY = Math.min(WORLD_HEIGHT, endY);
        
        // Draw filled tiles
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                float tileX = x * TILE_SIZE;
                float tileY = y * TILE_SIZE;
                
                int terrain = getTerrainType(x, y);
                
                // Water patches near edges
                if (x < 5 || y < 5 || x > WORLD_WIDTH - 5 || y > WORLD_HEIGHT - 5) {
                    if ((terrain % 5) < 2) {
                        shapeRenderer.setColor((terrain % 2 == 0) ? WATER_DARK : WATER_LIGHT);
                    } else {
                        shapeRenderer.setColor((terrain % 2 == 0) ? GRASS_DARK : GRASS_LIGHT);
                    }
                } else if ((x % 20 == 0 || y % 20 == 0) && Math.abs(x - y) % 3 == 0) {
                    // Paths
                    shapeRenderer.setColor(PATH_COLOR);
                } else {
                    // Grass with variation
                    shapeRenderer.setColor((terrain % 2 == 0) ? GRASS_DARK : GRASS_LIGHT);
                }
                
                shapeRenderer.rect(tileX, tileY, TILE_SIZE, TILE_SIZE);
            }
        }
        
        // Draw decorative elements
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                float tileX = x * TILE_SIZE + TILE_SIZE / 2;
                float tileY = y * TILE_SIZE + TILE_SIZE / 2;
                int terrain = getTerrainType(x, y);
                
                // Trees
                if ((terrain % 7) == 0 && x > 10 && y > 10 && x < WORLD_WIDTH - 10 && y < WORLD_HEIGHT - 10) {
                    // Tree trunk
                    shapeRenderer.setColor(0.4f, 0.25f, 0.1f, 1f);
                    shapeRenderer.rect(tileX - 3, tileY - 3, 6, 12);
                    
                    // Tree foliage
                    shapeRenderer.setColor(0.1f, 0.5f, 0.1f, 1f);
                    shapeRenderer.circle(tileX, tileY + 10, 15);
                    shapeRenderer.setColor(0.15f, 0.6f, 0.15f, 0.8f);
                    shapeRenderer.circle(tileX - 5, tileY + 12, 10);
                    shapeRenderer.circle(tileX + 5, tileY + 12, 10);
                }
                
                // Rocks
                if ((terrain % 11) == 0) {
                    shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1f);
                    float rockSize = 5 + (terrain % 5);
                    shapeRenderer.circle(tileX + (terrain % 10 - 5), tileY + (terrain % 8 - 4), rockSize);
                    shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
                    shapeRenderer.circle(tileX + (terrain % 10 - 5) - 2, tileY + (terrain % 8 - 4) + 1, rockSize * 0.6f);
                }
                
                // Flowers
                if ((terrain % 13) == 0 && (terrain % 2) == 0) {
                    // Random flower colors
                    if ((terrain % 3) == 0) {
                        shapeRenderer.setColor(1f, 0.3f, 0.3f, 1f); // Red
                    } else if ((terrain % 3) == 1) {
                        shapeRenderer.setColor(1f, 0.9f, 0.2f, 1f); // Yellow
                    } else {
                        shapeRenderer.setColor(0.8f, 0.4f, 1f, 1f); // Purple
                    }
                    shapeRenderer.circle(tileX + (terrain % 15 - 7), tileY + (terrain % 12 - 6), 3);
                }
            }
        }
        
        shapeRenderer.end();
        
        // Draw grid lines for clarity
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.2f, 0.4f, 0.2f, 0.2f);
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        shapeRenderer.end();
    }
}
