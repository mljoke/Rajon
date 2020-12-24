package com.mljoke.rajon.inventory.slot

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window

class SlotTooltip(private val slot: Slot, skin: Skin) : Window("Tooltip...", skin), SlotListener {

    init {
        hasChanged(slot)
        slot.addListener(this)
        isVisible = false
    }

    override fun hasChanged(slot: Slot) {
        if (slot.isEmpty) {
            isVisible = false
            return
        }

        // title displays the amount
        titleLabel.setText(slot.amount.toString() + "x " + slot.item)
        clear()
        val label = Label("Super awesome description of " + slot.item, skin)
        add(label)
        pack()
        setSize(label.width + 10, label.height + 20)
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
        // the listener sets this to true in case the slot is hovered
        // however, we don't want that in case the slot is empty
        if (slot.isEmpty) {
            super.setVisible(false)
        }
    }

}
