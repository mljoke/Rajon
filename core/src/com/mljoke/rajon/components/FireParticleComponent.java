package com.mljoke.rajon.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.particles.*;
import com.mljoke.rajon.java.Assets;

public class FireParticleComponent
        implements Component {

    public FireParticleComponent(ParticleSystem particleSystem) {
        used = false;
        com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        if (!Assets.assetManager.isLoaded("fire.pfx")) {
            Assets.assetManager.load("fire.pfx", ParticleEffect.class, loadParam);
            Assets.assetManager.finishLoading();
        }
        originalEffect = Assets.assetManager.get("fire.pfx");
    }

    public ParticleEffect originalEffect;
    public boolean used;
}