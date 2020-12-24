package com.mljoke.rajon.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btConvexShape
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController

class CharacterComponent : Component {
    var characterController: btKinematicCharacterController? = null
    var ghostObject: btPairCachingGhostObject? = null
    var ghostShape: btConvexShape? = null
    var characterDirection = Vector3()
    var walkDirection = Vector3()
}