package com.mmo.models;

/**
 * Represents an item in the game
 */
public class Item {
    private long id;
    private String name;
    private String description;
    private ItemType type;
    private ItemRarity rarity;
    private int value; // Gold value
    private boolean stackable;
    private int maxStack; // Max items per stack (if stackable)
    private int levelRequirement;
    
    // Item stats (for equipment)
    private int healthBonus;
    private int manaBonus;
    private int attackBonus;
    private int defenseBonus;
    
    // Consumable effects
    private int healthRestore;
    private int manaRestore;
    
    public Item() {
        // Default constructor for Kryo
    }
    
    public Item(long id, String name, String description, ItemType type, ItemRarity rarity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.rarity = rarity;
        this.value = 0;
        this.stackable = false;
        this.maxStack = 1;
        this.levelRequirement = 1;
    }
    
    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ItemType getType() { return type; }
    public void setType(ItemType type) { this.type = type; }
    
    public ItemRarity getRarity() { return rarity; }
    public void setRarity(ItemRarity rarity) { this.rarity = rarity; }
    
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    
    public boolean isStackable() { return stackable; }
    public void setStackable(boolean stackable) { this.stackable = stackable; }
    
    public int getMaxStack() { return maxStack; }
    public void setMaxStack(int maxStack) { this.maxStack = maxStack; }
    
    public int getLevelRequirement() { return levelRequirement; }
    public void setLevelRequirement(int levelRequirement) { this.levelRequirement = levelRequirement; }
    
    public int getHealthBonus() { return healthBonus; }
    public void setHealthBonus(int healthBonus) { this.healthBonus = healthBonus; }
    
    public int getManaBonus() { return manaBonus; }
    public void setManaBonus(int manaBonus) { this.manaBonus = manaBonus; }
    
    public int getAttackBonus() { return attackBonus; }
    public void setAttackBonus(int attackBonus) { this.attackBonus = attackBonus; }
    
    public int getDefenseBonus() { return defenseBonus; }
    public void setDefenseBonus(int defenseBonus) { this.defenseBonus = defenseBonus; }
    
    public int getHealthRestore() { return healthRestore; }
    public void setHealthRestore(int healthRestore) { this.healthRestore = healthRestore; }
    
    public int getManaRestore() { return manaRestore; }
    public void setManaRestore(int manaRestore) { this.manaRestore = manaRestore; }
}
