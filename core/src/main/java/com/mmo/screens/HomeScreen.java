package com.mmo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mmo.game.MMOGame;

/**
 * Home screen with options to login, register, or exit
 */
public class HomeScreen implements Screen {
    private final MMOGame game;
    private float buttonY;
    private float buttonWidth;
    private float buttonHeight;
    private float buttonSpacing;
    
    private Button loginButton;
    private Button registerButton;
    private Button exitButton;
    
    public HomeScreen(MMOGame game) {
        this.game = game;
        
        buttonWidth = 300;
        buttonHeight = 60;
        buttonSpacing = 20;
        buttonY = Gdx.graphics.getHeight() / 2f;
        
        float centerX = Gdx.graphics.getWidth() / 2f - buttonWidth / 2f;
        
        loginButton = new Button(centerX, buttonY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight, "LOGIN");
        registerButton = new Button(centerX, buttonY, buttonWidth, buttonHeight, "REGISTER");
        exitButton = new Button(centerX, buttonY - buttonHeight - buttonSpacing, buttonWidth, buttonHeight, "EXIT");
    }
    
    @Override
    public void show() {
        Gdx.app.log("HomeScreen", "Home screen shown");
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Draw title
        game.batch.begin();
        game.font.getData().setScale(3f);
        game.font.setColor(Color.GOLD);
        String title = "MMO 2D GAME";
        float titleWidth = game.font.getRegion().getRegionWidth();
        game.font.draw(game.batch, title, Gdx.graphics.getWidth() / 2f - 150, Gdx.graphics.getHeight() - 100);
        game.font.getData().setScale(1.5f);
        game.font.setColor(Color.WHITE);
        game.batch.end();
        
        // Draw buttons
        loginButton.render(game);
        registerButton.render(game);
        exitButton.render(game);
        
        // Handle input
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            
            if (loginButton.contains(touchX, touchY)) {
                game.setScreen(new LoginScreen(game));
                dispose();
            } else if (registerButton.contains(touchX, touchY)) {
                game.setScreen(new RegisterScreen(game));
                dispose();
            } else if (exitButton.contains(touchX, touchY)) {
                Gdx.app.exit();
            }
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
    
    // Simple button class
    private static class Button {
        float x, y, width, height;
        String text;
        Color color = new Color(0.2f, 0.3f, 0.5f, 1);
        Color hoverColor = new Color(0.3f, 0.4f, 0.6f, 1);
        
        Button(float x, float y, float width, float height, String text) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.text = text;
        }
        
        boolean contains(float px, float py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
        
        void render(MMOGame game) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            boolean hover = contains(touchX, touchY);
            
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(hover ? hoverColor : color);
            game.shapeRenderer.rect(x, y, width, height);
            game.shapeRenderer.end();
            
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            game.shapeRenderer.setColor(Color.WHITE);
            game.shapeRenderer.rect(x, y, width, height);
            game.shapeRenderer.end();
            
            game.batch.begin();
            float textWidth = game.font.getRegion().getRegionWidth() * 0.3f;
            game.font.draw(game.batch, text, x + width / 2f - textWidth, y + height / 2f + 10);
            game.batch.end();
        }
    }
}
