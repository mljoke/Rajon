package com.mljoke.rajon.inventory.slot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.mljoke.rajon.Core
import com.mljoke.rajon.Resources
import com.mljoke.rajon.inventory.TooltipListener
import com.mljoke.rajon.Assets
import com.mljoke.rajon.screens.GameScene

class SlotActor(private val skins: Skin, val slot: Slot) : Button(createStyle(skins, slot)), SlotListener {

    //val tooltip = SlotTooltip(slot, skins)
    var label = Label(slot.amount.toString(), skins)

    init {
        slot.addListener(this)
        val tooltip = SlotTooltip(slot, skins)
        addActor(tooltip)
        setSize(64f,64f)
        addListener(TooltipListener(tooltip, true))
        if (slot.item != null) addActor(label)
    }

    override fun hasChanged(slot: Slot) {
        if (slot.amount > 0) label.setText(slot.amount) else label.setText(null)
        style = createStyle(skins, slot)
    }
}

private fun createStyle(skin: Skin, slot: Slot): Button.ButtonStyle {
    val icons = Assets.assetManager.get<TextureAtlas>(Resources.icons)
    val image: TextureRegion
    if (slot.item != null) {
        image = icons.findRegion(slot.item!!.textureRegion)
    } else {
        image = icons.findRegion("none")
    }
    val style = Button.ButtonStyle(skin.get(Button.ButtonStyle::class.java))
    style.up = TextureRegionDrawable(image)
    style.up.apply {
        minHeight = Gdx.graphics.width * 0.08f
        minWidth = Gdx.graphics.width * 0.08f
    }

    style.down = TextureRegionDrawable(image)
    return style
}