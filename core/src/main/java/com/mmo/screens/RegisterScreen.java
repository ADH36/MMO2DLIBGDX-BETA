package com.mmo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mmo.game.MMOGame;
import com.mmo.network.Network;

/**
 * Registration screen for new users
 */
public class RegisterScreen implements Screen {
    private final MMOGame game;
    private String username = "";
    private String password = "";
    private String email = "";
    private int activeField = 0; // 0=username, 1=password, 2=email
    private String message = "";
    private Color messageColor = Color.WHITE;
    private boolean waitingForResponse = false;
    
    public RegisterScreen(MMOGame game) {
        this.game = game;
        setupNetworkListener();
        
        if (!game.isConnected()) {
            game.connectToServer("localhost", Network.TCP_PORT);
        }
    }
    
    private void setupNetworkListener() {
        game.client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.RegisterResponse) {
                    Network.RegisterResponse response = (Network.RegisterResponse) object;
                    handleRegisterResponse(response);
                }
            }
        });
    }
    
    private void handleRegisterResponse(Network.RegisterResponse response) {
        waitingForResponse = false;
        if (response.success) {
            message = "Registration successful! Redirecting to login...";
            messageColor = Color.GREEN;
            Gdx.app.postRunnable(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                game.setScreen(new LoginScreen(game));
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
        game.font.draw(game.batch, "REGISTER", centerX - 80, centerY + 200);
        
        // Username field
        game.font.getData().setScale(1.5f);
        game.font.setColor(activeField == 0 ? Color.YELLOW : Color.WHITE);
        game.font.draw(game.batch, "Username: " + username + (activeField == 0 ? "_" : ""), centerX - 200, centerY + 120);
        
        // Password field
        game.font.setColor(activeField == 1 ? Color.YELLOW : Color.WHITE);
        String passwordDisplay = "";
        for (int i = 0; i < password.length(); i++) passwordDisplay += "*";
        game.font.draw(game.batch, "Password: " + passwordDisplay + (activeField == 1 ? "_" : ""), centerX - 200, centerY + 70);
        
        // Email field
        game.font.setColor(activeField == 2 ? Color.YELLOW : Color.WHITE);
        game.font.draw(game.batch, "Email: " + email + (activeField == 2 ? "_" : ""), centerX - 200, centerY + 20);
        
        // Message
        game.font.setColor(messageColor);
        game.font.draw(game.batch, message, centerX - 200, centerY - 50);
        
        // Instructions
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.getData().setScale(1f);
        game.font.draw(game.batch, "TAB: Switch field | ENTER: Register | ESC: Back", centerX - 250, centerY - 150);
        
        if (waitingForResponse) {
            game.font.setColor(Color.YELLOW);
            game.font.draw(game.batch, "Creating account...", centerX - 150, centerY - 100);
        }
        
        game.batch.end();
        
        handleInput();
    }
    
    private void handleInput() {
        if (waitingForResponse) return;
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            activeField = (activeField + 1) % 3;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new HomeScreen(game));
            dispose();
            return;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                message = "Please fill in all fields";
                messageColor = Color.RED;
            } else {
                attemptRegister();
            }
            return;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (activeField == 0 && username.length() > 0) {
                username = username.substring(0, username.length() - 1);
            } else if (activeField == 1 && password.length() > 0) {
                password = password.substring(0, password.length() - 1);
            } else if (activeField == 2 && email.length() > 0) {
                email = email.substring(0, email.length() - 1);
            }
        }
        
        // Handle text input
        for (int i = 0; i < 256; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) i;
                if (Character.isLetterOrDigit(c) || (activeField == 2 && (c == '@' || c == '.'))) {
                    if (activeField == 0 && username.length() < 20) {
                        username += c;
                    } else if (activeField == 1 && password.length() < 20) {
                        password += c;
                    } else if (activeField == 2 && email.length() < 50) {
                        email += c;
                    }
                }
            }
        }
    }
    
    private void attemptRegister() {
        if (!game.isConnected()) {
            message = "Not connected to server. Connecting...";
            messageColor = Color.YELLOW;
            game.connectToServer("localhost", Network.TCP_PORT);
            return;
        }
        
        Network.RegisterRequest request = new Network.RegisterRequest();
        request.username = username;
        request.password = password;
        request.email = email;
        game.client.sendTCP(request);
        
        waitingForResponse = true;
        message = "Registering...";
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
