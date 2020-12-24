package com.mljoke.rajon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mljoke.rajon.java.Core;
import com.mljoke.rajon.GameUI;
import com.mljoke.rajon.GameWorld;
import com.mljoke.rajon.java.Settings;

public class GameScreen implements Screen {
    Core game;
    GameUI gameUI;
    GameWorld gameWorld;

    public GameScreen(Core game) {
        this.game = game;
        gameUI = new GameUI(game);
        gameWorld = new GameWorld(gameUI);
        Settings.Paused = false;
        Gdx.input.setInputProcessor(gameUI.stage);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        gameUI.update(delta);
        gameWorld.render(delta);
        gameUI.render();
    }

    @Override
    public void resize(int width, int height) {
        gameUI.resize(width, height);
        gameWorld.resize(width, height);
    }

    @Override
    public void dispose() {
        gameWorld.dispose();
        gameUI.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}