package com.mmo.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;

/**
 * Generates 3D-style textures programmatically for characters, buildings, and world objects
 */
public class TextureGenerator {
    
    /**
     * Generate a 3D-style character sprite with isometric perspective
     */
    public static Texture generateCharacterSprite(String className, int direction) {
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Draw character based on class and direction
        switch (className.toLowerCase()) {
            case "warrior":
                drawWarrior(pixmap, size, direction);
                break;
            case "mage":
                drawMage(pixmap, size, direction);
                break;
            case "archer":
                drawArcher(pixmap, size, direction);
                break;
            default:
                drawWarrior(pixmap, size, direction);
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private static void drawWarrior(Pixmap pixmap, int size, int direction) {
        int centerX = size / 2;
        int centerY = size / 2;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, centerY - 20, 18, 8);
        
        // Legs (isometric view)
        pixmap.setColor(0.3f, 0.2f, 0.1f, 1f);
        fillRect(pixmap, centerX - 8, centerY - 15, 6, 12);
        fillRect(pixmap, centerX + 2, centerY - 15, 6, 12);
        
        // Body armor - metallic gray with highlights
        pixmap.setColor(0.5f, 0.5f, 0.6f, 1f);
        fillRect(pixmap, centerX - 10, centerY - 5, 20, 18);
        
        // Armor highlights (3D effect)
        pixmap.setColor(0.7f, 0.7f, 0.8f, 1f);
        fillRect(pixmap, centerX - 9, centerY - 4, 8, 2);
        fillRect(pixmap, centerX - 9, centerY, 3, 10);
        
        // Armor shadows
        pixmap.setColor(0.3f, 0.3f, 0.4f, 1f);
        fillRect(pixmap, centerX + 6, centerY - 2, 3, 14);
        
        // Arms
        pixmap.setColor(0.5f, 0.5f, 0.6f, 1f);
        if (direction % 2 == 0) { // Side view
            fillRect(pixmap, centerX - 14, centerY - 2, 4, 12);
            fillRect(pixmap, centerX + 10, centerY - 2, 4, 12);
        } else { // Front/back view
            fillRect(pixmap, centerX - 12, centerY, 4, 10);
            fillRect(pixmap, centerX + 8, centerY, 4, 10);
        }
        
        // Head with helmet
        pixmap.setColor(0.8f, 0.6f, 0.5f, 1f); // Skin tone
        fillCircle(pixmap, centerX, centerY + 16, 8);
        
        // Helmet
        pixmap.setColor(0.5f, 0.5f, 0.6f, 1f);
        fillCircle(pixmap, centerX, centerY + 18, 9);
        pixmap.setColor(0.7f, 0.7f, 0.8f, 1f);
        fillCircle(pixmap, centerX - 3, centerY + 20, 3);
        
        // Shield (on side)
        if (direction == 2 || direction == 6) {
            pixmap.setColor(0.6f, 0.2f, 0.1f, 1f);
            fillCircle(pixmap, centerX + 16, centerY + 5, 10);
            pixmap.setColor(0.8f, 0.7f, 0.2f, 1f);
            drawCircle(pixmap, centerX + 16, centerY + 5, 8);
        }
    }
    
    private static void drawMage(Pixmap pixmap, int size, int direction) {
        int centerX = size / 2;
        int centerY = size / 2;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, centerY - 20, 16, 7);
        
        // Robe - flowing effect
        pixmap.setColor(0.2f, 0.1f, 0.5f, 1f); // Purple robe
        fillRect(pixmap, centerX - 12, centerY - 18, 24, 22);
        fillTriangle(pixmap, centerX - 14, centerY - 18, centerX - 8, centerY - 18, centerX - 14, centerY - 10);
        fillTriangle(pixmap, centerX + 14, centerY - 18, centerX + 8, centerY - 18, centerX + 14, centerY - 10);
        
        // Robe highlights (3D folds)
        pixmap.setColor(0.3f, 0.2f, 0.7f, 1f);
        fillRect(pixmap, centerX - 10, centerY - 16, 6, 18);
        fillRect(pixmap, centerX - 4, centerY - 14, 3, 16);
        
        // Robe shadows
        pixmap.setColor(0.1f, 0.05f, 0.3f, 1f);
        fillRect(pixmap, centerX + 4, centerY - 16, 6, 18);
        
        // Belt with magical glow
        pixmap.setColor(0.8f, 0.6f, 0.2f, 1f);
        fillRect(pixmap, centerX - 12, centerY + 2, 24, 3);
        pixmap.setColor(0.4f, 0.8f, 1f, 0.6f);
        fillCircle(pixmap, centerX, centerY + 3, 4);
        
        // Arms with sleeves
        pixmap.setColor(0.2f, 0.1f, 0.5f, 1f);
        if (direction % 2 == 0) {
            fillRect(pixmap, centerX - 16, centerY, 5, 10);
            fillRect(pixmap, centerX + 11, centerY, 5, 10);
        } else {
            fillRect(pixmap, centerX - 14, centerY + 2, 5, 8);
            fillRect(pixmap, centerX + 9, centerY + 2, 5, 8);
        }
        
        // Hands
        pixmap.setColor(0.8f, 0.6f, 0.5f, 1f);
        fillCircle(pixmap, centerX - 13, centerY + 10, 3);
        fillCircle(pixmap, centerX + 13, centerY + 10, 3);
        
        // Magical orb in hand
        pixmap.setColor(0.3f, 0.7f, 1f, 0.8f);
        fillCircle(pixmap, centerX + 13, centerY + 10, 5);
        pixmap.setColor(0.6f, 0.9f, 1f, 0.9f);
        fillCircle(pixmap, centerX + 11, centerY + 12, 2);
        
        // Head
        pixmap.setColor(0.8f, 0.6f, 0.5f, 1f);
        fillCircle(pixmap, centerX, centerY + 16, 8);
        
        // Wizard hat
        pixmap.setColor(0.2f, 0.1f, 0.5f, 1f);
        fillTriangle(pixmap, centerX - 10, centerY + 20, centerX + 10, centerY + 20, centerX, centerY + 38);
        pixmap.setColor(0.3f, 0.2f, 0.7f, 1f);
        fillTriangle(pixmap, centerX - 8, centerY + 22, centerX, centerY + 22, centerX - 2, centerY + 35);
        
        // Hat brim
        pixmap.setColor(0.2f, 0.1f, 0.5f, 1f);
        fillRect(pixmap, centerX - 12, centerY + 18, 24, 3);
    }
    
