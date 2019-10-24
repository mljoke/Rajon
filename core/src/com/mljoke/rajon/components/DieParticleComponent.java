package com.mljoke.rajon.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.mljoke.rajon.java.Assets;

public class DieParticleComponent implements Component {
    public ParticleEffect originalEffect;
    public boolean used = false;

    public DieParticleComponent(ParticleSystem particleSystem) {
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        if (!Assets.assetManager.isLoaded("dieparticle.pfx")) {
            Assets.assetManager.load("dieparticle.pfx", ParticleEffect.class, loadParam);
            Assets.assetManager.finishLoading();
        }
        originalEffect = Assets.assetManager.get("dieparticle.pfx");
    }
}