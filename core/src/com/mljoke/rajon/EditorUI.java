package com.mljoke.rajon;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.mljoke.rajon.java.Core.*;

public class EditorUI {

    public Stage stage;
    public ItemButton new_item_button;
    private Engine engine;

    public EditorUI(Engine engine) {
        this.engine = engine;
        stage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        set_widgets();
        configure_widgets();
    }

    private void set_widgets() {
//        new_item_button =  new ItemButton(stage, engine);
    }

    private void configure_widgets() {
        new_item_button.setPosition(VIRTUAL_WIDTH / 2 - 16, VIRTUAL_HEIGHT / 2 - 16);
        new_item_button.setSize(32, 32);
        stage.addActor(new_item_button);
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void dispose() {
        stage.dispose();
    }

}
