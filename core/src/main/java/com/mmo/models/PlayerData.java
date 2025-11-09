package com.mmo.models;

/**
 * Represents player data in the game world
 */
public class PlayerData {
    private long playerId;
    private String username;
    private CharacterData character;
    private boolean online;
    private long lastActivity;
    
    public PlayerData() {
        // Default constructor for Kryo
    }
    
    public PlayerData(long playerId, String username, CharacterData character) {
        this.playerId = playerId;
        this.username = username;
        this.character = character;
        this.online = true;
        this.lastActivity = System.currentTimeMillis();
    }
    
    // Getters and setters
    public long getPlayerId() { return playerId; }
    public void setPlayerId(long playerId) { this.playerId = playerId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public CharacterData getCharacter() { return character; }
    public void setCharacter(CharacterData character) { this.character = character; }
    
    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }
    
    public long getLastActivity() { return lastActivity; }
    public void setLastActivity(long lastActivity) { this.lastActivity = lastActivity; }
    
    public void updateActivity() {
        this.lastActivity = System.currentTimeMillis();
    }
}
