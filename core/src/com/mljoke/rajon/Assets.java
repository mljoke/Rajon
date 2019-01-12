package com.mljoke.rajon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
    public static Skin skin;
    public static AssetManager assetManager;

    public Assets() {
        skin = new Skin();
        FileHandle fileHandle = Gdx.files.internal("uiskin.json");
        FileHandle atlasFile = fileHandle.sibling("uiskin.atlas");
        if (atlasFile.exists()) {
            skin.addRegions(new TextureAtlas(atlasFile));
        }
        skin.load(fileHandle);

        assetManager = new AssetManager();
        for (String s : Resources.models) {
            assetManager.load(s, Model.class);
        }
        for (String s : Resources.textures) {
            assetManager.load(s, Texture.class);
        }
    }

    public static void dispose() {
        skin.dispose();
        assetManager.dispose();
    }
}
