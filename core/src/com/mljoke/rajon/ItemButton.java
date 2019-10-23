package com.mljoke.rajon;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mljoke.rajon.java.Settings;
import com.mljoke.rajon.managers.EntityFactory;

public class ItemButton extends Actor {
    TextButton btn = new TextButton("Generic", Assets.skin);
    Table tbRes;
    private Image crosshairDot, /*crosshairOuterRing,*/
            crosshairInnerRing;
    //private float outerRotationSpeed, innerRotationSpeed;

    public ItemButton(Stage stage, final Engine engine) {

        tbRes = new Table();
        addTypeButton(100, 20, 10f);
        btn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btn.setText("1232");
                engine.addEntity(EntityFactory.createStaticEntity("deer.g3db","deer", new Vector3()));
            }
        });
        stage.addActor(tbRes);
        crosshairDot = new Image(new Texture(Gdx.files.internal("crosshair/crossHairPoint.png")));
        crosshairInnerRing = new Image(new Texture(Gdx.files.internal("crosshair/crossHairInnerRing.png")));
        //crosshairOuterRing = new Image(new Texture(Gdx.files.internal("crosshair/crossHairOuterRing.png")));
        //outerRotationSpeed = 1F;
        //innerRotationSpeed = -1F;
    }
    private void addTypeButton(float w, float h, float scale) {
        tbRes.add(btn).width(80).height(60);
        tbRes.setPosition(w-3, 60);
    }
    @Override
    public void act(float delta) {
        if (Settings.Paused) return;
        //TODO: Add this in a later chapter
        //crosshairInnerRing.rotateBy(innerRotationSpeed);
        //crosshairOuterRing.rotateBy(outerRotationSpeed);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (Settings.Paused) return;
        tbRes.draw(batch, parentAlpha);
        crosshairDot.draw(batch, parentAlpha);
        crosshairInnerRing.draw(batch, parentAlpha);
        //TODO: Make this smaller
        //crosshairOuterRing.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        crosshairDot.setPosition(x - 16, y - 16);
        crosshairInnerRing.setPosition(x - 16, y - 16);
        //crosshairOuterRing.setPosition(x - 16, y - 16);
        crosshairInnerRing.setOrigin(crosshairInnerRing.getWidth() / 2, crosshairInnerRing.getHeight() / 2);
        //crosshairOuterRing.setOrigin(crosshairOuterRing.getWidth() / 4, crosshairOuterRing.getHeight() / 4);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        crosshairDot.setSize(width * 2, height * 2);
        crosshairInnerRing.setSize(width * 2, height * 2);
        //crosshairOuterRing.setSize(width * 2, height * 2);
    }

}
