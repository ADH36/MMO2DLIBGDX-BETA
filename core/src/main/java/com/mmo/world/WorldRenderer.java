package com.mmo.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Renders the game world (simple tile-based system)
 */
public class WorldRenderer {
    private static final int TILE_SIZE = 50;
    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 100;
    
    public void render(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
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
        
        // Draw grid
        shapeRenderer.setColor(new Color(0.3f, 0.5f, 0.3f, 0.5f));
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        
        // Draw some decorative elements (trees, rocks, etc.)
        shapeRenderer.setColor(new Color(0.1f, 0.3f, 0.1f, 1f));
        for (int x = startX; x <= endX; x += 5) {
            for (int y = startY; y <= endY; y += 5) {
                if ((x + y) % 3 == 0) {
                    // Draw a "tree"
                    float treeX = x * TILE_SIZE + TILE_SIZE / 2;
                    float treeY = y * TILE_SIZE + TILE_SIZE / 2;
                    shapeRenderer.circle(treeX, treeY, 15);
                }
            }
        }
        
        shapeRenderer.end();
    }
}
