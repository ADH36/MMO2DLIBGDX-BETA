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
import com.mmo.models.CharacterData;
import com.mmo.network.Network;

/**
 * Character selection screen - displays existing characters or prompts to create new one
 */
public class CharacterSelectionScreen implements Screen {
    private final MMOGame game;
    private CharacterData[] characters;
    private int selectedIndex = 0;
    private String message = "";
    private Color messageColor = Color.WHITE;
    private boolean loading = true;
    
    public CharacterSelectionScreen(MMOGame game) {
        this.game = game;
        setupNetworkListener();
        requestCharacterList();
    }
    
    private void setupNetworkListener() {
        game.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.CharacterListResponse) {
                    Network.CharacterListResponse response = (Network.CharacterListResponse) object;
                    handleCharacterListResponse(response);
                } else if (object instanceof Network.SelectCharacterResponse) {
                    Network.SelectCharacterResponse response = (Network.SelectCharacterResponse) object;
                    handleSelectCharacterResponse(response);
                }
            }
        });
    }
    
    private void requestCharacterList() {
        Network.CharacterListRequest request = new Network.CharacterListRequest();
        request.token = game.getAuthToken();
        game.client.sendTCP(request);
    }
    
    private void handleCharacterListResponse(Network.CharacterListResponse response) {
        loading = false;
        if (response.success) {
            characters = response.characters;
            if (characters == null || characters.length == 0) {
                message = "No characters found. Press C to create one.";
                messageColor = Color.YELLOW;
            }
        } else {
            message = "Failed to load characters";
            messageColor = Color.RED;
        }
    }
    
    private void handleSelectCharacterResponse(Network.SelectCharacterResponse response) {
        if (response.success) {
            message = "Character selected!";
            messageColor = Color.GREEN;
            Gdx.app.postRunnable(() -> {
                game.setScreen(new GameScreen(game, response.playerData));
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
        game.font.draw(game.batch, "SELECT CHARACTER", centerX - 180, centerY + 250);
        
        game.font.getData().setScale(1.5f);
        
        if (loading) {
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Loading characters...", centerX - 150, centerY);
        } else if (characters != null && characters.length > 0) {
            // Display characters
            float startY = centerY + 150;
            for (int i = 0; i < characters.length; i++) {
                CharacterData character = characters[i];
                Color color = (i == selectedIndex) ? Color.YELLOW : Color.WHITE;
                game.font.setColor(color);
                
                String charInfo = String.format("%s - Level %d %s",
                    character.getName(),
                    character.getLevel(),
                    character.getCharacterClass().getName());
                
                game.font.draw(game.batch, charInfo, centerX - 200, startY - (i * 50));
            }
            
            // Display selected character details
            if (selectedIndex < characters.length) {
                CharacterData selected = characters[selectedIndex];
                game.font.getData().setScale(1.2f);
                game.font.setColor(Color.LIGHT_GRAY);
                
                float detailY = centerY - 50;
                game.font.draw(game.batch, "HP: " + selected.getHealth() + "/" + selected.getMaxHealth(), centerX - 200, detailY);
                game.font.draw(game.batch, "MP: " + selected.getMana() + "/" + selected.getMaxMana(), centerX - 200, detailY - 30);
                game.font.draw(game.batch, "Attack: " + selected.getAttack(), centerX - 200, detailY - 60);
                game.font.draw(game.batch, "Defense: " + selected.getDefense(), centerX - 200, detailY - 90);
                
                game.font.getData().setScale(1.5f);
            }
        }
        
        // Message
        game.font.setColor(messageColor);
        game.font.draw(game.batch, message, centerX - 250, centerY - 150);
        
        // Instructions
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.getData().setScale(1f);
        game.font.draw(game.batch, "UP/DOWN: Select | ENTER: Choose | C: Create New | ESC: Logout", 
                      centerX - 300, 50);
        
        game.batch.end();
        
        handleInput();
    }
    
    private void handleInput() {
        if (loading) return;
        
        if (characters != null && characters.length > 0) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedIndex = Math.max(0, selectedIndex - 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                selectedIndex = Math.min(characters.length - 1, selectedIndex + 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                selectCharacter();
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            game.setScreen(new CharacterCreationScreen(game));
            dispose();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setAuthToken(null);
            game.setScreen(new HomeScreen(game));
            dispose();
        }
    }
    
    private void selectCharacter() {
        if (characters != null && selectedIndex < characters.length) {
            Network.SelectCharacterRequest request = new Network.SelectCharacterRequest();
            request.token = game.getAuthToken();
            request.characterId = characters[selectedIndex].getId();
            game.client.sendTCP(request);
            
            message = "Loading character...";
            messageColor = Color.YELLOW;
        }
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
