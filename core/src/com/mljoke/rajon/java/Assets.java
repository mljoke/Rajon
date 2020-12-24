package com.mljoke.rajon.java;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
    public static Skin skin;
    public static AssetManager assetManager;

    public Assets() {
        skin = new Skin();
        assetManager = new AssetManager();
//        FileHandle fileHandle = Gdx.files.internal("skin/uiskin.json");
//        assetManager.load("skin/uiskin.json", Skin.class);
        int i = 0;

        for (String s : Resources.models) {

            if (!assetManager.isLoaded(s)) {
                assetManager.load(s, Model.class);
                assetManager.finishLoading();
                for (Node node : assetManager.<Model>get(s).nodes) {
                    node.scale.scl(Resources.scaleF[i]);
                }
            }
            i++;
        }
        for (String s : Resources.textures) {
            if (!assetManager.isLoaded(s)) {
                assetManager.load(s, Texture.class);
                assetManager.finishLoading();
            }
        }

            if (!assetManager.isLoaded(Resources.skin)) {
                SkinLoader.SkinParameter params = new SkinLoader.SkinParameter(Resources.atlas);
                assetManager.load(Resources.icons, TextureAtlas.class);
                assetManager.load(Resources.skin, Skin.class, params);
                assetManager.finishLoading();
            }

        skin = assetManager.get(Resources.skin);
    }

    public static <T> T get(String index) {
        return assetManager.get(index);
    }

    public static void dispose() {
        skin.dispose();
        assetManager.dispose();
    }
}
