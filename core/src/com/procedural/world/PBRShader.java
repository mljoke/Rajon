package com.procedural.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.mljoke.rajon.Logger;
import com.mljoke.rajon.java.Resources;


/**
 * Created by PWorld on 09/08/2016.
 */
public class PBRShader implements Shader {
    ShaderProgram program;
    Camera camera;
    RenderContext context;
    Cubemap ref;

    int u_worldTrans;
    int u_projTrans;
    int u_view;
    int vLightA0;
    int vLightD0;
    int vLightS0;
    int vLightPos0;
    int albedo;
    int metallic;
    int normal;
    int sCubemapTexture;
    int	vRoughness;

    int ambientOcclusion;

    public Vector3 albedoColor=new Vector3(0.f,0.f,0.9f);
    public static Vector3 lightPos=new Vector3(1.f,1.f,0.9f);

    public float metallicValue=0.5f;
    public float rougness=0.9f;
    public float ambientOcclusionValue=1;

    Mesh currentMesh;

    public void loadReflection(String refl) {
        ref=new Cubemap(Gdx.files.internal("cubemaps/" + refl + "_c00.tga"), Gdx.files.internal("cubemaps/" + refl + "_c01.tga"),
                Gdx.files.internal("cubemaps/" + refl + "_c02.tga"), Gdx.files.internal("cubemaps/" + refl + "_c03.tga"),
                Gdx.files.internal("cubemaps/" + refl + "_c04.tga"), Gdx.files.internal("cubemaps/" + refl + "_c05.tga"));
        ref.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        ref.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    @Override
    public void init() {
        loadReflection("desertsky");

        String vert = Gdx.files.internal(Resources.vshader).readString();
        String frag = Gdx.files.internal(Resources.fshader).readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled()){
            Logger.log(Logger.ANDREAS,Logger.INFO, program.getLog());
            //throw new GdxRuntimeException(program.getLog());
        }
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_projTrans = program.getUniformLocation("u_projTrans");
        u_view = program.getUniformLocation("u_view");
        vLightPos0 = program.getUniformLocation("light.position");
        albedo = program.getUniformLocation("material.diffuse");
        normal = program.getUniformLocation("normalMap");
        metallic = program.getUniformLocation("material.specular");
        vLightA0 = program.getUniformLocation("light.ambient");
        vLightD0 = program.getUniformLocation("light.diffuse");
        vLightS0 = program.getUniformLocation("light.specular");
        program.getUniformLocation("gl_Color");
        sCubemapTexture = program.getUniformLocation("sCubemapTexture");
    }

    @Override
    public void end() {
        if (currentMesh != null) {
            currentMesh.unbind(program, tempArray.items);
            currentMesh = null;
        }
        program.end();
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera=camera;
        this.context=context;

        program.begin();
        program.setUniformMatrix(u_projTrans, camera.combined);
        program.setUniformf(u_view, camera.position);

        context.setDepthTest(GL20.GL_LEQUAL); /* context.setDepthTest(0) per disabilitare */
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    protected final IntArray tempArray = new IntArray();
    protected final int[] getAttributeLocations(Renderable renderable) {
        final IntIntMap attributes = new IntIntMap();
        final VertexAttributes attrs = renderable.meshPart.mesh.getVertexAttributes();
        final int c = attrs.size();
        for (int i = 0; i < c; i++) {
            final VertexAttribute attr = attrs.get(i);
            final int location = program.getAttributeLocation(attr.alias);
            if (location >= 0)
                attributes.put(attr.getKey(), location);
        }

        tempArray.clear();
        final int n = attrs.size();
        for (int i = 0; i < n; i++) {
            tempArray.add(attributes.get(attrs.get(i).getKey(), -1));
        }
        return tempArray.items;
    }
    Material currentMaterial;
    @Override
    public void render(Renderable renderable) {
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)){
            lightPos.x +=1f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)){
            lightPos.x -=1f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)){
            lightPos.y +=1f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)){
            lightPos.y -=1f;
        }
        if(currentMaterial!=renderable.material){
            currentMaterial=renderable.material;}
        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
        if(currentMaterial.get(PBRTextureAttribute.Albedo) != null) {
        ((PBRTextureAttribute)currentMaterial.get(PBRTextureAttribute.Albedo)).textureDescription.texture.bind(1);
        program.setUniformi(albedo, 1);}
        //program.setUniformf(albedo, albedoColor);
        if(currentMaterial.get(PBRTextureAttribute.Metallic) != null) {
        ((PBRTextureAttribute)currentMaterial.get(PBRTextureAttribute.Metallic)).textureDescription.texture.bind(2);
        program.setUniformi(metallic, 2);}
        if(currentMaterial.get(PBRTextureAttribute.Normal) != null){
        ((PBRTextureAttribute)currentMaterial.get(PBRTextureAttribute.Normal)).textureDescription.texture.bind(3);
        program.setUniformi(normal, 3);}
        //program.setUniformf(metallic, metallicValue);
        program.setUniformf(vLightPos0, new Vector3(lightPos));
        program.setUniformf(vLightA0,  0.5f, 0.5f, 0.5f);
        program.setUniformf(vLightD0,  0.5f, 0.5f, 0.5f); // darken the light a bit to fit the scene
        program.setUniformf(vLightS0, 1.0f, 1.0f, 1.0f);

        ref.bind(0);
        program.setUniformi(sCubemapTexture, 0);

        if (currentMesh != renderable.meshPart.mesh) {
            if (currentMesh != null)
                currentMesh.unbind(program, tempArray.items);
            currentMesh = renderable.meshPart.mesh;
            currentMesh.bind(program, getAttributeLocations(renderable));
        }

        renderable.meshPart.mesh.render(program,
                renderable.meshPart.primitiveType,
                renderable.meshPart.offset,
                renderable.meshPart.size,false);
    }

    @Override
    public void dispose() {

    }
}
