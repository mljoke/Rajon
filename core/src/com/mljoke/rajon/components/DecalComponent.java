package com.mljoke.rajon.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DecalComponent implements Component {
   public TextureRegion decal;

    public DecalComponent() {
        decal = new TextureRegion(new Texture(Gdx.files.internal("badlogic.jpg")));
    }
}
