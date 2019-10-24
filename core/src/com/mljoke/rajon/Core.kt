package com.mljoke.rajon

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.mljoke.rajon.java.Assets
import com.mljoke.rajon.java.Settings
import com.mljoke.rajon.screens.EditorScene

class Core : ApplicationAdapter() {
    var screen : Screen? = null

    override fun create() {
        Assets()
        Settings()
        setScreen(EditorScene(this))
    }

    override fun resize(width: Int, height: Int) { screen?.resize(width, height) }

    fun setScreen(screen: EditorScene) {
        if (this.screen != null){
            this.screen!!.hide()
            this.screen!!.dispose()
        }
        this.screen = screen
        if (this.screen != null){
            (this.screen as EditorScene).show()
            (this.screen as EditorScene).resize(graphics.width, graphics.height)
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

object Test {
    var HP = 100
    var EXP = 0
    var readline = readLine()
    @JvmStatic
    fun main(arg: Array<String>) {
        while(readline.equals("q")) {
            when(readline) {
                "1" -> HP = HP.minus(1)
                "2" ->  EXP += 1
                else -> " "
            }
            println("HP: $HP")
            println("EXP: $EXP")
            readline = readLine()
        }

    }
}