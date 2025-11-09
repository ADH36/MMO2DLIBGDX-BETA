package com.mmo.models;

/**
 * Represents a character class with unique abilities and stats
 */
public enum CharacterClass {
    WARRIOR("Warrior", 
            "A mighty melee fighter with high health and defense",
            new String[]{"Slash", "Shield Bash", "War Cry", "Charge"},
            new String[]{"A powerful sword attack", "Stun enemies with shield", "Boost team morale", "Rush to target"}),
    
    MAGE("Mage",
         "A master of arcane arts with powerful spells",
         new String[]{"Fireball", "Ice Lance", "Teleport", "Meteor Storm"},
         new String[]{"Launch a ball of fire", "Freeze enemies", "Teleport short distance", "Rain destruction from above"}),
    
    ARCHER("Archer",
           "A skilled ranged fighter with precision and agility",
           new String[]{"Power Shot", "Multi-Shot", "Trap", "Eagle Eye"},
           new String[]{"Charged arrow attack", "Hit multiple targets", "Set a trap", "Increase critical chance"}),
    
    ROGUE("Rogue",
          "A stealthy assassin with high critical damage",
          new String[]{"Backstab", "Vanish", "Poison Blade", "Shadow Step"},
          new String[]{"Critical strike from behind", "Become invisible", "Apply poison damage", "Teleport behind enemy"}),
    
    CLERIC("Cleric",
           "A holy warrior who heals and protects allies",
           new String[]{"Heal", "Holy Shield", "Smite", "Divine Blessing"},
           new String[]{"Restore health", "Create protective barrier", "Holy damage attack", "Buff all allies"});
    
    private final String name;
    private final String description;
    private final String[] abilityNames;
    private final String[] abilityDescriptions;
    
    CharacterClass(String name, String description, String[] abilityNames, String[] abilityDescriptions) {
        this.name = name;
        this.description = description;
        this.abilityNames = abilityNames;
        this.abilityDescriptions = abilityDescriptions;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String[] getAbilityNames() {
        return abilityNames;
    }
    
    public String[] getAbilityDescriptions() {
        return abilityDescriptions;
    }
    
    public int getBaseHealth() {
        switch (this) {
            case WARRIOR: return 150;
            case MAGE: return 80;
            case ARCHER: return 100;
            case ROGUE: return 90;
            case CLERIC: return 110;
            default: return 100;
        }
    }
    
    public int getBaseMana() {
        switch (this) {
            case WARRIOR: return 50;
            case MAGE: return 150;
            case ARCHER: return 80;
            case ROGUE: return 70;
            case CLERIC: return 120;
            default: return 100;
        }
    }
    
    public int getBaseAttack() {
        switch (this) {
            case WARRIOR: return 25;
            case MAGE: return 35;
            case ARCHER: return 22;
            case ROGUE: return 28;
            case CLERIC: return 18;
            default: return 20;
        }
    }
    
    public int getBaseDefense() {
        switch (this) {
            case WARRIOR: return 30;
            case MAGE: return 10;
            case ARCHER: return 15;
            case ROGUE: return 12;
            case CLERIC: return 20;
            default: return 15;
        }
    }
}
