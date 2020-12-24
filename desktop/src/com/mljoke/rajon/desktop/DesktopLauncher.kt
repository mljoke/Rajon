package com.mljoke.rajon.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.mljoke.rajon.Core

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration().apply {
            title = "Rajon"
            fullscreen = false
            width = 1600
            height = 900
            //useHDPI = true
            addIcon("icon.png", Files.FileType.Internal)
        }
        LwjglApplication(Core(), config)
    }
}