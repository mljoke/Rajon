package com.mljoke.rajon;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TestShaderProvider extends DefaultShaderProvider {
    public boolean error = false;
    public String name = "default";
    final static String tempFolder = "D:\\temp\\shaders";
    public void clear () {
        for (final Shader shader : shaders)
            shader.dispose();
        shaders.clear();
    }

    public boolean revert () {
        if (config.vertexShader == null || config.fragmentShader == null) return false;
        config.vertexShader = null;
        config.fragmentShader = null;
        clear();
        return true;
    }

    @Override
    public Shader getShader (Renderable renderable) {
        try {
            return super.getShader(renderable);
        } catch (Throwable e) {
            if (tempFolder != null && Gdx.app.getType() == Application.ApplicationType.Desktop)
                Gdx.files.absolute(tempFolder).child(name + ".log.txt").writeString(e.getMessage(), false);
            if (!revert()) {
                Gdx.app.error("ShaderCollectionTest", e.getMessage());
                throw new GdxRuntimeException("Error creating shader, cannot revert to default shader", e);
            }
            error = true;
            Gdx.app.error("ShaderTest", "Could not create shader, reverted to default shader.", e);
            return super.getShader(renderable);
        }
    }

    @Override
    protected Shader createShader (Renderable renderable) {
        if (config.vertexShader != null && config.fragmentShader != null && tempFolder != null
                && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            String prefix = DefaultShader.createPrefix(renderable, config);
            Gdx.files.absolute(tempFolder).child(name + ".vertex.glsl").writeString(prefix + config.vertexShader, false);
            Gdx.files.absolute(tempFolder).child(name + ".fragment.glsl").writeString(prefix + config.fragmentShader, false);
        }
        BaseShader result = new MultiPassShader(renderable, config);
        if (tempFolder != null && Gdx.app.getType() == Application.ApplicationType.Desktop)
            Gdx.files.absolute(tempFolder).child(name + ".log.txt").writeString(result.program.getLog(), false);
        return result;
    }
}
