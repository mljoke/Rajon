package com.mljoke.rajon.inventory

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.mljoke.rajon.inventory.slot.Slot

class Craft {
    val slots: Array<Slot>

    init {
        slots = Array(64)
        for (i in 0..63) {
            slots.add(Slot(null, 0))
        }

        // create some random items
       // slots[0].add(Item.values()[MathUtils.random(0, Item.values().size - 1)], 1)
        for ((i, slot) in slots.withIndex()) {
            slots[i].add(Item.values()[nextRandom()], nextRandom())
            if (slots[i].isEmpty)
                slots[i].clear()
        }

        // create a few random empty slots
        /*for (int i = 0; i < 3; i++) {
         Slot randomSlot = slots.get(MathUtils.random(0, slots.size - 1));
         randomSlot.take(randomSlot.getAmount());
         }*/
    }

    private fun nextRandom() = MathUtils.random(0, Item.values().size - 1)

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