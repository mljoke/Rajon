package com.mljoke.rajon.inventory.slot

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop

class SlotTarget(actor: SlotActor) : DragAndDrop.Target(actor) {

    private val targetSlot: Slot

    init {
        targetSlot = actor.slot
        actor.color = Color.LIGHT_GRAY
    }

    override fun drag(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int): Boolean {
        val payloadSlot = payload.getObject() as Slot
        if (targetSlot.item == payloadSlot.item ||
                targetSlot.item == null) {
            actor.color = Color.WHITE
            return true
        } else {
            getActor().setColor(Color.DARK_GRAY);
            return false;
        }
    }

    override fun drop(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int) {}

    override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {
        actor.color = Color.LIGHT_GRAY
    }

}