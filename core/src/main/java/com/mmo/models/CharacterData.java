package com.mmo.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents character data stored in the database
 */
public class CharacterData {
    private long id;
    private String name;
    private CharacterClass characterClass;
    private int level;
    private int experience;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int attack;
    private int defense;
    private float x;
    private float y;
    private long createdAt;
    private List<Ability> abilities;
    private long[] abilityCooldowns; // Tracks when each ability can be used next (timestamp)
    
    public CharacterData() {
        // Default constructor for Kryo
        abilities = new ArrayList<>();
        abilityCooldowns = new long[4]; // Support up to 4 abilities
    }
    
    public CharacterData(long id, String name, CharacterClass characterClass) {
        this.id = id;
        this.name = name;
        this.characterClass = characterClass;
        this.level = 1;
        this.experience = 0;
        this.maxHealth = characterClass.getBaseHealth();
        this.health = maxHealth;
        this.maxMana = characterClass.getBaseMana();
        this.mana = maxMana;
        this.attack = characterClass.getBaseAttack();
        this.defense = characterClass.getBaseDefense();
        this.x = 100;
        this.y = 100;
        this.createdAt = System.currentTimeMillis();
        this.abilities = new ArrayList<>();
        this.abilityCooldowns = new long[4];
        initializeAbilities();
    }
    
    private void initializeAbilities() {
        // Initialize abilities based on character class
        String[] names = characterClass.getAbilityNames();
        String[] descriptions = characterClass.getAbilityDescriptions();
        
        for (int i = 0; i < names.length; i++) {
            Ability ability = new Ability(
                names[i],
                descriptions[i],
                10 + (i * 5), // Mana cost increases per ability
                5 + i, // Cooldown increases per ability
                attack + (i * 5), // Damage scales with attack
                i == 0 && characterClass == CharacterClass.CLERIC ? 20 : 0, // Clerics heal
                100f + (i * 25f), // Range increases per ability
                "none"
            );
            abilities.add(ability);
        }
    }
    
    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public CharacterClass getCharacterClass() { return characterClass; }
    public void setCharacterClass(CharacterClass characterClass) { this.characterClass = characterClass; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    
    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = mana; }
    
    public int getMaxMana() { return maxMana; }
    public void setMaxMana(int maxMana) { this.maxMana = maxMana; }
    
    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }
    
    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }
    
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public List<Ability> getAbilities() { return abilities; }
    public void setAbilities(List<Ability> abilities) { this.abilities = abilities; }
    
    public long[] getAbilityCooldowns() { return abilityCooldowns; }
    public void setAbilityCooldowns(long[] abilityCooldowns) { this.abilityCooldowns = abilityCooldowns; }
    
    /**
     * Check if an ability is off cooldown
     */
    public boolean isAbilityReady(int abilityIndex) {
        if (abilityIndex < 0 || abilityIndex >= abilityCooldowns.length) {
            return false;
        }
        return System.currentTimeMillis() >= abilityCooldowns[abilityIndex];
    }
    
    /**
     * Set cooldown for an ability
     */
    public void setAbilityCooldown(int abilityIndex, int cooldownSeconds) {
        if (abilityIndex >= 0 && abilityIndex < abilityCooldowns.length) {
            abilityCooldowns[abilityIndex] = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        }
    }
}
