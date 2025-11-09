package com.mmo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mmo.models.CharacterClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates procedural textures for the game assets
 * Creates actual texture assets instead of using shapes
 */
public class AssetGenerator {
    
    private static Map<String, Texture> textureCache = new HashMap<>();
    
    /**
     * Generate grass tile texture with 3D-like appearance
     */
    public static Texture generateGrassTile() {
        if (textureCache.containsKey("grass")) {
            return textureCache.get("grass");
        }
        
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Base grass color with gradient for 3D effect
        for (int y = 0; y < size; y++) {
            float gradient = 1.0f - (y / (float)size) * 0.2f;
            Color baseColor = new Color(0.2f * gradient, 0.6f * gradient, 0.2f * gradient, 1f);
            
            for (int x = 0; x < size; x++) {
                // Add noise for variation
                float noise = (float)Math.sin(x * 0.5) * (float)Math.cos(y * 0.5) * 0.1f;
                Color c = new Color(
                    Math.max(0, Math.min(1, baseColor.r + noise)),
                    Math.max(0, Math.min(1, baseColor.g + noise)),
                    Math.max(0, Math.min(1, baseColor.b + noise)),
                    1f
                );
                pixmap.setColor(c);
                pixmap.drawPixel(x, y);
            }
        }
        
        // Add grass blade details
        pixmap.setColor(0.15f, 0.5f, 0.15f, 0.8f);
        for (int i = 0; i < 20; i++) {
            int x = (i * 13) % size;
            int y = (i * 17) % size;
            pixmap.drawLine(x, y, x, Math.min(size - 1, y + 4));
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put("grass", texture);
        return texture;
    }
    
    /**
     * Generate water tile texture with animated appearance
     */
    public static Texture generateWaterTile() {
        if (textureCache.containsKey("water")) {
            return textureCache.get("water");
        }
        
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Base water with depth gradient
        for (int y = 0; y < size; y++) {
            float depth = 1.0f - (y / (float)size) * 0.3f;
            for (int x = 0; x < size; x++) {
                float wave = (float)Math.sin(x * 0.2 + y * 0.15) * 0.1f;
                Color c = new Color(
                    0.1f * depth,
                    0.3f * depth + wave,
                    0.7f * depth + wave,
                    1f
                );
                pixmap.setColor(c);
                pixmap.drawPixel(x, y);
            }
        }
        
        // Add highlights for shimmer effect
        pixmap.setColor(0.4f, 0.6f, 1f, 0.6f);
        for (int i = 0; i < 15; i++) {
            int x = (i * 19) % size;
            int y = (i * 23) % size;
            pixmap.fillCircle(x, y, 3);
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put("water", texture);
        return texture;
    }
    
    /**
     * Generate path/dirt tile texture
     */
    public static Texture generatePathTile() {
        if (textureCache.containsKey("path")) {
            return textureCache.get("path");
        }
        
        int size = 64;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Base dirt color with 3D gradient
        for (int y = 0; y < size; y++) {
            float gradient = 1.0f - (y / (float)size) * 0.15f;
            for (int x = 0; x < size; x++) {
                float noise = (float)Math.sin(x * 0.3) * (float)Math.cos(y * 0.4) * 0.08f;
                Color c = new Color(
                    0.5f * gradient + noise,
                    0.4f * gradient + noise,
                    0.25f * gradient + noise,
                    1f
                );
                pixmap.setColor(c);
                pixmap.drawPixel(x, y);
            }
        }
        
        // Add rocks/pebbles for detail
        pixmap.setColor(0.3f, 0.25f, 0.15f, 1f);
        for (int i = 0; i < 25; i++) {
            int x = (i * 11) % size;
            int y = (i * 13) % size;
            pixmap.fillCircle(x, y, 2);
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put("path", texture);
        return texture;
    }
    
    /**
     * Generate character sprite for a specific class
     */
    public static Texture generateCharacterSprite(CharacterClass characterClass) {
        String key = "char_" + characterClass.name().toLowerCase();
        if (textureCache.containsKey(key)) {
            return textureCache.get(key);
        }
        
        int width = 32;
        int height = 48;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        // Get class-specific colors
        Color primaryColor = getClassColor(characterClass);
        Color secondaryColor = getClassSecondaryColor(characterClass);
        
        // Head (with 3D shading)
        pixmap.setColor(new Color(0.9f, 0.8f, 0.7f, 1f));
        pixmap.fillCircle(width / 2, height - 10, 6);
        // Head shadow
        pixmap.setColor(new Color(0.7f, 0.6f, 0.5f, 1f));
        pixmap.fillCircle(width / 2, height - 12, 6);
        
        // Body (class-colored armor/clothing with gradient)
        for (int y = height - 20; y >= height - 38; y--) {
            float gradient = (y - (height - 38)) / 18.0f;
            Color bodyColor = new Color(
                primaryColor.r * gradient,
                primaryColor.g * gradient,
                primaryColor.b * gradient,
                1f
            );
            pixmap.setColor(bodyColor);
            int bodyWidth = 10 + (int)((height - 20 - y) * 0.3f);
            pixmap.fillRectangle(width / 2 - bodyWidth / 2, y, bodyWidth, 1);
        }
        
        // Arms
        pixmap.setColor(secondaryColor);
        pixmap.fillRectangle(width / 2 - 10, height - 30, 4, 15);
        pixmap.fillRectangle(width / 2 + 6, height - 30, 4, 15);
        
        // Legs
        pixmap.fillRectangle(width / 2 - 7, height - 42, 5, 18);
        pixmap.fillRectangle(width / 2 + 2, height - 42, 5, 18);
        
        // Weapon indicator (based on class)
        drawClassWeapon(pixmap, characterClass, width, height);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put(key, texture);
        return texture;
    }
    
    /**
     * Generate health potion icon
     */
    public static Texture generateHealthPotionIcon() {
        if (textureCache.containsKey("health_potion")) {
            return textureCache.get("health_potion");
        }
        
        int size = 32;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Bottle outline
        pixmap.setColor(0.4f, 0.2f, 0.2f, 1f);
        pixmap.fillRectangle(10, 8, 12, 18);
        
        // Liquid with gradient (red)
        for (int y = 10; y < 24; y++) {
            float gradient = (y - 10) / 14.0f;
            pixmap.setColor(1f, 0.2f * gradient, 0.2f * gradient, 1f);
            pixmap.fillRectangle(11, y, 10, 1);
        }
        
        // Glass shine effect
        pixmap.setColor(1f, 1f, 1f, 0.6f);
        pixmap.fillCircle(14, 12, 2);
        
        // Cork/cap
        pixmap.setColor(0.6f, 0.4f, 0.2f, 1f);
        pixmap.fillRectangle(12, 4, 8, 6);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put("health_potion", texture);
        return texture;
    }
    
    /**
     * Generate mana potion icon
     */
    public static Texture generateManaPotionIcon() {
        if (textureCache.containsKey("mana_potion")) {
            return textureCache.get("mana_potion");
        }
        
        int size = 32;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Bottle outline
        pixmap.setColor(0.2f, 0.2f, 0.4f, 1f);
        pixmap.fillRectangle(10, 8, 12, 18);
        
        // Liquid with gradient (blue)
        for (int y = 10; y < 24; y++) {
            float gradient = (y - 10) / 14.0f;
            pixmap.setColor(0.2f * gradient, 0.4f + 0.2f * gradient, 1f, 1f);
            pixmap.fillRectangle(11, y, 10, 1);
        }
        
        // Glass shine effect
        pixmap.setColor(1f, 1f, 1f, 0.6f);
        pixmap.fillCircle(14, 12, 2);
        
        // Cork/cap
        pixmap.setColor(0.6f, 0.4f, 0.2f, 1f);
        pixmap.fillRectangle(12, 4, 8, 6);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put("mana_potion", texture);
        return texture;
    }
    
    /**
     * Generate weapon icon
     */
    public static Texture generateWeaponIcon(String weaponType) {
        String key = "weapon_" + weaponType;
        if (textureCache.containsKey(key)) {
            return textureCache.get(key);
        }
        
        int size = 32;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Draw sword as default
        // Blade with metallic gradient
        for (int i = 0; i < 20; i++) {
            float gradient = i / 20.0f;
            pixmap.setColor(0.6f + 0.3f * gradient, 0.6f + 0.3f * gradient, 0.7f + 0.2f * gradient, 1f);
            pixmap.fillRectangle(14, 4 + i, 4, 1);
        }
        
        // Handle
        pixmap.setColor(0.4f, 0.2f, 0.1f, 1f);
        pixmap.fillRectangle(13, 24, 6, 6);
        
        // Guard
        pixmap.setColor(0.7f, 0.6f, 0.2f, 1f);
        pixmap.fillRectangle(10, 22, 12, 2);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put(key, texture);
        return texture;
    }
    
    /**
     * Generate armor icon
     */
    public static Texture generateArmorIcon(String armorType) {
        String key = "armor_" + armorType;
        if (textureCache.containsKey(key)) {
            return textureCache.get(key);
        }
        
        int size = 32;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Chest plate with 3D effect
        for (int y = 8; y < 24; y++) {
            float gradient = (y - 8) / 16.0f;
            pixmap.setColor(0.5f + 0.3f * gradient, 0.5f + 0.3f * gradient, 0.6f + 0.2f * gradient, 1f);
            int width = 8 + (int)((16 - Math.abs(y - 16)) * 0.5f);
            pixmap.fillRectangle(16 - width / 2, y, width, 1);
        }
        
        // Shoulder pieces
        pixmap.setColor(0.6f, 0.6f, 0.7f, 1f);
        pixmap.fillCircle(8, 10, 3);
        pixmap.fillCircle(24, 10, 3);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put(key, texture);
        return texture;
    }
    
    /**
     * Generate gold coin icon
     */
    public static Texture generateGoldCoinIcon() {
        if (textureCache.containsKey("gold_coin")) {
            return textureCache.get("gold_coin");
        }
        
        int size = 24;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        // Coin body with gradient for 3D effect
        for (int y = 4; y < 20; y++) {
            for (int x = 4; x < 20; x++) {
                float dx = x - 12;
                float dy = y - 12;
                float distance = (float)Math.sqrt(dx * dx + dy * dy);
                
                if (distance < 8) {
                    float gradient = 1.0f - (distance / 8.0f) * 0.3f;
                    pixmap.setColor(1f * gradient, 0.8f * gradient, 0.2f * gradient, 1f);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        
        // Shine effect
        pixmap.setColor(1f, 1f, 0.8f, 0.8f);
        pixmap.fillCircle(10, 8, 2);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put("gold_coin", texture);
        return texture;
    }
    
    /**
     * Generate health bar texture with 3D styling
     */
    public static Texture generateHealthBarTexture() {
        if (textureCache.containsKey("health_bar")) {
            return textureCache.get("health_bar");
        }
        
        int width = 200;
        int height = 30;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        // Background with gradient
        for (int y = 0; y < height; y++) {
            float gradient = y / (float)height;
            pixmap.setColor(0.2f * gradient, 0.05f * gradient, 0.05f * gradient, 0.9f);
            pixmap.fillRectangle(0, y, width, 1);
        }
        
        // Border
        pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
        pixmap.drawRectangle(0, 0, width, height);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put("health_bar", texture);
        return texture;
    }
    
    /**
     * Generate mana bar texture with 3D styling
     */
    public static Texture generateManaBarTexture() {
        if (textureCache.containsKey("mana_bar")) {
            return textureCache.get("mana_bar");
        }
        
        int width = 200;
        int height = 30;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        // Background with gradient
        for (int y = 0; y < height; y++) {
            float gradient = y / (float)height;
            pixmap.setColor(0.05f * gradient, 0.05f * gradient, 0.2f * gradient, 0.9f);
            pixmap.fillRectangle(0, y, width, 1);
        }
        
        // Border
        pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
        pixmap.drawRectangle(0, 0, width, height);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        textureCache.put("mana_bar", texture);
        return texture;
    }
    
    // Helper methods
    
    private static Color getClassColor(CharacterClass characterClass) {
        switch (characterClass) {
            case WARRIOR: return new Color(0.8f, 0.2f, 0.2f, 1f); // Red
            case MAGE: return new Color(0.3f, 0.3f, 0.9f, 1f); // Blue
            case ARCHER: return new Color(0.2f, 0.7f, 0.2f, 1f); // Green
            case ROGUE: return new Color(0.4f, 0.2f, 0.5f, 1f); // Purple
            case CLERIC: return new Color(0.9f, 0.9f, 0.3f, 1f); // Gold
            default: return Color.WHITE;
        }
    }
    
    private static Color getClassSecondaryColor(CharacterClass characterClass) {
        switch (characterClass) {
            case WARRIOR: return new Color(0.5f, 0.5f, 0.5f, 1f); // Gray (armor)
            case MAGE: return new Color(0.5f, 0.2f, 0.6f, 1f); // Purple
            case ARCHER: return new Color(0.4f, 0.3f, 0.2f, 1f); // Brown
            case ROGUE: return new Color(0.2f, 0.2f, 0.2f, 1f); // Black
            case CLERIC: return new Color(0.9f, 0.9f, 0.9f, 1f); // White
            default: return Color.GRAY;
        }
    }
    
    private static void drawClassWeapon(Pixmap pixmap, CharacterClass characterClass, int width, int height) {
        pixmap.setColor(getClassSecondaryColor(characterClass));
        
        switch (characterClass) {
            case WARRIOR:
                // Sword
                pixmap.fillRectangle(width / 2 + 8, height - 35, 2, 12);
                break;
            case MAGE:
                // Staff
                pixmap.fillRectangle(width / 2 - 10, height - 40, 2, 18);
                pixmap.setColor(0.3f, 0.5f, 1f, 1f);
                pixmap.fillCircle(width / 2 - 9, height - 42, 3);
                break;
            case ARCHER:
                // Bow
                pixmap.drawLine(width / 2 + 8, height - 40, width / 2 + 8, height - 28);
                pixmap.drawLine(width / 2 + 8, height - 40, width / 2 + 12, height - 34);
                pixmap.drawLine(width / 2 + 8, height - 28, width / 2 + 12, height - 34);
                break;
            case ROGUE:
                // Dagger
                pixmap.fillRectangle(width / 2 + 8, height - 32, 1, 8);
                break;
            case CLERIC:
                // Holy symbol
                pixmap.setColor(1f, 1f, 0.3f, 1f);
                pixmap.fillRectangle(width / 2 - 10, height - 32, 2, 8);
                pixmap.fillRectangle(width / 2 - 13, height - 30, 8, 2);
                break;
        }
    }
    
    /**
     * Dispose all cached textures
     */
    public static void disposeAll() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
    }
}
