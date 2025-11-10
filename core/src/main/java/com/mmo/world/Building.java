package com.mmo.world;

/**
 * Represents a building in the game world
 */
public class Building {
    private String type; // house, shop, tower, castle
    private float x;
    private float y;
    private float width;
    private float height;
    
    public Building(String type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        
        // Set dimensions based on type
        switch (type.toLowerCase()) {
            case "house":
                this.width = 128;
                this.height = 128;
                break;
            case "shop":
                this.width = 128;
                this.height = 128;
                break;
            case "tower":
                this.width = 128;
                this.height = 128;
                break;
            case "castle":
                this.width = 128;
                this.height = 128;
                break;
            default:
                this.width = 128;
                this.height = 128;
        }
    }
    
    public String getType() {
        return type;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
}
