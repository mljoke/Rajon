package com.mljoke.rajon.inventory

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.mljoke.rajon.Assets
import com.mljoke.rajon.inventory.slot.SlotActor
import com.mljoke.rajon.inventory.slot.SlotSource
import com.mljoke.rajon.inventory.slot.SlotTarget

class CraftActor(craft: Craft, dragAndDrop: DragAndDrop, skin: Skin) : Window("Craft", skin) {

    init {
        //defaults().space(0f)
        clear()
        val mytable = Table()
        var i = 0
        for (slot in craft.slots) {
            val slotActor = SlotActor(skin, slot)
            dragAndDrop.addSource(SlotSource(slotActor))
            dragAndDrop.addTarget(SlotTarget(slotActor))
            mytable.add(slotActor)


            i++
            if (i % 12 == 0) {
                mytable.row()
            }
        }
        val pane = ScrollPane(mytable, Assets.skin)
        pane.setScrollingDisabled(true, false)
        add(pane)
        //row().fill().expandX();

        // it is hidden by default
        isVisible = false
    }
}