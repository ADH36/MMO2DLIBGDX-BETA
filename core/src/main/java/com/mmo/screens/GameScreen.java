package com.mmo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mmo.game.MMOGame;
import com.mmo.models.PlayerData;
import com.mmo.network.Network;
import com.mmo.world.WorldRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Main game screen - open world gameplay
 */
public class GameScreen implements Screen {
    private final MMOGame game;
    private final PlayerData playerData;
    private final OrthographicCamera camera;
    private final WorldRenderer worldRenderer;
    
    private Map<Long, Network.PlayerUpdate> otherPlayers;
    private Vector2 playerPosition;
    private Vector2 playerVelocity;
    private float moveSpeed = 150f;
    
    private String chatMessage = "";
    private boolean chatActive = false;
    private String[] chatHistory = new String[10];
    private int chatHistoryIndex = 0;
    
    public GameScreen(MMOGame game, PlayerData playerData) {
        this.game = game;
        this.playerData = playerData;
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        worldRenderer = new WorldRenderer();
        otherPlayers = new HashMap<>();
        
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
                        addChatMessage("Ability failed: " + response.message);
                    }
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
    
    @Override
    public void show() {}
    
    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }
    
    private void update(float delta) {
        // Handle input
        if (!chatActive) {
            handleMovementInput(delta);
            handleAbilityInput();
        }
        handleChatInput();
        
        // Update camera to follow player
        camera.position.set(playerPosition.x, playerPosition.y, 0);
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
    
    private void useAbility(int abilityIndex) {
        if (playerData.getCharacter().getAbilities() != null && 
            abilityIndex < playerData.getCharacter().getAbilities().size()) {
            
            Network.UseAbilityRequest request = new Network.UseAbilityRequest();
            request.abilityIndex = abilityIndex;
            request.targetX = playerPosition.x;
            request.targetY = playerPosition.y;
            game.client.sendTCP(request);
            
            String abilityName = playerData.getCharacter().getAbilities().get(abilityIndex).getName();
            addChatMessage("Used ability: " + abilityName);
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
            
            // Handle text input
            for (int i = 0; i < 256; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    char c = (char) i;
                    if ((Character.isLetterOrDigit(c) || c == ' ' || c == '!' || c == '?' || c == '.') 
                        && chatMessage.length() < 100) {
                        chatMessage += c;
                    }
                }
            }
        }
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
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Draw world
        worldRenderer.render(game.shapeRenderer, camera);
        
        // Draw players
        drawPlayers();
        
        // Draw UI (fixed position)
        drawUI();
    }
    
    private void drawPlayers() {
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Draw other players
        for (Network.PlayerUpdate player : otherPlayers.values()) {
            game.shapeRenderer.setColor(Color.RED);
            game.shapeRenderer.circle(player.x, player.y, 20);
        }
        
        // Draw local player
        game.shapeRenderer.setColor(Color.BLUE);
        game.shapeRenderer.circle(playerPosition.x, playerPosition.y, 20);
        
        game.shapeRenderer.end();
        
        // Draw player names
        game.batch.begin();
        game.font.getData().setScale(0.8f);
        
        for (Network.PlayerUpdate player : otherPlayers.values()) {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, player.name + " (Lv" + player.level + ")", 
                          player.x - 30, player.y + 35);
        }
        
        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, playerData.getCharacter().getName() + " (Lv" + playerData.getCharacter().getLevel() + ")", 
                      playerPosition.x - 30, playerPosition.y + 35);
        
        game.batch.end();
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
        
        // Draw abilities
        game.font.setColor(Color.GOLD);
        game.font.draw(game.batch, "Abilities:", uiX, uiY - 110);
        game.font.getData().setScale(1f);
        game.font.setColor(Color.LIGHT_GRAY);
        if (playerData.getCharacter().getAbilities() != null) {
            for (int i = 0; i < Math.min(4, playerData.getCharacter().getAbilities().size()); i++) {
                String abilityText = (i + 1) + ". " + playerData.getCharacter().getAbilities().get(i).getName();
                game.font.draw(game.batch, abilityText, uiX, uiY - 135 - (i * 20));
            }
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
        game.font.draw(game.batch, "WASD: Move | 1-4: Abilities | ENTER: Chat | ESC: Exit", 10, 30);
        
        game.batch.end();
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
    public void dispose() {}
}
