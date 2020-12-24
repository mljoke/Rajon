package com.mljoke.rajon.inventory.slot

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source
import com.mljoke.rajon.Assets
import com.mljoke.rajon.Resources

class SlotSource(actor: SlotActor) : Source(actor) {

    private val sourceSlot: Slot = actor.slot

    override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): Payload? {
        if (sourceSlot.amount === 0) return null

        val payload = Payload()
        val payloadSlot = sourceSlot.item?.let { Slot(it, sourceSlot.amount) }
        sourceSlot.take(sourceSlot.amount)
        payload.setObject(payloadSlot)

        val icons = Assets.assetManager.get(Resources.icons, TextureAtlas::class.java)
        val icon = icons.findRegion(payloadSlot!!.item!!.textureRegion)

        val dragActor = Image(icon)
        payload.dragActor = dragActor

        val validDragActor = Image(icon)
        validDragActor.setColor(0f, 1f, 0f, 1f);
        payload.validDragActor = validDragActor

        val invalidDragActor = Image(icon)
        invalidDragActor.setColor(1f, 0f, 0f, 1f);
        payload.invalidDragActor = invalidDragActor

        return payload
    }

    override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: Payload, target: DragAndDrop.Target?) {
        val payloadSlot = payload.getObject() as Slot
        if (target != null) {
            val targetSlot = (target.actor as SlotActor).slot
            if (targetSlot.item === payloadSlot.item || targetSlot.item == null) {
                payloadSlot.item?.let { targetSlot.add(it, payloadSlot.amount) }
            } else {
                val targetType = targetSlot.item
                val targetAmount = targetSlot.amount
                targetSlot.take(targetAmount)
                targetSlot.add(payloadSlot.item!!, payloadSlot.amount)
                targetType?.let { sourceSlot.add(it, targetAmount) }
            }
        } else {
            sourceSlot.add(payloadSlot.item!!, payloadSlot.amount)
        }
    }
}