package com.mmo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mmo.game.MMOGame;
import com.mmo.models.CharacterClass;
import com.mmo.network.Network;

/**
 * Character creation screen - allows player to create a new character
 */
public class CharacterCreationScreen implements Screen {
    private final MMOGame game;
    private String characterName = "";
    private CharacterClass[] classes = CharacterClass.values();
    private int selectedClassIndex = 0;
    private String message = "";
    private Color messageColor = Color.WHITE;
    private boolean waitingForResponse = false;
    
    public CharacterCreationScreen(MMOGame game) {
        this.game = game;
        setupNetworkListener();
    }
    
    private void setupNetworkListener() {
        game.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.CreateCharacterResponse) {
                    Network.CreateCharacterResponse response = (Network.CreateCharacterResponse) object;
                    handleCreateCharacterResponse(response);
                }
            }
        });
    }
    
    private void handleCreateCharacterResponse(Network.CreateCharacterResponse response) {
        waitingForResponse = false;
        if (response.success) {
            message = "Character created! Returning to character selection...";
            messageColor = Color.GREEN;
            Gdx.app.postRunnable(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                game.setScreen(new CharacterSelectionScreen(game));
                dispose();
            });
        } else {
            message = response.message;
            messageColor = Color.RED;
        }
    }
    
    @Override
    public void show() {}
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        
        game.batch.begin();
        
        // Title
        game.font.getData().setScale(2f);
        game.font.setColor(Color.GOLD);
        game.font.draw(game.batch, "CREATE CHARACTER", centerX - 180, centerY + 280);
        
        // Character name input
        game.font.getData().setScale(1.5f);
        game.font.setColor(Color.YELLOW);
        game.font.draw(game.batch, "Name: " + characterName + "_", centerX - 200, centerY + 200);
        
        // Class selection
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "Select Class:", centerX - 200, centerY + 140);
        
        // Display classes
        float classY = centerY + 100;
        for (int i = 0; i < classes.length; i++) {
            CharacterClass cls = classes[i];
            Color color = (i == selectedClassIndex) ? Color.YELLOW : Color.LIGHT_GRAY;
            game.font.setColor(color);
            game.font.draw(game.batch, cls.getName(), centerX - 180, classY - (i * 30));
        }
        
        // Display selected class details
        CharacterClass selectedClass = classes[selectedClassIndex];
        game.font.getData().setScale(1.2f);
        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, selectedClass.getDescription(), centerX - 280, classY - (classes.length * 30) - 20);
        
        // Display class stats
        game.font.setColor(Color.WHITE);
        float statsY = classY - (classes.length * 30) - 60;
        game.font.draw(game.batch, "Health: " + selectedClass.getBaseHealth(), centerX - 280, statsY);
        game.font.draw(game.batch, "Mana: " + selectedClass.getBaseMana(), centerX - 280, statsY - 25);
        game.font.draw(game.batch, "Attack: " + selectedClass.getBaseAttack(), centerX - 280, statsY - 50);
        game.font.draw(game.batch, "Defense: " + selectedClass.getBaseDefense(), centerX - 280, statsY - 75);
        
        // Display abilities
        game.font.setColor(Color.GOLD);
        game.font.draw(game.batch, "Abilities:", centerX - 280, statsY - 110);
        game.font.getData().setScale(1f);
        game.font.setColor(Color.LIGHT_GRAY);
        String[] abilities = selectedClass.getAbilityNames();
        String[] abilityDescs = selectedClass.getAbilityDescriptions();
        for (int i = 0; i < abilities.length; i++) {
            game.font.draw(game.batch, (i+1) + ". " + abilities[i] + " - " + abilityDescs[i], 
                          centerX - 280, statsY - 135 - (i * 20));
        }
        
        // Message
        game.font.getData().setScale(1.5f);
        game.font.setColor(messageColor);
        game.font.draw(game.batch, message, centerX - 280, 150);
        
        // Instructions
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.getData().setScale(1f);
        game.font.draw(game.batch, "Type name | LEFT/RIGHT: Change class | ENTER: Create | ESC: Back", 
                      centerX - 350, 50);
        
        if (waitingForResponse) {
            game.font.getData().setScale(1.5f);
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Creating character...", centerX - 150, 100);
        }
        
        game.batch.end();
        
        handleInput();
    }
    
    private void handleInput() {
        if (waitingForResponse) return;
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selectedClassIndex = (selectedClassIndex - 1 + classes.length) % classes.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selectedClassIndex = (selectedClassIndex + 1) % classes.length;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new CharacterSelectionScreen(game));
            dispose();
            return;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (characterName.isEmpty()) {
                message = "Please enter a character name";
                messageColor = Color.RED;
            } else if (characterName.length() < 3) {
                message = "Name must be at least 3 characters";
                messageColor = Color.RED;
            } else {
                createCharacter();
            }
            return;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (characterName.length() > 0) {
                characterName = characterName.substring(0, characterName.length() - 1);
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
                if (characterName.length() < 20) {
                    characterName += c;
                }
            }
        }
        
        // Handle numbers (0-9)
        for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) ('0' + (i - Input.Keys.NUM_0));
                if (characterName.length() < 20) {
                    characterName += c;
                }
            }
        }
        
        // Handle numpad numbers
        for (int i = Input.Keys.NUMPAD_0; i <= Input.Keys.NUMPAD_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) ('0' + (i - Input.Keys.NUMPAD_0));
                if (characterName.length() < 20) {
                    characterName += c;
                }
            }
        }
        
        // Handle space for character names
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (characterName.length() < 20) {
                characterName += " ";
            }
        }
    }
    
    private void createCharacter() {
        Network.CreateCharacterRequest request = new Network.CreateCharacterRequest();
        request.token = game.getAuthToken();
        request.characterName = characterName;
        request.characterClass = classes[selectedClassIndex];
        game.client.sendTCP(request);
        
        waitingForResponse = true;
        message = "Creating character...";
        messageColor = Color.YELLOW;
    }
    
    @Override
    public void resize(int width, int height) {}
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {}
}
