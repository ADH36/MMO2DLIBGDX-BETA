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
import com.mmo.network.Network;

/**
 * Login screen for existing users
 */
public class LoginScreen implements Screen {
    private final MMOGame game;
    private String username = "";
    private String password = "";
    private boolean usernameActive = true;
    private String message = "";
    private Color messageColor = Color.WHITE;
    private boolean waitingForResponse = false;
    
    public LoginScreen(MMOGame game) {
        this.game = game;
        setupNetworkListener();
        
        // Auto-connect to localhost for testing
        if (!game.isConnected()) {
            game.connectToServer("localhost", Network.TCP_PORT);
        }
    }
    
    private void setupNetworkListener() {
        game.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.LoginResponse) {
                    Network.LoginResponse response = (Network.LoginResponse) object;
                    handleLoginResponse(response);
                }
            }
        });
    }
    
    private void handleLoginResponse(Network.LoginResponse response) {
        waitingForResponse = false;
        if (response.success) {
            game.setAuthToken(response.token);
            message = "Login successful!";
            messageColor = Color.GREEN;
            Gdx.app.postRunnable(() -> {
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
        game.font.draw(game.batch, "LOGIN", centerX - 50, centerY + 200);
        
        // Username field
        game.font.getData().setScale(1.5f);
        game.font.setColor(usernameActive ? Color.YELLOW : Color.WHITE);
        game.font.draw(game.batch, "Username: " + username + (usernameActive ? "_" : ""), centerX - 200, centerY + 100);
        
        // Password field
        game.font.setColor(!usernameActive ? Color.YELLOW : Color.WHITE);
        String passwordDisplay = "";
        for (int i = 0; i < password.length(); i++) passwordDisplay += "*";
        game.font.draw(game.batch, "Password: " + passwordDisplay + (!usernameActive ? "_" : ""), centerX - 200, centerY + 50);
        
        // Message
        game.font.setColor(messageColor);
        game.font.draw(game.batch, message, centerX - 200, centerY - 50);
        
        // Instructions
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.getData().setScale(1f);
        game.font.draw(game.batch, "TAB: Switch field | ENTER: Login | ESC: Back", centerX - 250, centerY - 150);
        
        if (waitingForResponse) {
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Connecting to server...", centerX - 150, centerY - 100);
        }
        
        game.batch.end();
        
        // Handle input
        handleInput();
    }
    
    private void handleInput() {
        if (waitingForResponse) return;
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            usernameActive = !usernameActive;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new HomeScreen(game));
            dispose();
            return;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (username.isEmpty() || password.isEmpty()) {
                message = "Please fill in all fields";
                messageColor = Color.RED;
            } else {
                attemptLogin();
            }
            return;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (usernameActive && username.length() > 0) {
                username = username.substring(0, username.length() - 1);
            } else if (!usernameActive && password.length() > 0) {
                password = password.substring(0, password.length() - 1);
            }
        }
        
        // Handle text input
        for (int i = 0; i < 256; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) i;
                if (Character.isLetterOrDigit(c)) {
                    if (usernameActive && username.length() < 20) {
                        username += c;
                    } else if (!usernameActive && password.length() < 20) {
                        password += c;
                    }
                }
            }
        }
    }
    
    private void attemptLogin() {
        if (!game.isConnected()) {
            message = "Not connected to server. Connecting...";
            messageColor = Color.YELLOW;
            game.connectToServer("localhost", Network.TCP_PORT);
            return;
        }
        
        Network.LoginRequest request = new Network.LoginRequest();
        request.username = username;
        request.password = password;
        game.client.sendTCP(request);
        
        waitingForResponse = true;
        message = "Logging in...";
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
