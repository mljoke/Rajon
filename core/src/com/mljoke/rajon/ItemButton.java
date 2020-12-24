package com.mljoke.rajon;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mljoke.rajon.java.Settings;

public class ItemButton extends Button {
    TextButton btn = new TextButton(" ", Assets.skin);

    public ItemButton(String text) {
        btn.setText(text);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
            }
        });
        add(btn);
    }

    @Override
    public void act(float delta) {
        if (Settings.Paused) return;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Settings.Paused) return;
        btn.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        btn.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        btn.setSize(width, height);
    }

}
