#version 300 es

precision highp sampler2DArray;

layout( location = 0 ) in vec3 attrib_pos;
layout( location = 3 ) in vec2 attrib_uv;

layout(std140) uniform glfx_GLOBAL
{
	mat4 glfx_MVP;
	mat4 glfx_PROJ;
	mat4 glfx_MV;

	vec4 glfx_QUAT;
	
	vec4 u_scale;
};
layout(std140) uniform glfx_INSTANCES
{
	vec4 glfx_IDATA[48];
};
uniform uint glfx_CURRENT_I;
#define glfx_T_SPAWN (glfx_IDATA[glfx_CURRENT_I].x)
#define glfx_T_ANIM (glfx_IDATA[glfx_CURRENT_I].y)

out vec2 var_uv;

vec2 scaleUV(vec2 uv, vec2 scale) {
    vec2 scaledUV = uv;
    // 
    scaledUV *= scale;  
    scaledUV += (1. -  scale) * 0.5;
    return scaledUV;
}

void main()
{
	vec2 v = attrib_pos.xy;
	gl_Position = vec4( v, -1., 1. );
	var_uv = v*0.5 + 0.5;

	var_uv = scaleUV(var_uv, 1. / vec2(u_scale.x, u_scale.x));
}