package com.mljoke.rajon.java;

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
import com.mljoke.rajon.Assets;
import com.mljoke.rajon.Bullet.MotionState;
import com.mljoke.rajon.Logger;
import com.mljoke.rajon.components.*;
import com.mljoke.rajon.components.BulletComponent;
import com.mljoke.rajon.systems.BulletSystem;
import com.mljoke.rajon.systems.RenderSystem;
import com.procedural.world.PBRTextureAttribute;


public class EntityFactory {
    public static Entity createStaticEntity(String str, String material, Vector3 vec) {
        return createStaticEntity(new Model(), vec.x, vec.y, vec.z);
    }

    public static Entity createStaticEntity(Model model, float x, float y, float z) {
        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f, boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));
        Entity entity = new Entity();;
        com.mljoke.rajon.components.ModelComponent modelComponent = new com.mljoke.rajon.components.ModelComponent(model, x, y, z);
        entity.add(modelComponent);
        com.mljoke.rajon.components.BulletComponent bulletComponent = new com.mljoke.rajon.components.BulletComponent();
        bulletComponent.setBodyInfo(new btRigidBody.btRigidBodyConstructionInfo(0, null, col, Vector3.Zero));
        bulletComponent.setBody(new btRigidBody(bulletComponent.getBodyInfo()));
        bulletComponent.getBody().userData = entity;
        bulletComponent.setMotionState(new MotionState(modelComponent.getInstance().transform));
        ((btRigidBody) bulletComponent.getBody()).setMotionState(bulletComponent.getMotionState());
        entity.add(bulletComponent);
        return entity;
    }

    public static RenderSystem renderSystem;
    private static final ModelBuilder modelBuilder;
    private static final Texture playerTexture;
    private static final Model playerModel;
    private static Model enemyModel;
    private static com.mljoke.rajon.java.ModelComponent enemyModelComponent;

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
        //RenderSystem.decalBatch.add(decal);
        entity.add(decalComponent);

        return entity;
    }

    public static Entity createGimpact(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = new Entity();
        final Model chassisModel = playerModel;
        com.mljoke.rajon.java.ModelComponent modelComponent = new com.mljoke.rajon.java.ModelComponent(chassisModel, x, y, z);
        entity.add(modelComponent);
        GimpactComponent gimpactComponent = new GimpactComponent();
        gimpactComponent.chassisVertexArray = new btTriangleIndexVertexArray(chassisModel.meshParts);
        gimpactComponent.chassisShape = new btGImpactMeshShape(gimpactComponent.chassisVertexArray);
        gimpactComponent.chassisShape.setLocalScaling(new Vector3(1f, 1f, 1f));
        gimpactComponent.chassisShape.setMargin(0f);
        gimpactComponent.chassisShape.updateBound();
        entity.add(gimpactComponent);
        com.mljoke.rajon.components.BulletComponent bulletComponent = new com.mljoke.rajon.components.BulletComponent();
        Vector3 localInertia = new Vector3();
        gimpactComponent.chassisShape.calculateLocalInertia(12, localInertia);
        bulletComponent.setBodyInfo(new btRigidBody.btRigidBodyConstructionInfo(12, null, gimpactComponent.chassisShape, localInertia));
        bulletComponent.setBody(new btRigidBody(bulletComponent.getBodyInfo()));
        bulletComponent.getBody().userData = entity;
        bulletComponent.setMotionState(new MotionState(modelComponent.instance.transform));
        ((btRigidBody) bulletComponent.getBody()).setMotionState(bulletComponent.getMotionState());
        btGImpactCollisionAlgorithm.registerAlgorithm(bulletSystem.dispatcher);
        entity.add(bulletComponent);
        return entity;
    }


    private static Entity createCharacter(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = new Entity();
        com.mljoke.rajon.components.ModelComponent modelComponent = new com.mljoke.rajon.components.ModelComponent(playerModel, x, y, z);
        modelComponent.getInstance().transform.rotate(Vector3.X, 90);
        entity.add(modelComponent);
        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.setGhostObject(new btPairCachingGhostObject());
        characterComponent.getGhostObject().setWorldTransform(modelComponent.getInstance().transform);
        characterComponent.setGhostShape(new btCapsuleShape(2f, 2f));
        characterComponent.getGhostObject().setCollisionShape(characterComponent.getGhostShape());
        characterComponent.getGhostObject().setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.setCharacterController(new btKinematicCharacterController(characterComponent.getGhostObject(), characterComponent.getGhostShape(), .35f, Vector3.Y));
        characterComponent.getGhostObject().userData = entity;
        entity.add(characterComponent);
        bulletSystem.collisionWorld.addCollisionObject(entity.getComponent(CharacterComponent.class).getGhostObject(),
                (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short) (btBroadphaseProxy.CollisionFilterGroups.AllFilter));
        bulletSystem.collisionWorld.addAction(entity.getComponent(CharacterComponent.class).getCharacterController());
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
            enemyModel = Assets.assetManager.get("deer.g3db");
/*            for (Node node : enemyModel.nodes) {
                node.translation.sub(0, 1f, 0);
                node.scale.scl(0.05f);
            }*/
            enemyModel.calculateTransforms();
            enemyModelComponent = new com.mljoke.rajon.java.ModelComponent(enemyModel, x, y, z);
            //Material material = createMaterial("deer");
            //enemyModel.materials.get(0).set(material);
           // BlendingAttribute blendingAttribute;
            //material.set(blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            //enemyModelComponent.blendingAttribute = blendingAttribute;
        }

        ((BlendingAttribute) enemyModelComponent.instance.materials.get(0).get(BlendingAttribute.Type)).opacity = 1;
        com.mljoke.rajon.java.ModelComponent modelComponent = new com.mljoke.rajon.java.ModelComponent(enemyModel, x, y, z);
        entity.add(modelComponent);
        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.setGhostObject(new btPairCachingGhostObject());
        characterComponent.getGhostObject().setWorldTransform(modelComponent.instance.transform);
        characterComponent.setGhostShape(new btCapsuleShape(2f, 3f));
        characterComponent.getGhostObject().setCollisionShape(characterComponent.getGhostShape());
        characterComponent.getGhostObject().setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.setCharacterController(new btKinematicCharacterController(characterComponent.getGhostObject(), characterComponent.getGhostShape(), .55f, new Vector3(0, 1, 0)));
        characterComponent.getGhostObject().userData = entity;
        entity.add(characterComponent);
        bulletSystem.collisionWorld.addCollisionObject(entity.getComponent(CharacterComponent.class).getGhostObject(), (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter, (short) (btBroadphaseProxy.CollisionFilterGroups.AllFilter));
        bulletSystem.collisionWorld.addAction(entity.getComponent(CharacterComponent.class).getCharacterController());
        entity.add(new EnemyComponent(EnemyComponent.STATE.HUNTING));
        AnimationComponent animationComponent = new AnimationComponent(modelComponent.instance);
        //animationComponent.animate(EnemyAnimations.id, EnemyAnimations.offsetRun1, EnemyAnimations.durationRun1, -1, 1);
        entity.add(animationComponent);
        entity.add(new StatusComponent(animationComponent));
        //entity.add(new DieParticleComponent(RenderSystem.particleSystem));
        entity.add(new DecalComponent());
        return entity;
    }

    public static Entity loadGun(float x, float y, float z) {
        Model model = Assets.assetManager.get("gun.g3dj");
        Material material = createMaterial("snow-packed12");
        model.materials.get(0).set(material);
        com.mljoke.rajon.java.ModelComponent modelComponent = new com.mljoke.rajon.java.ModelComponent(model, x, y, z);
        modelComponent.instance.transform.rotate(0, 1, 0, 180);
        Entity gunEntity = new Entity();
        gunEntity.add(modelComponent);
        gunEntity.add(new GunComponent());
        gunEntity.add(new AnimationComponent(modelComponent.instance));
        //gunEntity.add(new FireParticleComponent(RenderSystem.particleSystem));
        return gunEntity;
    }

    public static Entity loadScene(int x, int y, int z) {
        Entity entity = new Entity();
        Model model = Assets.assetManager.get("arena.g3dj");
        //for (Node node : model.nodes) node.scale.scl(3.1f);
        //model.materials.get(0).set(createMaterial("rustediron-streaks"));
        //model.materials.get(1).set(createMaterial("snow-packed12"));
        com.mljoke.rajon.components.ModelComponent modelComponent = new com.mljoke.rajon.components.ModelComponent(model, x, y, z);
        entity.add(modelComponent);
        com.mljoke.rajon.components.BulletComponent bulletComponent = new BulletComponent();
        btCollisionShape shape = Bullet.obtainStaticNodeShape(model.nodes);
        //shape.setLocalScaling(new Vector3(3.1f, 3.1f, 3.1f));
        bulletComponent.setBodyInfo(new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, Vector3.Zero));
        bulletComponent.setBody(new btRigidBody(bulletComponent.getBodyInfo()));
        bulletComponent.getBody().userData = entity;
        bulletComponent.setMotionState(new MotionState(modelComponent.getInstance().transform));
        ((btRigidBody) bulletComponent.getBody()).setMotionState(bulletComponent.getMotionState());
        entity.add(bulletComponent);
        return entity;
    }

    public static Entity loadDome(int x, int y, int z) {
        Model model = Assets.assetManager.get("spacedome.g3db");
        model.materials.get(0).set(createMaterial("grass1"));
        com.mljoke.rajon.components.ModelComponent modelComponent = new com.mljoke.rajon.components.ModelComponent(model, x, y, z);
        Entity entity = new Entity();
        entity.add(modelComponent);
        return entity;
    }
}

