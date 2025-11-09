package com.mmo.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.esotericsoftware.kryonet.Client;
import com.mmo.network.Network;
import com.mmo.screens.HomeScreen;

/**
 * Main game class that manages the game lifecycle
 */
public class MMOGame extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public BitmapFont font;
    public Client client;
    
    private String authToken;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        
        // Initialize network client
        client = new Client(16384, 8192);
        Network.register(client);
        client.start();
        
        // Set the home screen as the initial screen
        setScreen(new HomeScreen(this));
    }
    
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        if (client.isConnected()) {
            client.close();
        }
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public boolean isConnected() {
        return client.isConnected();
    }
    
    public void connectToServer(String host, int port) {
        try {
            client.connect(5000, host, port, Network.UDP_PORT);
            Gdx.app.log("MMOGame", "Connected to server at " + host + ":" + port);
        } catch (Exception e) {
            Gdx.app.error("MMOGame", "Failed to connect to server", e);
        }
    }
}
