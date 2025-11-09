package com.mmo.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.mmo.models.CharacterClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all game assets including textures, sprites, and UI elements
 */
public class AssetManager {
    
    private static AssetManager instance;
    
    // Texture caches
    private Map<String, Texture> tileTextures;
    private Map<CharacterClass, Texture> characterTextures;
    private Map<String, Texture> itemTextures;
    private Map<String, Texture> uiTextures;
    
    private AssetManager() {
        tileTextures = new HashMap<>();
        characterTextures = new HashMap<>();
        itemTextures = new HashMap<>();
        uiTextures = new HashMap<>();
        initializeAssets();
    }
    
    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }
    
    private void initializeAssets() {
        // Generate tile textures
        tileTextures.put("grass", AssetGenerator.generateGrassTile());
        tileTextures.put("water", AssetGenerator.generateWaterTile());
        tileTextures.put("path", AssetGenerator.generatePathTile());
        
        // Generate character textures for all classes
        for (CharacterClass charClass : CharacterClass.values()) {
            characterTextures.put(charClass, AssetGenerator.generateCharacterSprite(charClass));
        }
        
        // Generate item textures
        itemTextures.put("health_potion", AssetGenerator.generateHealthPotionIcon());
        itemTextures.put("mana_potion", AssetGenerator.generateManaPotionIcon());
        itemTextures.put("weapon_sword", AssetGenerator.generateWeaponIcon("sword"));
        itemTextures.put("armor_basic", AssetGenerator.generateArmorIcon("basic"));
        itemTextures.put("gold_coin", AssetGenerator.generateGoldCoinIcon());
        
        // Generate UI textures
        uiTextures.put("health_bar", AssetGenerator.generateHealthBarTexture());
        uiTextures.put("mana_bar", AssetGenerator.generateManaBarTexture());
    }
    
    /**
     * Get tile texture by name
     */
    public Texture getTileTexture(String tileName) {
        return tileTextures.getOrDefault(tileName, tileTextures.get("grass"));
    }
    
    /**
     * Get character texture by class
     */
    public Texture getCharacterTexture(CharacterClass characterClass) {
        return characterTextures.getOrDefault(characterClass, characterTextures.get(CharacterClass.WARRIOR));
    }
    
    /**
     * Get item texture by item ID or name
     */
    public Texture getItemTexture(String itemName) {
        // Try to find exact match
        if (itemTextures.containsKey(itemName)) {
            return itemTextures.get(itemName);
        }
        
        // Try to match by type
        String lowerName = itemName.toLowerCase();
        if (lowerName.contains("health") || lowerName.contains("potion")) {
            return itemTextures.get("health_potion");
        } else if (lowerName.contains("mana")) {
            return itemTextures.get("mana_potion");
        } else if (lowerName.contains("sword") || lowerName.contains("weapon")) {
            return itemTextures.get("weapon_sword");
        } else if (lowerName.contains("armor")) {
            return itemTextures.get("armor_basic");
        }
        
        // Default to health potion icon for consumables
        return itemTextures.get("health_potion");
    }
    
    /**
     * Get UI texture by name
     */
    public Texture getUITexture(String uiName) {
        return uiTextures.get(uiName);
    }
    
    /**
     * Get gold coin texture
     */
    public Texture getGoldCoinTexture() {
        return itemTextures.get("gold_coin");
    }
    
    /**
     * Dispose all assets
     */
    public void dispose() {
        for (Texture texture : tileTextures.values()) {
            if (texture != null) texture.dispose();
        }
        for (Texture texture : characterTextures.values()) {
            if (texture != null) texture.dispose();
        }
        for (Texture texture : itemTextures.values()) {
            if (texture != null) texture.dispose();
        }
        for (Texture texture : uiTextures.values()) {
            if (texture != null) texture.dispose();
        }
        
        tileTextures.clear();
        characterTextures.clear();
        itemTextures.clear();
        uiTextures.clear();
    }
}
