package com.mljoke.rajon.inventory

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.mljoke.rajon.inventory.slot.SlotActor
import com.mljoke.rajon.inventory.slot.SlotSource
import com.mljoke.rajon.inventory.slot.SlotTarget
import com.mljoke.rajon.Assets

class InventoryActor(inventory: Inventory, dragAndDrop: DragAndDrop, skin: Skin) : Window("Inventory", skin) {

    init {
        var closeButton = TextButton("X", skin)
        titleTable.add(closeButton).height(padTop)
        closeButton.addListener(HidingClickListener(this))

        //setPosition(40f, Gdx.graphics.height.toFloat())
        defaults().space(10f)
        clear()
        val mytable = Table()
        //setBounds(Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.05f, Gdx.graphics.getWidth() * 0.9f, Gdx.graphics.getHeight() * 0.9f);
        var i = 0
        for (slot in inventory.slots) {
            val slotActor = SlotActor(skin, slot)
            dragAndDrop.addSource(SlotSource(slotActor))
            dragAndDrop.addTarget(SlotTarget(slotActor))
            mytable.add(slotActor)


            i++
            if (i % 6 == 0) {
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
