package com.mmo.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Database of predefined items in the game
 */
public class ItemDatabase {
    private static final Map<Long, Item> items = new HashMap<>();
    private static long nextId = 1;
    
    static {
        // Initialize default items
        initializeItems();
    }
    
    private static void initializeItems() {
        // Consumables
        createPotion(nextId++, "Health Potion", "Restores 50 HP", 50, 0, ItemRarity.COMMON, 10);
        createPotion(nextId++, "Mana Potion", "Restores 30 MP", 0, 30, ItemRarity.COMMON, 10);
        createPotion(nextId++, "Greater Health Potion", "Restores 100 HP", 100, 0, ItemRarity.UNCOMMON, 25);
        createPotion(nextId++, "Greater Mana Potion", "Restores 60 MP", 0, 60, ItemRarity.UNCOMMON, 25);
        
        // Weapons
        createWeapon(nextId++, "Iron Sword", "A basic iron sword", 10, 0, ItemRarity.COMMON, 1, 50);
        createWeapon(nextId++, "Steel Sword", "A well-crafted steel sword", 20, 0, ItemRarity.UNCOMMON, 5, 150);
        createWeapon(nextId++, "Legendary Blade", "An ancient legendary weapon", 50, 0, ItemRarity.LEGENDARY, 10, 1000);
        
        // Armor
        createArmor(nextId++, "Leather Armor", "Basic leather protection", 0, 5, ItemRarity.COMMON, 1, 30);
        createArmor(nextId++, "Chain Mail", "Sturdy chain mail armor", 10, 15, ItemRarity.UNCOMMON, 5, 100);
        createArmor(nextId++, "Dragon Scale Armor", "Armor made from dragon scales", 50, 50, ItemRarity.LEGENDARY, 10, 2000);
        
        // Materials
        createMaterial(nextId++, "Wood", "Common wooden material", ItemRarity.COMMON, 1);
        createMaterial(nextId++, "Iron Ore", "Raw iron ore", ItemRarity.COMMON, 5);
        createMaterial(nextId++, "Gold Ore", "Raw gold ore", ItemRarity.RARE, 20);
    }
    
    private static void createPotion(long id, String name, String description, 
                                     int healthRestore, int manaRestore, ItemRarity rarity, int value) {
        Item item = new Item(id, name, description, ItemType.CONSUMABLE, rarity);
        item.setValue(value);
        item.setStackable(true);
        item.setMaxStack(99);
        item.setHealthRestore(healthRestore);
        item.setManaRestore(manaRestore);
        items.put(id, item);
    }
    
    private static void createWeapon(long id, String name, String description, 
                                     int attackBonus, int defenseBonus, ItemRarity rarity, int levelReq, int value) {
        Item item = new Item(id, name, description, ItemType.WEAPON, rarity);
        item.setValue(value);
        item.setStackable(false);
        item.setAttackBonus(attackBonus);
        item.setDefenseBonus(defenseBonus);
        item.setLevelRequirement(levelReq);
        items.put(id, item);
    }
    
    private static void createArmor(long id, String name, String description, 
                                    int healthBonus, int defenseBonus, ItemRarity rarity, int levelReq, int value) {
        Item item = new Item(id, name, description, ItemType.ARMOR, rarity);
        item.setValue(value);
        item.setStackable(false);
        item.setHealthBonus(healthBonus);
        item.setDefenseBonus(defenseBonus);
        item.setLevelRequirement(levelReq);
        items.put(id, item);
    }
    
    private static void createMaterial(long id, String name, String description, ItemRarity rarity, int value) {
        Item item = new Item(id, name, description, ItemType.MATERIAL, rarity);
        item.setValue(value);
        item.setStackable(true);
        item.setMaxStack(999);
        items.put(id, item);
    }
    
    /**
     * Get an item by ID
     */
    public static Item getItem(long id) {
        return items.get(id);
    }
    
    /**
     * Get a copy of an item (to prevent modification of the template)
     */
    public static Item getItemCopy(long id) {
        Item original = items.get(id);
        if (original == null) {
            return null;
        }
        
        // Create a copy
        Item copy = new Item(original.getId(), original.getName(), 
                            original.getDescription(), original.getType(), original.getRarity());
        copy.setValue(original.getValue());
        copy.setStackable(original.isStackable());
        copy.setMaxStack(original.getMaxStack());
        copy.setLevelRequirement(original.getLevelRequirement());
        copy.setHealthBonus(original.getHealthBonus());
        copy.setManaBonus(original.getManaBonus());
        copy.setAttackBonus(original.getAttackBonus());
        copy.setDefenseBonus(original.getDefenseBonus());
        copy.setHealthRestore(original.getHealthRestore());
        copy.setManaRestore(original.getManaRestore());
        
        return copy;
    }
    
    /**
     * Get starter items for a new character
     */
    public static void giveStarterItems(Inventory inventory) {
        // Give some starting items
        inventory.addItem(getItemCopy(1), 5);  // 5 Health Potions
        inventory.addItem(getItemCopy(2), 5);  // 5 Mana Potions
        inventory.addItem(getItemCopy(5), 1);  // Iron Sword
        inventory.addItem(getItemCopy(8), 1);  // Leather Armor
        inventory.addGold(100);  // Starting gold
    }
}
