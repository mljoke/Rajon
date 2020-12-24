package com.mljoke.rajon.inventory.slot

import com.badlogic.gdx.utils.Array
import com.mljoke.rajon.inventory.Item

class Slot(item: Item?, amount: Int) {

    var item: Item?
        private set

    var amount: Int = 0
        private set

    private val slotListeners = Array<SlotListener>()

    val isEmpty: Boolean
        get() = item == null || amount <= 0

    init {
        this.item = item
        this.amount = amount
    }

    fun addListener(slotListener: SlotListener) {
        slotListeners.add(slotListener)
    }

    fun removeListener(slotListener: SlotListener) {
        slotListeners.removeValue(slotListener, true)
    }

    /**
     * Returns `true` in case this slot has the same item type and at
     * least the same amount of items as the given other slot.
     *
     * @param other
     * The other slot to be checked.
     * @return `True` in case this slot has the same item type and at
     * least the same amount of items as the given other slot.
     * `False` otherwise.
     */
    fun matches(other: Slot): Boolean {
        return this.item === other.item && this.amount >= other.amount
    }

    fun add(item: Item, amount: Int): Boolean {
        if (this.item === item || this.item == null) {
            this.item = item
            this.amount += amount
            notifyListeners()
            return true
        }

        return false
    }

    fun take(amount: Int): Boolean {
        if (this.amount >= amount) {
            this.amount -= amount
            if (this.amount == 0) {
                item = null
            }
            notifyListeners()
            return true
        }

        return false
    }

    fun clear() {
        this.item = null
        this.amount = 0
    }
    private fun notifyListeners() {
        for (slotListener in slotListeners) {
            slotListener.hasChanged(this)
        }
    }

    override fun toString(): String {
        return "Slot[$item:$amount]"
    }
}
