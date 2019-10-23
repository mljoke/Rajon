package com.mljoke.rajon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
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
        FileHandle fileHandle = Gdx.files.internal("skin/uiskin.json");
        FileHandle atlasFile = fileHandle.sibling("uiskin.atlas");
        if (atlasFile.exists()) {
            skin.addRegions(new TextureAtlas(atlasFile));
        }
        skin.load(fileHandle);
        int i = 0;
        assetManager = new AssetManager();
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
    }
    public static <T> T get(String index) {
            return assetManager.get(index);
    }
    public static void dispose() {
        skin.dispose();
        assetManager.dispose();
    }
}
