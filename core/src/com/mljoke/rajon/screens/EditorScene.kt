package com.mljoke.rajon.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.mljoke.rajon.Core
import com.mljoke.rajon.Editor

class EditorScene(var game: Core) : Screen {

    var editor: Editor = Editor()

    init {
        Gdx.input.isCursorCatched = true
    }

    override fun render(delta: Float) {
        editor.render(delta)
    }

    override fun hide() {}

    override fun show() {}

    override fun pause() {}

    override fun resume() {}

    override fun resize(width: Int, height: Int) {
        editor.resize(width, height)
    }

    override fun dispose() {
        editor.dispose()
    }

}