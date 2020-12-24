package com.mljoke.rajon.inventory

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop

class InventoryScreen : Screen {

    private var inventoryActor: InventoryActor? = null

    override fun show() {
        stage = Stage()
        Gdx.input.inputProcessor = stage
        val skin = Skin(Gdx.files.internal("uiskin.json"))
        //Skin skin = LibgdxUtils.assets.get("skins/uiskin.json", Skin.class);
        val dragAndDrop = DragAndDrop()
        inventoryActor = InventoryActor(Inventory(), dragAndDrop, skin)
        stage.addActor(inventoryActor)
    }

    override fun resume() {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            inventoryActor!!.isVisible = true
        }

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {
        // NOOP
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
        dispose()
    }

    override fun dispose() {
        stage.dispose()
    }

    companion object {

        var stage: Stage = Stage()
    }

}
