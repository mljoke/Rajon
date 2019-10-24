package com.mljoke.rajon;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.mljoke.rajon.components.CharacterComponent;
import com.mljoke.rajon.components.ModelComponent;
import com.mljoke.rajon.java.Settings;
import com.mljoke.rajon.managers.EntityFactory;
import com.mljoke.rajon.systems.*;
import com.procedural.world.PBRShader;

public class GameWorld {
    private static final boolean debug = true;
    private DebugDrawer debugDrawer;
    private Engine engine;
    private Entity character, gun, dome;
    public BulletSystem bulletSystem;
    public PlayerSystem playerSystem;
    private RenderSystem renderSystem;
    public GameWorld(GameUI gameUI) {
        Bullet.init();
        setDebug();
        addSystems(gameUI);
        addEntities();
    }

    private void setDebug() {
        if (debug) {
            debugDrawer = new DebugDrawer();
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        }
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
        engine.addSystem(renderSystem = new RenderSystem());
        EntityFactory.renderSystem = renderSystem;
        engine.addSystem(bulletSystem = new BulletSystem());
        engine.addSystem(playerSystem = new PlayerSystem(renderSystem.perspectiveCamera, gameUI, this));
       //engine.addSystem(new EnemySystem(this));
        engine.addSystem(new StatusSystem(this));
        if (debug) bulletSystem.collisionWorld.setDebugDrawer(this.debugDrawer);
    }

    private void addEntities() {
        loadLevel();
        createPlayer(0, 6, 0);
    }
    Entity cub;
    private void loadLevel() {
        cub = EntityFactory.createStaticEntity("cube.g3dj","mybricks3",PBRShader.lightPos);
        //engine.addEntity(EntityFactory.loadScene(0, 0, 0));
        //engine.addEntity(EntityFactory.createStaticEntity("deer.g3db","snow-packed12", new Vector3(10, 5, 10)));
        //engine.addEntity(EntityFactory.createStaticEntity("gun.g3dj","snow-packed12", new Vector3(-20, 5, -10)));
        //engine.addEntity(EntityFactory.createStaticEntity("cube.g3dj","rustediron2", new Vector3(-10, 8, -10)));
        engine.addEntity(cub);
        //engine.addEntity(EntityFactory.createDecal(renderSystem, 0, 15, 0));
        //engine.addEntity(dome = EntityFactory.loadDome(0, 0, 0));
        //playerSystem.dome = dome;
        Logger.log(Logger.ANDREAS,Logger.INFO, "" + GL30.GL_MAX_TEXTURE_SIZE);
        //GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
    }

    private void createPlayer(float x, float y, float z) {
        character = EntityFactory.createPlayer(bulletSystem, x, y, z);
        engine.addEntity(character);
        engine.addEntity(gun = EntityFactory.loadGun(2.5f, -1.9f, -4));
        playerSystem.gun = gun;
        renderSystem.gun = gun;
    }

    public void render(float delta) {
        renderWorld(delta);
        checkPause();
        cub.getComponent(ModelComponent.class).instance.transform.setToTranslation(PBRShader.lightPos);
    }

    private void checkPause() {
        if (Settings.Paused) {
            engine.getSystem(PlayerSystem.class).setProcessing(false);
//            engine.getSystem(EnemySystem.class).setProcessing(false);
            engine.getSystem(StatusSystem.class).setProcessing(false);
            engine.getSystem(BulletSystem.class).setProcessing(false);
        } else {
            engine.getSystem(PlayerSystem.class).setProcessing(true);
//            engine.getSystem(EnemySystem.class).setProcessing(true);
            engine.getSystem(StatusSystem.class).setProcessing(true);
            engine.getSystem(BulletSystem.class).setProcessing(true);
        }
    }

    protected void renderWorld(float delta) {
        engine.update(delta);
        if (debug) {
            debugDrawer.begin(renderSystem.perspectiveCamera);
            bulletSystem.collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
    }

    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

    public void dispose() {
        bulletSystem.collisionWorld.removeAction(character.getComponent(CharacterComponent.class).characterController);
        bulletSystem.collisionWorld.removeCollisionObject(character.getComponent(CharacterComponent.class).ghostObject);
        bulletSystem.dispose();

        bulletSystem = null;
        renderSystem.dispose();

        character.getComponent(CharacterComponent.class).characterController.dispose();
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();
//        EntityFactory.dispose();
    }

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }
}