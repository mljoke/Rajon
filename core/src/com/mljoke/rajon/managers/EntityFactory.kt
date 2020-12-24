package com.mljoke.rajon.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo
import com.mljoke.rajon.Assets
import com.mljoke.rajon.Bullet.MotionState
import com.mljoke.rajon.Logger
import com.mljoke.rajon.components.*
import com.mljoke.rajon.java.ModelComponent
import com.mljoke.rajon.systems.BulletSystem
import com.mljoke.rajon.systems.RenderSystem
import com.procedural.world.PBRTextureAttribute
import java.lang.Exception

object EntityFactory {
    fun createStaticEntity(str: String?, material: String?, vec: Vector3): Entity {
        return createStaticEntity(Model(), vec.x, vec.y, vec.z)
    }

    fun createStaticEntity(model: Model, x: Float, y: Float, z: Float): Entity {
        val boundingBox = BoundingBox().also { model.calculateBoundingBox(it) }
       //model.calculateBoundingBox(boundingBox)
        val tmpV = Vector3()
        val col: btCollisionShape =
            btBoxShape(tmpV.set(boundingBox.width * 0.5f, boundingBox.height * 0.5f, boundingBox.depth * 0.5f))
        val entity = Entity()
        val modelComponent = com.mljoke.rajon.components.ModelComponent(model, x, y, z)
        entity.add(modelComponent)
        val bulletComponent = BulletComponent()
        bulletComponent.bodyInfo = btRigidBodyConstructionInfo(0f, null, col, Vector3.Zero)
        bulletComponent.body = btRigidBody(bulletComponent.bodyInfo)
        (bulletComponent.body as btRigidBody).userData = entity
        bulletComponent.motionState = MotionState(modelComponent.instance.transform)
        (bulletComponent.body as btRigidBody?)!!.motionState = bulletComponent.motionState
        entity.add(bulletComponent)
        return entity
    }

    private fun createCharacter(bulletSystem: BulletSystem, x: Float, y: Float, z: Float): Entity {
        val entity = Entity()
        val modelComponent = com.mljoke.rajon.components.ModelComponent(playerModel, x, y, z)
        modelComponent.instance.transform.rotate(Vector3.X, 90f)
        entity.add(modelComponent)
        val characterComponent = CharacterComponent()
        characterComponent.ghostObject = btPairCachingGhostObject()
        characterComponent.ghostObject!!.worldTransform = modelComponent.instance.transform
        characterComponent.ghostShape = btCapsuleShape(2f, 2f)
        characterComponent.ghostObject!!.collisionShape = characterComponent.ghostShape
        characterComponent.ghostObject!!.collisionFlags = btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT
        characterComponent.characterController = btKinematicCharacterController(characterComponent.ghostObject,
            characterComponent.ghostShape,
            .35f,
            Vector3.Y)
        characterComponent.ghostObject!!.userData = entity
        entity.add(characterComponent)
        bulletSystem.collisionWorld.addCollisionObject(
            entity.getComponent(CharacterComponent::class.java).ghostObject,
            btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
            btBroadphaseProxy.CollisionFilterGroups.AllFilter)
        bulletSystem.collisionWorld.addAction(entity.getComponent(CharacterComponent::class.java).characterController)
        return entity
    }

    fun createPlayer(bulletSystem: BulletSystem, x: Float, y: Float, z: Float): Entity =
        createCharacter(bulletSystem, x, y, z).add(PlayerComponent())

