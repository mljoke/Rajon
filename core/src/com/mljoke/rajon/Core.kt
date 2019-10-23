package com.mljoke.rajon

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.mljoke.rajon.java.Settings
import com.mljoke.rajon.screens.EditorScreen

class Core : ApplicationAdapter() {
    var screen : Screen? = null

    override fun create() {
        Assets()
        Settings()
       //Gdx.input.isCatchBackKey(true)
        setScreen(EditorScreen(this))
    }

    override fun resize(width: Int, height: Int) { screen?.resize(width, height) }

    fun setScreen(screen: EditorScreen) {
        if (this.screen != null){
            this.screen!!.hide()
            this.screen!!.dispose()
        }
        this.screen = screen
        if (this.screen != null){
            (this.screen as EditorScreen).show()
            (this.screen as EditorScreen).resize(graphics.width, graphics.height)
        }
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        screen?.render(graphics.deltaTime)
    }

    override fun dispose() {
        Settings.save()
        Assets.dispose()
    }

    companion object {
        @kotlin.jvm.JvmField
        var VIRTUAL_HEIGHT = 900
        @kotlin.jvm.JvmField
        var VIRTUAL_WIDTH = 1600
    }


}