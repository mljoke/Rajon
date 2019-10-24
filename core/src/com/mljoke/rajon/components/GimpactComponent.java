package com.mljoke.rajon.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.bullet.collision.btGImpactMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;

public class GimpactComponent implements Component {
    public btTriangleIndexVertexArray chassisVertexArray;
    public btGImpactMeshShape chassisShape;
}
