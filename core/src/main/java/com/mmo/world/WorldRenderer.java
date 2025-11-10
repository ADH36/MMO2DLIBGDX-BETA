package com.mmo.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mmo.graphics.TextureGenerator;

/**
 * Renders the game world with 3D-style textured graphics (NO GRIDS)
 */
public class WorldRenderer {
    private static final int TILE_SIZE = 64;
    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 100;
    
    // Textures
    private Texture grassTile;
    private Texture dirtTile;
    private Texture stoneTile;
    private Texture waterTile;
    
    // Environment objects
    private Texture treeTexture;
    private Texture rockTexture;
    private Texture bushTexture;
    private Texture flowerTexture;
    
    // Buildings
    private Texture houseTexture;
    private Texture shopTexture;
    private Texture towerTexture;
    private Texture castleTexture;
    
    private Array<Building> buildings;
    private SpriteBatch batch;
    
    public WorldRenderer() {
        batch = new SpriteBatch();
        
        // Generate terrain textures
        grassTile = TextureGenerator.generateTerrainTile("grass");
        dirtTile = TextureGenerator.generateTerrainTile("dirt");
        stoneTile = TextureGenerator.generateTerrainTile("stone");
        waterTile = TextureGenerator.generateTerrainTile("water");
        
        // Generate environment object textures
        treeTexture = TextureGenerator.generateEnvironmentObject("tree");
        rockTexture = TextureGenerator.generateEnvironmentObject("rock");
        bushTexture = TextureGenerator.generateEnvironmentObject("bush");
        flowerTexture = TextureGenerator.generateEnvironmentObject("flower");
        
        // Generate building textures
        houseTexture = TextureGenerator.generateBuilding("house");
        shopTexture = TextureGenerator.generateBuilding("shop");
        towerTexture = TextureGenerator.generateBuilding("tower");
        castleTexture = TextureGenerator.generateBuilding("castle");
        
        // Create buildings in the world
        buildings = new Array<>();
        createWorldBuildings();
    }
    
    private void createWorldBuildings() {
        // Town area - central buildings
        buildings.add(new Building("castle", 2500, 2500)); // Central castle
        buildings.add(new Building("shop", 2200, 2300));
        buildings.add(new Building("shop", 2800, 2300));
        buildings.add(new Building("house", 2100, 2150));
        buildings.add(new Building("house", 2350, 2150));
        buildings.add(new Building("house", 2600, 2150));
        buildings.add(new Building("house", 2850, 2150));
        
        // Village area
        buildings.add(new Building("house", 1800, 1900));
        buildings.add(new Building("house", 2000, 1900));
        buildings.add(new Building("shop", 1900, 2050));
        
        // Towers at strategic points
        buildings.add(new Building("tower", 1500, 1500));
        buildings.add(new Building("tower", 3500, 1500));
        buildings.add(new Building("tower", 1500, 3500));
        buildings.add(new Building("tower", 3500, 3500));
        
        // More houses scattered around
        buildings.add(new Building("house", 3200, 2700));
        buildings.add(new Building("house", 3400, 2850));
        buildings.add(new Building("house", 2900, 2900));
        buildings.add(new Building("house", 1700, 2600));
        buildings.add(new Building("house", 1900, 2750));
    }
    
    // Simple noise for terrain variation
    private int getTerrainType(int x, int y) {
        int hash = (x * 374761393 + y * 668265263) & 0x7FFFFFFF;
        hash = (hash ^ 61) ^ (hash >> 16);
        hash = hash + (hash << 3);
        hash = hash ^ (hash >> 4);
        return hash;
    }
    
