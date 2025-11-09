package com.mmo.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mmo.models.Ability;
import com.mmo.models.CharacterClass;
import com.mmo.models.CharacterData;
import com.mmo.models.PlayerData;
import com.mmo.network.Network;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MMO Game Server - handles all game logic, authentication, and world state
 */
public class MMOServer {
    private Server server;
    private Map<String, UserAccount> accounts;
    private Map<String, String> sessionTokens; // token -> username
    private Map<Long, CharacterData> characters;
    private Map<Connection, PlayerData> activePlayers;
    private AtomicLong characterIdCounter;
    private AtomicLong playerIdCounter;
    
    private static final int WORLD_UPDATE_INTERVAL = 50; // ms
    private static final int MANA_REGEN_INTERVAL = 2000; // ms (2 seconds)
    private static final int MANA_REGEN_AMOUNT = 5; // mana points per tick
    private Timer worldUpdateTimer;
    private Timer manaRegenTimer;
    
    public MMOServer() {
        server = new Server(16384, 8192);
        accounts = new ConcurrentHashMap<>();
        sessionTokens = new ConcurrentHashMap<>();
        characters = new ConcurrentHashMap<>();
        activePlayers = new ConcurrentHashMap<>();
        characterIdCounter = new AtomicLong(1000);
        playerIdCounter = new AtomicLong(1);
        
        Network.register(server);
        setupListeners();
        createDefaultAccounts();
    }
    
    private void createDefaultAccounts() {
        // Create a default test account
        UserAccount testAccount = new UserAccount("test", "test", "test@test.com");
        accounts.put("test", testAccount);
        
        // Create a default character for testing
        CharacterData testChar = new CharacterData(1001, "TestWarrior", CharacterClass.WARRIOR);
        characters.put(1001L, testChar);
        testAccount.characterIds.add(1001L);
        
        System.out.println("Created default test account (username: test, password: test)");
    }
    
    private void setupListeners() {
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("Client connected: " + connection.getID());
            }
            
            @Override
            public void disconnected(Connection connection) {
                System.out.println("Client disconnected: " + connection.getID());
                handleDisconnect(connection);
            }
            
