package com.mljoke.rajon.inventory

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.mljoke.rajon.inventory.slot.Slot

class Inventory {

    val slots: Array<Slot>

    init {
        slots = Array(64)
        for (i in 0..63) {
            slots.add(Slot(null, 0))
        }

        // create some random items
        for (slot in slots) {
            slot.add(Item.values()[MathUtils.random(0, Item.values().size - 1)], MathUtils.random(0, Item.values().size - 1));
        }

        // create a few random empty slots
        for (i in 1..3) {
            var randomSlot:Slot = slots.get(MathUtils.random(0, slots.size - 1));
            randomSlot.take(randomSlot.amount);
        }
    }

    fun checkInventory(item: Item): Int {
        var amount = 0

        for (slot in slots) {
            if (slot.item === item) {
                amount += slot.amount
            }
        }

        return amount
    }

    fun store(item: Item, amount: Int): Boolean {
        // first check for a slot with the same item type
        val itemSlot = firstSlotWithItem(item)
        if (itemSlot != null) {
            itemSlot.add(item, amount)
            return true
        } else {
            // now check for an available empty slot
            val emptySlot = firstSlotWithItem(null)
            if (emptySlot != null) {
                emptySlot.add(item, amount)
                return true
            }
        }

        // no slot to add
        return false
    }

    private fun firstSlotWithItem(item: Item?): Slot? {
        for (slot in slots) {
            if (slot.item === item) {
                return slot
            }
        }

        return null
    }

}