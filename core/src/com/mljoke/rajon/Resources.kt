package com.mljoke.rajon

class Resources {
    companion object {
        var vshader = "shaders/VS_ShaderPlain.vsh"
        var fshader = "shaders/ShaderPlain.fsh"
        var skin = "uiskin.json"
        var atlas = "uiskin.atlas"
        var icons: String = "skin/icons.atlas"
        var scaleF = floatArrayOf(
            0.5f,
            0.05f,
            1f,
            1f,
            0.5f,
            1f,
            1f,
            0.05f
        )
        var models = arrayOf<String>("deer.g3db", "cube.g3dj")
        var textures = arrayOf(
            "badlogic.jpg",
            "player.png"
        )
    }
}