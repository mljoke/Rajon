package com.mljoke.rajon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mljoke.rajon.*;
import com.mljoke.rajon.EditorUI;
import com.mljoke.rajon.java.Settings;

public class EditorScreen implements Screen {

    Core game;
    EditorUI editorUI;
    Editor editor;

    public EditorScreen(Core game) {
        this.game = game;
        editor = new Editor();
        editorUI = new EditorUI(editor.engine);
        Settings.Paused = false;
        Gdx.input.setInputProcessor(editorUI.stage);
        //Gdx.input.setCursorCatched(true);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        editorUI.update(delta);
        editor.render(delta);
        editorUI.render();
    }

    @Override
    public void resize(int width, int height) {
        editorUI.resize(width, height);
        editor.resize(width, height);
    }

    @Override
    public void dispose() {
        editorUI.dispose();
        editor.dispose();
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
