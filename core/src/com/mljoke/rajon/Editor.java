package com.mljoke.rajon;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.mljoke.rajon.components.CharacterComponent;
import com.mljoke.rajon.managers.EntityFactory;
import com.mljoke.rajon.systems.*;

public class Editor {
    private static final boolean debug = true;
    private DebugDrawer debugDrawer;
    private Engine engine;
    public BulletSystem bulletSystem;
    private RenderSystem renderSystem;

    public Editor() {
        Bullet.init();
        setDebug();
        addSystems();
        addEntities();
    }

    private void setDebug() {
        if (debug) {
            debugDrawer = new DebugDrawer();
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        }
    }

    private void addSystems() {
        engine = new Engine();
        engine.addSystem(renderSystem = new RenderSystem());
        EntityFactory.renderSystem = renderSystem;
        engine.addSystem(bulletSystem = new BulletSystem());
        if (debug) bulletSystem.collisionWorld.setDebugDrawer(this.debugDrawer);
    }

    private void addEntities() {

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
            debugDrawer.begin(renderSystem.perspectiveCamera);
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
