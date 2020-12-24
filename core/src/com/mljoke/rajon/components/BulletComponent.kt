package com.mljoke.rajon.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState


class BulletComponent : Component {
    var motionState: btMotionState? = null
    var body: btCollisionObject? = null
    var bodyInfo: btRigidBodyConstructionInfo? = null
}