    public void render(OrthographicCamera camera) {
        // Calculate visible tiles
        int startX = (int) (camera.position.x - camera.viewportWidth / 2) / TILE_SIZE - 1;
        int endX = (int) (camera.position.x + camera.viewportWidth / 2) / TILE_SIZE + 2;
        int startY = (int) (camera.position.y - camera.viewportHeight / 2) / TILE_SIZE - 1;
        int endY = (int) (camera.position.y + camera.viewportHeight / 2) / TILE_SIZE + 2;
        
        // Clamp to world bounds
        startX = Math.max(0, startX);
        endX = Math.min(WORLD_WIDTH, endX);
        startY = Math.max(0, startY);
        endY = Math.min(WORLD_HEIGHT, endY);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Draw terrain tiles (NO GRID LINES)
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                float tileX = x * TILE_SIZE;
                float tileY = y * TILE_SIZE;
                
                int terrain = getTerrainType(x, y);
                Texture tileTexture;
                
                // Water at edges
                if (x < 5 || y < 5 || x > WORLD_WIDTH - 5 || y > WORLD_HEIGHT - 5) {
                    if ((terrain % 5) < 2) {
                        tileTexture = waterTile;
                    } else {
                        tileTexture = grassTile;
                    }
                } else if ((x % 20 == 0 || y % 20 == 0) && Math.abs(x - y) % 3 == 0) {
                    // Dirt paths
                    tileTexture = dirtTile;
                } else {
                    // Grass everywhere else
                    tileTexture = grassTile;
                }
                
                batch.draw(tileTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
            }
        }
        
        // Draw environment decorations
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                float tileX = x * TILE_SIZE;
                float tileY = y * TILE_SIZE;
                int terrain = getTerrainType(x, y);
                
                // Trees
                if ((terrain % 7) == 0 && x > 10 && y > 10 && x < WORLD_WIDTH - 10 && y < WORLD_HEIGHT - 10) {
                    if (!isPositionOccupiedByBuilding(tileX, tileY)) {
                        batch.draw(treeTexture, tileX, tileY, 64, 64);
                    }
                }
                
                // Rocks
                if ((terrain % 11) == 0 && !isPositionOccupiedByBuilding(tileX, tileY)) {
                    batch.draw(rockTexture, tileX + (terrain % 10 - 5), tileY + (terrain % 8 - 4), 64, 64);
                }
                
                // Bushes
                if ((terrain % 17) == 0 && x > 8 && y > 8 && !isPositionOccupiedByBuilding(tileX, tileY)) {
                    batch.draw(bushTexture, tileX + (terrain % 12 - 6), tileY + (terrain % 10 - 5), 64, 64);
                }
                
                // Flowers
                if ((terrain % 13) == 0 && (terrain % 2) == 0 && !isPositionOccupiedByBuilding(tileX, tileY)) {
                    batch.draw(flowerTexture, tileX + (terrain % 15 - 7), tileY + (terrain % 12 - 6), 64, 64);
                }
            }
        }
        
        // Draw buildings
        for (Building building : buildings) {
            // Only draw if in view
            if (building.getX() + building.getWidth() >= startX * TILE_SIZE && 
                building.getX() <= endX * TILE_SIZE &&
                building.getY() + building.getHeight() >= startY * TILE_SIZE && 
                building.getY() <= endY * TILE_SIZE) {
                
                Texture buildingTexture;
                switch (building.getType().toLowerCase()) {
                    case "house":
                        buildingTexture = houseTexture;
                        break;
                    case "shop":
                        buildingTexture = shopTexture;
                        break;
                    case "tower":
                        buildingTexture = towerTexture;
                        break;
                    case "castle":
                        buildingTexture = castleTexture;
                        break;
                    default:
                        buildingTexture = houseTexture;
                }
                
                batch.draw(buildingTexture, building.getX(), building.getY(), 
                          building.getWidth(), building.getHeight());
            }
        }
        
        batch.end();
    }
    
    private boolean isPositionOccupiedByBuilding(float x, float y) {
        for (Building building : buildings) {
            if (x >= building.getX() - 64 && x <= building.getX() + building.getWidth() + 64 &&
                y >= building.getY() - 64 && y <= building.getY() + building.getHeight() + 64) {
                return true;
            }
        }
        return false;
    }
    
    public void dispose() {
        batch.dispose();
        grassTile.dispose();
        dirtTile.dispose();
        stoneTile.dispose();
        waterTile.dispose();
        treeTexture.dispose();
        rockTexture.dispose();
        bushTexture.dispose();
        flowerTexture.dispose();
        houseTexture.dispose();
        shopTexture.dispose();
        towerTexture.dispose();
        castleTexture.dispose();
    }
    
    public Array<Building> getBuildings() {
        return buildings;
    }
}
