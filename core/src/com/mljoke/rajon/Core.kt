package com.mljoke.rajon

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx.gl
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.mljoke.rajon.java.Settings
import com.mljoke.rajon.screens.GameScene

class Core : ApplicationAdapter() {
    lateinit var screen: Screen

    override fun create() {
        //Load Resources
        Assets()
        //Load Settings
        Settings()
        //Create Perspective Camera
        setScreen(GameScene())
    }

    override fun resize(width: Int, height: Int) = screen.resize(width, height)

    private fun setScreen(scr: GameScene) = scr.apply { screen = this }

    override fun render() {
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        screen.render(graphics.deltaTime)
    }

    override fun dispose() {
        Settings.save()
        Assets.dispose()
    }

/*    *//**init simple model control*//*
    private fun movement() {
        instance.transform.getTranslation(position)
        with(position) {
            when {
                input.isKeyPressed(ESCAPE) -> app.exit()
                input.isKeyPressed(W) -> x = x.plus(graphics.deltaTime)
                input.isKeyPressed(S) -> x = x.minus(graphics.deltaTime)
                input.isKeyPressed(A) -> z = z.minus(graphics.deltaTime)
                input.isKeyPressed(D) -> z = z.plus(graphics.deltaTime)
            }
        }
    }

    private fun rotate() {
        with(rotation, {
            when {
                input.isKeyPressed(NUM_1) -> x = x.plus(graphics.deltaTime * 100)
                input.isKeyPressed(NUM_2) -> y = y.plus(graphics.deltaTime * 100)
                input.isKeyPressed(NUM_3) -> z = z.plus(graphics.deltaTime * 100)
            }
        })
    }

    private fun updateTransformation() {
        instance.transform.setFromEulerAngles(rotation.z, rotation.y, rotation.x)
            .trn(position.x, position.y, position.z)
            .scale(scale, scale, scale)
    }*/
}