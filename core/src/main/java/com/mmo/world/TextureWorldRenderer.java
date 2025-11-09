package com.mmo.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mmo.graphics.AssetManager;

/**
 * Renders the game world using actual texture assets instead of shapes
 * Provides a more 3D-like appearance with detailed sprites
 */
public class TextureWorldRenderer {
    private static final int TILE_SIZE = 50;
    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 100;
    
    private AssetManager assetManager;
    private Texture grassTexture;
    private Texture waterTexture;
    private Texture pathTexture;
    
    public TextureWorldRenderer() {
        assetManager = AssetManager.getInstance();
        grassTexture = assetManager.getTileTexture("grass");
        waterTexture = assetManager.getTileTexture("water");
        pathTexture = assetManager.getTileTexture("path");
    }
    
    // Simple noise for terrain variation
    private int getTerrainType(int x, int y) {
        int hash = (x * 374761393 + y * 668265263) & 0x7FFFFFFF;
        hash = (hash ^ 61) ^ (hash >> 16);
        hash = hash + (hash << 3);
        hash = hash ^ (hash >> 4);
        return hash;
    }
    
    /**
     * Render the world using textures
     */
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, OrthographicCamera camera) {
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
        
        // Draw base tiles using textures
        batch.begin();
        
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                float tileX = x * TILE_SIZE;
                float tileY = y * TILE_SIZE;
                
                int terrain = getTerrainType(x, y);
                
                // Determine tile type and draw appropriate texture
                Texture tileTexture;
                
                // Water patches near edges
                if (x < 5 || y < 5 || x > WORLD_WIDTH - 5 || y > WORLD_HEIGHT - 5) {
                    if ((terrain % 5) < 2) {
                        tileTexture = waterTexture;
                    } else {
                        tileTexture = grassTexture;
                    }
                } else if ((x % 20 == 0 || y % 20 == 0) && Math.abs(x - y) % 3 == 0) {
                    // Paths
                    tileTexture = pathTexture;
                } else {
                    // Grass (default)
                    tileTexture = grassTexture;
                }
                
                // Draw tile with slight variation in brightness for depth
                float brightness = 0.95f + (terrain % 10) * 0.005f;
                batch.setColor(brightness, brightness, brightness, 1f);
                batch.draw(tileTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
            }
        }
        
        batch.setColor(Color.WHITE);
        batch.end();
        
        // Draw decorative elements with shapes (trees, rocks, flowers)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                float tileX = x * TILE_SIZE + TILE_SIZE / 2;
                float tileY = y * TILE_SIZE + TILE_SIZE / 2;
                int terrain = getTerrainType(x, y);
                
                // Enhanced trees with shadows
                if ((terrain % 7) == 0 && x > 10 && y > 10 && x < WORLD_WIDTH - 10 && y < WORLD_HEIGHT - 10) {
                    // Tree shadow
                    shapeRenderer.setColor(0, 0, 0, 0.3f);
                    shapeRenderer.circle(tileX + 3, tileY - 5, 18);
                    
                    // Tree trunk with gradient
                    shapeRenderer.setColor(0.35f, 0.22f, 0.08f, 1f);
                    shapeRenderer.rect(tileX - 4, tileY - 3, 8, 14);
                    shapeRenderer.setColor(0.45f, 0.28f, 0.12f, 1f);
                    shapeRenderer.rect(tileX - 3, tileY - 3, 3, 14);
                    
                    // Tree foliage with multiple layers for depth
                    shapeRenderer.setColor(0.08f, 0.4f, 0.08f, 1f);
                    shapeRenderer.circle(tileX, tileY + 10, 16);
                    shapeRenderer.setColor(0.12f, 0.5f, 0.12f, 0.9f);
                    shapeRenderer.circle(tileX - 6, tileY + 12, 11);
                    shapeRenderer.circle(tileX + 6, tileY + 12, 11);
                    shapeRenderer.setColor(0.15f, 0.6f, 0.15f, 0.8f);
                    shapeRenderer.circle(tileX, tileY + 15, 8);
                    // Highlight for depth
                    shapeRenderer.setColor(0.2f, 0.7f, 0.2f, 0.6f);
                    shapeRenderer.circle(tileX - 3, tileY + 13, 5);
                }
                
                // Enhanced rocks with shading
                if ((terrain % 11) == 0) {
                    float rockSize = 5 + (terrain % 5);
                    float rockX = tileX + (terrain % 10 - 5);
                    float rockY = tileY + (terrain % 8 - 4);
                    
                    // Rock shadow
                    shapeRenderer.setColor(0, 0, 0, 0.4f);
                    shapeRenderer.circle(rockX + 2, rockY - 2, rockSize);
                    
                    // Dark base
                    shapeRenderer.setColor(0.35f, 0.35f, 0.35f, 1f);
                    shapeRenderer.circle(rockX, rockY, rockSize);
                    // Light highlight
                    shapeRenderer.setColor(0.55f, 0.55f, 0.55f, 1f);
                    shapeRenderer.circle(rockX - 2, rockY + 1, rockSize * 0.6f);
                    // Bright spot
                    shapeRenderer.setColor(0.65f, 0.65f, 0.65f, 0.8f);
                    shapeRenderer.circle(rockX - 1, rockY + 2, rockSize * 0.3f);
                }
                
                // Enhanced flowers with stems
                if ((terrain % 13) == 0 && (terrain % 2) == 0) {
                    float flowerX = tileX + (terrain % 15 - 7);
                    float flowerY = tileY + (terrain % 12 - 6);
                    
                    // Flower stem
                    shapeRenderer.setColor(0.2f, 0.5f, 0.2f, 1f);
                    shapeRenderer.rectLine(flowerX, flowerY - 4, flowerX, flowerY + 2, 1);
                    
                    // Flower petals with variation
                    if ((terrain % 3) == 0) {
                        shapeRenderer.setColor(1f, 0.2f, 0.2f, 1f); // Red
                    } else if ((terrain % 3) == 1) {
                        shapeRenderer.setColor(1f, 0.85f, 0.1f, 1f); // Yellow
                    } else {
                        shapeRenderer.setColor(0.75f, 0.3f, 1f, 1f); // Purple
                    }
                    shapeRenderer.circle(flowerX, flowerY + 2, 3.5f);
                    // Center
                    shapeRenderer.setColor(1f, 0.9f, 0.3f, 1f);
                    shapeRenderer.circle(flowerX, flowerY + 2, 1.5f);
                }
                
                // Add bushes for variety
                if ((terrain % 17) == 0 && x > 8 && y > 8) {
                    float bushX = tileX + (terrain % 12 - 6);
                    float bushY = tileY + (terrain % 10 - 5);
                    
                    shapeRenderer.setColor(0.15f, 0.45f, 0.15f, 0.8f);
                    shapeRenderer.circle(bushX, bushY, 6);
                    shapeRenderer.setColor(0.2f, 0.55f, 0.2f, 0.7f);
                    shapeRenderer.circle(bushX - 3, bushY + 1, 4);
                    shapeRenderer.circle(bushX + 3, bushY + 1, 4);
                }
            }
        }
        
        shapeRenderer.end();
    }
    
    /**
     * Dispose of resources
     */
    public void dispose() {
        // AssetManager handles disposal
    }
}
