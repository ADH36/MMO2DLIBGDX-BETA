package com.mmo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mmo.game.MMOGame;
import com.mmo.graphics.AbilityEffect;
import com.mmo.graphics.ParticleSystem;
import com.mmo.graphics.PlayerAnimation;
import com.mmo.graphics.TextureGenerator;
import com.mmo.models.Ability;
import com.mmo.models.PlayerData;
import com.mmo.network.Network;
import com.mmo.world.WorldRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Main game screen - open world gameplay with 3D-style graphics
 */
public class GameScreen implements Screen {
    private final MMOGame game;
    private final PlayerData playerData;
    private final OrthographicCamera camera;
    private final WorldRenderer worldRenderer;
    private final ParticleSystem particleSystem;
    private final PlayerAnimation playerAnimation;
    private final Map<Long, PlayerAnimation> otherPlayerAnimations;
    private final AbilityEffect abilityEffect;
    
    // Character textures
    private Texture playerTexture;
    private Map<Long, Texture> otherPlayerTextures;
    
    private Map<Long, Network.PlayerUpdate> otherPlayers;
    private Vector2 playerPosition;
    private Vector2 playerVelocity;
    private float moveSpeed = 150f;
    private float cameraShake = 0f;
    private Vector2 cameraOffset = new Vector2();
    
    private String chatMessage = "";
    private boolean chatActive = false;
    private String[] chatHistory = new String[10];
    private int chatHistoryIndex = 0;
    
    private long selectedTargetId = -1; // Currently selected target for combat
    private String combatFeedback = ""; // Combat feedback message
    private long combatFeedbackTime = 0; // When to clear combat feedback
    private static final long FEEDBACK_DURATION = 3000; // Show feedback for 3 seconds
    
    private boolean inventoryOpen = false; // Inventory UI state
    private int selectedInventorySlot = -1; // Currently selected inventory slot
    
    public GameScreen(MMOGame game, PlayerData playerData) {
        this.game = game;
        this.playerData = playerData;
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        worldRenderer = new WorldRenderer();
        particleSystem = new ParticleSystem();
        playerAnimation = new PlayerAnimation();
        otherPlayerAnimations = new HashMap<>();
        otherPlayerTextures = new HashMap<>();
        abilityEffect = new AbilityEffect();
        otherPlayers = new HashMap<>();
        
        // Generate player texture based on class
        String className = playerData.getCharacter().getCharacterClass().getName();
        playerTexture = TextureGenerator.generateCharacterSprite(className, 0);
        
        playerPosition = new Vector2(playerData.getCharacter().getX(), playerData.getCharacter().getY());
        playerVelocity = new Vector2(0, 0);
        
        setupNetworkListener();
        
        addChatMessage("Welcome to the world, " + playerData.getCharacter().getName() + "!");
    }
    
