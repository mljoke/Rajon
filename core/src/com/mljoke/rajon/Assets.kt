package com.mljoke.rajon

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class Assets {

    init {
        assetManager = AssetManager()

        /** Load Textures */
        Resources.textures.forEach { s -> loadAsset(s, Texture::class.java) }
        /** Load Textures */
        for ((i,s) in Resources.models.withIndex()) {
            loadAsset(s, Model::class.java)
            assetManager.get<Model>(s).nodes.forEach { node ->
                    node.scale.scl(Resources.scaleF[i]) }
        }
        /** Load GUI Skin */
        val params = SkinParameter(Resources.atlas)
        loadAsset(Resources.icons, TextureAtlas::class.java)
        loadAsset(Resources.skin, Skin::class.java, params)

        skin = Skin().run { assetManager.get(Resources.skin, Skin::class.java) }

    }

    operator fun <T> get(index: String?): T = assetManager[index]

    /**Function loadAsset load Resources from file
     * @param type type of Resource to load, for example Model::class.java*/
    private fun <T> loadAsset(name: String, type: Class<T>, param: AssetLoaderParameters<T>? = null) {
        with(assetManager) {
            when {
                isLoaded(name).not() -> {
                    println("Load $name")
                    load(name, type, param)
                    finishLoading()
                }
            }
        }
    }

    companion object {
        lateinit var assetManager: AssetManager
        lateinit var skin: Skin

        fun dispose() {
            skin.dispose()
            assetManager.dispose()
        }
    }
}