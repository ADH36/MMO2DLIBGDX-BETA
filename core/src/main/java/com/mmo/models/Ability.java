package com.mmo.models;

/**
 * Represents an ability that a character can use
 */
public class Ability {
    private String name;
    private String description;
    private int manaCost;
    private int cooldown;
    private int damage;
    private int healing;
    private float range;
    private String effect;
    
    public Ability() {
        // Default constructor for Kryo
    }
    
    public Ability(String name, String description, int manaCost, int cooldown, 
                   int damage, int healing, float range, String effect) {
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.damage = damage;
        this.healing = healing;
        this.range = range;
        this.effect = effect;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getManaCost() { return manaCost; }
    public void setManaCost(int manaCost) { this.manaCost = manaCost; }
    
    public int getCooldown() { return cooldown; }
    public void setCooldown(int cooldown) { this.cooldown = cooldown; }
    
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    
    public int getHealing() { return healing; }
    public void setHealing(int healing) { this.healing = healing; }
    
    public float getRange() { return range; }
    public void setRange(float range) { this.range = range; }
    
    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }
}
