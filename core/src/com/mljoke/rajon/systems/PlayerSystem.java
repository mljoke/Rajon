package com.mljoke.rajon.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.mljoke.rajon.*;
import com.mljoke.rajon.GameUI;
import com.mljoke.rajon.GameWorld;
import com.mljoke.rajon.java.Settings;
import com.mljoke.rajon.components.*;

public class PlayerSystem extends EntitySystem implements EntityListener {

    public Entity dome;
    private Entity player;
    public Entity gun;
    private PlayerComponent playerComponent;
    private GameUI gameUI;
    private CharacterComponent characterComponent;
    private ModelComponent modelComponent;
    private final Vector3 tmp = new Vector3();
    private final PerspectiveCamera camera;
    private GameWorld gameWorld;
    private Editor editor;
    private ClosestRayResultCallback rayTestCB;
    private Vector3 rayFrom = new Vector3();
    private Vector3 rayTo = new Vector3();
    private Vector3 smooth = new Vector3();


    public PlayerSystem(PerspectiveCamera camera, GameUI gameUI, GameWorld gameWorld) {
        this.camera = camera;
        this.gameUI = gameUI;
        this.gameWorld = gameWorld;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    }
    public PlayerSystem(PerspectiveCamera camera, Editor gameWorld) {
        this.camera = camera;
        this.editor = gameWorld;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    @Override
    public void entityAdded(Entity entity) {
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        characterComponent = entity.getComponent(CharacterComponent.class);
        modelComponent = entity.getComponent(ModelComponent.class);
    }

    @Override
    public void entityRemoved(Entity entity) {
    }

    @Override
    public void update(float delta) {
        if (player == null) return;
        updateMovement(delta);
        updateStatus();
        checkGameOver();

    }

    private void checkGameOver() {
        if (playerComponent.health <= 0 && !Settings.Paused) {
            Settings.Paused = true;
            gameUI.gameOverWidget.gameOver();
        }
    }

    private void updateMovement(float delta) {
        float deltaX = -Gdx.input.getDeltaX() * 0.2f;
        float deltaY = -Gdx.input.getDeltaY() * 0.2f;

        tmp.set(0, 0, 0);

        camera.rotate(camera.up, deltaX);

        tmp.set(camera.direction).crs(camera.up);
        camera.direction.rotate(tmp, deltaY);

        tmp.set(0, 0, 0);
        characterComponent.characterDirection.set(-1, 0, 0).rot(modelComponent.instance.transform).nor();
        characterComponent.walkDirection.set(0, 0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            characterComponent.walkDirection.add(camera.direction.x, 0, camera.direction.z);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.W)) {
            Control.run(delta);
            characterComponent.walkDirection.scl(2f);
        } else Control.run(-delta);
        camera.fieldOfView = Control.getFoV();
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            characterComponent.walkDirection.sub(camera.direction.x, 0, camera.direction.z);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) tmp.set(camera.direction).crs(camera.up).scl(-1);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) tmp.set(camera.direction).crs(camera.up);
        characterComponent.walkDirection.add(tmp);
        characterComponent.walkDirection.scl(20f * delta);
        characterComponent.characterController.setWalkDirection(characterComponent.walkDirection);
        Matrix4 ghost = new Matrix4();
        Vector3 translation = new Vector3();
        characterComponent.ghostObject.getWorldTransform(ghost);   //TODO export this
        ghost.getTranslation(translation);
        modelComponent.instance.transform.set(translation.x, translation.y, translation.z, camera.direction.x, camera.direction.y, camera.direction.z, 0);
        camera.position.set(translation.x, translation.y, translation.z);
        camera.update(true);
        //dome.getComponent(ModelComponent.class).instance.transform.setToTranslation(translation.x, translation.y, translation.z);
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            //if (characterComponent.characterController.onGround()) {
                characterComponent.characterController.jump(new Vector3(0, 20, 0));
            //}
        }
        if (Gdx.input.justTouched()) fire();
        gun.getComponent(ModelComponent.class).instance.transform.getTranslation(smooth);
        gun.getComponent(ModelComponent.class).instance.transform.translate((smooth.x + (deltaX - smooth.x) * 0.05f) - 2.5f, 0, 0);
    }

    private void fire() {
        Ray ray = camera.getPickRay(Gdx.graphics.getWidth() >> 1, Gdx.graphics.getHeight() >> 1);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom); /* 50 meters max from the   origin*/   /* Because we reuse the ClosestRayResultCallback, we need reset it's values*/
        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.setRayFromWorld(rayFrom);
        rayTestCB.setRayToWorld(rayTo);
        if (gameWorld != null) {
            gameWorld.bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);
        } else {
            editor.bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);
        }
        if (rayTestCB.hasHit()) {
            final btCollisionObject obj = rayTestCB.getCollisionObject();
            if (((Entity) obj.userData).getComponent(EnemyComponent.class) != null) {
                if (((Entity) obj.userData).getComponent(StatusComponent.class).health > 0) {
                    ((Entity) obj.userData).getComponent(StatusComponent.class).health -= 10;
                } else {
                    ((Entity) obj.userData).getComponent(StatusComponent.class).setAlive(false);
                    PlayerComponent.score += 100;
                }

            }
        }

        ParticleEffect effect = gun.getComponent(FireParticleComponent.class).originalEffect.copy();
        ((RegularEmitter) effect.getControllers().first().emitter).setEmissionMode(RegularEmitter.EmissionMode.Enabled);
        effect.setTransform(gun.getComponent(ModelComponent.class).instance.transform);
        effect.scale(2.5f, -1.9f, -4);
        effect.init();
        effect.start();
        RenderSystem.particleSystem.add(effect);//TODO
        gun.getComponent(AnimationComponent.class).animate(gun.getComponent(ModelComponent.class).instance.animations.get(0).id, 1, 1);
        Logger.log(Logger.ANDREAS, Logger.INFO, gun.getComponent(ModelComponent.class).instance.animations.get(0).id);
    }

    private void updateStatus() {
        //gameUI.healthWidget.setValue(playerComponent.health);
    }
}
