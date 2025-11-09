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
    
    private float animTime = 0f;
    private float particleTime = 0f;
    
    public HomeScreen(MMOGame game) {
        this.game = game;
        
        buttonWidth = 320;
        buttonHeight = 70;
        buttonSpacing = 25;
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
        animTime += delta;
        particleTime += delta;
        
        // Enhanced gradient background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Draw gradient background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int steps = 20;
        float stepHeight = Gdx.graphics.getHeight() / (float)steps;
        for (int i = 0; i < steps; i++) {
            float progress = i / (float)steps;
            // Dark blue to purple gradient
            float r = 0.1f + progress * 0.2f;
            float g = 0.05f + progress * 0.15f;
            float b = 0.3f + progress * 0.1f;
            game.shapeRenderer.setColor(r, g, b, 1);
            game.shapeRenderer.rect(0, i * stepHeight, Gdx.graphics.getWidth(), stepHeight);
        }
        game.shapeRenderer.end();
        
        // Draw animated particles in background
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 30; i++) {
            float x = ((i * 73f + particleTime * 10) % Gdx.graphics.getWidth());
            float y = ((i * 127f + particleTime * 15) % Gdx.graphics.getHeight());
            float alpha = (float)(Math.sin(particleTime + i) * 0.3 + 0.3);
            game.shapeRenderer.setColor(1, 1, 1, alpha);
            game.shapeRenderer.circle(x, y, 2);
        }
        game.shapeRenderer.end();
        
        // Draw enhanced title with shadow and glow
        game.batch.begin();
        String title = "LEGENDS OF THE REALM";
        String subtitle = "A 2D MMO Adventure";
        
        // Title shadow
        game.font.getData().setScale(3.5f);
        game.font.setColor(0, 0, 0, 0.7f);
        game.font.draw(game.batch, title, Gdx.graphics.getWidth() / 2f - 320, Gdx.graphics.getHeight() - 85);
        
        // Title with animated glow
        float glowIntensity = (float)(Math.sin(animTime * 2) * 0.2 + 0.8);
        game.font.setColor(Color.GOLD.r * glowIntensity, Color.GOLD.g * glowIntensity, Color.GOLD.b * glowIntensity, 1);
        game.font.draw(game.batch, title, Gdx.graphics.getWidth() / 2f - 325, Gdx.graphics.getHeight() - 90);
        
        // Subtitle
        game.font.getData().setScale(1.3f);
        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, subtitle, Gdx.graphics.getWidth() / 2f - 150, Gdx.graphics.getHeight() - 140);
        
        game.font.getData().setScale(1.5f);
        game.font.setColor(Color.WHITE);
        game.batch.end();
        
        // Draw buttons with animation
        loginButton.render(game, animTime);
        registerButton.render(game, animTime);
        exitButton.render(game, animTime);
        
        // Draw version info
        game.batch.begin();
        game.font.getData().setScale(0.8f);
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.draw(game.batch, "Version 1.0 Beta", 10, 30);
        game.font.getData().setScale(1.5f);
        game.batch.end();
        
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
    
    // Enhanced button class with gradient and shadow effects
    private static class Button {
        float x, y, width, height;
        String text;
        Color baseColor = new Color(0.15f, 0.25f, 0.45f, 0.9f);
        Color hoverColor = new Color(0.25f, 0.4f, 0.65f, 1f);
        Color borderColor = new Color(0.6f, 0.7f, 0.9f, 1);
        Color glowColor = new Color(0.3f, 0.5f, 0.8f, 0.5f);
        
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
        
        void render(MMOGame game, float animTime) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            boolean hover = contains(touchX, touchY);
            
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            
            // Shadow
            game.shapeRenderer.setColor(0, 0, 0, 0.5f);
            game.shapeRenderer.rect(x + 5, y - 5, width, height);
            
            // Button gradient background
            int gradientSteps = 10;
            float stepHeight = height / gradientSteps;
            for (int i = 0; i < gradientSteps; i++) {
                float progress = i / (float)gradientSteps;
                Color currentColor = hover ? hoverColor : baseColor;
                float factor = hover ? 1.0f - progress * 0.3f : 1.0f - progress * 0.2f;
                game.shapeRenderer.setColor(
                    currentColor.r * factor,
                    currentColor.g * factor,
                    currentColor.b * factor,
                    currentColor.a
                );
                game.shapeRenderer.rect(x, y + i * stepHeight, width, stepHeight);
            }
            
            // Glow effect on hover
            if (hover) {
                float glowSize = 8;
                game.shapeRenderer.setColor(glowColor);
                game.shapeRenderer.rect(x - glowSize, y - glowSize, width + glowSize * 2, height + glowSize * 2);
            }
            
            game.shapeRenderer.end();
            
            // Border with double outline for depth
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            Gdx.gl.glLineWidth(3);
            game.shapeRenderer.setColor(hover ? Color.WHITE : borderColor);
            game.shapeRenderer.rect(x, y, width, height);
            
            // Inner border for depth
            Gdx.gl.glLineWidth(1);
            game.shapeRenderer.setColor(hover ? Color.GOLD : new Color(0.4f, 0.5f, 0.7f, 0.5f));
            game.shapeRenderer.rect(x + 3, y + 3, width - 6, height - 6);
            Gdx.gl.glLineWidth(1);
            game.shapeRenderer.end();
            
            // Text with shadow
            game.batch.begin();
            game.font.getData().setScale(1.8f);
            
            // Calculate text position for centering
            float textWidth = text.length() * 20; // Approximate
            float textX = x + width / 2f - textWidth / 2f;
            float textY = y + height / 2f + 12;
            
            // Text shadow
            game.font.setColor(0, 0, 0, 0.8f);
            game.font.draw(game.batch, text, textX + 2, textY - 2);
            
            // Text with subtle animation on hover
            if (hover) {
                float scale = 1.0f + (float)(Math.sin(animTime * 5) * 0.05);
                game.font.getData().setScale(1.8f * scale);
                game.font.setColor(Color.GOLD);
            } else {
                game.font.setColor(Color.WHITE);
            }
            game.font.draw(game.batch, text, textX, textY);
            
            game.font.getData().setScale(1.5f);
            game.batch.end();
        }
    }
}
