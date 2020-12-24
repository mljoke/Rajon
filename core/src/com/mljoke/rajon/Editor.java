package com.mljoke.rajon;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.mljoke.rajon.java.Settings;
import com.mljoke.rajon.java.EntityFactory;
import com.mljoke.rajon.systems.*;

public class Editor {
    public static boolean debug = true;
    private DebugDrawer debugDrawer;
    public Engine engine;
    public BulletSystem bulletSystem;
    private RenderSystem renderSystem;
    private Entity character, gun;
    public PlayerSystem playerSystem;

    public Editor() {
        Bullet.init();
        setDebug();
        addSystems();
        addEntities();
    }

    private void setDebug() {
            debugDrawer = new DebugDrawer();
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
    }

    private void addSystems() {
        engine = new Engine();
        //engine.addSystem(renderSystem = new RenderSystem());
        EntityFactory.renderSystem = renderSystem;
        //engine.addSystem(playerSystem = new PlayerSystem(renderSystem.perspectiveCamera, this));
        engine.addSystem(bulletSystem = new BulletSystem());
        bulletSystem.collisionWorld.setDebugDrawer(this.debugDrawer);
    }

    private void addEntities() {
        engine.addEntity(EntityFactory.loadScene(0, 0, 0));
        //engine.addEntity(EntityFactory.createGimpact(bulletSystem, 0, 10, 0));
       // engine.addEntity(EntityFactory.createEnemy(bulletSystem, 0, 20, 0));
        createPlayer(0, 6, 0);
    }
    private void createPlayer(float x, float y, float z) {
        character = EntityFactory.createPlayer(bulletSystem, x, y, z);
        engine.addEntity(character);
        engine.addEntity(gun = EntityFactory.loadGun(2.5f, -1.9f, -4));
        playerSystem.gun = gun;
        //renderSystem.gun = gun;
    }
    private void loadLevel() {
        engine.addEntity(EntityFactory.loadScene(0, 0, 0));
        //engine.addEntity(dome = EntityFactory.loadDome(0, 0, 0));
        //playerSystem.dome = dome;
    }


    public void render(float delta) {
        renderWorld(delta);
        checkPause();
    }

    private void checkPause() {
        if (Settings.Paused) {
            engine.getSystem(BulletSystem.class).setProcessing(false);
        } else {
            engine.getSystem(BulletSystem.class).setProcessing(true);
        }
    }

    protected void renderWorld(float delta) {
        engine.update(delta);
        if (debug) {
            //debugDrawer.begin(renderSystem.perspectiveCamera);
            bulletSystem.collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
    }

    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

    public void dispose() {
        bulletSystem.dispose();
        bulletSystem = null;
        renderSystem.dispose();

//        EntityFactory.dispose();
    }

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }
}
