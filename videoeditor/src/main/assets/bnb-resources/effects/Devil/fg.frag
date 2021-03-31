#version 300 es

precision highp float;

#define Y_OFFSET 0.1
#define Y_SCALE 0.9
#define X_OFFSET -0.1
#define X_SCALE 2.1
in vec2 var_uv;

layout(std140) uniform glfx_GLOBAL
{
	mat4 glfx_MVP;
	mat4 glfx_PROJ;
	mat4 glfx_MV;
};

layout( location = 0 ) out vec4 frag_color;

uniform sampler2D glfx_VIDEO;

void main()
{	
	vec2 uv = var_uv;
	// uv.y = 1. - uv.y;
	
	uv.y *= Y_SCALE;
	uv.y += Y_OFFSET;

  uv.x *= X_SCALE;
	uv.x += X_OFFSET;
	// uv.x *= 0.5;
	vec3 rgb = texture(glfx_VIDEO,uv).xyz;

	// uv.x += 0.5;
	float a = texture(glfx_VIDEO,uv).x;
	frag_color = vec4(rgb,a);
}
