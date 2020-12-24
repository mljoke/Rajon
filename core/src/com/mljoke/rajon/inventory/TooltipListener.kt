package com.mljoke.rajon.inventory

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

class TooltipListener(private val tooltip: Actor, private val followCursor: Boolean) : InputListener() {

    private var inside: Boolean = false

    private val position = Vector2()
    private val tmp = Vector2()
    private val offset = Vector2(10f, 10f)

    override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
        if (inside && followCursor) {

            //event!!.listenerActor.localToStageCoordinates(tmp.set(x,y))

           // tooltip.setPosition(tmp.x+ position.x + offset.x,  tmp.y+ position.y + offset.y)
        }
        return false
    }

    override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        inside = true
        tooltip.isVisible = true

        tmp.set(x, y)
        println(tmp)
        event.listenerActor.localToStageCoordinates(tmp)

        println("after $tmp")
        tooltip.setPosition(tmp.x + position.x + offset.x, tmp.y + position.y + offset.y)
        tooltip.toBack()
    }

    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
        inside = false
        tooltip.isVisible = false
    }

    /**
     * The offset of the tooltip from the touch position. It should not be
     * positive as the tooltip will flicker otherwise.
     */
    fun setOffset(offsetX: Float, offsetY: Float) {
        offset.set(offsetX, offsetY)
    }

}