    var renderSystem: RenderSystem? = null
    private var modelBuilder: ModelBuilder? = null
    private var playerTexture: Texture? = null
    private var playerModel: Model? = null
    private var enemyModel: Model? = null
    private var enemyModelComponent: ModelComponent? = null
    fun createMaterial(materialName: String): Material {
        val material = Material()
        try {
            material.set(PBRTextureAttribute.createAlbedo(Texture("materials/$materialName/Diffuse.png")))
            material.set(PBRTextureAttribute.createMetallic(Texture("materials/$materialName/Specular.png")))
            material.set(PBRTextureAttribute.createRoughness(Texture("materials/$materialName/Glossiness.png")))
            material.set(PBRTextureAttribute.createAmbientOcclusion(Texture(
                "materials/$materialName/AO.png")))
            material.set(PBRTextureAttribute.createHeight(Texture("materials/$materialName/Height.png")))
            material.set(PBRTextureAttribute.createNormal(Texture("materials/$materialName/Normal.png")))
        } catch (e: Exception) {
            Logger.log(Logger.ANDREAS, Logger.INFO, e.message)
        }
        return material
    }

    fun createDecal(renderSystem: RenderSystem?, x: Float, y: Float, z: Float): Entity {
        val entity = Entity()
        val decalComponent = DecalComponent()
        val decal = Decal.newDecal(10f, 10f, decalComponent.decal)
        decal.setPosition(x, y, z)
        //RenderSystem.decalBatch.add(decal);
        entity.add(decalComponent)
        return entity
    }

    fun createGimpact(bulletSystem: BulletSystem, x: Float, y: Float, z: Float): Entity {
        val entity = Entity()
        val chassisModel = playerModel
        val modelComponent = ModelComponent(chassisModel, x, y, z)
        entity.add(modelComponent)
        val gimpactComponent = GimpactComponent()
        gimpactComponent.chassisVertexArray = btTriangleIndexVertexArray(chassisModel!!.meshParts)
        gimpactComponent.chassisShape = btGImpactMeshShape(gimpactComponent.chassisVertexArray)
        gimpactComponent.chassisShape.setLocalScaling(Vector3(1f, 1f, 1f))
        gimpactComponent.chassisShape.setMargin(0f)
        gimpactComponent.chassisShape.updateBound()
        entity.add(gimpactComponent)
        val bulletComponent = BulletComponent()
        val localInertia = Vector3()
        gimpactComponent.chassisShape.calculateLocalInertia(12f, localInertia)
        bulletComponent.bodyInfo = btRigidBodyConstructionInfo(12f, null, gimpactComponent.chassisShape, localInertia)
        bulletComponent.body = btRigidBody(bulletComponent.bodyInfo)
        (bulletComponent.body as btRigidBody).userData = entity
        bulletComponent.motionState = MotionState(modelComponent.instance.transform)
        (bulletComponent.body as btRigidBody?)!!.motionState = bulletComponent.motionState
        btGImpactCollisionAlgorithm.registerAlgorithm(bulletSystem.dispatcher)
        entity.add(bulletComponent)
        return entity
    }