    private static void drawArcher(Pixmap pixmap, int size, int direction) {
        int centerX = size / 2;
        int centerY = size / 2;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, centerY - 20, 17, 7);
        
        // Legs - leather pants
        pixmap.setColor(0.4f, 0.3f, 0.2f, 1f);
        fillRect(pixmap, centerX - 7, centerY - 15, 5, 12);
        fillRect(pixmap, centerX + 2, centerY - 15, 5, 12);
        
        // Body - leather armor with green tunic
        pixmap.setColor(0.2f, 0.5f, 0.2f, 1f);
        fillRect(pixmap, centerX - 9, centerY - 5, 18, 16);
        
        // Leather vest
        pixmap.setColor(0.45f, 0.35f, 0.2f, 1f);
        fillRect(pixmap, centerX - 8, centerY - 4, 16, 14);
        
        // Vest highlights (3D effect)
        pixmap.setColor(0.55f, 0.45f, 0.3f, 1f);
        fillRect(pixmap, centerX - 7, centerY - 3, 6, 2);
        fillRect(pixmap, centerX - 7, centerY + 2, 3, 8);
        
        // Vest shadows
        pixmap.setColor(0.3f, 0.25f, 0.15f, 1f);
        fillRect(pixmap, centerX + 4, centerY - 2, 3, 12);
        
        // Arms
        pixmap.setColor(0.2f, 0.5f, 0.2f, 1f);
        if (direction == 2 || direction == 6) { // Side view
            fillRect(pixmap, centerX - 13, centerY - 2, 4, 12);
            fillRect(pixmap, centerX + 9, centerY - 2, 4, 12);
        } else {
            fillRect(pixmap, centerX - 11, centerY, 4, 10);
            fillRect(pixmap, centerX + 7, centerY, 4, 10);
        }
        
        // Quiver with arrows
        pixmap.setColor(0.4f, 0.25f, 0.1f, 1f);
        fillRect(pixmap, centerX + 8, centerY + 8, 6, 12);
        
        // Arrow feathers
        pixmap.setColor(0.8f, 0.3f, 0.2f, 1f);
        fillRect(pixmap, centerX + 9, centerY + 18, 1, 4);
        fillRect(pixmap, centerX + 11, centerY + 18, 1, 4);
        fillRect(pixmap, centerX + 13, centerY + 18, 1, 4);
        
        // Head
        pixmap.setColor(0.8f, 0.6f, 0.5f, 1f);
        fillCircle(pixmap, centerX, centerY + 16, 7);
        
        // Hood
        pixmap.setColor(0.15f, 0.4f, 0.15f, 1f);
        fillCircle(pixmap, centerX, centerY + 18, 9);
        fillTriangle(pixmap, centerX - 8, centerY + 22, centerX + 8, centerY + 22, centerX, centerY + 28);
        
