//
//  ShaderPlain.fsh
//
#version 300 es
precision highp float;
in vec4       vMaterialSpecular;
in vec3       vMaterialDiffuse;
out vec4 fragColor;
in vec4 vertexColor;

uniform vec3      u_view;
uniform vec3      vLight0;
uniform vec3      vLightPos0;

in vec3 dynamicDiffuse;
in vec3 eye;
//in vec3 normal;			//NOTE: Need to be high precision
in vec3 FragPos; 	//NOTE: Need to be high precision
in vec2 texCoord;

uniform samplerCube sCubemapTexture;
uniform vec2	vRoughness;
uniform sampler2D  diffuseT;
uniform sampler2D   specularT;
uniform sampler2D normalMap;

struct Material {
    sampler2D diffuse;
    sampler2D      specular;
    float     shininess;
};

uniform Material material;

struct Light {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform Light light;
uniform float ambientOcclusion;



#define M_PI 3.1415926535897932384626433832795

vec3 FresnelSchlickWithRoughness(vec3 SpecularColor, vec3 E, vec3 N, float Gloss)
{
	return SpecularColor + (max(vec3(Gloss,Gloss,Gloss), SpecularColor) - SpecularColor) * pow(1.0 - clamp(dot(E, N), 0.0, 1.0), 5.0);
}

float zNear = 0.1;
float zFar  = 100.0;

float LinearizeDepth(float depth)
{
    // преобразуем обратно в NDC
    float z = depth * 2.0 - 1.0;
    return (2.0 * zNear * zFar) / (zFar + zNear - z * (zFar - zNear));
}

void main()
{
	// Mipmap index
	//float MipmapIndex = vRoughness.x * vRoughness.y;	//vRoughness.y = mip-level - 1

	//
	// Diffuse (Lambart)
	//
	//vec3 diffuseEnvColor = textureLod(sCubemapTexture, normal, MipmapIndex).xyz * vMaterialDiffuse / M_PI;
	//vec3 diffuseEnvColor = textureCube(sCubemapTexture, normal, MipmapIndex).xyz * vMaterialDiffuse / M_PI;
	//And Dynamic diffuse lighting is done per vertex

	//
	// Specular (Phong + Schlick Fresnel)
    //
    //vec3 eyeNormalized = normalize(eye);
   // vec3 reflection = reflect(-eyeNormalized, normal);
    //float fresnelTerm = pow(1.0 - clamp(dot(eyeNormalized, halfvecLight0), 0.0, 1.0), 5.0);

	// Dynamic Specular	
	//NOTE: Need to be high precision
	//float NdotH = max(dot(normalize(normal), normalize(halfvecLight0)), 0.0);
    //float fPower = exp2(10.0 * (1.0 - vRoughness.x) + 1.0);
   // float dynamicSpecular = pow(NdotH, fPower);
	//Normalizing factor for phong
	//float normalizeSpecular = (fPower + 1.0) / (M_PI * 2.0);
	
	//dynamicSpecular = (dynamicSpecular + fresnelTerm) * normalizeSpecular;
	
	//Envmap Specular

	//Fresnel equation for pre-filtered envmap
	//http://seblagarde.wordpress.com/2011/08/17/hello-world/
	//vec3 fresnel = FresnelSchlickWithRoughness(vMaterialSpecular.xyz, eyeNormalized, normal, 1.0 - vRoughness.x);
	//vec3 specularEnvColor = textureLod(sCubemapTexture, reflection, MipmapIndex).xyz * fresnel;
	//vec3 specularEnvColor = textureCube(sCubemapTexture, reflection, MipmapIndex).xyz * fresnel;
	//linearise
	//specularEnvColor.xyz = pow(specularEnvColor.xyz, vec3(2.2,2.2,2.2));

	vec3 ambient = light.ambient * vec3(texture(material.diffuse, texCoord));
    vec3 normal = texture(normalMap, texCoord).rgb;
        normal = normalize(normal * 2.0 - 1.0);
	vec3 norm = normal;
    vec3 lightDir = normalize(light.position - FragPos);
	float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * (diff * vec3(texture(material.diffuse, texCoord)));

    float specularStrength = 0.5f;
    vec3 viewDir = normalize(u_view - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
    vec3 specular = light.specular * (spec * vec3(texture(material.specular, texCoord)));

    float depth = LinearizeDepth(gl_FragCoord.z) / zFar;
    fragColor = vec4(vec3(depth), 1.0);

    fragColor = vec4(ambient+diffuse+specular,1);

   // fragColor = vec4(1f);
	//
	// Gamma conversion
	//fragmentColor.xyz = pow(fragmentColor.xyz, vec3(1.0/1.8, 1.0/1.8, 1.0/1.8));
}