package com.mmo.models;

/**
 * Represents a combat action performed by a player
 */
public class CombatAction {
    private long attackerId;
    private long targetId;
    private int abilityIndex;
    private int damage;
    private int healing;
    private boolean isCritical;
    private long timestamp;
    
    public CombatAction() {
        // Default constructor for Kryo
    }
    
    public CombatAction(long attackerId, long targetId, int abilityIndex, int damage, int healing, boolean isCritical) {
        this.attackerId = attackerId;
        this.targetId = targetId;
        this.abilityIndex = abilityIndex;
        this.damage = damage;
        this.healing = healing;
        this.isCritical = isCritical;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and setters
    public long getAttackerId() { return attackerId; }
    public void setAttackerId(long attackerId) { this.attackerId = attackerId; }
    
    public long getTargetId() { return targetId; }
    public void setTargetId(long targetId) { this.targetId = targetId; }
    
    public int getAbilityIndex() { return abilityIndex; }
    public void setAbilityIndex(int abilityIndex) { this.abilityIndex = abilityIndex; }
    
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    
    public int getHealing() { return healing; }
    public void setHealing(int healing) { this.healing = healing; }
    
    public boolean isCritical() { return isCritical; }
    public void setCritical(boolean critical) { isCritical = critical; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
