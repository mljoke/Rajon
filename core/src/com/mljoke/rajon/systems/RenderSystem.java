package com.mljoke.rajon.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.mljoke.rajon.Core;
import com.mljoke.rajon.java.Settings;
import com.mljoke.rajon.components.*;
import com.procedural.world.PBRSadherTexture;
import com.procedural.world.PBRShader;

public class RenderSystem extends EntitySystem {
    private static final float FOV = 67F;
    private ImmutableArray<Entity> entities;
    private ModelBatch batch;
    public Environment environment;
    private DirectionalShadowLight shadowLight;
    public PerspectiveCamera perspectiveCamera, gunCamera;
    public Entity gun;
    private Vector3 position;
    public static ParticleSystem particleSystem;
    public static DecalBatch decalBatch;
    protected ModelBatch shaderBatch;
    protected BaseShaderProvider shaderProvider;
    Cubemap cubemap;
    PointLight pointLight;
    public PBRSadherTexture pbrSadherTexture;
    public PBRShader pbrShader;

    public RenderSystem() {
        perspectiveCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        perspectiveCamera.far = 1000f;
        environment = new Environment();
       // environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
        //environment.set(new ColorAttribute(ColorAttribute.Fog, 0.13f, 0.13f, 0.13f, 1f));
        //pbrSadherTexture=new PBRSadherTexture();
        //pbrSadherTexture.init();
        pbrShader = new PBRShader();
        pbrShader.init();
        //environment.add(pointLight = new PointLight().set(0.2f, 0.8f, 0.2f, 0f, 0f, 0f, 100f));
        //shadowLight = new DirectionalShadowLight(1024 * 5, 1024 * 5, 200f, 200f, 1f, 300f);
       //shadowLight.set(0.8f, 0.8f, 0.8f, 0, -0.1f, 0.1f);

        environment.add(shadowLight);

        environment.shadowMap = shadowLight;
       //environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1.0f, -0.8f));
//        shaderProvider = new TestShaderProvider();
        shaderProvider = new BaseShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                return pbrShader;
            }
        };
        batch = new ModelBatch(shaderProvider);
        gunCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        gunCamera.far = 100f;

        position = new Vector3();

        particleSystem = ParticleSystem.get();
        PointSpriteParticleBatch pointSpriteParticleBatch = new PointSpriteParticleBatch();
        pointSpriteParticleBatch.setCamera(perspectiveCamera);
        particleSystem.add(pointSpriteParticleBatch);
        BillboardParticleBatch billboardParticleBatch = new BillboardParticleBatch();
        billboardParticleBatch.setCamera(perspectiveCamera);
        particleSystem.add(billboardParticleBatch);

        decalBatch = new DecalBatch(new CameraGroupStrategy(perspectiveCamera));
    }

    // Event called when an entity is added to the engine
    public void addedToEngine(Engine e) {
        // Grabs all entities with desired components
        entities = e.getEntitiesFor(Family.all(ModelComponent.class).get());
    }

    public void update(float delta) {
        //drawShadows(delta);
        drawModels();
    }

/*    private boolean isVisible(Camera cam, final ModelInstance instance) {
        return cam.frustum.pointInFrustum(instance.transform.getTranslation(position));
    }*/

    private void drawShadows(float delta) {
        shadowLight.begin(Vector3.Zero, perspectiveCamera.direction);
        batch.begin(perspectiveCamera);
        for (int x = 0; x < entities.size(); x++) {
            if (entities.get(x).getComponent(PlayerComponent.class) != null || entities.get(x).getComponent(EnemyComponent.class) != null) {
                ModelComponent mod = entities.get(x).getComponent(ModelComponent.class);
                //if (isVisible(perspectiveCamera, mod.instance))
                    batch.render(mod.instance);
            }
            if (entities.get(x).getComponent(AnimationComponent.class) != null & !Settings.Paused)
                entities.get(x).getComponent(AnimationComponent.class).update(delta);
        }
        batch.end();
        shadowLight.end();
    }

    private void drawModels() {
        batch.begin(perspectiveCamera);
        //decalBatch.flush();
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getComponent(GunComponent.class) == null) {
                ModelComponent mod = entities.get(i).getComponent(ModelComponent.class);
                //testAttribute = (TestAttribute)mod.model.materials.get(0).get(TestAttribute.ID);
                //mod.instance.materials.get(0).clear();
                //mod.instance.materials.get(0).set(Assets.assetManager.get("shaders/materials/brick01.g3dj", Model.class).materials.get(0));
               batch.render(mod.instance, environment);

            }
        }
        batch.end();

        renderParticleEffects();


        drawGun();

    }

    private void renderParticleEffects() {
        batch.begin(gunCamera);
        particleSystem.update(); // technically not necessary for rendering
        particleSystem.begin();
        particleSystem.draw();
        particleSystem.end();
        batch.render(particleSystem);
        batch.end();
    }

    private void drawGun() {
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(gunCamera);
        batch.render(gun.getComponent(ModelComponent.class).instance);
        batch.end();
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
        gunCamera.viewportHeight = height;
        gunCamera.viewportWidth = width;
    }

    public void dispose() {
        batch.dispose();
        batch = null;
    }

}