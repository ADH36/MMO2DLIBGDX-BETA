package com.mmo.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.mmo.models.*;

/**
 * Network protocol definitions for MMO game
 * Registers all network messages with Kryo serialization
 */
public class Network {
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;
    
    /**
     * Register all network classes for serialization
     */
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        
        // Authentication messages
        kryo.register(LoginRequest.class);
        kryo.register(LoginResponse.class);
        kryo.register(RegisterRequest.class);
        kryo.register(RegisterResponse.class);
        
        // Character messages
        kryo.register(CharacterListRequest.class);
        kryo.register(CharacterListResponse.class);
        kryo.register(CreateCharacterRequest.class);
        kryo.register(CreateCharacterResponse.class);
        kryo.register(SelectCharacterRequest.class);
        kryo.register(SelectCharacterResponse.class);
        
        // Game messages
        kryo.register(PlayerMoveRequest.class);
        kryo.register(PlayerMoveResponse.class);
        kryo.register(PlayerUpdate.class);
        kryo.register(WorldUpdate.class);
        kryo.register(ChatMessage.class);
        kryo.register(UseAbilityRequest.class);
        kryo.register(UseAbilityResponse.class);
        kryo.register(CombatEvent.class);
        kryo.register(AttackRequest.class);
        kryo.register(PlayerDeath.class);
        kryo.register(PlayerRespawn.class);
        
        // Data models
        kryo.register(PlayerData.class);
        kryo.register(CharacterData.class);
        kryo.register(CharacterClass.class);
        kryo.register(Ability.class);
        kryo.register(CombatAction.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(String[].class);
        kryo.register(long[].class);
    }
    
    // Authentication Messages
    public static class LoginRequest {
        public String username;
        public String password;
    }
    
    public static class LoginResponse {
        public boolean success;
        public String message;
        public String token;
    }
    
    public static class RegisterRequest {
        public String username;
        public String password;
        public String email;
    }
    
    public static class RegisterResponse {
        public boolean success;
        public String message;
    }
    
    // Character Messages
    public static class CharacterListRequest {
        public String token;
    }
    
    public static class CharacterListResponse {
        public boolean success;
        public CharacterData[] characters;
    }
    
    public static class CreateCharacterRequest {
        public String token;
        public String characterName;
        public CharacterClass characterClass;
    }
    
    public static class CreateCharacterResponse {
        public boolean success;
        public String message;
        public CharacterData character;
    }
    
    public static class SelectCharacterRequest {
        public String token;
        public long characterId;
    }
    
    public static class SelectCharacterResponse {
        public boolean success;
        public String message;
        public PlayerData playerData;
    }
    
    // Game Messages
    public static class PlayerMoveRequest {
        public float x;
        public float y;
    }
    
    public static class PlayerMoveResponse {
        public long playerId;
        public float x;
        public float y;
    }
    
    public static class PlayerUpdate {
        public long playerId;
        public float x;
        public float y;
        public String name;
        public int level;
        public int health;
        public int maxHealth;
    }
    
    public static class WorldUpdate {
        public PlayerUpdate[] players;
    }
    
    public static class ChatMessage {
        public String sender;
        public String message;
        public long timestamp;
    }
    
    public static class UseAbilityRequest {
        public int abilityIndex;
        public long targetPlayerId; // Changed to target a specific player
        public float targetX;
        public float targetY;
    }
    
    public static class UseAbilityResponse {
        public boolean success;
        public String message;
        public int currentMana;
        public int currentHealth;
    }
    
    public static class AttackRequest {
        public long targetPlayerId;
        public int abilityIndex;
    }
    
    public static class CombatEvent {
        public long attackerId;
        public String attackerName;
        public long targetId;
        public String targetName;
        public String abilityName;
        public int damage;
        public int healing;
        public boolean isCritical;
        public int targetHealthAfter;
        public int attackerManaAfter;
        public long timestamp;
    }
    
    public static class PlayerDeath {
        public long playerId;
        public String playerName;
        public long killerId;
        public String killerName;
    }
    
    public static class PlayerRespawn {
        public long playerId;
        public float x;
        public float y;
    }
}