            @Override
            public void received(Connection connection, Object object) {
                handleMessage(connection, object);
            }
        });
    }
    
    private void handleMessage(Connection connection, Object object) {
        if (object instanceof Network.LoginRequest) {
            handleLogin(connection, (Network.LoginRequest) object);
        } else if (object instanceof Network.RegisterRequest) {
            handleRegister(connection, (Network.RegisterRequest) object);
        } else if (object instanceof Network.CharacterListRequest) {
            handleCharacterList(connection, (Network.CharacterListRequest) object);
        } else if (object instanceof Network.CreateCharacterRequest) {
            handleCreateCharacter(connection, (Network.CreateCharacterRequest) object);
        } else if (object instanceof Network.SelectCharacterRequest) {
            handleSelectCharacter(connection, (Network.SelectCharacterRequest) object);
        } else if (object instanceof Network.PlayerMoveRequest) {
            handlePlayerMove(connection, (Network.PlayerMoveRequest) object);
        } else if (object instanceof Network.ChatMessage) {
            handleChatMessage(connection, (Network.ChatMessage) object);
        } else if (object instanceof Network.UseAbilityRequest) {
            handleUseAbility(connection, (Network.UseAbilityRequest) object);
        } else if (object instanceof Network.AttackRequest) {
            handleAttack(connection, (Network.AttackRequest) object);
        } else if (object instanceof Network.UseItemRequest) {
            handleUseItem(connection, (Network.UseItemRequest) object);
        } else if (object instanceof Network.EquipItemRequest) {
            handleEquipItem(connection, (Network.EquipItemRequest) object);
        } else if (object instanceof Network.UnequipItemRequest) {
            handleUnequipItem(connection, (Network.UnequipItemRequest) object);
        }
    }
    
    private void handleLogin(Connection connection, Network.LoginRequest request) {
        Network.LoginResponse response = new Network.LoginResponse();
        
        UserAccount account = accounts.get(request.username);
        if (account != null && account.password.equals(request.password)) {
            String token = UUID.randomUUID().toString();
            sessionTokens.put(token, request.username);
            
            response.success = true;
            response.message = "Login successful";
            response.token = token;
            
            System.out.println("User logged in: " + request.username);
        } else {
            response.success = false;
            response.message = "Invalid username or password";
        }
        
        connection.sendTCP(response);
    }
    
    private void handleRegister(Connection connection, Network.RegisterRequest request) {
        Network.RegisterResponse response = new Network.RegisterResponse();
        
        if (accounts.containsKey(request.username)) {
            response.success = false;
            response.message = "Username already exists";
        } else if (request.username.length() < 3) {
            response.success = false;
            response.message = "Username must be at least 3 characters";
        } else if (request.password.length() < 3) {
            response.success = false;
            response.message = "Password must be at least 3 characters";
        } else {
            UserAccount newAccount = new UserAccount(request.username, request.password, request.email);
            accounts.put(request.username, newAccount);
            
            response.success = true;
            response.message = "Account created successfully";
            
            System.out.println("New account registered: " + request.username);
        }
        
        connection.sendTCP(response);
    }
    
    private void handleCharacterList(Connection connection, Network.CharacterListRequest request) {
        Network.CharacterListResponse response = new Network.CharacterListResponse();
        
        String username = sessionTokens.get(request.token);
        if (username != null) {
            UserAccount account = accounts.get(username);
            if (account != null) {
                List<CharacterData> charList = new ArrayList<>();
                for (Long charId : account.characterIds) {
                    CharacterData character = characters.get(charId);
                    if (character != null) {
                        charList.add(character);
                    }
                }
                
                response.success = true;
                response.characters = charList.toArray(new CharacterData[0]);
                
                System.out.println("Sent character list to " + username + " (" + charList.size() + " characters)");
            }
        } else {
            response.success = false;
            response.characters = new CharacterData[0];
        }
        
        connection.sendTCP(response);
    }
    
    private void handleCreateCharacter(Connection connection, Network.CreateCharacterRequest request) {
        Network.CreateCharacterResponse response = new Network.CreateCharacterResponse();
        
        String username = sessionTokens.get(request.token);
        if (username != null) {
            UserAccount account = accounts.get(username);
            if (account != null) {
                if (account.characterIds.size() >= 5) {
                    response.success = false;
                    response.message = "Maximum 5 characters per account";
                } else {
                    long charId = characterIdCounter.incrementAndGet();
                    CharacterData newCharacter = new CharacterData(charId, request.characterName, request.characterClass);
                    
                    characters.put(charId, newCharacter);
                    account.characterIds.add(charId);
                    
                    response.success = true;
                    response.message = "Character created successfully";
                    response.character = newCharacter;
                    
                    System.out.println("Character created: " + request.characterName + " for user " + username);
                }
            }
        } else {
            response.success = false;
            response.message = "Invalid session";
        }
        
        connection.sendTCP(response);
    }
    
    private void handleSelectCharacter(Connection connection, Network.SelectCharacterRequest request) {
        Network.SelectCharacterResponse response = new Network.SelectCharacterResponse();
        
        String username = sessionTokens.get(request.token);
        if (username != null) {
            CharacterData character = characters.get(request.characterId);
            if (character != null) {
                long playerId = playerIdCounter.incrementAndGet();
                PlayerData playerData = new PlayerData(playerId, username, character);
                
                activePlayers.put(connection, playerData);
                
                response.success = true;
                response.message = "Character selected";
                response.playerData = playerData;
                
                System.out.println("Player " + username + " entered world as " + character.getName());
            } else {
                response.success = false;
                response.message = "Character not found";
            }
        } else {
            response.success = false;
            response.message = "Invalid session";
        }
        
        connection.sendTCP(response);
    }
    
    private void handlePlayerMove(Connection connection, Network.PlayerMoveRequest request) {
        PlayerData playerData = activePlayers.get(connection);
        if (playerData != null) {
            playerData.getCharacter().setX(request.x);
            playerData.getCharacter().setY(request.y);
            playerData.updateActivity();
        }
    }
    
    private void handleChatMessage(Connection connection, Network.ChatMessage message) {
        PlayerData playerData = activePlayers.get(connection);
        if (playerData != null) {
            System.out.println("Chat from " + message.sender + ": " + message.message);
            
            // Broadcast to all connected players
            for (Connection conn : activePlayers.keySet()) {
                conn.sendTCP(message);
            }
        }
    }
    
    private void handleUseAbility(Connection connection, Network.UseAbilityRequest request) {
        Network.UseAbilityResponse response = new Network.UseAbilityResponse();
        
        PlayerData playerData = activePlayers.get(connection);
        if (playerData != null) {
            CharacterData character = playerData.getCharacter();
            
            // Validate ability index
            if (request.abilityIndex < 0 || request.abilityIndex >= character.getAbilities().size()) {
                response.success = false;
                response.message = "Invalid ability index";
                connection.sendTCP(response);
                return;
            }
            
            Ability ability = character.getAbilities().get(request.abilityIndex);
            
            // Check cooldown
            if (!character.isAbilityReady(request.abilityIndex)) {
                response.success = false;
                response.message = "Ability is on cooldown";
                response.currentMana = character.getMana();
                response.currentHealth = character.getHealth();
                connection.sendTCP(response);
                return;
            }
            
            // Check mana
            if (character.getMana() < ability.getManaCost()) {
                response.success = false;
                response.message = "Not enough mana";
                response.currentMana = character.getMana();
                response.currentHealth = character.getHealth();
                connection.sendTCP(response);
                return;
            }
            
            // Find target player
            PlayerData targetPlayer = null;
            if (request.targetPlayerId > 0) {
                for (PlayerData pd : activePlayers.values()) {
                    if (pd.getPlayerId() == request.targetPlayerId) {
                        targetPlayer = pd;
                        break;
                    }
                }
            }
            
            // Calculate damage/healing
            int damage = ability.getDamage();
            int healing = ability.getHealing();
            boolean isCritical = Math.random() < 0.15; // 15% crit chance
            
            if (isCritical && damage > 0) {
                damage = (int)(damage * 1.5); // 150% damage on crit
            }
            
            // Apply effects
            character.setMana(character.getMana() - ability.getManaCost());
            character.setAbilityCooldown(request.abilityIndex, ability.getCooldown());
            
            if (targetPlayer != null) {
                CharacterData targetChar = targetPlayer.getCharacter();
                
                // Check range
                float distance = (float)Math.sqrt(
                    Math.pow(character.getX() - targetChar.getX(), 2) + 
                    Math.pow(character.getY() - targetChar.getY(), 2)
                );
                
                if (distance > ability.getRange()) {
                    response.success = false;
                    response.message = "Target out of range";
                    response.currentMana = character.getMana();
                    response.currentHealth = character.getHealth();
                    connection.sendTCP(response);
                    return;
                }
                
                // Apply damage or healing
                if (damage > 0) {
                    int actualDamage = Math.max(1, damage - targetChar.getDefense() / 2);
                    targetChar.setHealth(Math.max(0, targetChar.getHealth() - actualDamage));
                    damage = actualDamage;
                    
                    // Check for death
                    if (targetChar.getHealth() <= 0) {
                        handlePlayerDeath(targetPlayer, playerData);
                    }
                } else if (healing > 0) {
                    targetChar.setHealth(Math.min(targetChar.getMaxHealth(), targetChar.getHealth() + healing));
                }
                
                // Broadcast combat event
                Network.CombatEvent combatEvent = new Network.CombatEvent();
                combatEvent.attackerId = playerData.getPlayerId();
                combatEvent.attackerName = character.getName();
                combatEvent.targetId = targetPlayer.getPlayerId();
                combatEvent.targetName = targetChar.getName();
                combatEvent.abilityName = ability.getName();
                combatEvent.damage = damage;
                combatEvent.healing = healing;
                combatEvent.isCritical = isCritical;
                combatEvent.targetHealthAfter = targetChar.getHealth();
                combatEvent.attackerManaAfter = character.getMana();
                combatEvent.timestamp = System.currentTimeMillis();
                
                for (Connection conn : activePlayers.keySet()) {
                    conn.sendTCP(combatEvent);
                }
                
                System.out.println("Combat: " + character.getName() + " used " + ability.getName() + 
                                 " on " + targetChar.getName() + " for " + damage + " damage");
            } else if (healing > 0) {
                // Self-heal
                character.setHealth(Math.min(character.getMaxHealth(), character.getHealth() + healing));
                
                Network.CombatEvent combatEvent = new Network.CombatEvent();
                combatEvent.attackerId = playerData.getPlayerId();
                combatEvent.attackerName = character.getName();
                combatEvent.targetId = playerData.getPlayerId();
                combatEvent.targetName = character.getName();
                combatEvent.abilityName = ability.getName();
                combatEvent.damage = 0;
                combatEvent.healing = healing;
                combatEvent.isCritical = false;
                combatEvent.targetHealthAfter = character.getHealth();
                combatEvent.attackerManaAfter = character.getMana();
                combatEvent.timestamp = System.currentTimeMillis();
                
                for (Connection conn : activePlayers.keySet()) {
                    conn.sendTCP(combatEvent);
                }
            }
            
            response.success = true;
            response.message = "Ability used successfully";
            response.currentMana = character.getMana();
            response.currentHealth = character.getHealth();
            
            System.out.println(character.getName() + " used ability " + ability.getName());
        } else {
            response.success = false;
            response.message = "Player not found";
        }
        
        connection.sendTCP(response);
    }
    
    private void handleAttack(Connection connection, Network.AttackRequest request) {
        PlayerData playerData = activePlayers.get(connection);
        if (playerData != null && request.abilityIndex >= 0) {
            Network.UseAbilityRequest abilityRequest = new Network.UseAbilityRequest();
            abilityRequest.abilityIndex = request.abilityIndex;
            abilityRequest.targetPlayerId = request.targetPlayerId;
            handleUseAbility(connection, abilityRequest);
        }
    }
    
    private void handlePlayerDeath(PlayerData deadPlayer, PlayerData killer) {
        // Broadcast death message
        Network.PlayerDeath deathMsg = new Network.PlayerDeath();
        deathMsg.playerId = deadPlayer.getPlayerId();
        deathMsg.playerName = deadPlayer.getCharacter().getName();
        deathMsg.killerId = killer.getPlayerId();
        deathMsg.killerName = killer.getCharacter().getName();
        
        for (Connection conn : activePlayers.keySet()) {
            conn.sendTCP(deathMsg);
        }
        
        System.out.println(deadPlayer.getCharacter().getName() + " was killed by " + killer.getCharacter().getName());
        
        // Respawn player after 3 seconds
        Timer respawnTimer = new Timer();
        respawnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                respawnPlayer(deadPlayer);
            }
        }, 3000);
    }
    
    private void respawnPlayer(PlayerData player) {
        CharacterData character = player.getCharacter();
        character.setHealth(character.getMaxHealth());
        character.setMana(character.getMaxMana());
        character.setX(100);
        character.setY(100);
        
        // Broadcast respawn
        Network.PlayerRespawn respawnMsg = new Network.PlayerRespawn();
        respawnMsg.playerId = player.getPlayerId();
        respawnMsg.x = 100;
        respawnMsg.y = 100;
        
        for (Connection conn : activePlayers.keySet()) {
            conn.sendTCP(respawnMsg);
        }
        
        System.out.println(character.getName() + " respawned");
    }
    
    private void handleUseItem(Connection connection, Network.UseItemRequest request) {
        PlayerData playerData = activePlayers.get(connection);
        Network.UseItemResponse response = new Network.UseItemResponse();
        
        if (playerData == null) {
            response.success = false;
            response.message = "Player not found";
            connection.sendTCP(response);
            return;
        }
        
        CharacterData character = playerData.getCharacter();
        com.mmo.models.Inventory inventory = character.getInventory();
        com.mmo.models.InventoryItem invItem = inventory.getItemAtSlot(request.slotIndex);
        
        if (invItem == null) {
            response.success = false;
            response.message = "No item in this slot";
            connection.sendTCP(response);
            return;
        }
        
        com.mmo.models.Item item = invItem.getItem();
        
        // Check if item is consumable
        if (item.getType() != com.mmo.models.ItemType.CONSUMABLE) {
            response.success = false;
            response.message = "This item cannot be used";
            connection.sendTCP(response);
            return;
        }
        
        // Use the item
        if (!inventory.useItem(request.slotIndex)) {
            response.success = false;
            response.message = "Failed to use item";
            connection.sendTCP(response);
            return;
        }
        
        // Apply item effects
        int healthRestored = 0;
        int manaRestored = 0;
        
        if (item.getHealthRestore() > 0) {
            int oldHealth = character.getHealth();
            int newHealth = Math.min(oldHealth + item.getHealthRestore(), character.getMaxHealth());
            character.setHealth(newHealth);
            healthRestored = newHealth - oldHealth;
        }
        
        if (item.getManaRestore() > 0) {
            int oldMana = character.getMana();
            int newMana = Math.min(oldMana + item.getManaRestore(), character.getMaxMana());
            character.setMana(newMana);
            manaRestored = newMana - oldMana;
        }
        
        // If item quantity is 0, remove it
        if (invItem.getQuantity() == 0) {
            inventory.getItems().remove(invItem);
        }
        
        response.success = true;
        response.message = "Used " + item.getName();
        response.healthRestored = healthRestored;
        response.manaRestored = manaRestored;
        connection.sendTCP(response);
        
        System.out.println(character.getName() + " used " + item.getName() + 
                          " (HP: +" + healthRestored + ", MP: +" + manaRestored + ")");
    }
    
    private void handleEquipItem(Connection connection, Network.EquipItemRequest request) {
        PlayerData playerData = activePlayers.get(connection);
        Network.EquipItemResponse response = new Network.EquipItemResponse();
        
        if (playerData == null) {
            response.success = false;
            response.message = "Player not found";
            connection.sendTCP(response);
            return;
        }
        
        CharacterData character = playerData.getCharacter();
        com.mmo.models.Inventory inventory = character.getInventory();
        com.mmo.models.InventoryItem invItem = inventory.getItemAtSlot(request.slotIndex);
        
        if (invItem == null) {
            response.success = false;
            response.message = "No item in this slot";
            connection.sendTCP(response);
            return;
        }
        
        com.mmo.models.Item item = invItem.getItem();
        
        // Check if item is equippable
        if (item.getType() != com.mmo.models.ItemType.WEAPON && 
            item.getType() != com.mmo.models.ItemType.ARMOR) {
            response.success = false;
            response.message = "This item cannot be equipped";
            connection.sendTCP(response);
            return;
        }
        
        // Determine equipment slot based on item type
        com.mmo.models.EquipmentSlot slot = null;
        if (item.getType() == com.mmo.models.ItemType.WEAPON) {
            slot = com.mmo.models.EquipmentSlot.WEAPON;
        } else if (item.getType() == com.mmo.models.ItemType.ARMOR) {
            slot = com.mmo.models.EquipmentSlot.ARMOR;
        }
        
        // Check if slot already has an item equipped
        if (character.hasEquippedItem(slot)) {
            com.mmo.models.Item equippedItem = character.getEquippedItem(slot);
            
            // Remove bonuses from previously equipped item
            removeEquipmentBonuses(character, equippedItem);
            
            // Unequip and add back to inventory
            character.unequipItem(slot);
            if (!inventory.addItem(equippedItem, 1)) {
                // If inventory is full, we can't swap
                response.success = false;
                response.message = "Inventory is full. Cannot swap equipment.";
                // Re-apply the bonuses we removed
                applyEquipmentBonuses(character, equippedItem);
                character.equipItem(slot, equippedItem);
                connection.sendTCP(response);
                return;
            }
        }
        
        // Remove item from inventory and equip it
        com.mmo.models.Item itemToEquip = inventory.removeItemFromSlot(request.slotIndex);
        if (itemToEquip == null) {
            response.success = false;
            response.message = "Failed to remove item from inventory";
            connection.sendTCP(response);
            return;
        }
        
        character.equipItem(slot, itemToEquip);
        
        // Apply stat bonuses from newly equipped item
        applyEquipmentBonuses(character, itemToEquip);
        
        response.success = true;
        response.message = "Equipped " + itemToEquip.getName();
        response.updatedCharacter = character;
        connection.sendTCP(response);
        
        System.out.println(character.getName() + " equipped " + itemToEquip.getName());
    }
    
    private void handleUnequipItem(Connection connection, Network.UnequipItemRequest request) {
        PlayerData playerData = activePlayers.get(connection);
        Network.UnequipItemResponse response = new Network.UnequipItemResponse();
        
        if (playerData == null) {
            response.success = false;
            response.message = "Player not found";
            connection.sendTCP(response);
            return;
        }
        
        CharacterData character = playerData.getCharacter();
        com.mmo.models.Inventory inventory = character.getInventory();
        
        // Check if there's an item equipped in this slot
        if (!character.hasEquippedItem(request.equipmentSlot)) {
            response.success = false;
            response.message = "No item equipped in this slot";
            connection.sendTCP(response);
            return;
        }
        
        // Check if inventory has space
        if (!inventory.hasSpace()) {
            response.success = false;
            response.message = "Inventory is full";
            connection.sendTCP(response);
            return;
        }
        
        // Get equipped item and remove it
        com.mmo.models.Item equippedItem = character.unequipItem(request.equipmentSlot);
        
        // Remove stat bonuses
        removeEquipmentBonuses(character, equippedItem);
        
        // Add item back to inventory
        if (!inventory.addItem(equippedItem, 1)) {
            // If adding fails, re-equip the item
            character.equipItem(request.equipmentSlot, equippedItem);
            applyEquipmentBonuses(character, equippedItem);
            response.success = false;
            response.message = "Failed to add item to inventory";
            connection.sendTCP(response);
            return;
        }
        
        response.success = true;
        response.message = "Unequipped " + equippedItem.getName();
        response.updatedCharacter = character;
        connection.sendTCP(response);
        
        System.out.println(character.getName() + " unequipped " + equippedItem.getName());
    }
    
    /**
     * Apply stat bonuses from equipped item to character
     */
    private void applyEquipmentBonuses(CharacterData character, com.mmo.models.Item item) {
        character.setMaxHealth(character.getMaxHealth() + item.getHealthBonus());
        character.setHealth(character.getHealth() + item.getHealthBonus());
        character.setMaxMana(character.getMaxMana() + item.getManaBonus());
        character.setMana(character.getMana() + item.getManaBonus());
        character.setAttack(character.getAttack() + item.getAttackBonus());
        character.setDefense(character.getDefense() + item.getDefenseBonus());
    }
    
    /**
     * Remove stat bonuses from unequipped item from character
     */
    private void removeEquipmentBonuses(CharacterData character, com.mmo.models.Item item) {
        character.setMaxHealth(character.getMaxHealth() - item.getHealthBonus());
        // Make sure current health doesn't exceed new max health
        character.setHealth(Math.min(character.getHealth() - item.getHealthBonus(), character.getMaxHealth()));
        character.setMaxMana(character.getMaxMana() - item.getManaBonus());
        // Make sure current mana doesn't exceed new max mana
        character.setMana(Math.min(character.getMana() - item.getManaBonus(), character.getMaxMana()));
        character.setAttack(character.getAttack() - item.getAttackBonus());
        character.setDefense(character.getDefense() - item.getDefenseBonus());
    }
    
    private void handleDisconnect(Connection connection) {
        PlayerData playerData = activePlayers.remove(connection);
        if (playerData != null) {
            System.out.println("Player " + playerData.getCharacter().getName() + " left the world");
        }
    }
    
    private void startWorldUpdates() {
        worldUpdateTimer = new Timer();
        worldUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                broadcastWorldUpdate();
            }
        }, 0, WORLD_UPDATE_INTERVAL);
    }
    
    private void broadcastWorldUpdate() {
        if (activePlayers.isEmpty()) return;
        
        Network.WorldUpdate update = new Network.WorldUpdate();
        List<Network.PlayerUpdate> playerUpdates = new ArrayList<>();
        
        for (PlayerData playerData : activePlayers.values()) {
            Network.PlayerUpdate playerUpdate = new Network.PlayerUpdate();
            playerUpdate.playerId = playerData.getPlayerId();
            playerUpdate.x = playerData.getCharacter().getX();
            playerUpdate.y = playerData.getCharacter().getY();
            playerUpdate.name = playerData.getCharacter().getName();
            playerUpdate.level = playerData.getCharacter().getLevel();
            playerUpdate.health = playerData.getCharacter().getHealth();
            playerUpdate.maxHealth = playerData.getCharacter().getMaxHealth();
            playerUpdates.add(playerUpdate);
        }
        
        update.players = playerUpdates.toArray(new Network.PlayerUpdate[0]);
        
        // Broadcast to all players
        for (Connection connection : activePlayers.keySet()) {
            connection.sendUDP(update);
        }
    }
    
    public void start() {
        try {
            server.bind(Network.TCP_PORT, Network.UDP_PORT);
            server.start();
            
            System.out.println("==============================================");
            System.out.println("MMO Server Started");
            System.out.println("==============================================");
            System.out.println("TCP Port: " + Network.TCP_PORT);
            System.out.println("UDP Port: " + Network.UDP_PORT);
            System.out.println("==============================================");
            System.out.println("Monitoring:");
            System.out.println("- Registered accounts: " + accounts.size());
            System.out.println("- Active players: " + activePlayers.size());
            System.out.println("==============================================");
            
            startWorldUpdates();
            startManaRegeneration();
            startMonitoring();
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void startMonitoring() {
        Timer monitorTimer = new Timer();
        monitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                printServerStatus();
            }
        }, 10000, 10000); // Every 10 seconds
    }
    
    private void printServerStatus() {
        System.out.println("\n=== Server Status ===");
        System.out.println("Active Players: " + activePlayers.size());
        System.out.println("Total Accounts: " + accounts.size());
        System.out.println("Total Characters: " + characters.size());
        System.out.println("Active Sessions: " + sessionTokens.size());
        
        if (!activePlayers.isEmpty()) {
            System.out.println("\nOnline Players:");
            for (PlayerData player : activePlayers.values()) {
                System.out.println("  - " + player.getCharacter().getName() + 
                                 " (Level " + player.getCharacter().getLevel() + 
                                 ") at (" + player.getCharacter().getX() + ", " + 
                                 player.getCharacter().getY() + ")");
            }
        }
        System.out.println("====================\n");
    }
    
    private void startManaRegeneration() {
        manaRegenTimer = new Timer();
        manaRegenTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                regenerateMana();
            }
        }, MANA_REGEN_INTERVAL, MANA_REGEN_INTERVAL);
    }
    
    private void regenerateMana() {
        for (PlayerData playerData : activePlayers.values()) {
            CharacterData character = playerData.getCharacter();
            int currentMana = character.getMana();
            int maxMana = character.getMaxMana();
            
            if (currentMana < maxMana) {
                character.setMana(Math.min(maxMana, currentMana + MANA_REGEN_AMOUNT));
            }
        }
    }
    
    public void stop() {
        if (worldUpdateTimer != null) {
            worldUpdateTimer.cancel();
        }
        if (manaRegenTimer != null) {
            manaRegenTimer.cancel();
        }
        server.stop();
        System.out.println("Server stopped");
    }
    
    public static void main(String[] args) {
        MMOServer server = new MMOServer();
        server.start();
        
        // Keep server running
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nType 'exit' to stop the server");
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                server.stop();
                scanner.close();
                System.exit(0);
                break;
            } else if (input.equalsIgnoreCase("status")) {
                server.printServerStatus();
            }
        }
    }
    
    // User account class
    private static class UserAccount {
        String username;
        String password;
        String email;
        List<Long> characterIds;
        long createdAt;
        
        UserAccount(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.characterIds = new ArrayList<>();
            this.createdAt = System.currentTimeMillis();
        }
    }
}
