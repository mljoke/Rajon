package com.mljoke.rajon.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.math.Matrix4

class ModelComponent(val model: Model?, x: Float, y: Float, z: Float): Component {
    val instance: ModelInstance = ModelInstance(model, Matrix4().setToTranslation(x, y, z))
    var blendingAttribute: BlendingAttribute? = null

    fun update(delta: Float) {
        if (blendingAttribute != null) blendingAttribute!!.opacity = blendingAttribute!!.opacity - delta / 2
    }
}