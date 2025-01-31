//
//  ShaderPlain.vsh
//
#version 300 es
precision highp float;
layout(location = 0) in  vec3 a_position;
layout(location = 1) in vec3    a_normal;
layout(location = 2) in vec2    a_texCoord0;

out vec3    normal;
out vec3    FragPos;

uniform mat4      u_projTrans;
uniform mat4      u_worldTrans;
uniform vec3      u_view;

out vec2 texCoord;

void main(void)
{
    vec4 p = vec4(a_position,1);
    FragPos = vec3(u_worldTrans * p);
    gl_Position = u_projTrans * (u_worldTrans *  p);
    normal = a_normal;
    texCoord = a_texCoord0;
}