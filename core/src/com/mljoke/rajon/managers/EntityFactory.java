package com.mljoke.rajon.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mljoke.rajon.java.Assets;
import com.mljoke.rajon.Bullet.MotionState;
import com.mljoke.rajon.Logger;
import com.mljoke.rajon.components.*;
import com.mljoke.rajon.systems.BulletSystem;
import com.mljoke.rajon.systems.RenderSystem;
import com.procedural.world.PBRTextureAttribute;


public class EntityFactory {
    private static boolean sclNodes = true;

    public static Entity createStaticEntity(String str, String material, Vector3 vec) {
        return createStaticEntity(str, material, vec.x, vec.y, vec.z);
    }

    public static Entity createStaticEntity(String name, String material, float x, float y, float z) {
        Model model = Assets.assetManager.get(name);
        final BoundingBox boundingBox = new BoundingBox();
        float scale = 0.05f;

        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();

        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f * scale, boundingBox.getHeight() * 0.5f * scale, boundingBox.getDepth() * 0.5f * scale));
        Entity entity = new Entity();
        Material mat = createMaterial(material);
        model.materials.get(0).set(mat);
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        entity.add(modelComponent);
        BulletComponent bulletComponent = new BulletComponent();
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, col, Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body).setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);
        return entity;
    }

    public static RenderSystem renderSystem;
    private static final ModelBuilder modelBuilder;
    private static final Texture playerTexture;
    private static final Model playerModel;
    private static Model enemyModel;
    private static ModelComponent enemyModelComponent;

    public static Material createMaterial(String materialName) {
        Material material = new Material();
        try {
            material.set(PBRTextureAttribute.createAlbedo(new Texture("materials/" + materialName + "/Diffuse.png")));
            material.set(PBRTextureAttribute.createMetallic(new Texture("materials/" + materialName + "/Specular.png")));
            material.set(PBRTextureAttribute.createRoughness(new Texture("materials/" + materialName + "/Glossiness.png")));
            material.set(PBRTextureAttribute.createAmbientOcclusion(new Texture("materials/" + materialName + "/AO.png")));
            material.set(PBRTextureAttribute.createHeight(new Texture("materials/" + materialName + "/Height.png")));
            material.set(PBRTextureAttribute.createNormal(new Texture("materials/" + materialName + "/Normal.png")));
        } catch (Exception e) {
            Logger.log(Logger.ANDREAS, Logger.INFO, e.getMessage());
        }


        return material;
    }

    static {
        modelBuilder = new ModelBuilder();
        playerTexture = Assets.assetManager.get("badlogic.jpg");
        Material material = createMaterial("carvedlimestoneground1");

        playerModel = modelBuilder.createCapsule(2f, 6f, 16, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }

    public static Entity createDecal(RenderSystem renderSystem, float x, float y, float z) {
        Entity entity = new Entity();
        DecalComponent decalComponent = new DecalComponent();
        Decal decal = Decal.newDecal(10, 10, decalComponent.decal);
        decal.setPosition(x, y, z);
        RenderSystem.decalBatch.add(decal);
        entity.add(decalComponent);

        return entity;
    }

    public static Entity createGimpact(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = new Entity();
        final Model chassisModel = playerModel;
        ModelComponent modelComponent = new ModelComponent(chassisModel, x, y, z);
        entity.add(modelComponent);
        GimpactComponent gimpactComponent = new GimpactComponent();
        gimpactComponent.chassisVertexArray = new btTriangleIndexVertexArray(chassisModel.meshParts);
        gimpactComponent.chassisShape = new btGImpactMeshShape(gimpactComponent.chassisVertexArray);
        gimpactComponent.chassisShape.setLocalScaling(new Vector3(1f, 1f, 1f));
        gimpactComponent.chassisShape.setMargin(0f);
        gimpactComponent.chassisShape.updateBound();
        entity.add(gimpactComponent);
        BulletComponent bulletComponent = new BulletComponent();
        Vector3 localInertia = new Vector3();
        gimpactComponent.chassisShape.calculateLocalInertia(1, localInertia);
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(11, null, gimpactComponent.chassisShape, localInertia);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body).setMotionState(bulletComponent.motionState);
        btGImpactCollisionAlgorithm.registerAlgorithm(bulletSystem.dispatcher);
        entity.add(bulletComponent);
        return entity;
    }


    private static Entity createCharacter(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = new Entity();
        ModelComponent modelComponent = new ModelComponent(playerModel, x, y, z);
        modelComponent.instance.transform.rotate(Vector3.X, 90);
        entity.add(modelComponent);
        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject, characterComponent.ghostShape, .35f, Vector3.Y);
        characterComponent.ghostObject.userData = entity;
        entity.add(characterComponent);
        bulletSystem.collisionWorld.addCollisionObject(entity.getComponent(CharacterComponent.class).ghostObject,
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) (btBroadphaseProxy.CollisionFilterGroups.AllFilter));
        bulletSystem.collisionWorld.addAction(entity.getComponent(CharacterComponent.class).characterController);
        return entity;
    }

    public static Entity createPlayer(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = createCharacter(bulletSystem, x, y, z);
        entity.add(new PlayerComponent());
        return entity;
    }

    //Now we are able to spawn an enemy with the following creation function:
    public static Entity createEnemy(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = new Entity();
        if (enemyModel == null) {
            enemyModel = Assets.get("deer.g3db");
/*            for (Node node : enemyModel.nodes) {
                node.translation.sub(0, 1f, 0);
                node.scale.scl(0.05f);
            }*/
            enemyModel.calculateTransforms();
            enemyModelComponent = new ModelComponent(enemyModel, x, y, z);
            Material material = createMaterial("deer");
            enemyModel.materials.get(0).set(material);
            BlendingAttribute blendingAttribute;
            material.set(blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            enemyModelComponent.blendingAttribute = blendingAttribute;
        }

        ((BlendingAttribute) enemyModelComponent.instance.materials.get(0).get(BlendingAttribute.Type)).opacity = 1;
        ModelComponent modelComponent = new ModelComponent(enemyModel, x, y, z);
        entity.add(modelComponent);
        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 3f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject, characterComponent.ghostShape, .55f, new Vector3(0, 1, 0));
        characterComponent.ghostObject.userData = entity;
        entity.add(characterComponent);
        bulletSystem.collisionWorld.addCollisionObject(entity.getComponent(CharacterComponent.class).ghostObject, (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter, (short) (btBroadphaseProxy.CollisionFilterGroups.AllFilter));
        bulletSystem.collisionWorld.addAction(entity.getComponent(CharacterComponent.class).characterController);
        entity.add(new EnemyComponent(EnemyComponent.STATE.HUNTING));
        AnimationComponent animationComponent = new AnimationComponent(modelComponent.instance);
        //animationComponent.animate(EnemyAnimations.id, EnemyAnimations.offsetRun1, EnemyAnimations.durationRun1, -1, 1);
        entity.add(animationComponent);
        entity.add(new StatusComponent(animationComponent));
        entity.add(new DieParticleComponent(RenderSystem.particleSystem));
        entity.add(new DecalComponent());
        return entity;
    }

    public static Entity loadGun(float x, float y, float z) {
        Model model = Assets.assetManager.get("gun.g3dj");
        Material material = createMaterial("snow-packed12");
        model.materials.get(0).set(material);
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        modelComponent.instance.transform.rotate(0, 1, 0, 180);
        Entity gunEntity = new Entity();
        gunEntity.add(modelComponent);
        gunEntity.add(new GunComponent());
        gunEntity.add(new AnimationComponent(modelComponent.instance));
        gunEntity.add(new FireParticleComponent(RenderSystem.particleSystem));
        return gunEntity;
    }

    public static Entity loadScene(int x, int y, int z) {
        Entity entity = new Entity();
        Model model = Assets.assetManager.get("arena.g3dj");
        //for (Node node : model.nodes) node.scale.scl(3.1f);
        model.materials.get(0).set(createMaterial("rustediron-streaks"));
        model.materials.get(1).set(createMaterial("snow-packed12"));
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        entity.add(modelComponent);
        BulletComponent bulletComponent = new BulletComponent();
        btCollisionShape shape = Bullet.obtainStaticNodeShape(model.nodes);
        //shape.setLocalScaling(new Vector3(3.1f, 3.1f, 3.1f));
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body).setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);
        return entity;
    }

    public static Entity loadDome(int x, int y, int z) {
        Model model = Assets.assetManager.get("spacedome.g3db");
        model.materials.get(0).set(createMaterial("grass1"));
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        Entity entity = new Entity();
        entity.add(modelComponent);
        return entity;
    }
}