        // Bow (in hand for side view)
        if (direction == 2 || direction == 6) {
            pixmap.setColor(0.5f, 0.35f, 0.2f, 1f);
            drawLine(pixmap, centerX - 18, centerY, centerX - 18, centerY + 20, 2);
            pixmap.setColor(0.9f, 0.9f, 0.8f, 1f);
            drawLine(pixmap, centerX - 18, centerY + 1, centerX - 18, centerY + 19, 1);
        }
    }
    
    /**
     * Generate 3D-style building sprite
     */
    public static Texture generateBuilding(String buildingType) {
        int width = 128;
        int height = 128;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        switch (buildingType.toLowerCase()) {
            case "house":
                drawHouse(pixmap, width, height);
                break;
            case "shop":
                drawShop(pixmap, width, height);
                break;
            case "tower":
                drawTower(pixmap, width, height);
                break;
            case "castle":
                drawCastle(pixmap, width, height);
                break;
            default:
                drawHouse(pixmap, width, height);
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private static void drawHouse(Pixmap pixmap, int width, int height) {
        int centerX = width / 2;
        int baseY = 30;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, baseY - 5, 50, 15);
        
        // House base (stone foundation)
        pixmap.setColor(0.5f, 0.5f, 0.5f, 1f);
        fillRect(pixmap, centerX - 40, baseY, 80, 8);
        pixmap.setColor(0.6f, 0.6f, 0.6f, 1f);
        fillRect(pixmap, centerX - 38, baseY + 1, 30, 6);
        
        // Walls - isometric wooden planks
        pixmap.setColor(0.6f, 0.4f, 0.2f, 1f);
        fillRect(pixmap, centerX - 38, baseY + 8, 76, 50);
        
        // Wood grain effect
        pixmap.setColor(0.7f, 0.5f, 0.3f, 1f);
        for (int i = 0; i < 5; i++) {
            fillRect(pixmap, centerX - 36, baseY + 10 + i * 10, 72, 2);
        }
        
        // Wall shadows
        pixmap.setColor(0.45f, 0.3f, 0.15f, 1f);
        fillRect(pixmap, centerX + 28, baseY + 8, 10, 50);
        
        // Door
        pixmap.setColor(0.3f, 0.2f, 0.1f, 1f);
        fillRect(pixmap, centerX - 10, baseY + 8, 20, 35);
        pixmap.setColor(0.4f, 0.3f, 0.15f, 1f);
        fillRect(pixmap, centerX - 9, baseY + 9, 8, 33);
        
        // Door handle
        pixmap.setColor(0.8f, 0.7f, 0.2f, 1f);
        fillCircle(pixmap, centerX + 6, baseY + 28, 2);
        
        // Windows
        pixmap.setColor(0.4f, 0.6f, 0.8f, 0.7f);
        fillRect(pixmap, centerX - 30, baseY + 30, 15, 18);
        fillRect(pixmap, centerX + 15, baseY + 30, 15, 18);
        
        // Window frames
        pixmap.setColor(0.5f, 0.35f, 0.2f, 1f);
        drawRect(pixmap, centerX - 30, baseY + 30, 15, 18, 2);
        drawRect(pixmap, centerX + 15, baseY + 30, 15, 18, 2);
        drawLine(pixmap, centerX - 30, baseY + 39, centerX - 15, baseY + 39, 2);
        drawLine(pixmap, centerX + 15, baseY + 39, centerX + 30, baseY + 39, 2);
        
        // Roof - isometric view
        pixmap.setColor(0.5f, 0.2f, 0.1f, 1f);
        fillTriangle(pixmap, centerX - 45, baseY + 58, centerX + 45, baseY + 58, centerX, baseY + 95);
        
        // Roof tiles (3D effect)
        pixmap.setColor(0.6f, 0.3f, 0.15f, 1f);
        for (int i = 0; i < 6; i++) {
            int y = baseY + 60 + i * 6;
            int xOffset = i * 7;
            fillTriangle(pixmap, centerX - 43 + xOffset, y, centerX - 7 + xOffset, y, centerX - 25 + xOffset, y + 5);
        }
        
        // Roof shadow side
        pixmap.setColor(0.4f, 0.15f, 0.08f, 1f);
        fillTriangle(pixmap, centerX, baseY + 58, centerX + 45, baseY + 58, centerX, baseY + 95);
        
        // Chimney
        pixmap.setColor(0.5f, 0.3f, 0.2f, 1f);
        fillRect(pixmap, centerX + 15, baseY + 70, 12, 20);
        pixmap.setColor(0.6f, 0.4f, 0.3f, 1f);
        fillRect(pixmap, centerX + 16, baseY + 71, 5, 18);
        pixmap.setColor(0.3f, 0.2f, 0.15f, 1f);
        fillRect(pixmap, centerX + 15, baseY + 88, 12, 3);
        
        // Smoke
        pixmap.setColor(0.7f, 0.7f, 0.7f, 0.5f);
        fillCircle(pixmap, centerX + 21, baseY + 95, 4);
        fillCircle(pixmap, centerX + 19, baseY + 100, 5);
        fillCircle(pixmap, centerX + 23, baseY + 105, 4);
    }
    
    private static void drawShop(Pixmap pixmap, int width, int height) {
        int centerX = width / 2;
        int baseY = 25;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, baseY - 5, 55, 18);
        
        // Base
        pixmap.setColor(0.55f, 0.45f, 0.35f, 1f);
        fillRect(pixmap, centerX - 45, baseY, 90, 10);
        
        // Walls - brick pattern
        pixmap.setColor(0.7f, 0.5f, 0.4f, 1f);
        fillRect(pixmap, centerX - 43, baseY + 10, 86, 55);
        
        // Brick texture
        pixmap.setColor(0.6f, 0.4f, 0.3f, 1f);
        for (int y = 0; y < 55; y += 8) {
            for (int x = 0; x < 86; x += 16) {
                int offsetX = (y % 16 == 0) ? 0 : 8;
                fillRect(pixmap, centerX - 43 + x + offsetX, baseY + 10 + y, 15, 7);
            }
        }
        
        // Large shop window
        pixmap.setColor(0.3f, 0.5f, 0.7f, 0.8f);
        fillRect(pixmap, centerX - 35, baseY + 20, 70, 35);
        
        // Window frame
        pixmap.setColor(0.5f, 0.4f, 0.3f, 1f);
        drawRect(pixmap, centerX - 35, baseY + 20, 70, 35, 3);
        drawLine(pixmap, centerX, baseY + 20, centerX, baseY + 55, 3);
        drawLine(pixmap, centerX - 35, baseY + 37, centerX + 35, baseY + 37, 3);
        
        // Door
        pixmap.setColor(0.4f, 0.25f, 0.15f, 1f);
        fillRect(pixmap, centerX - 12, baseY + 10, 24, 40);
        pixmap.setColor(0.5f, 0.35f, 0.25f, 1f);
        fillRect(pixmap, centerX - 11, baseY + 11, 10, 38);
        
        // Awning (shop canopy)
        pixmap.setColor(0.8f, 0.3f, 0.2f, 1f);
        fillRect(pixmap, centerX - 50, baseY + 65, 100, 8);
        fillTriangle(pixmap, centerX - 50, baseY + 65, centerX - 50, baseY + 73, centerX - 45, baseY + 68);
        fillTriangle(pixmap, centerX + 50, baseY + 65, centerX + 50, baseY + 73, centerX + 45, baseY + 68);
        
        // Awning stripes
        pixmap.setColor(1f, 1f, 1f, 1f);
        for (int i = 0; i < 5; i++) {
            fillRect(pixmap, centerX - 45 + i * 22, baseY + 65, 10, 8);
        }
        
        // Sign
        pixmap.setColor(0.6f, 0.5f, 0.3f, 1f);
        fillRect(pixmap, centerX - 30, baseY + 75, 60, 18);
        pixmap.setColor(0.7f, 0.6f, 0.4f, 1f);
        fillRect(pixmap, centerX - 28, baseY + 77, 56, 14);
        
        // Sign text representation
        pixmap.setColor(0.3f, 0.2f, 0.1f, 1f);
        fillRect(pixmap, centerX - 22, baseY + 81, 44, 3);
        fillRect(pixmap, centerX - 15, baseY + 86, 30, 2);
    }
    
    private static void drawTower(Pixmap pixmap, int width, int height) {
        int centerX = width / 2;
        int baseY = 20;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, baseY - 5, 35, 12);
        
        // Tower base
        pixmap.setColor(0.5f, 0.5f, 0.5f, 1f);
        fillRect(pixmap, centerX - 28, baseY, 56, 15);
        
        // Stone blocks
        pixmap.setColor(0.55f, 0.55f, 0.55f, 1f);
        for (int i = 0; i < 3; i++) {
            fillRect(pixmap, centerX - 26 + i * 18, baseY + 1, 16, 13);
        }
        
        // Main tower body
        pixmap.setColor(0.6f, 0.6f, 0.6f, 1f);
        fillRect(pixmap, centerX - 25, baseY + 15, 50, 70);
        
        // Stone texture
        pixmap.setColor(0.65f, 0.65f, 0.65f, 1f);
        for (int y = 0; y < 70; y += 12) {
            for (int x = 0; x < 50; x += 15) {
                int offsetX = (y % 24 == 0) ? 0 : 7;
                fillRect(pixmap, centerX - 25 + x + offsetX, baseY + 15 + y, 14, 11);
            }
        }
        
        // Tower shadow side
        pixmap.setColor(0.45f, 0.45f, 0.45f, 1f);
        fillRect(pixmap, centerX + 18, baseY + 15, 7, 70);
        
        // Windows
        pixmap.setColor(0.2f, 0.2f, 0.3f, 1f);
        for (int i = 0; i < 3; i++) {
            int windowY = baseY + 25 + i * 20;
            fillRect(pixmap, centerX - 5, windowY, 10, 15);
            // Window light
            pixmap.setColor(0.9f, 0.8f, 0.4f, 0.6f);
            fillRect(pixmap, centerX - 3, windowY + 2, 6, 11);
            pixmap.setColor(0.2f, 0.2f, 0.3f, 1f);
        }
        
        // Battlements
        pixmap.setColor(0.6f, 0.6f, 0.6f, 1f);
        for (int i = 0; i < 6; i++) {
            fillRect(pixmap, centerX - 28 + i * 11, baseY + 85, 9, 12);
        }
        
        // Battlement highlights
        pixmap.setColor(0.7f, 0.7f, 0.7f, 1f);
        for (int i = 0; i < 6; i++) {
            fillRect(pixmap, centerX - 27 + i * 11, baseY + 86, 4, 10);
        }
        
        // Roof
        pixmap.setColor(0.4f, 0.2f, 0.2f, 1f);
        fillTriangle(pixmap, centerX - 30, baseY + 97, centerX + 30, baseY + 97, centerX, baseY + 118);
        
        // Roof highlight
        pixmap.setColor(0.5f, 0.3f, 0.3f, 1f);
        fillTriangle(pixmap, centerX - 28, baseY + 97, centerX, baseY + 97, centerX - 14, baseY + 107);
        
        // Flag pole
        pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
        fillRect(pixmap, centerX - 1, baseY + 115, 2, 12);
        
        // Flag
        pixmap.setColor(0.8f, 0.2f, 0.2f, 1f);
        fillTriangle(pixmap, centerX + 1, baseY + 125, centerX + 1, baseY + 117, centerX + 10, baseY + 121);
    }
    
    private static void drawCastle(Pixmap pixmap, int width, int height) {
        int centerX = width / 2;
        int baseY = 15;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, baseY - 3, 60, 20);
        
        // Main castle wall
        pixmap.setColor(0.6f, 0.6f, 0.65f, 1f);
        fillRect(pixmap, centerX - 50, baseY, 100, 60);
        
        // Stone blocks
        pixmap.setColor(0.65f, 0.65f, 0.7f, 1f);
        for (int y = 0; y < 60; y += 10) {
            for (int x = 0; x < 100; x += 20) {
                int offsetX = (y % 20 == 0) ? 0 : 10;
                fillRect(pixmap, centerX - 50 + x + offsetX, baseY + y, 19, 9);
            }
        }
        
        // Castle gate
        pixmap.setColor(0.25f, 0.2f, 0.15f, 1f);
        fillRect(pixmap, centerX - 15, baseY, 30, 40);
        
        // Gate details (portcullis)
        pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
        for (int i = 0; i < 5; i++) {
            fillRect(pixmap, centerX - 12 + i * 6, baseY, 2, 40);
        }
        for (int i = 0; i < 8; i++) {
            fillRect(pixmap, centerX - 15, baseY + i * 5, 30, 2);
        }
        
        // Side towers
        pixmap.setColor(0.55f, 0.55f, 0.6f, 1f);
        fillRect(pixmap, centerX - 60, baseY + 10, 25, 70);
        fillRect(pixmap, centerX + 35, baseY + 10, 25, 70);
        
        // Tower stone texture
        pixmap.setColor(0.6f, 0.6f, 0.65f, 1f);
        for (int y = 0; y < 70; y += 10) {
            fillRect(pixmap, centerX - 58, baseY + 10 + y, 21, 9);
            fillRect(pixmap, centerX + 37, baseY + 10 + y, 21, 9);
        }
        
        // Battlements on main wall
        pixmap.setColor(0.6f, 0.6f, 0.65f, 1f);
        for (int i = 0; i < 9; i++) {
            fillRect(pixmap, centerX - 48 + i * 12, baseY + 60, 10, 10);
        }
        
        // Tower battlements
        for (int i = 0; i < 3; i++) {
            fillRect(pixmap, centerX - 58 + i * 11, baseY + 80, 9, 8);
            fillRect(pixmap, centerX + 37 + i * 11, baseY + 80, 9, 8);
        }
        
        // Tower roofs
        pixmap.setColor(0.5f, 0.25f, 0.25f, 1f);
        fillTriangle(pixmap, centerX - 62, baseY + 88, centerX - 33, baseY + 88, centerX - 47.5f, baseY + 108);
        fillTriangle(pixmap, centerX + 33, baseY + 88, centerX + 62, baseY + 88, centerX + 47.5f, baseY + 108);
        
        // Windows in towers
        pixmap.setColor(0.9f, 0.8f, 0.4f, 0.7f);
        fillRect(pixmap, centerX - 50, baseY + 45, 8, 12);
        fillRect(pixmap, centerX + 42, baseY + 45, 8, 12);
        fillRect(pixmap, centerX - 50, baseY + 25, 8, 12);
        fillRect(pixmap, centerX + 42, baseY + 25, 8, 12);
    }
    
    /**
     * Generate environment object textures
     */
    public static Texture generateEnvironmentObject(String objectType) {
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        switch (objectType.toLowerCase()) {
            case "tree":
                drawTree3D(pixmap, size);
                break;
            case "rock":
                drawRock3D(pixmap, size);
                break;
            case "bush":
                drawBush3D(pixmap, size);
                break;
            case "flower":
                drawFlower3D(pixmap, size);
                break;
            default:
                drawTree3D(pixmap, size);
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private static void drawTree3D(Pixmap pixmap, int size) {
        int centerX = size / 2;
        int baseY = 10;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, baseY, 20, 8);
        
        // Trunk with volume
        pixmap.setColor(0.35f, 0.25f, 0.15f, 1f);
        fillRect(pixmap, centerX - 5, baseY + 5, 10, 20);
        
        // Trunk highlight (3D effect)
        pixmap.setColor(0.45f, 0.32f, 0.2f, 1f);
        fillRect(pixmap, centerX - 4, baseY + 6, 4, 18);
        
        // Trunk shadow
        pixmap.setColor(0.25f, 0.18f, 0.1f, 1f);
        fillRect(pixmap, centerX + 2, baseY + 5, 3, 20);
        
        // Foliage layers (3D depth)
        pixmap.setColor(0.1f, 0.4f, 0.1f, 1f);
        fillCircle(pixmap, centerX, baseY + 25, 18);
        
        pixmap.setColor(0.15f, 0.5f, 0.15f, 1f);
        fillCircle(pixmap, centerX - 8, baseY + 28, 14);
        fillCircle(pixmap, centerX + 8, baseY + 28, 14);
        
        pixmap.setColor(0.2f, 0.6f, 0.2f, 1f);
        fillCircle(pixmap, centerX, baseY + 32, 12);
        
        // Light highlight on foliage
        pixmap.setColor(0.3f, 0.75f, 0.3f, 0.8f);
        fillCircle(pixmap, centerX - 5, baseY + 30, 6);
        fillCircle(pixmap, centerX + 3, baseY + 34, 4);
    }
    
    private static void drawRock3D(Pixmap pixmap, int size) {
        int centerX = size / 2;
        int centerY = size / 2;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.35f);
        fillEllipse(pixmap, centerX + 2, centerY - 8, 18, 7);
        
        // Main rock body
        pixmap.setColor(0.45f, 0.45f, 0.45f, 1f);
        fillCircle(pixmap, centerX, centerY, 14);
        
        // Rock facets (angular look)
        pixmap.setColor(0.5f, 0.5f, 0.5f, 1f);
        fillTriangle(pixmap, centerX - 10, centerY - 5, centerX - 2, centerY - 12, centerX + 4, centerY - 6);
        fillTriangle(pixmap, centerX - 12, centerY + 2, centerX - 4, centerY - 4, centerX - 2, centerY + 8);
        
        // Highlights
        pixmap.setColor(0.65f, 0.65f, 0.65f, 1f);
        fillCircle(pixmap, centerX - 4, centerY + 2, 5);
        fillTriangle(pixmap, centerX + 2, centerY - 8, centerX + 8, centerY - 4, centerX + 6, centerY);
        
        // Shadows
        pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
        fillTriangle(pixmap, centerX + 4, centerY + 4, centerX + 10, centerY + 2, centerX + 8, centerY + 10);
        fillCircle(pixmap, centerX + 6, centerY + 6, 4);
    }
    
    private static void drawBush3D(Pixmap pixmap, int size) {
        int centerX = size / 2;
        int baseY = 15;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.3f);
        fillEllipse(pixmap, centerX, baseY, 22, 8);
        
        // Bush clusters
        pixmap.setColor(0.2f, 0.5f, 0.2f, 1f);
        fillCircle(pixmap, centerX - 8, baseY + 10, 10);
        fillCircle(pixmap, centerX + 8, baseY + 10, 10);
        fillCircle(pixmap, centerX, baseY + 12, 12);
        
        // Lighter top clusters
        pixmap.setColor(0.25f, 0.6f, 0.25f, 1f);
        fillCircle(pixmap, centerX - 6, baseY + 15, 8);
        fillCircle(pixmap, centerX + 6, baseY + 15, 8);
        
        // Highlights
        pixmap.setColor(0.35f, 0.7f, 0.35f, 0.7f);
        fillCircle(pixmap, centerX - 4, baseY + 16, 4);
        fillCircle(pixmap, centerX + 2, baseY + 18, 3);
        
        // Berries
        pixmap.setColor(0.8f, 0.2f, 0.2f, 1f);
        fillCircle(pixmap, centerX - 10, baseY + 12, 2);
        fillCircle(pixmap, centerX + 9, baseY + 14, 2);
        fillCircle(pixmap, centerX - 2, baseY + 10, 2);
    }
    
    private static void drawFlower3D(Pixmap pixmap, int size) {
        int centerX = size / 2;
        int baseY = 10;
        
        // Shadow
        pixmap.setColor(0, 0, 0, 0.25f);
        fillEllipse(pixmap, centerX, baseY, 8, 3);
        
        // Stem
        pixmap.setColor(0.2f, 0.6f, 0.2f, 1f);
        fillRect(pixmap, centerX - 1, baseY + 2, 2, 20);
        
        // Leaves
        pixmap.setColor(0.25f, 0.65f, 0.25f, 1f);
        fillCircle(pixmap, centerX - 5, baseY + 10, 4);
        fillCircle(pixmap, centerX + 5, baseY + 15, 4);
        
        // Flower petals
        pixmap.setColor(0.9f, 0.3f, 0.4f, 1f);
        fillCircle(pixmap, centerX, baseY + 26, 5);
        fillCircle(pixmap, centerX - 5, baseY + 22, 4);
        fillCircle(pixmap, centerX + 5, baseY + 22, 4);
        fillCircle(pixmap, centerX - 4, baseY + 30, 4);
        fillCircle(pixmap, centerX + 4, baseY + 30, 4);
        
        // Flower center
        pixmap.setColor(0.95f, 0.85f, 0.2f, 1f);
        fillCircle(pixmap, centerX, baseY + 26, 3);
    }
    
    /**
     * Generate ground/terrain texture
     */
    public static Texture generateTerrainTile(String terrainType) {
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        switch (terrainType.toLowerCase()) {
            case "grass":
                drawGrassTile(pixmap, size);
                break;
            case "dirt":
                drawDirtTile(pixmap, size);
                break;
            case "stone":
                drawStoneTile(pixmap, size);
                break;
            case "water":
                drawWaterTile(pixmap, size);
                break;
            default:
                drawGrassTile(pixmap, size);
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private static void drawGrassTile(Pixmap pixmap, int size) {
        // Base grass color
        pixmap.setColor(0.25f, 0.6f, 0.25f, 1f);
        pixmap.fill();
        
        // Grass variation patches
        pixmap.setColor(0.22f, 0.55f, 0.22f, 1f);
        for (int i = 0; i < 15; i++) {
            int x = (i * 7 + 3) % size;
            int y = (i * 11 + 5) % size;
            fillCircle(pixmap, x, y, 3 + (i % 3));
        }
        
        // Lighter patches
        pixmap.setColor(0.28f, 0.65f, 0.28f, 0.7f);
        for (int i = 0; i < 10; i++) {
            int x = (i * 13 + 7) % size;
            int y = (i * 17 + 9) % size;
            fillCircle(pixmap, x, y, 2 + (i % 2));
        }
        
        // Grass blades
        pixmap.setColor(0.15f, 0.5f, 0.15f, 0.6f);
        for (int i = 0; i < 30; i++) {
            int x = (i * 5 + 2) % size;
            int y = (i * 7 + 4) % size;
            fillRect(pixmap, x, y, 1, 3);
        }
    }
    
    private static void drawDirtTile(Pixmap pixmap, int size) {
        // Base dirt color
        pixmap.setColor(0.5f, 0.4f, 0.25f, 1f);
        pixmap.fill();
        
        // Darker patches
        pixmap.setColor(0.45f, 0.35f, 0.22f, 1f);
        for (int i = 0; i < 20; i++) {
            int x = (i * 9 + 4) % size;
            int y = (i * 13 + 6) % size;
            fillCircle(pixmap, x, y, 2 + (i % 4));
        }
        
        // Lighter patches
        pixmap.setColor(0.55f, 0.45f, 0.28f, 1f);
        for (int i = 0; i < 15; i++) {
            int x = (i * 11 + 7) % size;
            int y = (i * 19 + 3) % size;
            fillCircle(pixmap, x, y, 2 + (i % 3));
        }
        
        // Small rocks/pebbles
        pixmap.setColor(0.4f, 0.4f, 0.4f, 1f);
        for (int i = 0; i < 8; i++) {
            int x = (i * 17 + 5) % size;
            int y = (i * 23 + 8) % size;
            fillCircle(pixmap, x, y, 1 + (i % 2));
        }
    }
    
    private static void drawStoneTile(Pixmap pixmap, int size) {
        // Base stone color
        pixmap.setColor(0.55f, 0.55f, 0.55f, 1f);
        pixmap.fill();
        
        // Stone blocks pattern
        pixmap.setColor(0.5f, 0.5f, 0.5f, 1f);
        for (int x = 0; x < size; x += 16) {
            for (int y = 0; y < size; y += 16) {
                fillRect(pixmap, x, y, 15, 15);
            }
        }
        
        // Mortar lines
        pixmap.setColor(0.45f, 0.45f, 0.45f, 1f);
        for (int x = 0; x < size; x += 16) {
            fillRect(pixmap, x, 0, 1, size);
        }
        for (int y = 0; y < size; y += 16) {
            fillRect(pixmap, 0, y, size, 1);
        }
        
        // Highlights
        pixmap.setColor(0.65f, 0.65f, 0.65f, 1f);
        for (int x = 0; x < size; x += 16) {
            for (int y = 0; y < size; y += 16) {
                fillRect(pixmap, x + 1, y + 1, 6, 6);
            }
        }
    }
    
    private static void drawWaterTile(Pixmap pixmap, int size) {
        // Base water color
        pixmap.setColor(0.15f, 0.4f, 0.8f, 1f);
        pixmap.fill();
        
        // Water waves (darker)
        pixmap.setColor(0.1f, 0.3f, 0.7f, 1f);
        for (int i = 0; i < 8; i++) {
            int y = i * 8 + 2;
            for (int x = 0; x < size; x += 4) {
                fillCircle(pixmap, x + (i % 4), y, 2);
            }
        }
        
        // Water highlights (lighter)
        pixmap.setColor(0.25f, 0.55f, 0.95f, 0.8f);
        for (int i = 0; i < 6; i++) {
            int x = (i * 13 + 5) % size;
            int y = (i * 17 + 7) % size;
            fillCircle(pixmap, x, y, 3);
        }
        
        // Shimmer spots
        pixmap.setColor(0.5f, 0.7f, 1f, 0.6f);
        for (int i = 0; i < 4; i++) {
            int x = (i * 19 + 10) % size;
            int y = (i * 23 + 12) % size;
            fillCircle(pixmap, x, y, 2);
        }
    }
    
    // Helper drawing functions
    private static void fillCircle(Pixmap pixmap, float centerX, float centerY, float radius) {
        pixmap.fillCircle((int)centerX, (int)centerY, (int)radius);
    }
    
    private static void drawCircle(Pixmap pixmap, float centerX, float centerY, float radius) {
        pixmap.drawCircle((int)centerX, (int)centerY, (int)radius);
    }
    
    private static void fillRect(Pixmap pixmap, float x, float y, float width, float height) {
        pixmap.fillRectangle((int)x, (int)y, (int)width, (int)height);
    }
    
    private static void drawRect(Pixmap pixmap, float x, float y, float width, float height, int thickness) {
        for (int i = 0; i < thickness; i++) {
            pixmap.drawRectangle((int)x + i, (int)y + i, (int)width - 2*i, (int)height - 2*i);
        }
    }
    
    private static void fillTriangle(Pixmap pixmap, float x1, float y1, float x2, float y2, float x3, float y3) {
        pixmap.fillTriangle((int)x1, (int)y1, (int)x2, (int)y2, (int)x3, (int)y3);
    }
    
    private static void fillEllipse(Pixmap pixmap, float centerX, float centerY, float radiusX, float radiusY) {
        for (int y = -(int)radiusY; y <= radiusY; y++) {
            for (int x = -(int)radiusX; x <= radiusX; x++) {
                if ((x * x) / (radiusX * radiusX) + (y * y) / (radiusY * radiusY) <= 1) {
                    pixmap.drawPixel((int)centerX + x, (int)centerY + y);
                }
            }
        }
    }
    
    private static void drawLine(Pixmap pixmap, float x1, float y1, float x2, float y2, int thickness) {
        for (int i = 0; i < thickness; i++) {
            pixmap.drawLine((int)x1, (int)y1 + i, (int)x2, (int)y2 + i);
        }
    }
}