    private void setupNetworkListener() {
        game.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.WorldUpdate) {
                    Network.WorldUpdate update = (Network.WorldUpdate) object;
                    handleWorldUpdate(update);
                } else if (object instanceof Network.ChatMessage) {
                    Network.ChatMessage msg = (Network.ChatMessage) object;
                    addChatMessage(msg.sender + ": " + msg.message);
                } else if (object instanceof Network.UseAbilityResponse) {
                    Network.UseAbilityResponse response = (Network.UseAbilityResponse) object;
                    if (!response.success) {
                        showCombatFeedback(response.message);
                    } else {
                        // Update local character stats
                        playerData.getCharacter().setMana(response.currentMana);
                        playerData.getCharacter().setHealth(response.currentHealth);
                    }
                } else if (object instanceof Network.CombatEvent) {
                    Network.CombatEvent event = (Network.CombatEvent) object;
                    handleCombatEvent(event);
                } else if (object instanceof Network.PlayerDeath) {
                    Network.PlayerDeath death = (Network.PlayerDeath) object;
                    handlePlayerDeath(death);
                } else if (object instanceof Network.PlayerRespawn) {
                    Network.PlayerRespawn respawn = (Network.PlayerRespawn) object;
                    handlePlayerRespawn(respawn);
                } else if (object instanceof Network.UseItemResponse) {
                    Network.UseItemResponse response = (Network.UseItemResponse) object;
                    handleUseItemResponse(response);
                } else if (object instanceof Network.EquipItemResponse) {
                    Network.EquipItemResponse response = (Network.EquipItemResponse) object;
                    handleEquipItemResponse(response);
                } else if (object instanceof Network.UnequipItemResponse) {
                    Network.UnequipItemResponse response = (Network.UnequipItemResponse) object;
                    handleUnequipItemResponse(response);
                }
            }
        });
    }
    
    private void handleWorldUpdate(Network.WorldUpdate update) {
        otherPlayers.clear();
        if (update.players != null) {
            for (Network.PlayerUpdate player : update.players) {
                if (player.playerId != playerData.getPlayerId()) {
                    otherPlayers.put(player.playerId, player);
                }
            }
        }
    }
    
    private void addChatMessage(String message) {
        chatHistory[chatHistoryIndex] = message;
        chatHistoryIndex = (chatHistoryIndex + 1) % chatHistory.length;
    }
    
    private void showCombatFeedback(String message) {
        combatFeedback = message;
        combatFeedbackTime = System.currentTimeMillis() + FEEDBACK_DURATION;
    }
    
    private void handleCombatEvent(Network.CombatEvent event) {
        StringBuilder message = new StringBuilder();
        
        // Get attacker and target positions for visual effects
        float attackerX = playerPosition.x;
        float attackerY = playerPosition.y;
        float targetX = playerPosition.x;
        float targetY = playerPosition.y;
        
        if (event.attackerId != playerData.getPlayerId()) {
            Network.PlayerUpdate attacker = otherPlayers.get(event.attackerId);
            if (attacker != null) {
                attackerX = attacker.x;
                attackerY = attacker.y;
            }
        }
        
        if (event.targetId != playerData.getPlayerId()) {
            Network.PlayerUpdate target = otherPlayers.get(event.targetId);
            if (target != null) {
                targetX = target.x;
                targetY = target.y;
            }
        }
        
        // Trigger ability visual effect
        AbilityEffect.EffectType effectType = getEffectTypeForAbility(event.abilityName);
        abilityEffect.start(attackerX, attackerY, targetX, targetY, event.abilityName, effectType);
        
        if (event.attackerId == playerData.getPlayerId()) {
            // You attacked someone
            if (event.damage > 0) {
                message.append("You hit ").append(event.targetName).append(" with ").append(event.abilityName);
                if (event.isCritical) message.append(" (CRITICAL!)");
                message.append(" for ").append(event.damage).append(" damage!");
                particleSystem.createHitEffect(targetX, targetY, event.isCritical);
                playerAnimation.playAttackAnimation();
            } else if (event.healing > 0) {
                message.append("You healed ").append(event.targetName).append(" for ").append(event.healing).append(" HP!");
                particleSystem.createHealEffect(targetX, targetY);
            }
        } else if (event.targetId == playerData.getPlayerId()) {
            // You were attacked
            if (event.damage > 0) {
                message.append(event.attackerName).append(" hit you with ").append(event.abilityName);
                if (event.isCritical) message.append(" (CRITICAL!)");
                message.append(" for ").append(event.damage).append(" damage!");
                // Update local health
                playerData.getCharacter().setHealth(event.targetHealthAfter);
                particleSystem.createHitEffect(playerPosition.x, playerPosition.y, event.isCritical);
                cameraShake = event.isCritical ? 15f : 8f;
            } else if (event.healing > 0) {
                message.append(event.attackerName).append(" healed you for ").append(event.healing).append(" HP!");
                playerData.getCharacter().setHealth(event.targetHealthAfter);
                particleSystem.createHealEffect(playerPosition.x, playerPosition.y);
            }
        } else {
            // Someone else's combat
            if (event.damage > 0) {
                message.append(event.attackerName).append(" hit ").append(event.targetName).append(" for ").append(event.damage);
                if (event.isCritical) message.append(" CRIT");
                message.append("!");
                particleSystem.createHitEffect(targetX, targetY, event.isCritical);
            }
        }
        
        if (message.length() > 0) {
            addChatMessage(message.toString());
            showCombatFeedback(message.toString());
        }
    }
    
    private AbilityEffect.EffectType getEffectTypeForAbility(String abilityName) {
        if (abilityName == null) return AbilityEffect.EffectType.PROJECTILE;
        
        String lower = abilityName.toLowerCase();
        if (lower.contains("meteor") || lower.contains("storm") || lower.contains("multi")) {
            return AbilityEffect.EffectType.AREA;
        } else if (lower.contains("beam") || lower.contains("teleport")) {
            return AbilityEffect.EffectType.BEAM;
        } else if (lower.contains("buff") || lower.contains("shield") || lower.contains("blessing")) {
            return AbilityEffect.EffectType.BUFF;
        } else {
            return AbilityEffect.EffectType.PROJECTILE;
        }
    }
    
    private void handlePlayerDeath(Network.PlayerDeath death) {
        String message;
        if (death.playerId == playerData.getPlayerId()) {
            message = "You were killed by " + death.killerName + "! Respawning...";
            showCombatFeedback("YOU DIED!");
        } else if (death.killerId == playerData.getPlayerId()) {
            message = "You killed " + death.playerName + "!";
            showCombatFeedback("ENEMY SLAIN!");
        } else {
            message = death.playerName + " was killed by " + death.killerName;
        }
        addChatMessage(message);
    }
    
    private void handlePlayerRespawn(Network.PlayerRespawn respawn) {
        if (respawn.playerId == playerData.getPlayerId()) {
            playerPosition.set(respawn.x, respawn.y);
            playerData.getCharacter().setX(respawn.x);
            playerData.getCharacter().setY(respawn.y);
            addChatMessage("You have respawned!");
            showCombatFeedback("RESPAWNED");
        }
    }
    
    private void handleUseItemResponse(Network.UseItemResponse response) {
        if (response.success) {
            // Update local stats
            if (response.healthRestored > 0) {
                int newHealth = Math.min(
                    playerData.getCharacter().getHealth() + response.healthRestored,
                    playerData.getCharacter().getMaxHealth()
                );
                playerData.getCharacter().setHealth(newHealth);
                showCombatFeedback("+" + response.healthRestored + " HP");
                particleSystem.createHealEffect(playerPosition.x, playerPosition.y);
            }
            if (response.manaRestored > 0) {
                int newMana = Math.min(
                    playerData.getCharacter().getMana() + response.manaRestored,
                    playerData.getCharacter().getMaxMana()
                );
                playerData.getCharacter().setMana(newMana);
                showCombatFeedback("+" + response.manaRestored + " MP");
                particleSystem.createBurst(playerPosition.x, playerPosition.y, Color.CYAN, 15, 100f);
            }
            addChatMessage(response.message);
        } else {
            showCombatFeedback(response.message);
        }
    }
    
    private void handleEquipItemResponse(Network.EquipItemResponse response) {
        if (response.success) {
            // Update local character with updated stats from server
            if (response.updatedCharacter != null) {
                playerData.setCharacter(response.updatedCharacter);
            }
            showCombatFeedback(response.message);
            addChatMessage(response.message);
            particleSystem.createBurst(playerPosition.x, playerPosition.y, Color.GOLD, 20, 120f);
        } else {
            showCombatFeedback(response.message);
        }
    }
    
    private void handleUnequipItemResponse(Network.UnequipItemResponse response) {
        if (response.success) {
            // Update local character with updated stats from server
            if (response.updatedCharacter != null) {
                playerData.setCharacter(response.updatedCharacter);
            }
            showCombatFeedback(response.message);
            addChatMessage(response.message);
        } else {
            showCombatFeedback(response.message);
        }
    }
    
    @Override
    public void show() {}
    
    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }
    
    private void update(float delta) {
        // Update particles
        particleSystem.update(delta);
        
        // Update player animation
        boolean isMoving = playerVelocity.len() > 0;
        playerAnimation.update(delta, isMoving, playerVelocity.x, playerVelocity.y);
        
        // Update ability effects
        abilityEffect.update(delta);
        
        // Update other player animations
        for (Map.Entry<Long, Network.PlayerUpdate> entry : otherPlayers.entrySet()) {
            if (!otherPlayerAnimations.containsKey(entry.getKey())) {
                otherPlayerAnimations.put(entry.getKey(), new PlayerAnimation());
            }
            // Assume other players are idle (we don't have their velocity)
            otherPlayerAnimations.get(entry.getKey()).update(delta, false, 0, 0);
        }
        
        // Update camera shake
        if (cameraShake > 0) {
            cameraShake = Math.max(0, cameraShake - delta * 30f);
            cameraOffset.set(
                (float)(Math.random() - 0.5f) * cameraShake,
                (float)(Math.random() - 0.5f) * cameraShake
            );
        } else {
            cameraOffset.set(0, 0);
        }
        
        // Clear combat feedback if expired
        if (System.currentTimeMillis() > combatFeedbackTime) {
            combatFeedback = "";
        }
        
        // Handle input
        if (!chatActive && !inventoryOpen) {
            handleMovementInput(delta);
            handleAbilityInput();
            handleTargetSelection();
        }
        handleChatInput();
        handleInventoryInput();
        
        // Update camera to follow player with shake
        camera.position.set(playerPosition.x + cameraOffset.x, playerPosition.y + cameraOffset.y, 0);
        camera.update();
    }
    
    private void handleMovementInput(float delta) {
        playerVelocity.set(0, 0);
        
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerVelocity.y = moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerVelocity.y = -moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerVelocity.x = -moveSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerVelocity.x = moveSpeed;
        }
        
        // Normalize diagonal movement
        if (playerVelocity.len() > 0) {
            playerVelocity.nor().scl(moveSpeed);
        }
        
        // Update position
        playerPosition.add(playerVelocity.x * delta, playerVelocity.y * delta);
        
        // Create movement trail particles
        if (playerVelocity.len() > 0 && Math.random() < 0.3) {
            particleSystem.createMovementTrail(playerPosition.x, playerPosition.y, 
                new Color(0.5f, 0.5f, 1f, 0.5f));
        }
        
        // Send position update to server
        if (playerVelocity.len() > 0) {
            Network.PlayerMoveRequest request = new Network.PlayerMoveRequest();
            request.x = playerPosition.x;
            request.y = playerPosition.y;
            game.client.sendUDP(request);
        }
    }
    
    private void handleAbilityInput() {
        for (int i = Input.Keys.NUM_1; i <= Input.Keys.NUM_4; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                int abilityIndex = i - Input.Keys.NUM_1;
                useAbility(abilityIndex);
            }
        }
    }
    
    private void handleTargetSelection() {
        // Use TAB to cycle through nearby targets
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            selectNextTarget();
        }
        
        // Use T to target nearest enemy
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            selectNearestTarget();
        }
    }
    
    private void selectNextTarget() {
        if (otherPlayers.isEmpty()) {
            selectedTargetId = -1;
            return;
        }
        
        boolean foundCurrent = false;
        long firstId = -1;
        
        for (Network.PlayerUpdate player : otherPlayers.values()) {
            if (firstId == -1) {
                firstId = player.playerId;
            }
            
            if (foundCurrent) {
                selectedTargetId = player.playerId;
                addChatMessage("Targeted: " + player.name);
                return;
            }
            
            if (player.playerId == selectedTargetId) {
                foundCurrent = true;
            }
        }
        
        // Cycle back to first or select first
        selectedTargetId = firstId;
        if (selectedTargetId > 0) {
            Network.PlayerUpdate target = otherPlayers.get(selectedTargetId);
            if (target != null) {
                addChatMessage("Targeted: " + target.name);
            }
        }
    }
    
    private void selectNearestTarget() {
        if (otherPlayers.isEmpty()) {
            selectedTargetId = -1;
            return;
        }
        
        float minDistance = Float.MAX_VALUE;
        Network.PlayerUpdate nearestPlayer = null;
        
        for (Network.PlayerUpdate player : otherPlayers.values()) {
            float distance = (float)Math.sqrt(
                Math.pow(playerPosition.x - player.x, 2) + 
                Math.pow(playerPosition.y - player.y, 2)
            );
            
            if (distance < minDistance) {
                minDistance = distance;
                nearestPlayer = player;
            }
        }
        
        if (nearestPlayer != null) {
            selectedTargetId = nearestPlayer.playerId;
            addChatMessage("Targeted: " + nearestPlayer.name + " (" + (int)minDistance + " units away)");
        }
    }
    
    private void useAbility(int abilityIndex) {
        if (playerData.getCharacter().getAbilities() != null && 
            abilityIndex < playerData.getCharacter().getAbilities().size()) {
            
            Ability ability = playerData.getCharacter().getAbilities().get(abilityIndex);
            
            Network.UseAbilityRequest request = new Network.UseAbilityRequest();
            request.abilityIndex = abilityIndex;
            request.targetPlayerId = selectedTargetId;
            request.targetX = playerPosition.x;
            request.targetY = playerPosition.y;
            game.client.sendTCP(request);
            
            String targetInfo = "";
            if (selectedTargetId > 0) {
                Network.PlayerUpdate target = otherPlayers.get(selectedTargetId);
                if (target != null) {
                    targetInfo = " on " + target.name;
                }
            }
            
            addChatMessage("Using " + ability.getName() + targetInfo);
        }
    }
    
    private void handleChatInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (chatActive) {
                if (!chatMessage.isEmpty()) {
                    sendChatMessage(chatMessage);
                    chatMessage = "";
                }
                chatActive = false;
            } else {
                chatActive = true;
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (chatActive) {
                chatActive = false;
                chatMessage = "";
            } else {
                // Exit to character selection
                game.setScreen(new CharacterSelectionScreen(game));
                dispose();
            }
        }
        
        if (chatActive) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                if (chatMessage.length() > 0) {
                    chatMessage = chatMessage.substring(0, chatMessage.length() - 1);
                }
            }
            
            // Handle text input using proper key mapping
            // Handle letters (A-Z)
            for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    char c = (char) ('a' + (i - Input.Keys.A));
                    // Check if shift is pressed for uppercase
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                        Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                        c = Character.toUpperCase(c);
                    }
                    if (chatMessage.length() < 100) {
                        chatMessage += c;
                    }
                }
            }
            
            // Handle numbers (0-9)
            for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    char c = (char) ('0' + (i - Input.Keys.NUM_0));
                    if (chatMessage.length() < 100) {
                        chatMessage += c;
                    }
                }
            }
            
            // Handle numpad numbers
            for (int i = Input.Keys.NUMPAD_0; i <= Input.Keys.NUMPAD_9; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    char c = (char) ('0' + (i - Input.Keys.NUMPAD_0));
                    if (chatMessage.length() < 100) {
                        chatMessage += c;
                    }
                }
            }
            
            // Handle space and punctuation
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (chatMessage.length() < 100) {
                    chatMessage += " ";
                }
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
                if (chatMessage.length() < 100) {
                    chatMessage += ".";
                }
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.COMMA)) {
                if (chatMessage.length() < 100) {
                    chatMessage += ",";
                }
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.SEMICOLON)) {
                if (chatMessage.length() < 100) {
                    chatMessage += ";";
                }
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.APOSTROPHE)) {
                if (chatMessage.length() < 100) {
                    chatMessage += "'";
                }
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.SLASH)) {
                if (chatMessage.length() < 100) {
                    chatMessage += "/";
                }
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
                if (chatMessage.length() < 100) {
                    chatMessage += "-";
                }
            }
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
                if (chatMessage.length() < 100) {
                    chatMessage += "=";
                }
            }
        }
    }
    
    private void handleInventoryInput() {
        // Toggle inventory with 'I' key
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen;
            if (inventoryOpen) {
                addChatMessage("Inventory opened");
            }
        }
        
        // Handle inventory interactions when open
        if (inventoryOpen) {
            // Close inventory with ESC
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                inventoryOpen = false;
                selectedInventorySlot = -1;
                return;
            }
            
            // Use items with number keys (0-9 for slots 0-9)
            for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    int slotIndex = i - Input.Keys.NUM_0;
                    useInventoryItem(slotIndex);
                    break;
                }
            }
            
            // Equip/unequip items with E key (0-9 for slots 0-9)
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
                    if (Gdx.input.isKeyPressed(i)) {
                        int slotIndex = i - Input.Keys.NUM_0;
                        equipInventoryItem(slotIndex);
                        break;
                    }
                }
            }
            
            // Unequip weapon with U+W
            if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
                if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                    unequipItem(com.mmo.models.EquipmentSlot.WEAPON);
                } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    unequipItem(com.mmo.models.EquipmentSlot.ARMOR);
                }
            }
        }
    }
    
    private void useInventoryItem(int slotIndex) {
        com.mmo.models.InventoryItem invItem = playerData.getCharacter().getInventory().getItemAtSlot(slotIndex);
        if (invItem == null) {
            showCombatFeedback("No item in slot " + slotIndex);
            return;
        }
        
        // Only consumables can be used
        if (invItem.getItem().getType() != com.mmo.models.ItemType.CONSUMABLE) {
            showCombatFeedback("Cannot use this item");
            return;
        }
        
        // Send use item request to server
        Network.UseItemRequest request = new Network.UseItemRequest();
        request.slotIndex = slotIndex;
        game.client.sendTCP(request);
    }
    
    private void equipInventoryItem(int slotIndex) {
        com.mmo.models.InventoryItem invItem = playerData.getCharacter().getInventory().getItemAtSlot(slotIndex);
        if (invItem == null) {
            showCombatFeedback("No item in slot " + slotIndex);
            return;
        }
        
        // Check if item is equippable
        com.mmo.models.ItemType type = invItem.getItem().getType();
        if (type != com.mmo.models.ItemType.WEAPON && type != com.mmo.models.ItemType.ARMOR) {
            showCombatFeedback("Cannot equip this item");
            return;
        }
        
        // Send equip item request to server
        Network.EquipItemRequest request = new Network.EquipItemRequest();
        request.slotIndex = slotIndex;
        game.client.sendTCP(request);
    }
    
    private void unequipItem(com.mmo.models.EquipmentSlot equipmentSlot) {
        // Check if there's an item in this slot
        if (!playerData.getCharacter().hasEquippedItem(equipmentSlot)) {
            showCombatFeedback("No item equipped in this slot");
            return;
        }
        
        // Send unequip item request to server
        Network.UnequipItemRequest request = new Network.UnequipItemRequest();
        request.equipmentSlot = equipmentSlot;
        game.client.sendTCP(request);
    }
    
    private void sendChatMessage(String message) {
        Network.ChatMessage chatMsg = new Network.ChatMessage();
        chatMsg.sender = playerData.getCharacter().getName();
        chatMsg.message = message;
        chatMsg.timestamp = System.currentTimeMillis();
        game.client.sendTCP(chatMsg);
    }
    
    private void draw() {
        Gdx.gl.glClearColor(0.2f, 0.6f, 0.3f, 1); // Green background for grass
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Set camera
        game.batch.setProjectionMatrix(camera.combined);
        
        // Draw world with textures (NO GRIDS)
        worldRenderer.render(camera);
        
        // Draw players with sprites
        drawPlayers();
        
        // Draw UI (fixed position)
        drawUI();
    }
    
    private void drawPlayers() {
        // Draw particles and effects first
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        particleSystem.render(game.shapeRenderer);
        abilityEffect.render(game.shapeRenderer);
        game.shapeRenderer.end();
        
        // Draw character sprites
        game.batch.begin();
        
        // Draw other players with 3D sprites
        for (Network.PlayerUpdate player : otherPlayers.values()) {
            // Get or create texture for this player
            Texture otherTexture = otherPlayerTextures.get(player.playerId);
            if (otherTexture == null) {
                // Determine class from player data (default to warrior if not available)
                String className = "warrior"; // You might want to send class info in PlayerUpdate
                otherTexture = TextureGenerator.generateCharacterSprite(className, 0);
                otherPlayerTextures.put(player.playerId, otherTexture);
            }
            
            // Draw selection highlight
            if (player.playerId == selectedTargetId) {
                game.batch.setColor(1f, 1f, 0f, 0.5f); // Yellow tint for selection
            } else {
                game.batch.setColor(Color.WHITE);
            }
            
            // Draw the sprite (centered on position)
            game.batch.draw(otherTexture, player.x - 32, player.y - 32, 64, 64);
            game.batch.setColor(Color.WHITE);
        }
        
        // Draw local player sprite
        game.batch.draw(playerTexture, playerPosition.x - 32, playerPosition.y - 32, 64, 64);
        
        game.batch.end();
        
        // Draw health bars
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Network.PlayerUpdate player : otherPlayers.values()) {
            drawHealthBar(player.x, player.y, player.health, player.maxHealth);
        }
        drawHealthBar(playerPosition.x, playerPosition.y, 
                     playerData.getCharacter().getHealth(), 
                     playerData.getCharacter().getMaxHealth());
        game.shapeRenderer.end();
        
        // Draw player names
        game.batch.begin();
        game.font.getData().setScale(0.8f);
        
        for (Network.PlayerUpdate player : otherPlayers.values()) {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, player.name + " (Lv" + player.level + ")", 
                          player.x - 30, player.y + 50);
        }
        
        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, playerData.getCharacter().getName() + " (Lv" + playerData.getCharacter().getLevel() + ")", 
                      playerPosition.x - 30, playerPosition.y + 50);
        
        game.batch.end();
    }
    
    private void drawHealthBar(float x, float y, int health, int maxHealth) {
        float barWidth = 40;
        float barHeight = 5;
        float healthPercent = (float)health / maxHealth;
        
        // Shadow
        game.shapeRenderer.setColor(0, 0, 0, 0.5f);
        game.shapeRenderer.rect(x - barWidth / 2 + 1, y + 29, barWidth, barHeight);
        
        // Background (dark red)
        game.shapeRenderer.setColor(0.3f, 0, 0, 0.8f);
        game.shapeRenderer.rect(x - barWidth / 2, y + 30, barWidth, barHeight);
        
        // Health bar with color gradient based on health percentage
        Color healthColor;
        if (healthPercent > 0.6f) {
            healthColor = new Color(0, 0.8f, 0, 1); // Green
        } else if (healthPercent > 0.3f) {
            healthColor = new Color(1f, 0.8f, 0, 1); // Yellow
        } else {
            healthColor = new Color(1f, 0.2f, 0, 1); // Red
        }
        
        game.shapeRenderer.setColor(healthColor);
        game.shapeRenderer.rect(x - barWidth / 2, y + 30, barWidth * healthPercent, barHeight);
        
        // Highlight on top of health bar
        game.shapeRenderer.setColor(1, 1, 1, 0.3f);
        game.shapeRenderer.rect(x - barWidth / 2, y + 30 + barHeight - 1, barWidth * healthPercent, 1);
        
        // Border
        game.shapeRenderer.setColor(Color.BLACK);
        game.shapeRenderer.rect(x - barWidth / 2 - 1, y + 30 - 1, barWidth + 2, 1); // Top
        game.shapeRenderer.rect(x - barWidth / 2 - 1, y + 30 + barHeight, barWidth + 2, 1); // Bottom
        game.shapeRenderer.rect(x - barWidth / 2 - 1, y + 30, 1, barHeight); // Left
        game.shapeRenderer.rect(x + barWidth / 2, y + 30, 1, barHeight); // Right
    }
    
    private void drawUI() {
        // Reset to screen coordinates
        game.batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        game.batch.begin();
        
        // Draw character stats
        game.font.getData().setScale(1.2f);
        game.font.setColor(Color.WHITE);
        float uiX = 10;
        float uiY = Gdx.graphics.getHeight() - 10;
        
        game.font.draw(game.batch, playerData.getCharacter().getName(), uiX, uiY);
        game.font.draw(game.batch, "Level: " + playerData.getCharacter().getLevel(), uiX, uiY - 25);
        game.font.draw(game.batch, "HP: " + playerData.getCharacter().getHealth() + "/" + playerData.getCharacter().getMaxHealth(), uiX, uiY - 50);
        game.font.draw(game.batch, "MP: " + playerData.getCharacter().getMana() + "/" + playerData.getCharacter().getMaxMana(), uiX, uiY - 75);
        
        // Draw abilities with enhanced styling
        game.font.setColor(Color.GOLD);
        game.font.getData().setScale(1.3f);
        game.font.draw(game.batch, "ABILITIES", uiX, uiY - 110);
        
        // End batch to draw ability slots
        game.batch.end();
        
        // Draw ability slot backgrounds
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (playerData.getCharacter().getAbilities() != null) {
            for (int i = 0; i < Math.min(4, playerData.getCharacter().getAbilities().size()); i++) {
                Ability ability = playerData.getCharacter().getAbilities().get(i);
                float abilityY = uiY - 140 - (i * 45);
                
                boolean onCooldown = !playerData.getCharacter().isAbilityReady(i);
                boolean notEnoughMana = playerData.getCharacter().getMana() < ability.getManaCost();
                
                // Shadow
                game.shapeRenderer.setColor(0, 0, 0, 0.5f);
                game.shapeRenderer.rect(uiX + 3, abilityY - 33, 200, 38);
                
                // Slot background based on state
                if (onCooldown) {
                    game.shapeRenderer.setColor(0.2f, 0.1f, 0.1f, 0.8f);
                } else if (notEnoughMana) {
                    game.shapeRenderer.setColor(0.1f, 0.1f, 0.2f, 0.8f);
                } else {
                    game.shapeRenderer.setColor(0.15f, 0.25f, 0.35f, 0.8f);
                }
                game.shapeRenderer.rect(uiX, abilityY - 35, 200, 38);
                
                // Icon background circle
                if (onCooldown) {
                    game.shapeRenderer.setColor(0.3f, 0.1f, 0.1f, 0.9f);
                } else if (notEnoughMana) {
                    game.shapeRenderer.setColor(0.1f, 0.1f, 0.3f, 0.9f);
                } else {
                    game.shapeRenderer.setColor(0.2f, 0.35f, 0.5f, 0.9f);
                }
                game.shapeRenderer.circle(uiX + 18, abilityY - 16, 14);
            }
        }
        game.shapeRenderer.end();
        
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (playerData.getCharacter().getAbilities() != null) {
            for (int i = 0; i < Math.min(4, playerData.getCharacter().getAbilities().size()); i++) {
                Ability ability = playerData.getCharacter().getAbilities().get(i);
                float abilityY = uiY - 140 - (i * 45);
                
                boolean onCooldown = !playerData.getCharacter().isAbilityReady(i);
                boolean notEnoughMana = playerData.getCharacter().getMana() < ability.getManaCost();
                
                // Border
                if (onCooldown) {
                    game.shapeRenderer.setColor(Color.RED);
                } else if (notEnoughMana) {
                    game.shapeRenderer.setColor(Color.GRAY);
                } else {
                    game.shapeRenderer.setColor(Color.CYAN);
                }
                Gdx.gl.glLineWidth(2);
                game.shapeRenderer.rect(uiX, abilityY - 35, 200, 38);
                
                // Icon border
                game.shapeRenderer.circle(uiX + 18, abilityY - 16, 14);
                Gdx.gl.glLineWidth(1);
            }
        }
        game.shapeRenderer.end();
        
        // Begin batch for text
        game.batch.begin();
        game.font.getData().setScale(1f);
        if (playerData.getCharacter().getAbilities() != null) {
            for (int i = 0; i < Math.min(4, playerData.getCharacter().getAbilities().size()); i++) {
                Ability ability = playerData.getCharacter().getAbilities().get(i);
                float abilityY = uiY - 140 - (i * 45);
                
                // Check if ability is on cooldown
                boolean onCooldown = !playerData.getCharacter().isAbilityReady(i);
                boolean notEnoughMana = playerData.getCharacter().getMana() < ability.getManaCost();
                
                // Draw ability icon (number)
                game.font.getData().setScale(1.2f);
                if (onCooldown) {
                    game.font.setColor(Color.RED);
                } else if (notEnoughMana) {
                    game.font.setColor(Color.GRAY);
                } else {
                    game.font.setColor(Color.GOLD);
                }
                game.font.draw(game.batch, String.valueOf(i + 1), uiX + 13, abilityY - 9);
                
                // Draw ability name and info
                game.font.getData().setScale(0.9f);
                if (onCooldown) {
                    game.font.setColor(Color.LIGHT_GRAY);
                    long cooldownRemaining = (playerData.getCharacter().getAbilityCooldowns()[i] - System.currentTimeMillis()) / 1000;
                    game.font.draw(game.batch, ability.getName(), uiX + 38, abilityY - 9);
                    game.font.setColor(Color.RED);
                    game.font.getData().setScale(0.7f);
                    game.font.draw(game.batch, cooldownRemaining + "s", uiX + 38, abilityY - 23);
                } else if (notEnoughMana) {
                    game.font.setColor(Color.GRAY);
                    game.font.draw(game.batch, ability.getName(), uiX + 38, abilityY - 9);
                    game.font.getData().setScale(0.7f);
                    game.font.draw(game.batch, "Not enough mana", uiX + 38, abilityY - 23);
                } else {
                    game.font.setColor(Color.WHITE);
                    game.font.draw(game.batch, ability.getName(), uiX + 38, abilityY - 9);
                    game.font.setColor(Color.CYAN);
                    game.font.getData().setScale(0.7f);
                    game.font.draw(game.batch, ability.getManaCost() + " MP", uiX + 38, abilityY - 23);
                }
            }
        }
        
        // Draw target info
        if (selectedTargetId > 0) {
            Network.PlayerUpdate target = otherPlayers.get(selectedTargetId);
            if (target != null) {
                game.font.setColor(Color.YELLOW);
                game.font.getData().setScale(1.2f);
                game.font.draw(game.batch, "Target: " + target.name, uiX, uiY - 230);
                
                // Calculate distance
                float distance = (float)Math.sqrt(
                    Math.pow(playerPosition.x - target.x, 2) + 
                    Math.pow(playerPosition.y - target.y, 2)
                );
                game.font.getData().setScale(1f);
                game.font.setColor(Color.LIGHT_GRAY);
                game.font.draw(game.batch, "Distance: " + (int)distance, uiX, uiY - 255);
            }
        }
        
        // Draw combat feedback
        if (!combatFeedback.isEmpty()) {
            game.font.getData().setScale(1.5f);
            game.font.setColor(Color.RED);
            float feedbackX = Gdx.graphics.getWidth() / 2 - 150;
            float feedbackY = Gdx.graphics.getHeight() / 2 + 100;
            game.font.draw(game.batch, combatFeedback, feedbackX, feedbackY);
        }
        
        // Draw chat
        game.font.getData().setScale(1f);
        game.font.setColor(Color.WHITE);
        float chatY = 200;
        for (int i = 0; i < chatHistory.length; i++) {
            int index = (chatHistoryIndex - chatHistory.length + i + chatHistory.length) % chatHistory.length;
            if (chatHistory[index] != null) {
                game.font.draw(game.batch, chatHistory[index], 10, chatY + (i * 20));
            }
        }
        
        // Draw chat input
        if (chatActive) {
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Say: " + chatMessage + "_", 10, 160);
        }
        
        // Draw controls
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.getData().setScale(0.8f);
        game.font.draw(game.batch, "WASD: Move | 1-4: Abilities | TAB/T: Target | I: Inventory | ENTER: Chat | ESC: Exit", 10, 30);
        
        // Draw inventory if open
        if (inventoryOpen) {
            drawInventory();
        }
        
        game.batch.end();
    }
    
    private void drawInventory() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        
        // Enhanced background panel with gradient
        int panelWidth = 650;
        int panelHeight = 500;
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = (screenHeight - panelHeight) / 2;
        
        // End batch to use shape renderer
        game.batch.end();
        
        // Draw shadow
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0, 0, 0, 0.6f);
        game.shapeRenderer.rect(panelX + 8, panelY - 8, panelWidth, panelHeight);
        game.shapeRenderer.end();
        
        // Draw gradient background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int gradientSteps = 20;
        float stepHeight = panelHeight / (float)gradientSteps;
        for (int i = 0; i < gradientSteps; i++) {
            float progress = i / (float)gradientSteps;
            float darkness = 0.15f - progress * 0.05f;
            game.shapeRenderer.setColor(darkness, darkness * 0.8f, darkness * 0.6f, 0.95f);
            game.shapeRenderer.rect(panelX, panelY + i * stepHeight, panelWidth, stepHeight);
        }
        game.shapeRenderer.end();
        
        // Draw decorative borders
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3);
        game.shapeRenderer.setColor(Color.GOLD);
        game.shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        
        Gdx.gl.glLineWidth(2);
        game.shapeRenderer.setColor(new Color(0.8f, 0.6f, 0.2f, 0.8f));
        game.shapeRenderer.rect(panelX + 5, panelY + 5, panelWidth - 10, panelHeight - 10);
        
        Gdx.gl.glLineWidth(1);
        game.shapeRenderer.end();
        
        // Begin batch again for text
        game.batch.begin();
        
        // Title with shadow
        game.font.getData().setScale(1.8f);
        game.font.setColor(0, 0, 0, 0.8f);
        game.font.draw(game.batch, "ADVENTURER'S BAG", panelX + 22, panelY + panelHeight - 18);
        game.font.setColor(Color.GOLD);
        game.font.draw(game.batch, "ADVENTURER'S BAG", panelX + 20, panelY + panelHeight - 20);
        
        // Gold with coin icon
        game.font.getData().setScale(1.3f);
        game.font.setColor(0, 0, 0, 0.8f);
        int gold = playerData.getCharacter().getInventory().getGold();
        game.font.draw(game.batch, "Gold: " + gold, panelX + panelWidth - 148, panelY + panelHeight - 18);
        game.font.setColor(Color.YELLOW);
        game.font.draw(game.batch, "Gold: " + gold, panelX + panelWidth - 150, panelY + panelHeight - 20);
        
        // End batch for grid rendering
        game.batch.end();
        
        // Draw inventory grid (4 columns x 5 rows = 20 slots)
        int slotSize = 75;
        int slotPadding = 12;
        int columns = 4;
        int rows = 5;
        int gridStartX = panelX + 35;
        int gridStartY = panelY + panelHeight - 90;
        
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int slotIndex = row * columns + col;
                int slotX = gridStartX + col * (slotSize + slotPadding);
                int slotY = gridStartY - row * (slotSize + slotPadding);
                
                com.mmo.models.InventoryItem invItem = playerData.getCharacter().getInventory().getItemAtSlot(slotIndex);
                
                // Slot shadow
                game.shapeRenderer.setColor(0, 0, 0, 0.5f);
                game.shapeRenderer.rect(slotX + 3, slotY - 3, slotSize, slotSize);
                
                // Slot background with gradient
                if (invItem != null) {
                    // Highlight slot with item based on rarity
                    Color rarityColor = getRarityColor(invItem.getItem().getRarity());
                    game.shapeRenderer.setColor(rarityColor.r * 0.15f, rarityColor.g * 0.15f, rarityColor.b * 0.15f, 0.8f);
                } else {
                    game.shapeRenderer.setColor(0.15f, 0.15f, 0.18f, 0.9f);
                }
                game.shapeRenderer.rect(slotX, slotY, slotSize, slotSize);
                
                // Inner slot highlight for depth
                if (invItem != null) {
                    Color rarityColor = getRarityColor(invItem.getItem().getRarity());
                    game.shapeRenderer.setColor(rarityColor.r * 0.25f, rarityColor.g * 0.25f, rarityColor.b * 0.25f, 0.6f);
                } else {
                    game.shapeRenderer.setColor(0.25f, 0.25f, 0.28f, 0.6f);
                }
                game.shapeRenderer.rect(slotX + 2, slotY + 2, slotSize - 4, slotSize - 4);
            }
        }
        
        game.shapeRenderer.end();
        
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(2);
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int slotIndex = row * columns + col;
                int slotX = gridStartX + col * (slotSize + slotPadding);
                int slotY = gridStartY - row * (slotSize + slotPadding);
                
                com.mmo.models.InventoryItem invItem = playerData.getCharacter().getInventory().getItemAtSlot(slotIndex);
                
                // Slot border based on rarity
                if (invItem != null) {
                    Color rarityColor = getRarityColor(invItem.getItem().getRarity());
                    game.shapeRenderer.setColor(rarityColor);
                } else {
                    game.shapeRenderer.setColor(0.4f, 0.4f, 0.5f, 0.8f);
                }
                game.shapeRenderer.rect(slotX, slotY, slotSize, slotSize);
                
                // Inner border for beveled effect
                if (invItem != null) {
                    game.shapeRenderer.setColor(Color.WHITE.r * 0.3f, Color.WHITE.g * 0.3f, Color.WHITE.b * 0.3f, 0.4f);
                } else {
                    game.shapeRenderer.setColor(0.3f, 0.3f, 0.35f, 0.4f);
                }
                game.shapeRenderer.rect(slotX + 3, slotY + 3, slotSize - 6, slotSize - 6);
            }
        }
        
        Gdx.gl.glLineWidth(1);
        game.shapeRenderer.end();
        
        // Begin batch again for item text
        game.batch.begin();
        
        // Draw items in inventory
        game.font.getData().setScale(0.85f);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int slotIndex = row * columns + col;
                int slotX = gridStartX + col * (slotSize + slotPadding);
                int slotY = gridStartY - row * (slotSize + slotPadding);
                
                // Draw slot number (only for first 10 slots)
                if (slotIndex < 10) {
                    game.font.setColor(0, 0, 0, 0.8f);
                    game.font.getData().setScale(0.65f);
                    game.font.draw(game.batch, String.valueOf(slotIndex), slotX + slotSize - 14, slotY + slotSize - 7);
                    game.font.setColor(Color.LIGHT_GRAY);
                    game.font.draw(game.batch, String.valueOf(slotIndex), slotX + slotSize - 15, slotY + slotSize - 8);
                    game.font.getData().setScale(0.85f);
                }
                
                com.mmo.models.InventoryItem invItem = playerData.getCharacter().getInventory().getItemAtSlot(slotIndex);
                if (invItem != null) {
                    // Draw item icon representation
                    String itemIcon = getItemIcon(invItem.getItem().getType());
                    game.font.getData().setScale(2.0f);
                    Color rarityColor = getRarityColor(invItem.getItem().getRarity());
                    game.font.setColor(0, 0, 0, 0.8f);
                    game.font.draw(game.batch, itemIcon, slotX + slotSize / 2 - 11, slotY + slotSize / 2 + 9);
                    game.font.setColor(rarityColor);
                    game.font.draw(game.batch, itemIcon, slotX + slotSize / 2 - 12, slotY + slotSize / 2 + 10);
                    
                    // Draw item name
                    game.font.getData().setScale(0.7f);
                    game.font.setColor(0, 0, 0, 0.9f);
                    String itemName = invItem.getItem().getName();
                    if (itemName.length() > 9) {
                        itemName = itemName.substring(0, 9);
                    }
                    game.font.draw(game.batch, itemName, slotX + 4, slotY + 14);
                    game.font.setColor(rarityColor);
                    game.font.draw(game.batch, itemName, slotX + 3, slotY + 15);
                    
                    // Draw quantity if stackable
                    if (invItem.getItem().isStackable()) {
                        game.font.getData().setScale(0.9f);
                        game.font.setColor(0, 0, 0, 0.9f);
                        game.font.draw(game.batch, "x" + invItem.getQuantity(), slotX + slotSize - 27, slotY + 19);
                        game.font.setColor(Color.WHITE);
                        game.font.draw(game.batch, "x" + invItem.getQuantity(), slotX + slotSize - 28, slotY + 20);
                    }
                    game.font.getData().setScale(0.85f);
                }
            }
        }
        
        // Instructions with better styling
        game.font.getData().setScale(0.75f);
        game.font.setColor(0, 0, 0, 0.8f);
        game.font.draw(game.batch, "0-9: Use Item | E+0-9: Equip | U+W: Unequip Weapon | U+A: Unequip Armor", 
                      panelX + 22, panelY + 27);
        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, "0-9: Use Item | E+0-9: Equip | U+W: Unequip Weapon | U+A: Unequip Armor", 
                      panelX + 20, panelY + 25);
        
        // Draw equipped items section with enhanced styling
        int equipX = panelX + 410;
        int equipY = panelY + panelHeight - 90;
        
        // Section background
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0.1f, 0.1f, 0.15f, 0.7f);
        game.shapeRenderer.rect(equipX - 10, panelY + 50, 230, panelHeight - 120);
        game.shapeRenderer.end();
        
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(Color.GOLD);
        Gdx.gl.glLineWidth(2);
        game.shapeRenderer.rect(equipX - 10, panelY + 50, 230, panelHeight - 120);
        Gdx.gl.glLineWidth(1);
        game.shapeRenderer.end();
        
        game.batch.begin();
        
        game.font.getData().setScale(1.3f);
        game.font.setColor(0, 0, 0, 0.8f);
        game.font.draw(game.batch, "EQUIPPED", equipX + 22, equipY + 32);
        game.font.setColor(Color.GOLD);
        game.font.draw(game.batch, "EQUIPPED", equipX + 20, equipY + 30);
        
        game.font.getData().setScale(0.95f);
        
        // Show equipped weapon with icon
        com.mmo.models.Item weaponItem = playerData.getCharacter().getEquippedItem(com.mmo.models.EquipmentSlot.WEAPON);
        if (weaponItem != null) {
            // Weapon icon
            game.font.getData().setScale(1.5f);
            Color weaponColor = getRarityColor(weaponItem.getRarity());
            game.font.setColor(0, 0, 0, 0.8f);
            game.font.draw(game.batch, "⚔", equipX - 4, equipY + 2);
            game.font.setColor(weaponColor);
            game.font.draw(game.batch, "⚔", equipX - 5, equipY + 3);
            
            game.font.getData().setScale(0.85f);
            game.font.setColor(0, 0, 0, 0.8f);
            game.font.draw(game.batch, "Weapon:", equipX + 21, equipY + 1);
            game.font.setColor(Color.LIGHT_GRAY);
            game.font.draw(game.batch, "Weapon:", equipX + 20, equipY);
            
            game.font.setColor(0, 0, 0, 0.9f);
            game.font.draw(game.batch, weaponItem.getName(), equipX + 11, equipY - 19);
            game.font.setColor(weaponColor);
            game.font.draw(game.batch, weaponItem.getName(), equipX + 10, equipY - 20);
            
            game.font.setColor(0, 0, 0, 0.8f);
            game.font.getData().setScale(0.7f);
            game.font.draw(game.batch, "ATK +" + weaponItem.getAttackBonus(), equipX + 11, equipY - 34);
            game.font.setColor(Color.GREEN);
            game.font.draw(game.batch, "ATK +" + weaponItem.getAttackBonus(), equipX + 10, equipY - 35);
            game.font.getData().setScale(0.95f);
        } else {
            game.font.getData().setScale(0.85f);
            game.font.setColor(Color.DARK_GRAY);
            game.font.draw(game.batch, "Weapon: Empty", equipX, equipY);
        }
        
        // Show equipped armor with icon
        com.mmo.models.Item armorItem = playerData.getCharacter().getEquippedItem(com.mmo.models.EquipmentSlot.ARMOR);
        if (armorItem != null) {
            // Armor icon
            game.font.getData().setScale(1.5f);
            Color armorColor = getRarityColor(armorItem.getRarity());
            game.font.setColor(0, 0, 0, 0.8f);
            game.font.draw(game.batch, "🛡", equipX - 4, equipY - 78);
            game.font.setColor(armorColor);
            game.font.draw(game.batch, "🛡", equipX - 5, equipY - 77);
            
            game.font.getData().setScale(0.85f);
            game.font.setColor(0, 0, 0, 0.8f);
            game.font.draw(game.batch, "Armor:", equipX + 21, equipY - 79);
            game.font.setColor(Color.LIGHT_GRAY);
            game.font.draw(game.batch, "Armor:", equipX + 20, equipY - 80);
            
            game.font.setColor(0, 0, 0, 0.9f);
            game.font.draw(game.batch, armorItem.getName(), equipX + 11, equipY - 99);
            game.font.setColor(armorColor);
            game.font.draw(game.batch, armorItem.getName(), equipX + 10, equipY - 100);
            
            game.font.setColor(0, 0, 0, 0.8f);
            game.font.getData().setScale(0.7f);
            String bonusText = "";
            if (armorItem.getDefenseBonus() > 0) bonusText += "DEF +" + armorItem.getDefenseBonus() + " ";
            if (armorItem.getHealthBonus() > 0) bonusText += "HP +" + armorItem.getHealthBonus();
            game.font.draw(game.batch, bonusText, equipX + 11, equipY - 114);
            game.font.setColor(Color.GREEN);
            game.font.draw(game.batch, bonusText, equipX + 10, equipY - 115);
            game.font.getData().setScale(0.95f);
        } else {
            game.font.getData().setScale(0.85f);
            game.font.setColor(Color.DARK_GRAY);
            game.font.draw(game.batch, "Armor: Empty", equipX, equipY - 80);
        }
    }
    
    // Get simple icon representation for item types
    private String getItemIcon(com.mmo.models.ItemType itemType) {
        switch (itemType) {
            case WEAPON: return "⚔";
            case ARMOR: return "🛡";
            case CONSUMABLE: return "⚗";
            case MATERIAL: return "◆";
            case QUEST: return "★";
            default: return "?";
        }
    }
    
    private Color getRarityColor(com.mmo.models.ItemRarity rarity) {
        switch (rarity) {
            case COMMON: return Color.WHITE;
            case UNCOMMON: return Color.GREEN;
            case RARE: return Color.BLUE;
            case EPIC: return Color.PURPLE;
            case LEGENDARY: return Color.ORANGE;
            default: return Color.GRAY;
        }
    }
    
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        worldRenderer.dispose();
        playerTexture.dispose();
        for (Texture texture : otherPlayerTextures.values()) {
            texture.dispose();
        }
        otherPlayerTextures.clear();
    }
}