    //Now we are able to spawn an enemy with the following creation function:
    fun createEnemy(bulletSystem: BulletSystem, x: Float, y: Float, z: Float): Entity {
        val entity = Entity()
        if (enemyModel == null) {
            enemyModel = Assets.assetManager.get<Model>("deer.g3db")
            /*            for (Node node : enemyModel.nodes) {
                node.translation.sub(0, 1f, 0);
                node.scale.scl(0.05f);
            }*/
            enemyModel?.calculateTransforms()
            enemyModelComponent = ModelComponent(enemyModel, x, y, z)
            //Material material = createMaterial("deer");
            //enemyModel.materials.get(0).set(material);
            // BlendingAttribute blendingAttribute;
            //material.set(blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            //enemyModelComponent.blendingAttribute = blendingAttribute;
        }
        (enemyModelComponent!!.instance.materials[0][BlendingAttribute.Type] as BlendingAttribute).opacity = 1f
        val modelComponent = ModelComponent(enemyModel, x, y, z)
        entity.add(modelComponent)
        val characterComponent = CharacterComponent()
        characterComponent.ghostObject = btPairCachingGhostObject()
        characterComponent.ghostObject!!.worldTransform = modelComponent.instance.transform
        characterComponent.ghostShape = btCapsuleShape(2f, 3f)
        characterComponent.ghostObject!!.collisionShape = characterComponent.ghostShape
        characterComponent.ghostObject!!.collisionFlags = btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT
        characterComponent.characterController = btKinematicCharacterController(characterComponent.ghostObject,
            characterComponent.ghostShape,         .55f,         Vector3(0f, 1f, 0f))
        characterComponent.ghostObject!!.userData = entity
        entity.add(characterComponent)
        bulletSystem.collisionWorld.addCollisionObject(entity.getComponent(
            CharacterComponent::class.java).ghostObject,
            btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
            btBroadphaseProxy.CollisionFilterGroups.AllFilter)
        bulletSystem.collisionWorld.addAction(entity.getComponent(CharacterComponent::class.java).characterController)
        entity.add(EnemyComponent(EnemyComponent.STATE.HUNTING))
        val animationComponent = AnimationComponent(modelComponent.instance)
        //animationComponent.animate(EnemyAnimations.id, EnemyAnimations.offsetRun1, EnemyAnimations.durationRun1, -1, 1);
        entity.add(animationComponent)
        entity.add(StatusComponent(animationComponent))
        //entity.add(new DieParticleComponent(RenderSystem.particleSystem));
        entity.add(DecalComponent())
        return entity
    }

    fun loadGun(x: Float, y: Float, z: Float): Entity {
        val model = Assets.assetManager.get<Model>("gun.g3dj")
        val material = createMaterial("snow-packed12")
        model.materials[0].set(material)
        val modelComponent = ModelComponent(model, x, y, z)
        modelComponent.instance.transform.rotate(0f, 1f, 0f, 180f)
        val gunEntity = Entity()
        gunEntity.add(modelComponent)
        gunEntity.add(GunComponent())
        gunEntity.add(AnimationComponent(modelComponent.instance))
        //gunEntity.add(new FireParticleComponent(RenderSystem.particleSystem));
        return gunEntity
    }

    fun loadScene(x: Int, y: Int, z: Int): Entity {
        val entity = Entity()
        val model = Assets.assetManager.get<Model>("arena.g3dj")
        //for (Node node : model.nodes) node.scale.scl(3.1f);
        //model.materials.get(0).set(createMaterial("rustediron-streaks"));
        //model.materials.get(1).set(createMaterial("snow-packed12"));
        val modelComponent = com.mljoke.rajon.components.ModelComponent(model,
            x.toFloat(), y.toFloat(), z.toFloat())
        entity.add(modelComponent)
        val bulletComponent = BulletComponent()
        val shape = Bullet.obtainStaticNodeShape(model.nodes)
        //shape.setLocalScaling(new Vector3(3.1f, 3.1f, 3.1f));
        bulletComponent.bodyInfo = btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero)
        bulletComponent.body = btRigidBody(bulletComponent.bodyInfo)
        (bulletComponent.body as btRigidBody).userData = entity
        bulletComponent.motionState = MotionState(modelComponent.instance.transform)
        (bulletComponent.body as btRigidBody?)!!.motionState = bulletComponent.motionState
        entity.add(bulletComponent)
        return entity
    }

    fun loadDome(x: Int, y: Int, z: Int): Entity {
        val model = Assets.assetManager.get<Model>("spacedome.g3db")
        model.materials[0].set(createMaterial("grass1"))
        val modelComponent = com.mljoke.rajon.components.ModelComponent(model,
            x.toFloat(), y.toFloat(), z.toFloat())
        val entity = Entity()
        entity.add(modelComponent)
        return entity
    }

    init {
        modelBuilder = ModelBuilder()
        playerTexture = Assets.assetManager.get<Texture>("badlogic.jpg")
        val material = createMaterial("carvedlimestoneground1")
        playerModel = modelBuilder!!.createCapsule(2f,
            6f,
            16,
            material,
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong())
    }
}