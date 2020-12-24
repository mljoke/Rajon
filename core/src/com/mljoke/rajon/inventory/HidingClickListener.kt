package com.mljoke.rajon.inventory

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class HidingClickListener(private val actor: Actor) : ClickListener() {

    override fun clicked(event: InputEvent?, x: Float, y: Float) {
        actor.isVisible = false
    }

}